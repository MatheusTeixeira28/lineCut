package com.br.linecut.data.repository

import android.util.Log
import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.data.models.FirebaseOrder
import com.br.linecut.data.models.FirebaseStore
import com.br.linecut.data.models.OrderRating
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
                                    
                                    val firebaseOrder = orderSnapshot.getValue(FirebaseOrder::class.java)?.copy(id = orderId)
                                    
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
            Log.e("RateOrderScreen", "❌ getOrderById - Usuário não autenticado")
            Log.e("OrderRepository", "Usuário não autenticado")
            return null
        }
        
        val userId = currentUser.uid
        Log.d("RateOrderScreen", "==== getOrderById CHAMADO ====")
        Log.d("RateOrderScreen", "  - OrderId: $orderId")
        Log.d("RateOrderScreen", "  - UserId: $userId")
        Log.d("OrderRepository", "Buscando pedido específico: $orderId para usuário: $userId")
        
        return try {
            // Buscar o pedido na tabela 'pedidos'
            Log.d("RateOrderScreen", "Buscando em: pedidos/$orderId")
            val orderSnapshot = database.reference
                .child("pedidos")
                .child(orderId)
                .get()
                .await()
            
            Log.d("RateOrderScreen", "Snapshot exists: ${orderSnapshot.exists()}")
            Log.d("RateOrderScreen", "Snapshot hasChildren: ${orderSnapshot.hasChildren()}")
            
            if (!orderSnapshot.exists()) {
                Log.w("RateOrderScreen", "❌ Pedido $orderId NÃO ENCONTRADO na tabela 'pedidos'")
                Log.w("OrderRepository", "Pedido $orderId não encontrado")
                return null
            }
            
            Log.d("RateOrderScreen", "✅ Pedido encontrado na tabela 'pedidos'")
            Log.d("OrderRepository", "Dados do pedido: ${orderSnapshot.value}")
            
            // O Firebase pode retornar dados em formato aninhado com o ID como chave
            // Precisamos verificar e extrair corretamente
            val firebaseOrder = try {
                Log.d("RateOrderScreen", "Tentando converter snapshot para FirebaseOrder...")
                val result = if (orderSnapshot.hasChildren()) {
                    // Estrutura: { orderId: { dados } } - pegar o primeiro filho
                    Log.d("RateOrderScreen", "Snapshot tem filhos - estrutura aninhada")
                    val firstChild = orderSnapshot.children.firstOrNull()
                    Log.d("RateOrderScreen", "Primeiro filho key: ${firstChild?.key}")
                    firstChild?.getValue(FirebaseOrder::class.java)?.copy(id = firstChild.key ?: orderId)
                } else {
                    // Estrutura direta: { dados }
                    Log.d("RateOrderScreen", "Estrutura direta - sem filhos")
                    orderSnapshot.getValue(FirebaseOrder::class.java)?.copy(id = orderId)
                }
                Log.d("RateOrderScreen", "Conversão automática: ${if (result != null) "SUCESSO ✅" else "FALHOU ❌"}")
                result
            } catch (e: Exception) {
                Log.e("RateOrderScreen", "❌ Erro ao converter pedido: ${e.message}", e)
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
                Log.e("RateOrderScreen", "❌ FirebaseOrder é NULL após conversão")
                Log.e("OrderRepository", "Erro ao converter pedido $orderId")
                return null
            }
            
            Log.d("RateOrderScreen", "✅ FirebaseOrder convertido com sucesso")
            Log.d("RateOrderScreen", "  - id: '${firebaseOrder.id}'")
            Log.d("RateOrderScreen", "  - userId: '${firebaseOrder.userId}'")
            Log.d("RateOrderScreen", "  - idLanchonete: '${firebaseOrder.idLanchonete}'")
            Log.d("RateOrderScreen", "  - statusPagamento: '${firebaseOrder.statusPagamento}'")
            Log.d("RateOrderScreen", "  - statusPedido: '${firebaseOrder.statusPedido}'")
            
            Log.d("OrderRepository", "FirebaseOrder convertido:")
            Log.d("OrderRepository", "  - id: '${firebaseOrder.id}'")
            Log.d("OrderRepository", "  - userId: '${firebaseOrder.userId}'")
            Log.d("OrderRepository", "  - idLanchonete: '${firebaseOrder.idLanchonete}'")
            Log.d("OrderRepository", "  - statusPagamento: '${firebaseOrder.statusPagamento}'")
            Log.d("OrderRepository", "  - statusPedido: '${firebaseOrder.statusPedido}'")
            Log.d("OrderRepository", "  - qrCodePedido: ${if (firebaseOrder.qrCodePedido.isEmpty()) "VAZIO ❌" else "OK ✅ (${firebaseOrder.qrCodePedido.length} chars)"}")
            Log.d("OrderRepository", "  - pixCopiaCola: ${if (firebaseOrder.pixCopiaCola.isEmpty()) "VAZIO ❌" else "OK ✅ (${firebaseOrder.pixCopiaCola.length} chars)"}")
            
            // SEGURANÇA: Verificar se o pedido pertence ao usuário autenticado
            Log.d("RateOrderScreen", "Verificando segurança - userId do pedido: '${firebaseOrder.userId}' vs userId autenticado: '$userId'")
            if (firebaseOrder.userId != userId) {
                Log.w("RateOrderScreen", "❌ PEDIDO NÃO PERTENCE AO USUÁRIO")
                Log.w("RateOrderScreen", "  - Pedido pertence a: '${firebaseOrder.userId}'")
                Log.w("RateOrderScreen", "  - Usuário autenticado: '$userId'")
                Log.w("OrderRepository", "❌ Pedido $orderId não pertence ao usuário $userId (pertence a '${firebaseOrder.userId}')")
                return null
            }
            
            Log.d("RateOrderScreen", "✅ Pedido validado - pertence ao usuário")
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
                        
                        // Buscar preco_unitario (para exibir na UI)
                        val precoUnitario = (itemMap["preco_unitario"] as? Double) 
                                            ?: (itemMap["preco_unitario"] as? Long)?.toDouble()
                        
                        // Buscar preco_total (subtotal já calculado)
                        val precoTotal = (itemMap["preco_total"] as? Double) 
                                        ?: (itemMap["preco_total"] as? Long)?.toDouble()
                        
                        // Se temos preco_total, usar ele. Senão, calcular (preco_unitario * quantidade)
                        val price = precoTotal 
                                    ?: (precoUnitario?.let { it * quantity })
                                    ?: 0.0
                        
                        orderItems.add(OrderDetailItem(name, quantity, price))
                        Log.d("OrderRepository", "  - Item: $name x$quantity - preco_unitario: $precoUnitario, preco_total: $precoTotal, final: R$ $price")
                    }
                } catch (e: Exception) {
                    Log.e("OrderRepository", "Erro ao converter item: ${e.message}", e)
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
                statusPedido = firebaseOrder.statusPedido,
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
                pixCopiaCola = pixCopiaCola,
                storeId = firebaseOrder.idLanchonete
            ).also { orderDetail ->
                Log.d("OrderRepository", "==== ORDER DETAIL CRIADO ====")
                Log.d("OrderRepository", "orderId: ${orderDetail.orderId}")
                Log.d("OrderRepository", "paymentStatus: ${orderDetail.paymentStatus}")
                Log.d("OrderRepository", "statusPagamento: ${orderDetail.statusPagamento}")
                Log.d("OrderRepository", "statusPedido: ${orderDetail.statusPedido}")
                Log.d("OrderRepository", "Firebase statusPagamento: ${firebaseOrder.statusPagamento}")
                Log.d("OrderRepository", "Firebase statusPedido: ${firebaseOrder.statusPedido}")
                Log.d("OrderRepository", "Condição (firebaseOrder.statusPagamento == 'pago'): ${firebaseOrder.statusPagamento == "pago"}")
            }
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
     * Busca informações da lanchonete e avaliação no Firebase e converte o pedido
     */
    private fun fetchStoreInfo(firebaseOrder: FirebaseOrder, callback: (Order?) -> Unit) {
        val storeId = firebaseOrder.idLanchonete
        
        if (storeId.isEmpty()) {
            Log.w("OrderRepository", "ID da lanchonete vazio, usando valores padrão")
            callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", "", null))
            return
        }
        
        Log.d("OrderRepository", "Buscando informações da lanchonete: $storeId")
        
        // Buscar informações da lanchonete
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
                            
                            // Buscar avaliação do pedido
                            fetchRatingForOrder(storeId, firebaseOrder.id) { averageRating ->
                                callback(convertFirebaseOrderToOrder(firebaseOrder, storeName, storeCategory, storeImageUrl, averageRating))
                            }
                        } else {
                            Log.w("OrderRepository", "FirebaseStore é null, usando valores padrão")
                            callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", "", null))
                        }
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Erro ao converter lanchonete $storeId: ${e.message}", e)
                        callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", "", null))
                    }
                } else {
                    Log.w("OrderRepository", "Lanchonete $storeId não encontrada, usando valores padrão")
                    callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", "", null))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderRepository", "Erro ao buscar lanchonete $storeId: ${exception.message}", exception)
                callback(convertFirebaseOrderToOrder(firebaseOrder, "Lanchonete", "Categoria", "", null))
            }
    }
    
    /**
     * Busca a avaliação de um pedido e calcula a média
     * Retorna a média exata (1.0-5.0) ou null se não houver avaliação
     */
    private fun fetchRatingForOrder(storeId: String, orderId: String, callback: (Double?) -> Unit) {
        database.reference
            .child("pedidos_por_lanchonete")
            .child(storeId)
            .child(orderId)
            .child("avaliacao")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    try {
                        val rating = snapshot.getValue(OrderRating::class.java)
                        if (rating != null) {
                            // Calcular média das 3 notas (mantém decimais)
                            val average = (rating.atendimento + rating.qualidade + rating.velocidade) / 3.0
                            
                            Log.d("OrderRepository", "Avaliação encontrada para pedido $orderId: atendimento=${rating.atendimento}, qualidade=${rating.qualidade}, velocidade=${rating.velocidade}, média=$average")
                            callback(average)
                        } else {
                            Log.d("OrderRepository", "Nenhuma avaliação para pedido $orderId")
                            callback(null)
                        }
                    } catch (e: Exception) {
                        Log.e("OrderRepository", "Erro ao processar avaliação: ${e.message}", e)
                        callback(null)
                    }
                } else {
                    Log.d("OrderRepository", "Sem avaliação para pedido $orderId")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("OrderRepository", "Erro ao buscar avaliação: ${exception.message}", exception)
                callback(null)
            }
    }
    
    /**
     * Converte FirebaseOrder para Order (modelo da UI)
     */
    private fun convertFirebaseOrderToOrder(
        firebaseOrder: FirebaseOrder,
        storeName: String,
        storeCategory: String,
        storeImageUrl: String,
        averageRating: Double? = null
    ): Order {
        Log.d("OrderRepository", "==== CONVERTENDO FIREBASE ORDER PARA ORDER ====")
        Log.d("OrderRepository", "Firebase Order ID: ${firebaseOrder.id}")
        Log.d("OrderRepository", "Store Name: $storeName")
        Log.d("OrderRepository", "Average Rating recebido: $averageRating")
        
        val orderStatus = when (firebaseOrder.status.uppercase()) {
            "IN_PROGRESS", "EM_ANDAMENTO" -> OrderStatus.IN_PROGRESS
            "COMPLETED", "CONCLUIDO", "CONCLUÍDO" -> OrderStatus.COMPLETED
            "CANCELLED", "CANCELADO" -> OrderStatus.CANCELLED
            else -> OrderStatus.IN_PROGRESS
        }
        
        // Formatar a data
        val dateString = formatDate(firebaseOrder.date)
        
        val ratingFloat = averageRating?.toFloat()
        Log.d("OrderRepository", "Rating convertido para Float: $ratingFloat")
        
        val order = Order(
            id = firebaseOrder.id,
            orderNumber = firebaseOrder.orderNumber,
            date = dateString,
            storeName = storeName,
            storeCategory = storeCategory,
            status = orderStatus,
            total = firebaseOrder.total,
            rating = ratingFloat, // Converter para Float
            canRate = firebaseOrder.canRate,
            storeImageUrl = storeImageUrl,
            storeId = firebaseOrder.idLanchonete
        )
        
        Log.d("OrderRepository", "Order criado - ID: ${order.id}, Rating: ${order.rating}, Status: ${order.status}")
        return order
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
    
    /**
     * Salva a avaliação do pedido no Firebase
     * Caminho: pedidos_por_lanchonete/{storeId}/{orderId}/avaliacao
     * 
     * @param storeId ID da lanchonete
     * @param orderId ID do pedido
     * @param qualityRating Nota de qualidade (1-5)
     * @param speedRating Nota de velocidade (1-5)
     * @param serviceRating Nota de atendimento (1-5)
     * @return Result<Unit> indicando sucesso ou erro
     */
    suspend fun saveOrderRating(
        storeId: String,
        orderId: String,
        qualityRating: Int,
        speedRating: Int,
        serviceRating: Int
    ): Result<Unit> {
        return try {
            Log.d("OrderRepository", "Salvando avaliação para pedido $orderId da lanchonete $storeId")
            
            // Validar parâmetros
            if (storeId.isEmpty() || orderId.isEmpty()) {
                Log.e("OrderRepository", "StoreId ou OrderId vazios")
                return Result.failure(IllegalArgumentException("StoreId e OrderId são obrigatórios"))
            }
            
            if (qualityRating !in 1..5 || speedRating !in 1..5 || serviceRating !in 1..5) {
                Log.e("OrderRepository", "Notas devem estar entre 1 e 5")
                return Result.failure(IllegalArgumentException("Notas devem estar entre 1 e 5"))
            }
            
            // Criar timestamp no formato ISO 8601
            val currentTimestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(Date())
            
            // Criar objeto de avaliação
            val rating = OrderRating(
                atendimento = serviceRating,
                qualidade = qualityRating,
                velocidade = speedRating,
                dataAvaliacao = currentTimestamp
            )
            
            Log.d("OrderRepository", "Avaliação criada: atendimento=$serviceRating, qualidade=$qualityRating, velocidade=$speedRating")
            Log.d("OrderRepository", "Data da avaliação: $currentTimestamp")
            
            // Salvar no caminho: pedidos_por_lanchonete/{storeId}/{orderId}/avaliacao
            database.reference
                .child("pedidos_por_lanchonete")
                .child(storeId)
                .child(orderId)
                .child("avaliacao")
                .setValue(rating)
                .await()
            
            Log.d("OrderRepository", "✅ Avaliação salva com sucesso!")
            Result.success(Unit)
            
        } catch (e: Exception) {
            Log.e("OrderRepository", "❌ Erro ao salvar avaliação: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Busca a avaliação de um pedido específico
     * Caminho: pedidos_por_lanchonete/{storeId}/{orderId}/avaliacao
     * 
     * @param storeId ID da lanchonete
     * @param orderId ID do pedido
     * @return OrderRating? - null se não houver avaliação
     */
    suspend fun getOrderRating(storeId: String, orderId: String): OrderRating? {
        return try {
            Log.d("OrderRepository", "Buscando avaliação - StoreId: $storeId, OrderId: $orderId")
            
            if (storeId.isEmpty() || orderId.isEmpty()) {
                Log.w("OrderRepository", "StoreId ou OrderId vazios")
                return null
            }
            
            val snapshot = database.reference
                .child("pedidos_por_lanchonete")
                .child(storeId)
                .child(orderId)
                .child("avaliacao")
                .get()
                .await()
            
            if (snapshot.exists()) {
                val rating = snapshot.getValue(OrderRating::class.java)
                Log.d("OrderRepository", "✅ Avaliação encontrada: atendimento=${rating?.atendimento}, qualidade=${rating?.qualidade}, velocidade=${rating?.velocidade}")
                rating
            } else {
                Log.d("OrderRepository", "Nenhuma avaliação encontrada para este pedido")
                null
            }
        } catch (e: Exception) {
            Log.e("OrderRepository", "❌ Erro ao buscar avaliação: ${e.message}", e)
            null
        }
    }
}
