package com.br.linecut.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.models.Company
import com.br.linecut.data.repository.AuthRepository
import com.br.linecut.data.repository.CompanyRepository
import com.br.linecut.ui.screens.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar dados de empresas/lanchonetes
 */
class CompanyViewModel : ViewModel() {
    private val repository = CompanyRepository()
    private val authRepository = AuthRepository()
    
    private val _companies = MutableStateFlow<List<Company>>(emptyList())
    val companies: StateFlow<List<Company>> = _companies.asStateFlow()
    
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()
    
    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())
    val favoriteIds: StateFlow<List<String>> = _favoriteIds.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadCompanies()
        loadFavorites()
    }
    
    /**
     * Carrega os IDs dos favoritos do usuário
     */
    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = authRepository.getFavorites()
                _favoriteIds.value = favorites
                
                // Atualizar stores com status de favorito
                updateStoresWithFavorites()
            } catch (e: Exception) {
                // Silenciar erro de favoritos - não é crítico
            }
        }
    }
    
    /**
     * Atualiza a lista de stores com o status de favorito
     */
    private fun updateStoresWithFavorites() {
        _stores.value = _stores.value.map { store ->
            store.copy(isFavorite = _favoriteIds.value.contains(store.id))
        }
    }
    
    /**
     * Carrega todas as empresas do Firebase
     */
    fun loadCompanies() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.getCompanies().collect { companiesList ->
                    _companies.value = companiesList
                    
                    // Converter empresas para Store (sem pré-carregar imagens)
                    val storesList = companiesList.map { company ->
                        Store(
                            id = company.id,
                            name = company.nome_lanchonete,
                            category = company.description,
                            location = company.polo,
                            distance = "", // Deixar vazio por enquanto
                            imageRes = android.R.drawable.ic_menu_gallery, // Placeholder
                            imageUrl = company.image_url,
                            isFavorite = _favoriteIds.value.contains(company.id),
                            chavePix = company.chave_pix
                        )
                    }
                    
                    _stores.value = storesList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar lojas: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Toggle do status de favorito de uma loja
     */
    fun toggleFavorite(storeId: String) {
        viewModelScope.launch {
            try {
                val isFavorite = _favoriteIds.value.contains(storeId)
                
                if (isFavorite) {
                    // Remover dos favoritos
                    val success = authRepository.removeFavorite(storeId)
                    if (success) {
                        _favoriteIds.value = _favoriteIds.value.filter { it != storeId }
                        updateStoresWithFavorites()
                    }
                } else {
                    // Adicionar aos favoritos
                    val success = authRepository.addFavorite(storeId)
                    if (success) {
                        _favoriteIds.value = _favoriteIds.value + storeId
                        updateStoresWithFavorites()
                    }
                }
            } catch (e: Exception) {
                // Silenciar erro - não é crítico
            }
        }
    }
    
    /**
     * Recarrega as empresas
     */
    fun refresh() {
        loadCompanies()
        loadFavorites()
    }
}