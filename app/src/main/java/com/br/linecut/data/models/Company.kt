package com.br.linecut.data.models

/**
 * Modelo de dados para empresas/lanchonetes do Firebase
 */
data class Company(
    val id: String = "",
    val nome_lanchonete: String = "",
    val description: String = "",
    val polo: String = "",
    val image_url: String = "",
    val cnpj: String = "",
    val chave_pix: String = ""
)