package com.br.linecut.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.models.ProductCategory
import com.br.linecut.data.repository.ProductCategoryRepository
import com.br.linecut.ui.screens.MenuCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar categorias de produtos
 */
class ProductCategoryViewModel(
    private val repository: ProductCategoryRepository = ProductCategoryRepository()
) : ViewModel() {

    private val _categories = MutableStateFlow<List<ProductCategory>>(emptyList())
    val categories: StateFlow<List<ProductCategory>> = _categories

    private val _menuCategories = MutableStateFlow<List<MenuCategory>>(emptyList())
    val menuCategories: StateFlow<List<MenuCategory>> = _menuCategories

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCategories()
    }

    /**
     * Carrega as categorias do Firebase
     */
    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.getProductCategories().collect { categoriesList ->
                    _categories.value = categoriesList
                    
                    // Converter ProductCategory para MenuCategory
                    // NÃO selecionar nenhuma por padrão para mostrar todos os produtos
                    val menuCategoriesList = categoriesList.map { category ->
                        MenuCategory(
                            id = category.id,
                            name = category.nome,
                            isSelected = false
                        )
                    }
                    
                    _menuCategories.value = menuCategoriesList
                    
                    // NÃO selecionar categoria por padrão - mostrar todos os produtos
                    _selectedCategoryId.value = null
                    
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar categorias: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Seleciona uma categoria
     */
    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = if (categoryId.isEmpty()) null else categoryId
        
        // Atualizar lista de MenuCategory com nova seleção
        _menuCategories.value = _menuCategories.value.map { category ->
            category.copy(isSelected = category.id == categoryId && categoryId.isNotEmpty())
        }
    }

    /**
     * Recarrega as categorias
     */
    fun refresh() {
        loadCategories()
    }
}
