package com.br.linecut.data.repository

import android.content.Context
import android.util.Log
import com.br.linecut.ui.screens.profile.Notification
import com.br.linecut.ui.screens.profile.NotificationType
import com.br.linecut.utils.NotificationHelper
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class NotificationRepository(private val context: Context? = null) {
    private val database = FirebaseDatabase.getInstance()
    private val notificationsRef = database.getReference("notificacoes")
    
    /**
     * Cria uma notificação de pedido realizado
     */
    suspend fun createOrderPlacedNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val title = "Pedido realizado"
            val message = "Recebemos seu pedido na $storeName. Em breve ele será preparado!"
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PLACED",
                "title" to title,
                "message" to message,
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação criada: $notificationId para pedido $orderId")
            
            // Mostrar notificação do sistema Android
            context?.let {
                NotificationHelper.showNotification(
                    context = it,
                    notificationId = NotificationHelper.generateNotificationId(),
                    title = title,
                    message = message
                )
                Log.d("NotificationRepository", "🔔 Notificação do sistema mostrada")
            }
            
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido em preparo
     */
    suspend fun createOrderPreparingNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val title = "Pedido em preparo"
            val message = "Seu pedido na $storeName está sendo preparado!"
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PREPARING",
                "title" to title,
                "message" to message,
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de preparo criada para pedido $orderId")
            
            // Mostrar notificação do sistema Android
            context?.let {
                NotificationHelper.showNotification(
                    context = it,
                    notificationId = NotificationHelper.generateNotificationId(),
                    title = title,
                    message = message
                )
                Log.d("NotificationRepository", "🔔 Notificação do sistema mostrada")
            }
            
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de preparo", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido pronto
     */
    suspend fun createOrderReadyNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val title = "Pedido pronto para retirada"
            val message = "Seu pedido está pronto! Retire no balcão quando quiser."
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_READY",
                "title" to title,
                "message" to message,
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de pedido pronto criada para $orderId")
            
            // Mostrar notificação do sistema Android
            context?.let {
                NotificationHelper.showNotification(
                    context = it,
                    notificationId = NotificationHelper.generateNotificationId(),
                    title = title,
                    message = message
                )
                Log.d("NotificationRepository", "🔔 Notificação do sistema mostrada")
            }
            
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de pedido pronto", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido retirado
     */
    suspend fun createOrderPickedUpNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val title = "Pedido retirado"
            val message = "Você retirou seu pedido na $storeName. Aproveite sua refeição!"
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PICKED_UP",
                "title" to title,
                "message" to message,
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de pedido retirado criada para $orderId")
            
            // Mostrar notificação do sistema Android
            context?.let {
                NotificationHelper.showNotification(
                    context = it,
                    notificationId = NotificationHelper.generateNotificationId(),
                    title = title,
                    message = message
                )
                Log.d("NotificationRepository", "🔔 Notificação do sistema mostrada")
            }
            
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de pedido retirado", e)
            false
        }
    }
    
    /**
     * Cria uma notificação para avaliar pedido
     */
    suspend fun createRatingNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val title = "Avalie seu pedido"
            val message = "O que achou do seu pedido? Sua opinião é muito importante!"
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "RATING",
                "title" to title,
                "message" to message,
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to true // Mostrar estrelas de avaliação
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de avaliação criada para pedido $orderId")
            
            // Mostrar notificação do sistema Android
            context?.let {
                NotificationHelper.showNotification(
                    context = it,
                    notificationId = NotificationHelper.generateNotificationId(),
                    title = title,
                    message = message
                )
                Log.d("NotificationRepository", "🔔 Notificação do sistema mostrada")
            }
            
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de avaliação", e)
            false
        }
    }
    
    /**
     * Observa notificações do usuário em tempo real
     */
    fun getUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Lista temporária com timestamp para ordenação
                val notificationsWithTimestamp = mutableListOf<Pair<Notification, Long>>()
                
                for (child in snapshot.children) {
                    try {
                        val id = child.child("id").getValue(String::class.java) ?: continue
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val message = child.child("message").getValue(String::class.java) ?: ""
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                        val typeStr = child.child("type").getValue(String::class.java) ?: "ORDER_PLACED"
                        val showRating = child.child("showRating").getValue(Boolean::class.java) ?: false
                        
                        val type = when (typeStr) {
                            "RATING" -> NotificationType.RATING
                            "ORDER_PICKED_UP" -> NotificationType.ORDER_PICKED_UP
                            "ORDER_READY" -> NotificationType.ORDER_READY
                            "ORDER_PREPARING" -> NotificationType.ORDER_PREPARING
                            else -> NotificationType.ORDER_PLACED
                        }
                        
                        val time = formatTimestamp(timestamp)
                        
                        val notification = Notification(
                            id = id,
                            title = title,
                            message = message,
                            time = time,
                            type = type,
                            showRating = showRating
                        )
                        
                        // Adicionar com timestamp para ordenação
                        notificationsWithTimestamp.add(Pair(notification, timestamp))
                    } catch (e: Exception) {
                        Log.e("NotificationRepository", "Erro ao converter notificação", e)
                    }
                }
                
                // Ordenar por timestamp (mais recente primeiro) e extrair apenas as notificações
                val notifications = notificationsWithTimestamp
                    .sortedByDescending { it.second }
                    .map { it.first }
                
                Log.d("NotificationRepository", "✅ ${notifications.size} notificações carregadas e ordenadas")
                trySend(notifications)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("NotificationRepository", "Erro ao observar notificações", error.toException())
                close(error.toException())
            }
        }
        
        notificationsRef.child(userId).orderByChild("timestamp").addValueEventListener(listener)
        
        awaitClose {
            notificationsRef.child(userId).removeEventListener(listener)
        }
    }
    
    /**
     * Formata timestamp para exibição
     */
    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        
        return when {
            diff < 60000 -> "Agora"
            diff < 3600000 -> "${diff / 60000} min atrás"
            diff < 86400000 -> "${diff / 3600000}h atrás"
            else -> {
                val sdf = SimpleDateFormat("dd/MM 'às' HH:mm", Locale("pt", "BR"))
                sdf.format(Date(timestamp))
            }
        }
    }
    
    /**
     * Deleta todas as notificações do usuário
     */
    suspend fun deleteAllNotifications(userId: String): Boolean {
        return try {
            notificationsRef.child(userId).removeValue().await()
            Log.d("NotificationRepository", "✅ Todas as notificações do usuário $userId foram deletadas")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao deletar notificações", e)
            false
        }
    }
}

