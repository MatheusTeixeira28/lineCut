package com.br.linecut.data.repository

import com.br.linecut.data.models.ProductCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repositório para gerenciar dados de categorias de produtos
 */
class ProductCategoryRepository {

    /**
     * Obtém todas as categorias de produtos do Firebase
     * @return Flow com lista de categorias
     */
    fun getProductCategories(): Flow<List<ProductCategory>> = callbackFlow {
        val categoriesRef = FirebaseDatabase.getInstance().getReference("categorias_produto")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<ProductCategory>()

                for (childSnapshot in snapshot.children) {
                    try {
                        // No Firebase, as categorias são strings simples indexadas por números
                        // Ex: 0: "Salgados", 1: "Lanches", 2: "Bebidas"
                        val categoryName = childSnapshot.getValue(String::class.java)
                        val categoryIndex = childSnapshot.key
                        
                        if (categoryName != null && categoryIndex != null) {
                            // Normalizar o nome para usar como ID (minúsculas, sem acentos)
                            val categoryId = categoryName
                                .lowercase()
                                .replace("á", "a")
                                .replace("é", "e")
                                .replace("í", "i")
                                .replace("ó", "o")
                                .replace("ú", "u")
                                .replace("ã", "a")
                                .replace("õ", "o")
                                .replace("ç", "c")
                                .replace(" ", "_")
                            
                            // Criar objeto ProductCategory
                            val category = ProductCategory(
                                id = categoryId,
                                nome = categoryName,
                                descricao = ""
                            )
                            categories.add(category)
                        }
                    } catch (e: Exception) {
                        // Log detalhado do erro mas continua processando outras categorias
                        println("Erro ao processar categoria ${childSnapshot.key}: ${e.message}")
                        e.printStackTrace()
                    }
                }

                trySend(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        categoriesRef.addValueEventListener(listener)

        awaitClose {
            categoriesRef.removeEventListener(listener)
        }
    }
}
