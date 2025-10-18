package com.br.linecut.data.models

/**
 * Dados da cobrança PIX
 */
data class CobData(
    val calendario: Calendario,
    val txid: String,
    val revisao: Int,
    val loc: Loc,
    val location: String,
    val status: String,
    val valor: Valor,
    val chave: String,
    val solicitacaoPagador: String,
    val pixCopiaECola: String
)
