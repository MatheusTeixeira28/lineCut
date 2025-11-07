package com.br.linecut.data.repository

import android.util.Log
import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.data.models.FirebaseOrder
import com.br.linecut.data.models.FirebaseStore
import com.br.linecut.ui.screens.Order
import com.br.linecut.ui.screens.OrderDetail
import com.br.linecut.ui.screens.OrderDetailItem
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
     * Busca um pedido específico pelo ID
     * Retorna OrderDetail com todas as informações necessárias para a tela de detalhes
     */
    suspend fun getOrderById(orderId: String): OrderDetail? {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("OrderRepository", "Usuário não autenticado")
            return null
        }
        
        val userId = currentUser.uid
        Log.d("OrderRepository", "Buscando pedido específico: $orderId para usuário: $userId")
        
        return try {
            // Buscar o pedido na tabela 'pedidos'
            val orderSnapshot = database.reference
                .child("pedidos")
                .child(orderId)
                .get()
                .await()
            
            if (!orderSnapshot.exists()) {
                Log.w("OrderRepository", "Pedido $orderId não encontrado")
                return null
            }
            
            Log.d("OrderRepository", "Dados do pedido: ${orderSnapshot.value}")
            
            // O Firebase pode retornar dados em formato aninhado com o ID como chave
            // Precisamos verificar e extrair corretamente
            val firebaseOrder = try {
                if (orderSnapshot.hasChildren()) {
                    // Estrutura: { orderId: { dados } } - pegar o primeiro filho
                    val firstChild = orderSnapshot.children.firstOrNull()
                    firstChild?.getValue(FirebaseOrder::class.java)?.copy(id = orderId)
                } else {
                    // Estrutura direta: { dados }
                    orderSnapshot.getValue(FirebaseOrder::class.java)?.copy(id = orderId)
                }
            } catch (e: Exception) {
                Log.e("OrderRepository", "Erro ao converter pedido para FirebaseOrder: ${e.message}", e)
                
                // Tentar conversão manual como fallback
                try {
                    val dataMap = orderSnapshot.value as? Map<*, *>
                    if (dataMap != null) {
                        Log.d("OrderRepository", "Tentando conversão manual do pedido")
                        FirebaseOrder(
                            id = orderId,
                            codTransacaoPagamento = dataMap["cod_transacao_pagamento"] as? String ?: "",
                            pixCopiaCola = dataMap["pix_copia_cola"] as? String ?: "",
                            datahoraCriacao = dataMap["datahora_criacao"] as? String ?: "",
                            datahoraPagamento = dataMap["datahora_pagamento"] as? String ?: "",
                            idLanchonete = dataMap["id_lanchonete"] as? String ?: "",
                            userId = dataMap["id_usuario"] as? String ?: "",
                            items = dataMap["items"] as? Map<String, Any>,
                            metodoPagamento = dataMap["metodo_pagamento"] as? String ?: "",
                            precoTotal = (dataMap["preco_total"] as? Number)?.toDouble() ?: 0.0,
                            qrCodePedido = dataMap["qr_code_pedido"] as? String ?: "",
                            statusPagamento = dataMap["status_pagamento"] as? String ?: "",
                            statusPedido = dataMap["status_pedido"] as? String ?: ""
                        )
                    } else {
                        null
                    }
                } catch (e2: Exception) {
                    Log.e("OrderRepository", "Erro na conversão manual: ${e2.message}", e2)
                    null
                }
            }
            
            if (firebaseOrder == null) {
                Log.e("OrderRepository", "Erro ao converter pedido $orderId")
                return null
            }
            
            Log.d("OrderRepository", "FirebaseOrder convertido:")
            Log.d("OrderRepository", "  - id: '${firebaseOrder.id}'")
            Log.d("OrderRepository", "  - userId: '${firebaseOrder.userId}'")
            Log.d("OrderRepository", "  - idLanchonete: '${firebaseOrder.idLanchonete}'")
            Log.d("OrderRepository", "  - statusPagamento: '${firebaseOrder.statusPagamento}'")
            Log.d("OrderRepository", "  - statusPedido: '${firebaseOrder.statusPedido}'")
            
            // SEGURANÇA: Verificar se o pedido pertence ao usuário autenticado
            if (firebaseOrder.userId != userId) {
                Log.w("OrderRepository", "❌ Pedido $orderId não pertence ao usuário $userId (pertence a '${firebaseOrder.userId}')")
                return null
            }
            
            Log.d("OrderRepository", "✅ Pedido validado - pertence ao usuário")
            
            // Buscar informações da lanchonete
            val storeInfo = fetchStoreInfoSync(firebaseOrder.idLanchonete)
            
            // Converter items do Firebase para OrderDetailItem
            val orderItems = mutableListOf<OrderDetailItem>()
            firebaseOrder.items?.forEach { (itemId, itemData) ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val itemMap = itemData as? Map<String, Any>
                    if (itemMap != null) {
                        val name = itemMap["nome_produto"] as? String ?: itemMap["nome"] as? String ?: "Item"
                        val quantity = (itemMap["quantidade"] as? Long)?.toInt() ?: 1
                        val price = (itemMap["subtotal"] as? Double) 
                                    ?: (itemMap["preco_unitario"] as? Double) 
                                    ?: (itemMap["preco"] as? Double) 
                                    ?: 0.0
                        
                        orderItems.add(OrderDetailItem(name, quantity, price))
                        Log.d("OrderRepository", "  - Item: $name x$quantity = R$ $price")
                    }
                } catch (e: Exception) {
                    Log.e("OrderRepository", "Erro ao converter item: ${e.message}")
                }
            }
            
            // Se não há items, adicionar placeholder
            if (orderItems.isEmpty()) {
                orderItems.add(OrderDetailItem("Itens do pedido", 1, firebaseOrder.total))
            }
            
            // Formatar data
            val dateString = formatDate(firebaseOrder.date)
            
            // Determinar status do pedido
            val status = when (firebaseOrder.statusPedido.lowercase()) {
                "pendente", "em_preparo" -> "Em preparo"
                "pronto" -> "Pronto para retirada"
                "entregue" -> "Pedido concluído"
                "cancelado" -> "Pedido cancelado"
                else -> "Em preparo"
            }
            
            Log.d("OrderRepository", "✅ OrderDetail criado com sucesso")
            Log.d("OrderRepository", "  - Total de items: ${orderItems.size}")
            Log.d("OrderRepository", "  - Status: $status")
            Log.d("OrderRepository", "  - StatusPagamento: ${firebaseOrder.statusPagamento}")
            
            // Buscar pix_copia_cola e qr_code_pedido diretamente do firebaseOrder
            val pixCopiaCola = firebaseOrder.pixCopiaCola.takeIf { it.isNotEmpty() }
            val qrCodeBase64Raw = firebaseOrder.qrCodePedido.takeIf { it.isNotEmpty() }
            
            // Remover prefixo "data:image/png;base64," se existir (compatibilidade com pedidos antigos)
            val qrCodeBase64 = qrCodeBase64Raw?.let { raw ->
                if (raw.contains("base64,")) {
                    raw.substringAfter("base64,")
                } else {
                    raw
                }
            }
            
            if (firebaseOrder.codTransacaoPagamento.isNotEmpty()) {
                Log.d("OrderRepository", "✅ PIX copia e cola: ${if (pixCopiaCola.isNullOrEmpty()) "VAZIO" else "OK"}")
                Log.d("OrderRepository", "✅ QR Code (qr_code_pedido): ${if (qrCodeBase64.isNullOrEmpty()) "VAZIO" else "${qrCodeBase64.length} chars"}")
            }
            
            // Criar OrderDetail
            OrderDetail(
                orderId = firebaseOrder.orderNumber,
                storeName = storeInfo.first,
                storeType = storeInfo.second,
                date = dateString,
                status = status,
                paymentStatus = if (firebaseOrder.statusPagamento == "pago") "aprovado" else "pendente",
                statusPagamento = firebaseOrder.statusPagamento,
                items = orderItems,
                total = firebaseOrder.total,
                paymentMethod = when (firebaseOrder.metodoPagamento.uppercase()) {
                    "PIX" -> "PIX"
                    "CREDITO" -> "Cartão de Crédito"
                    "DEBITO" -> "Cartão de Débito"
                    else -> firebaseOrder.metodoPagamento
                },
                pickupLocation = "Local de retirada", // TODO: Buscar do Firebase
                rating = null, // TODO: Implementar sistema de avaliação
                imageRes = com.br.linecut.R.drawable.burger_queen, // Placeholder
                remainingTime = null,
                createdAtMillis = firebaseOrder.date,
                qrCodeBase64 = qrCodeBase64,
                pixCopiaCola = pixCopiaCola
            )
        } catch (e: Exception) {
            Log.e("OrderRepository", "Erro ao buscar pedido $orderId: ${e.message}", e)
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Busca informações da lanchonete de forma síncrona
     * Retorna Pair<storeName, storeCategory>
     */
    private suspend fun fetchStoreInfoSync(storeId: String): Pair<String, String> {
        if (storeId.isEmpty()) {
            return Pair("Lanchonete", "Categoria")
        }
        
        return try {
            val storeSnapshot = database.reference
                .child("empresas")
                .child(storeId)
                .get()
                .await()
            
            if (storeSnapshot.exists()) {
                val firebaseStore = storeSnapshot.getValue(FirebaseStore::class.java)
                if (firebaseStore != null) {
                    Pair(
                        firebaseStore.nomeLanchonete.ifEmpty { "Lanchonete" },
                        firebaseStore.description.ifEmpty { "Categoria" }
                    )
                } else {
                    Pair("Lanchonete", "Categoria")
                }
            } else {
                Pair("Lanchonete", "Categoria")
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "Erro ao buscar lanchonete $storeId: ${e.message}")
            Pair("Lanchonete", "Categoria")
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
