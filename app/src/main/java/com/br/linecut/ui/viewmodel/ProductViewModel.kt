package com.br.linecut.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.models.Product
import com.br.linecut.data.repository.ProductRepository
import com.br.linecut.ui.screens.MenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar dados de produtos
 */
class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()
    
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    
    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()
    
    private val _filteredMenuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val filteredMenuItems: StateFlow<List<MenuItem>> = _filteredMenuItems.asStateFlow()
    
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Carrega os produtos de um restaurante específico
     */
    fun loadProductsByRestaurant(restaurantId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.getProductsByRestaurant(restaurantId).collect { productsList ->
                    _products.value = productsList
                    
                    // Converter produtos para MenuItem
                    val menuItemsList = productsList.map { product ->
                        MenuItem(
                            id = product.id,
                            name = product.name,
                            description = product.description,
                            price = product.price,
                            category = normalizeCategory(product.category), // Normalizar categoria
                            imageRes = android.R.drawable.ic_menu_gallery, // Placeholder
                            imageUrl = product.image_url,
                            quantity = 0
                        )
                    }
                    
                    _menuItems.value = menuItemsList
                    _isLoading.value = false
                    
                    // Aplicar filtro se houver categoria selecionada
                    filterByCategory(_selectedCategory.value)
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar produtos: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Normaliza o nome da categoria (remove acentos, converte para minúsculas, etc)
     */
    private fun normalizeCategory(category: String): String {
        return category
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
    }
    
    /**
     * Filtra os produtos por categoria
     */
    fun filterByCategory(categoryId: String?) {
        _selectedCategory.value = categoryId
        
        _filteredMenuItems.value = if (categoryId.isNullOrEmpty()) {
            // Se não houver categoria selecionada, mostrar todos os produtos
            _menuItems.value
        } else {
            // Filtrar produtos pela categoria selecionada
            _menuItems.value.filter { it.category == categoryId }
        }
    }
    
    /**
     * Recarrega os produtos
     */
    fun refresh(restaurantId: String) {
        loadProductsByRestaurant(restaurantId)
    }
}
