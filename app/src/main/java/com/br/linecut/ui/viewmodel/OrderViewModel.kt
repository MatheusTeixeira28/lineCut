package com.br.linecut.ui.viewmodel

import android.util.Log
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
        Log.d("OrderViewModel", "==== CARREGANDO PEDIDOS DO USUÁRIO ====")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.getUserOrders().collect { ordersList ->
                    Log.d("OrderViewModel", "Pedidos recebidos: ${ordersList.size}")
                    ordersList.forEachIndexed { index, order ->
                        Log.d("OrderViewModel", "Pedido $index:")
                        Log.d("OrderViewModel", "  - ID: ${order.id}")
                        Log.d("OrderViewModel", "  - Número: ${order.orderNumber}")
                        Log.d("OrderViewModel", "  - Loja: ${order.storeName}")
                        Log.d("OrderViewModel", "  - Status: ${order.status}")
                        Log.d("OrderViewModel", "  - Rating: ${order.rating}")
                        Log.d("OrderViewModel", "  - Can Rate: ${order.canRate}")
                    }
                    _orders.value = ordersList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("OrderViewModel", "Erro ao carregar pedidos", e)
                _error.value = "Erro ao carregar pedidos: ${e.message}"
                _isLoading.value = false
            }
        }
    }
}
