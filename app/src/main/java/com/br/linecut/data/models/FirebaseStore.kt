package com.br.linecut.data.models

import com.google.firebase.database.PropertyName

/**
 * Modelo de dados para empresas (lanchonetes) do Firebase
 */
data class FirebaseStore(
    val id: String = "",
    
    @get:PropertyName("nome_lanchonete")
    @set:PropertyName("nome_lanchonete")
    var nomeLanchonete: String = "",
    
    val description: String = "",
    
    @get:PropertyName("image_url")
    @set:PropertyName("image_url")
    var imageUrl: String = ""
)
