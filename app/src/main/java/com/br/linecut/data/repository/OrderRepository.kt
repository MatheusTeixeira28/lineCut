package com.br.linecut.data.repository

import android.util.Log
import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.data.models.FirebaseOrder
import com.br.linecut.data.models.FirebaseStore
import com.br.linecut.ui.screens.Order
import com.br.linecut.ui.screens.OrderStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Repositório para gerenciar operações de pedidos no Firebase
 */
class OrderRepository {
    private val auth: FirebaseAuth = FirebaseConfig.auth
    private val database: FirebaseDatabase = FirebaseConfig.realtimeDatabase
    
    /**
     * Busca os pedidos do usuário autenticado
     * Primeiro busca os IDs dos pedidos em pedidos_por_usuario/{userId}
     * Depois busca os detalhes de cada pedido na tabela pedidos
     */
    fun getUserOrders(): Flow<List<Order>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("OrderRepository", "Usuário não autenticado")
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        
        val userId = currentUser.uid
        Log.d("OrderRepository", "Buscando pedidos para o usuário: $userId")
        
        // Referência para pedidos_por_usuario/{userId}
        val userOrdersRef = database.reference
            .child("pedidos_por_usuario")
            .child(userId)
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("OrderRepository", "Snapshot recebido: ${snapshot.exists()}")
                Log.d("OrderRepository", "Número de filhos: ${snapshot.childrenCount}")
                
                if (!snapshot.exists()) {
                    Log.d("OrderRepository", "Nenhum pedido encontrado para o usuário")
                    trySend(emptyList())
                    return
                }
                
                // Lista para armazenar os IDs dos pedidos
                val orderIds = mutableListOf<String>()
                
                // Iterar sobre os pedidos do usuário
                for (orderSnapshot in snapshot.children) {
                    val orderId = orderSnapshot.key
                    if (orderId != null) {
                        orderIds.add(orderId)
                        Log.d("OrderRepository", "ID do pedido encontrado: $orderId")
                    }
                }
                
                if (orderIds.isEmpty()) {
                    Log.d("OrderRepository", "Lista de IDs de pedidos vazia")
                    trySend(emptyList())
                    return
                }
                
                Log.d("OrderRepository", "Total de IDs encontrados: ${orderIds.size}")
                
                // Buscar os detalhes de cada pedido na tabela 'pedidos'
                val orders = mutableListOf<Order>()
                var processedCount = 0
                
                for (orderId in orderIds) {
                    Log.d("OrderRepository", "Buscando pedido: $orderId")
                    database.reference
                        .child("pedidos")
                        .child(orderId)
                        .get()
                        .addOnSuccessListener { orderSnapshot ->
                            Log.d("OrderRepository", "Resposta para $orderId - existe: ${orderSnapshot.exists()}")
                            
                            if (orderSnapshot.exists()) {
                                try {
                                    // Log COMPLETO dos dados brutos para debug
                                    Log.d("OrderRepository", "==== PEDIDO $orderId ====")
                                    Log.d("OrderRepository", "Dados completos: ${orderSnapshot.value}")
                                    
                                    // Listar todos os campos
                                    for (child in orderSnapshot.children) {
                                        Log.d("OrderRepository", "Campo '${child.key}': ${child.value}")
                                    }
                                    
                                    val firebaseOrder = orderSnapshot.getValue(FirebaseOrder::class.java)
                                    
                                    if (firebaseOrder != null) {
                                        Log.d("OrderRepository", "Objeto convertido:")
                                        Log.d("OrderRepository", "  - id: '${firebaseOrder.id}'")
                                        Log.d("OrderRepository", "  - orderNumber: '${firebaseOrder.orderNumber}'")
                                        Log.d("OrderRepository", "  - userId: '${firebaseOrder.userId}'")
                                        Log.d("OrderRepository", "  - idLanchonete: '${firebaseOrder.idLanchonete}'")
                                        Log.d("OrderRepository", "  - status: '${firebaseOrder.status}'")
                                        Log.d("OrderRepository", "  - total: ${firebaseOrder.total}")
                                        Log.d("OrderRepository", "Usuario esperado: '$userId'")
                                        
                                        // SEGURANÇA: Verificar se o pedido pertence EXATAMENTE ao usuário autenticado
                                        // Pedidos sem userId ou de outros usuários NÃO devem ser exibidos
                                        if (firebaseOrder.userId == userId && firebaseOrder.userId.isNotEmpty()) {
                                            // Buscar informações da lanchonete antes de converter
                                            fetchStoreInfo(firebaseOrder) { order ->
                                                if (order != null) {
                                                    orders.add(order)
                                                    Log.d("OrderRepository", "✅ Pedido convertido com sucesso: ${order.orderNumber}")
                                                }
                                                
                                                processedCount++
                                                Log.d("OrderRepository", "Progresso: $processedCount/${orderIds.size}")
                                                
                                                // Quando todos os pedidos forem processados, enviar a lista
                                                if (processedCount == orderIds.size) {
                                                    // Ordenar por data (mais recente primeiro)
                                                    val sortedOrders = orders.sortedByDescending { it.date }
                                                    Log.d("OrderRepository", "Enviando ${sortedOrders.size} pedidos para a UI")
                                                    trySend(sortedOrders)
                                                }
                                            }
                                        } else {
                                            if (firebaseOrder.userId.isEmpty()) {
                                                Log.w("OrderRepository", "❌ Pedido $orderId rejeitado: userId vazio")
                                            } else {
                                                Log.w("OrderRepository", "❌ Pedido $orderId rejeitado: pertence a '${firebaseOrder.userId}', não a '$userId'")
                                            }
                                            
                                            processedCount++
                                            if (processedCount == orderIds.size) {
                                                val sortedOrders = orders.sortedByDescending { it.date }
                                                trySend(sortedOrders)
                                            }
                                        }
                                    } else {
                                        Log.e("OrderRepository", "FirebaseOrder é null após conversão para $orderId")
                                        processedCount++
                                        if (processedCount == orderIds.size) {
                                            val sortedOrders = orders.sortedByDescending { it.date }
                                            trySend(sortedOrders)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("OrderRepository", "Erro ao converter pedido $orderId: ${e.message}", e)
                                    e.printStackTrace()
                                    processedCount++
                                    if (processedCount == orderIds.size) {
                                        val sortedOrders = orders.sortedByDescending { it.date }
                                        trySend(sortedOrders)
                                    }
                                }
                            } else {
                                Log.w("OrderRepository", "Pedido $orderId não encontrado na tabela 'pedidos'")
                                processedCount++
                                if (processedCount == orderIds.size) {
                                    val sortedOrders = orders.sortedByDescending { it.date }
                                    trySend(sortedOrders)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("OrderRepository", "Erro ao buscar pedido $orderId: ${exception.message}", exception)
                            exception.printStackTrace()
                            processedCount++
                            
                            if (processedCount == orderIds.size) {
                                val sortedOrders = orders.sortedByDescending { it.date }
                                trySend(sortedOrders)
                            }
                        }
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("OrderRepository", "Erro ao buscar pedidos do usuário: ${error.message}")
                trySend(emptyList())
            }
        }
        
        userOrdersRef.addValueEventListener(listener)
        
        awaitClose {
            userOrdersRef.removeEventListener(listener)
        }
    }
    
    /**
     * Busca informações da lanchonete no Firebase e converte o pedido
     */
    private fun fetchStoreInfo(firebaseOrder: FirebaseOrder, callback: (Order?) -> Unit) {
        val storeId = firebaseOrder.idLanchonete
        
        if (storeId.isEmpty()) {
            Log.w("OrderRepository", "ID da lanchonete vazio, usando valores padrão")
            callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", ""))
            return
        }
        
        Log.d("OrderRepository", "Buscando informações da lanchonete: $storeId")
        
        database.reference
            .child("empresas")
            .child(storeId)
            .get()
            .addOnSuccessListener { storeSnapshot ->
                if (storeSnapshot.exists()) {
                    try {
                        Log.d("OrderRepository", "Dados da lanchonete: ${storeSnapshot.value}")
                        
                        val firebaseStore = storeSnapshot.getValue(FirebaseStore::class.java)
                        
                        if (firebaseStore != null) {
                            val storeName = firebaseStore.nomeLanchonete.ifEmpty { "Lanchonete" }
                            val storeCategory = firebaseStore.description.ifEmpty { "Categoria" }
                            val storeImageUrl = firebaseStore.imageUrl
                            
                            Log.d("OrderRepository", "Lanchonete encontrada: $storeName - $storeCategory - imageUrl: $storeImageUrl")
                            callback(convertFirebaseOrderToOrder(firebaseOrder, storeName, storeCategory, storeImageUrl))
                        } else {
                            Log.w("OrderRepository", "FirebaseStore é null, usando valores padrão")
                            callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", ""))
                        }
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Erro ao converter lanchonete $storeId: ${e.message}", e)
                        callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", ""))
                    }
                } else {
                    Log.w("OrderRepository", "Lanchonete $storeId não encontrada, usando valores padrão")
                    callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", ""))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderRepository", "Erro ao buscar lanchonete $storeId: ${exception.message}", exception)
                callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", ""))
            }
    }
    
    /**
     * Converte FirebaseOrder para Order (modelo da UI)
     */
    private fun convertFirebaseOrderToOrder(
        firebaseOrder: FirebaseOrder,
        storeName: String,
        storeCategory: String,
        storeImageUrl: String
    ): Order {
        val orderStatus = when (firebaseOrder.status.uppercase()) {
            "IN_PROGRESS", "EM_ANDAMENTO" -> OrderStatus.IN_PROGRESS
            "COMPLETED", "CONCLUIDO", "CONCLUÍDO" -> OrderStatus.COMPLETED
            "CANCELLED", "CANCELADO" -> OrderStatus.CANCELLED
            else -> OrderStatus.IN_PROGRESS
        }
        
        // Formatar a data
        val dateString = formatDate(firebaseOrder.date)
        
        return Order(
            id = firebaseOrder.id,
            orderNumber = firebaseOrder.orderNumber,
            date = dateString,
            storeName = storeName,
            storeCategory = storeCategory,
            status = orderStatus,
            total = firebaseOrder.total,
            rating = firebaseOrder.rating,
            canRate = firebaseOrder.canRate,
            storeImageUrl = storeImageUrl
        )
    }
    
    /**
     * Formata timestamp para string de data legível
     */
    private fun formatDate(timestamp: Long): String {
        return try {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("pt", "BR"))
            formatter.format(date)
        } catch (e: Exception) {
            Log.e("OrderRepository", "Erro ao formatar data: ${e.message}", e)
            "Data indisponível"
        }
    }
}
