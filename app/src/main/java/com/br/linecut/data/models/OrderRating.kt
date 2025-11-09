package com.br.linecut.data.models

import com.google.firebase.database.PropertyName

/**
 * Modelo de dados para avaliação de pedidos
 * Estrutura conforme Firebase: pedidos_por_lanchonete/{storeId}/{orderId}/avaliacao
 */
data class OrderRating(
    @get:PropertyName("atendimento")
    @set:PropertyName("atendimento")
    var atendimento: Int = 0, // Nota de 1 a 5 para atendimento
    
    @get:PropertyName("qualidade")
    @set:PropertyName("qualidade")
    var qualidade: Int = 0, // Nota de 1 a 5 para qualidade
    
    @get:PropertyName("velocidade")
    @set:PropertyName("velocidade")
    var velocidade: Int = 0, // Nota de 1 a 5 para velocidade
    
    @get:PropertyName("data_avaliacao")
    @set:PropertyName("data_avaliacao")
    var dataAvaliacao: String = "" // Timestamp no formato ISO 8601 (ex: "2025-11-09T00:22:12Z")
)
