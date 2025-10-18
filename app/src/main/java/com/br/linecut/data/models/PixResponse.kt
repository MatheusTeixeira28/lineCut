package com.br.linecut.data.models

/**
 * Response completo da API PIX
 */
data class PixResponse(
    val qrCodeImage: String,
    val cobData: CobData
)
