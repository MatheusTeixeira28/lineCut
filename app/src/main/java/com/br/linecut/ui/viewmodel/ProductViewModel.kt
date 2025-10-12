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
                            category = product.category,
                            imageRes = android.R.drawable.ic_menu_gallery, // Placeholder
                            imageUrl = product.image_url,
                            quantity = 0
                        )
                    }
                    
                    _menuItems.value = menuItemsList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar produtos: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Recarrega os produtos
     */
    fun refresh(restaurantId: String) {
        loadProductsByRestaurant(restaurantId)
    }
}
