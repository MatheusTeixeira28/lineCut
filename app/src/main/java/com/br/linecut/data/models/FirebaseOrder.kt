package com.br.linecut.data.models

import com.google.firebase.database.PropertyName

/**
 * Modelo de dados para pedidos do Firebase
 */
data class FirebaseOrder(
    val id: String = "",
    
    @get:PropertyName("cod_transacao_pagamento")
    @set:PropertyName("cod_transacao_pagamento")
    var codTransacaoPagamento: String = "",
    
    @get:PropertyName("datahora_criacao")
    @set:PropertyName("datahora_criacao")
    var datahoraCriacao: String = "",
    
    @get:PropertyName("datahora_pagamento")
    @set:PropertyName("datahora_pagamento")
    var datahoraPagamento: String = "",
    
    @get:PropertyName("id_lanchonete")
    @set:PropertyName("id_lanchonete")
    var idLanchonete: String = "",
    
    @get:PropertyName("id_usuario")
    @set:PropertyName("id_usuario")
    var userId: String = "", // ID do usuário que fez o pedido
    
    var items: Map<String, Any>? = null, // Lista de itens do pedido
    
    @get:PropertyName("metodo_pagamento")
    @set:PropertyName("metodo_pagamento")
    var metodoPagamento: String = "",
    
    @get:PropertyName("preco_total")
    @set:PropertyName("preco_total")
    var precoTotal: Double = 0.0,
    
    @get:PropertyName("qr_code_pedido")
    @set:PropertyName("qr_code_pedido")
    var qrCodePedido: String = "",
    
    @get:PropertyName("status_pagamento")
    @set:PropertyName("status_pagamento")
    var statusPagamento: String = "",
    
    @get:PropertyName("status_pedido")
    @set:PropertyName("status_pedido")
    var statusPedido: String = "" // "pendente", "em_preparo", "pronto", "entregue", "cancelado"
) {
    // Propriedades computadas para compatibilidade com o modelo Order da UI
    val orderNumber: String
        get() = qrCodePedido.takeIf { it.isNotEmpty() } ?: id.takeLast(8)
    
    val date: Long
        get() = try {
            // Converter string de data para timestamp
            // Formato esperado: "2025-10-18T22:48:02Z"
            val dateStr = datahoraCriacao.replace("Z", "")
            java.time.ZonedDateTime.parse(datahoraCriacao).toInstant().toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    
    val storeName: String
        get() = "" // Será preenchido buscando dados da lanchonete
    
    val storeCategory: String
        get() = "" // Será preenchido buscando dados da lanchonete
    
    val status: String
        get() = when (statusPedido.lowercase()) {
            "pendente", "em_preparo" -> "IN_PROGRESS"
            "pronto", "entregue" -> "COMPLETED"
            "cancelado" -> "CANCELLED"
            else -> "IN_PROGRESS"
        }
    
    val total: Double
        get() = precoTotal
    
    val rating: Int?
        get() = null // TODO: Implementar sistema de avaliação
    
    val canRate: Boolean
        get() = statusPedido.lowercase() in listOf("pronto", "entregue")
}
