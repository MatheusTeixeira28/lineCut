package com.br.linecut.data.models

import com.br.linecut.ui.screens.CartItem
import com.br.linecut.ui.screens.OrderStatus
import com.google.firebase.Timestamp
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Enum para status de pagamento do pedido
 */
enum class PaymentStatus {
    WAITING_PAYMENT,
    PAYED
}

/**
 * Modelo de dados para Pedido
 */
data class Pedido(
    val id_pedido: String = UUID.randomUUID().toString(),
    var status_pedido: PaymentStatus = PaymentStatus.WAITING_PAYMENT,
    var order_status: OrderStatus = OrderStatus.IN_PROGRESS,
    var qr_code: String = "",
    var cod_transacao: String = "",
    var datahora_pagamento: Timestamp? = null,
    var avaliacao_pedido: Int? = null,
    var avaliacao_velocidade: Int? = null,
    val itens: List<CartItem> = emptyList(),
    val total: Double = 0.0
) {
    /**
     * Calcula o total do pedido baseado nos itens do carrinho
     */
    fun calcular_total(carrinho: List<CartItem>): Double {
        return carrinho.sumOf { item ->
            item.price * item.quantity
        }
    }

    /**
     * Cria e envia o pedido para o Firebase
     */
    suspend fun criar_pedido(): Result<String> {
        return try {
            val database = FirebaseDatabase.getInstance()
            val pedidoRef = database.getReference("pedido").child(id_pedido)
            
            // Converter para Map para salvar no Firebase
            val pedidoMap = mapOf(
                "id_pedido" to id_pedido,
                "status_pedido" to status_pedido.name,
                "order_status" to order_status.name,
                "qr_code" to qr_code,
                "cod_transacao" to cod_transacao,
                "datahora_pagamento" to datahora_pagamento,
                "avaliacao_pedido" to avaliacao_pedido,
                "avaliacao_velocidade" to avaliacao_velocidade,
                "itens" to itens.map { item ->
                    mapOf(
                        "id" to item.id,
                        "name" to item.name,
                        "price" to item.price,
                        "imageUrl" to item.imageUrl,
                        "quantity" to item.quantity
                    )
                },
                "total" to total
            )
            
            pedidoRef.setValue(pedidoMap).await()
            Result.success(id_pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Atualiza o pedido existente no Firebase
     */
    suspend fun atualizar_pedido(): Result<String> {
        return try {
            val database = FirebaseDatabase.getInstance()
            val pedidoRef = database.getReference("pedido").child(id_pedido)
            
            // Converter para Map para atualizar no Firebase
            val pedidoMap = mapOf(
                "status_pedido" to status_pedido.name,
                "order_status" to order_status.name,
                "qr_code" to qr_code,
                "cod_transacao" to cod_transacao,
                "datahora_pagamento" to datahora_pagamento,
                "avaliacao_pedido" to avaliacao_pedido,
                "avaliacao_velocidade" to avaliacao_velocidade,
                "itens" to itens.map { item ->
                    mapOf(
                        "id" to item.id,
                        "name" to item.name,
                        "price" to item.price,
                        "imageUrl" to item.imageUrl,
                        "quantity" to item.quantity
                    )
                },
                "total" to total
            )
            
            pedidoRef.updateChildren(pedidoMap).await()
            Result.success(id_pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        /**
         * Cria um novo pedido a partir de um carrinho de compras
         */
        fun fromCarrinho(carrinho: List<CartItem>): Pedido {
            val pedido = Pedido(itens = carrinho)
            val total = pedido.calcular_total(carrinho)
            return pedido.copy(total = total)
        }
    }
}
