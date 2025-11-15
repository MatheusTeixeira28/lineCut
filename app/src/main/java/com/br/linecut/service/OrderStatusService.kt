package com.br.linecut.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.br.linecut.R
import com.br.linecut.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.*

/**
 * Foreground Service que monitora mudanças de status dos pedidos do usuário
 * mesmo quando o app está em segundo plano.
 * 
 * Funcionalidades:
 * - Monitora todos os pedidos ativos do usuário no Firebase
 * - Cria notificações automáticas quando status muda
 * - Mantém-se ativo em background através de Foreground Service
 */
class OrderStatusService : Service() {
    
    companion object {
        private const val TAG = "OrderStatusService"
        private const val CHANNEL_ID = "order_status_channel"
        private const val NOTIFICATION_ID = 1001
        
        fun start(context: Context) {
            val intent = Intent(context, OrderStatusService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stop(context: Context) {
            val intent = Intent(context, OrderStatusService::class.java)
            context.stopService(intent)
        }
    }
    
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Mapa para rastrear status anterior de cada pedido
    private val previousOrderStatuses = mutableMapOf<String, String>()
    
    // Lista de listeners ativos para cleanup
    private val activeListeners = mutableListOf<DatabaseReference>()
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service criado")
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service iniciado")
        
        try {
            // Criar notificação de foreground
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LineCut")
                .setContentText("Monitorando seus pedidos")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Use seu ícone aqui
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build()
            
            startForeground(NOTIFICATION_ID, notification)
            
            // Iniciar monitoramento de pedidos
            startMonitoringOrders()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao iniciar foreground service", e)
            // Se falhar, ainda assim tentar monitorar (sem foreground)
            startMonitoringOrders()
        }
        
        return START_STICKY // Reinicia automaticamente se o sistema matar o service
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destruído")
        
        // Limpar todos os listeners
        activeListeners.forEach { it.removeEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {}
            override fun onCancelled(error: DatabaseError) {}
        })}
        activeListeners.clear()
        
        // Cancelar todas as coroutines
        serviceScope.cancel()
    }
    
    /**
     * Cria canal de notificação para Android 8+
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoramento de Pedidos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitora o status dos seus pedidos em tempo real"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Inicia o monitoramento de todos os pedidos do usuário
     */
    private fun startMonitoringOrders() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "Usuário não autenticado")
            stopSelf()
            return
        }
        
        Log.d(TAG, "Iniciando monitoramento de pedidos para userId: $userId")
        
        // Referência para os pedidos do usuário
        val pedidosRef = database.getReference("pedidos")
        val query = pedidosRef.orderByChild("id_usuario").equalTo(userId)
        
        // Listener para novos pedidos e mudanças
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "Pedidos atualizados: ${snapshot.childrenCount} pedidos encontrados")
                
                snapshot.children.forEach { pedidoSnapshot ->
                    val orderId = pedidoSnapshot.key ?: return@forEach
                    val statusPedido = pedidoSnapshot.child("status_pedido").getValue(String::class.java) ?: "pendente"
                    val idLanchonete = pedidoSnapshot.child("id_lanchonete").getValue(String::class.java) ?: ""
                    
                    // Verificar se o status mudou
                    val previousStatus = previousOrderStatuses[orderId]
                    
                    if (previousStatus != null && previousStatus != statusPedido) {
                        Log.d(TAG, "Status do pedido $orderId mudou de $previousStatus para $statusPedido")
                        
                        // Buscar nome da lanchonete e criar notificação
                        handleStatusChange(userId, orderId, statusPedido, idLanchonete)
                    }
                    
                    // Atualizar status anterior
                    previousOrderStatuses[orderId] = statusPedido
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Erro ao monitorar pedidos: ${error.message}")
            }
        }
        
        query.addValueEventListener(listener)
        activeListeners.add(query.ref)
    }
    
    /**
     * Processa mudança de status e cria notificação apropriada
     */
    private fun handleStatusChange(
        userId: String,
        orderId: String,
        newStatus: String,
        storeId: String
    ) {
        serviceScope.launch {
            try {
                // Buscar nome da lanchonete
                val storeName = getStoreName(storeId)
                
                Log.d(TAG, "Criando notificação para status: $newStatus, loja: $storeName")
                
                // Criar notificação baseada no novo status
                when (newStatus) {
                    "em_preparo" -> {
                        notificationRepository.createOrderPreparingNotification(
                            userId = userId,
                            orderId = orderId,
                            storeName = storeName
                        )
                    }
                    "pronto" -> {
                        notificationRepository.createOrderReadyNotification(
                            userId = userId,
                            orderId = orderId,
                            storeName = storeName
                        )
                    }
                    "retirado", "entregue" -> {
                        notificationRepository.createOrderPickedUpNotification(
                            userId = userId,
                            orderId = orderId,
                            storeName = storeName
                        )
                        
                        // Também criar notificação de avaliação
                        delay(1000) // Pequeno delay entre notificações
                        notificationRepository.createRatingNotification(
                            userId = userId,
                            orderId = orderId,
                            storeName = storeName
                        )
                    }
                }
                
                Log.d(TAG, "Notificação criada com sucesso")
                
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao processar mudança de status", e)
            }
        }
    }
    
    /**
     * Busca o nome da lanchonete no Firebase
     */
    private suspend fun getStoreName(storeId: String): String = suspendCancellableCoroutine { continuation ->
        database.getReference("empresas")
            .child(storeId)
            .child("nome_lanchonete")
            .get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getValue(String::class.java) ?: "Estabelecimento"
                continuation.resume(name) {}
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Erro ao buscar nome da lanchonete", error)
                continuation.resume("Estabelecimento") {}
            }
    }
}
