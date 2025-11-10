package com.br.linecut.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.repository.UserRepository
import com.br.linecut.ui.screens.Store
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()
    
    private val _favoriteStores = MutableStateFlow<List<Store>>(emptyList())
    val favoriteStores: StateFlow<List<Store>> = _favoriteStores.asStateFlow()
    
    private val _isLoadingFavorites = MutableStateFlow(false)
    val isLoadingFavorites: StateFlow<Boolean> = _isLoadingFavorites.asStateFlow()
    
    private val _favoritesError = MutableStateFlow<String?>(null)
    val favoritesError: StateFlow<String?> = _favoritesError.asStateFlow()
    
    /**
     * Carrega as lojas favoritas do usuário logado
     */
    fun loadFavoriteStores() {
        viewModelScope.launch {
            try {
                _isLoadingFavorites.value = true
                _favoritesError.value = null
                
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Log.e("UserViewModel", "Usuário não autenticado")
                    _favoritesError.value = "Usuário não autenticado"
                    _favoriteStores.value = emptyList()
                    return@launch
                }
                
                Log.d("UserViewModel", "Carregando favoritos para userId: $userId")
                
                val stores = userRepository.getFavoriteStores(userId)
                _favoriteStores.value = stores
                
                Log.d("UserViewModel", "Favoritos carregados: ${stores.size} lojas")
                stores.forEach { store ->
                    Log.d("UserViewModel", "  - ${store.name} (${store.id})")
                }
                
            } catch (e: Exception) {
                Log.e("UserViewModel", "Erro ao carregar favoritos", e)
                _favoritesError.value = e.message ?: "Erro desconhecido"
                _favoriteStores.value = emptyList()
            } finally {
                _isLoadingFavorites.value = false
            }
        }
    }
    
    /**
     * Adiciona ou remove uma loja dos favoritos
     */
    fun toggleFavorite(storeId: String) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: return@launch
                
                Log.d("UserViewModel", "Toggle favorito - userId: $userId, storeId: $storeId")
                
                val success = userRepository.toggleFavoriteStore(userId, storeId)
                
                if (success) {
                    Log.d("UserViewModel", "Favorito atualizado com sucesso")
                    // Recarregar lista de favoritos
                    loadFavoriteStores()
                } else {
                    Log.e("UserViewModel", "Falha ao atualizar favorito")
                }
                
            } catch (e: Exception) {
                Log.e("UserViewModel", "Erro ao atualizar favorito", e)
            }
        }
    }
}
