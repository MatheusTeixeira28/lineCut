package com.br.linecut.data.models

/**
 * Modelo de dados para produtos das lanchonetes do Firebase
 */
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: String = "",
    val image_url: String = "",
    val restaurant_id: String = ""
)
