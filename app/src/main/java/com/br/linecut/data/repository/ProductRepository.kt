package com.br.linecut.data.repository

import com.br.linecut.data.models.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Repositório para gerenciar dados de produtos
 */
class ProductRepository {

    /**
     * Obtém todos os produtos de um restaurante específico
     * @param restaurantId ID do restaurante
     * @return Flow com lista de produtos
     */
    fun getProductsByRestaurant(restaurantId: String): Flow<List<Product>> = callbackFlow {
        val productsRef = FirebaseDatabase.getInstance().getReference("restaurants")
            .child(restaurantId)
            .child("products")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = mutableListOf<Product>()

                for (childSnapshot in snapshot.children) {
                    try {
                        val product = childSnapshot.getValue(Product::class.java)
                        if (product != null) {
                            // Adicionar o ID do snapshot ao objeto product
                            val productWithId = product.copy(
                                id = childSnapshot.key ?: product.id,
                                restaurant_id = restaurantId
                            )
                            products.add(productWithId)
                        }
                    } catch (e: Exception) {
                        // Log detalhado do erro mas continua processando outros produtos
                        println("Erro ao deserializar produto ${childSnapshot.key}: ${e.message}")
                        e.printStackTrace()
                    }
                }

                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        productsRef.addValueEventListener(listener)

        awaitClose {
            productsRef.removeEventListener(listener)
        }
    }
}
