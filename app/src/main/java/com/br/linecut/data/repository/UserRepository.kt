package com.br.linecut.data.repository

import android.util.Log
import com.br.linecut.ui.screens.Store
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository {
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("usuarios")
    private val companiesRef = database.getReference("empresas")
    
    /**
     * Busca as lojas favoritas do usuário
     */
    suspend fun getFavoriteStores(userId: String): List<Store> = suspendCoroutine { continuation ->
        Log.d("UserRepository", "==== BUSCANDO FAVORITOS ====")
        Log.d("UserRepository", "UserId: $userId")
        Log.d("UserRepository", "Path: usuarios/$userId/favoritos")
        
        usersRef.child(userId).child("favoritos").get()
            .addOnSuccessListener { snapshot ->
                Log.d("UserRepository", "Snapshot exists: ${snapshot.exists()}")
                Log.d("UserRepository", "Snapshot value: ${snapshot.value}")
                Log.d("UserRepository", "Snapshot children count: ${snapshot.childrenCount}")
                
                val favoriteIds = mutableListOf<String>()
                
                // Verificar se é uma string única ou um array
                when (val value = snapshot.value) {
                    is String -> {
                        // Favorito único armazenado como string
                        Log.d("UserRepository", "Favorito único (String): $value")
                        favoriteIds.add(value)
                    }
                    is List<*> -> {
                        // Array de favoritos
                        Log.d("UserRepository", "Array de favoritos")
                        value.filterIsInstance<String>().forEach { id ->
                            favoriteIds.add(id)
                            Log.d("UserRepository", "Adicionado ID do array: $id")
                        }
                    }
                    else -> {
                        // Tentar iterar pelos children (caso seja um mapa)
                        Log.d("UserRepository", "Iterando children")
                        for (child in snapshot.children) {
                            Log.d("UserRepository", "Child key: ${child.key}, value: ${child.value}")
                            val storeId = child.getValue(String::class.java)
                            if (storeId != null) {
                                favoriteIds.add(storeId)
                                Log.d("UserRepository", "Adicionado ID: $storeId")
                            }
                        }
                    }
                }
                
                Log.d("UserRepository", "IDs de favoritos encontrados: $favoriteIds")
                
                if (favoriteIds.isEmpty()) {
                    continuation.resume(emptyList())
                    return@addOnSuccessListener
                }
                
                // Buscar detalhes de cada loja favorita
                val stores = mutableListOf<Store>()
                var loadedCount = 0
                
                favoriteIds.forEach { storeId ->
                    Log.d("UserRepository", "Buscando loja: empresas/$storeId")
                    companiesRef.child(storeId).get()
                        .addOnSuccessListener { companySnapshot ->
                            Log.d("UserRepository", "Snapshot loja $storeId exists: ${companySnapshot.exists()}")
                            Log.d("UserRepository", "Snapshot loja value: ${companySnapshot.value}")
                            
                            if (companySnapshot.exists()) {
                                try {
                                    val store = convertFirebaseStoreToStore(storeId, companySnapshot)
                                    stores.add(store)
                                    Log.d("UserRepository", "✅ Loja carregada: ${store.name}")
                                } catch (e: Exception) {
                                    Log.e("UserRepository", "❌ Erro ao converter loja $storeId", e)
                                }
                            } else {
                                Log.w("UserRepository", "⚠️ Loja $storeId não encontrada")
                            }
                            
                            loadedCount++
                            Log.d("UserRepository", "Progress: $loadedCount/${favoriteIds.size}")
                            if (loadedCount == favoriteIds.size) {
                                Log.d("UserRepository", "✅ TODAS AS LOJAS CARREGADAS: ${stores.size}")
                                continuation.resume(stores)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("UserRepository", "❌ Erro ao buscar loja $storeId", e)
                            loadedCount++
                            if (loadedCount == favoriteIds.size) {
                                continuation.resume(stores)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao buscar favoritos", e)
                continuation.resumeWithException(e)
            }
    }
    
    /**
     * Adiciona ou remove uma loja dos favoritos
     */
    suspend fun toggleFavoriteStore(userId: String, storeId: String): Boolean = suspendCoroutine { continuation ->
        val favoritesRef = usersRef.child(userId).child("favoritos")
        
        favoritesRef.get()
            .addOnSuccessListener { snapshot ->
                val favoriteIds = mutableListOf<String>()
                
                // Extrair IDs atuais
                for (child in snapshot.children) {
                    val id = child.getValue(String::class.java)
                    if (id != null) {
                        favoriteIds.add(id)
                    }
                }
                
                // Verificar se já está nos favoritos
                if (favoriteIds.contains(storeId)) {
                    // Remover dos favoritos
                    favoriteIds.remove(storeId)
                    Log.d("UserRepository", "Removendo $storeId dos favoritos")
                } else {
                    // Adicionar aos favoritos
                    favoriteIds.add(storeId)
                    Log.d("UserRepository", "Adicionando $storeId aos favoritos")
                }
                
                // Atualizar no Firebase
                favoritesRef.setValue(favoriteIds)
                    .addOnSuccessListener {
                        Log.d("UserRepository", "Favoritos atualizados com sucesso")
                        continuation.resume(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("UserRepository", "Erro ao atualizar favoritos", e)
                        continuation.resume(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("UserRepository", "Erro ao buscar favoritos", e)
                continuation.resume(false)
            }
    }
    
    /**
     * Converte dados do Firebase para objeto Store
     */
    private fun convertFirebaseStoreToStore(storeId: String, snapshot: DataSnapshot): Store {
        Log.d("UserRepository", "==== CONVERTENDO LOJA $storeId ====")
        Log.d("UserRepository", "Snapshot completo: ${snapshot.value}")
        
        val nome = snapshot.child("nome_lanchonete").getValue(String::class.java) ?: "Loja"
        val categoria = snapshot.child("description").getValue(String::class.java) ?: "Categoria"
        val endereco = snapshot.child("endereco").getValue(String::class.java) ?: "Endereço"
        val imageUrl = snapshot.child("image_url").getValue(String::class.java) ?: ""
        val chavePix = snapshot.child("chave_pix").getValue(String::class.java) ?: ""
        
        Log.d("UserRepository", "Nome: $nome")
        Log.d("UserRepository", "Categoria: $categoria")
        Log.d("UserRepository", "Endereço: $endereco")
        Log.d("UserRepository", "ImageURL: '$imageUrl'")
        Log.d("UserRepository", "ChavePix: $chavePix")
        
        return Store(
            id = storeId,
            name = nome,
            category = categoria,
            location = endereco,
            distance = "0m", // Distância pode ser calculada depois
            imageUrl = imageUrl,
            isFavorite = true, // Como está na lista de favoritos, marcar como true
            chavePix = chavePix
        )
    }
}
