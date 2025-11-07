package com.br.linecut.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.repository.OrderRepository
import com.br.linecut.ui.screens.Order
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar os pedidos do usuário
 */
class OrderViewModel : ViewModel() {
    private val repository = OrderRepository()
    
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    /**
     * Carrega os pedidos do usuário autenticado
     */
    fun loadUserOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.getUserOrders().collect { ordersList ->
                    _orders.value = ordersList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = "Erro ao carregar pedidos: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
