package com.br.linecut.data.models

import com.br.linecut.ui.screens.CartItem
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.UUID

/**
 * Modelo de dados para Pedido seguindo o padrão do message.txt
 * - Pedido principal em /pedidos/{id}
 * - Índice por usuário em /pedidos_por_usuario/{uid}/{pedido_id}
 * - Índice por lanchonete em /pedidos_por_lanchonete/{id_lanchonete}/{pedido_id}
 */
data class Pedido(
    val id_pedido: String = UUID.randomUUID().toString(),
    val id_usuario: String = "",
    val id_lanchonete: String = "",
    var status_pedido: String = "pendente", // pendente, em_preparo, pronto_para_retirada, finalizado, cancelado
    var preco_total: Double = 0.0,
    var datahora_criacao: String = "",
    var datahora_pagamento: String = "",
    var cod_transacao_pagamento: String = "",
    var qr_code_pedido: String = "",
    val items: List<PedidoItem> = emptyList(),
    var avaliacao: Avaliacao? = null
) {
    /**
     * Calcula o total do pedido baseado nos itens do carrinho
     */
    fun calcular_total(carrinho: List<CartItem>): Double {
        return carrinho.sumOf { item -> item.price * item.quantity }
    }

    /**
     * Cria e envia o pedido para o Firebase
     */
    suspend fun criar_pedido(): Result<String> {
        return try {
            val database = FirebaseDatabase.getInstance()
            // Gerar pushId do Firebase
            val pedidoRef = database.getReference("pedidos").push()
            val pedidoId = pedidoRef.key ?: UUID.randomUUID().toString()

            // Montar snapshot dos itens
            val itemsMap = items.associateBy({ UUID.randomUUID().toString() }, { item ->
                mapOf(
                    "id_produto" to item.id_produto,
                    "nome_produto" to item.nome_produto,
                    "preco_unitario" to item.preco_unitario,
                    "quantidade" to item.quantidade,
                    "subtotal" to item.subtotal
                )
            })

            // Montar avaliação aninhada
            val avaliacaoMap = avaliacao?.let {
                mapOf(
                    "atendimento" to it.atendimento,
                    "qualidade_produtos" to it.qualidade_produtos,
                    "velocidade_entrega" to it.velocidade_entrega,
                    "nota" to it.nota,
                    "data_avaliacao" to it.data_avaliacao
                )
            }

            // Montar pedido principal
            val pedidoMap = mapOf(
                "id_usuario" to id_usuario,
                "id_lanchonete" to id_lanchonete,
                "status_pedido" to status_pedido,
                "preco_total" to preco_total,
                "datahora_criacao" to datahora_criacao,
                "datahora_pagamento" to datahora_pagamento,
                "cod_transacao_pagamento" to cod_transacao_pagamento,
                "qr_code_pedido" to pedidoId,
                "items" to itemsMap,
                "avaliacao" to avaliacaoMap
            )

            // Salvar pedido principal
            pedidoRef.setValue(pedidoMap).await()

            // Índice por usuário
            val usuarioRef = database.getReference("pedidos_por_usuario").child(id_usuario).child(pedidoId)
            usuarioRef.setValue(true).await()

            // Índice por lanchonete
            val lanchoneteRef = database.getReference("pedidos_por_lanchonete").child(id_lanchonete).child(pedidoId)
            val lanchoneteMap = mapOf(
                "status" to status_pedido,
                "data_criacao" to datahora_criacao,
                "preco_total" to preco_total
            )
            lanchoneteRef.setValue(lanchoneteMap).await()

            Result.success(pedidoId)
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
            val pedidoRef = database.getReference("pedidos").child(id_pedido)

            // Montar snapshot dos itens
            val itemsMap = items.associateBy({ UUID.randomUUID().toString() }, { item ->
                mapOf(
                    "id_produto" to item.id_produto,
                    "nome_produto" to item.nome_produto,
                    "preco_unitario" to item.preco_unitario,
                    "quantidade" to item.quantidade,
                    "subtotal" to item.subtotal
                )
            })

            // Montar avaliação aninhada
            val avaliacaoMap = avaliacao?.let {
                mapOf(
                    "atendimento" to it.atendimento,
                    "qualidade_produtos" to it.qualidade_produtos,
                    "velocidade_entrega" to it.velocidade_entrega,
                    "nota" to it.nota,
                    "data_avaliacao" to it.data_avaliacao
                )
            }

            // Montar pedido principal
            val pedidoMap = mapOf(
                "id_usuario" to id_usuario,
                "id_lanchonete" to id_lanchonete,
                "status_pedido" to status_pedido,
                "preco_total" to preco_total,
                "datahora_criacao" to datahora_criacao,
                "datahora_pagamento" to datahora_pagamento,
                "cod_transacao_pagamento" to cod_transacao_pagamento,
                "qr_code_pedido" to id_pedido,
                "items" to itemsMap,
                "avaliacao" to avaliacaoMap
            )

            pedidoRef.updateChildren(pedidoMap).await()

            // Índice por usuário
            val usuarioRef = database.getReference("pedidos_por_usuario").child(id_usuario).child(id_pedido)
            usuarioRef.setValue(true).await()

            // Índice por lanchonete
            val lanchoneteRef = database.getReference("pedidos_por_lanchonete").child(id_lanchonete).child(id_pedido)
            val lanchoneteMap = mapOf(
                "status" to status_pedido,
                "data_criacao" to datahora_criacao,
                "preco_total" to preco_total
            )
            lanchoneteRef.setValue(lanchoneteMap).await()

            Result.success(id_pedido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    companion object {
        /**
         * Cria um novo pedido a partir de um carrinho de compras
         */
        fun fromCarrinho(
            carrinho: List<CartItem>,
            idUsuario: String,
            idLanchonete: String
        ): Pedido {
            val items = carrinho.map {
                PedidoItem(
                    id_produto = it.id,
                    nome_produto = it.name,
                    preco_unitario = it.price,
                    quantidade = it.quantity,
                    subtotal = it.price * it.quantity
                )
            }
            val total = items.sumOf { it.subtotal }
            val now = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US).apply {
                timeZone = java.util.TimeZone.getTimeZone("UTC")
            }.format(java.util.Date())
            return Pedido(
                id_usuario = idUsuario,
                id_lanchonete = idLanchonete,
                status_pedido = "pendente",
                preco_total = total,
                datahora_criacao = now,
                items = items
            )
        }
    }
}

/**
 * Item do pedido com snapshot dos dados do produto
 */
data class PedidoItem(
    val id_produto: String,
    val nome_produto: String,
    val preco_unitario: Double,
    val quantidade: Int,
    val subtotal: Double
)

/**
 * Avaliação aninhada do pedido
 */
data class Avaliacao(
    val atendimento: Int = 0,
    val qualidade_produtos: Int = 0,
    val velocidade_entrega: Int = 0,
    val nota: String = "",
    val data_avaliacao: String = ""
)
