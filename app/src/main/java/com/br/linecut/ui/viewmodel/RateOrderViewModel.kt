package com.br.linecut.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar a lógica da tela de avaliação de pedidos
 */
class RateOrderViewModel : ViewModel() {
    
    private val orderRepository = OrderRepository()
    
    // Estado da UI
    private val _uiState = MutableStateFlow<RatingUiState>(RatingUiState.Idle)
    val uiState: StateFlow<RatingUiState> = _uiState
    
    /**
     * Envia a avaliação do pedido para o Firebase
     */
    fun submitRating(
        storeId: String,
        orderId: String,
        qualityRating: Int,
        speedRating: Int,
        serviceRating: Int
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = RatingUiState.Loading
                
                Log.d("RateOrderViewModel", "Enviando avaliação...")
                Log.d("RateOrderViewModel", "StoreId: $storeId")
                Log.d("RateOrderViewModel", "OrderId: $orderId")
                Log.d("RateOrderViewModel", "Notas: quality=$qualityRating, speed=$speedRating, service=$serviceRating")
                
                val result = orderRepository.saveOrderRating(
                    storeId = storeId,
                    orderId = orderId,
                    qualityRating = qualityRating,
                    speedRating = speedRating,
                    serviceRating = serviceRating
                )
                
                result.onSuccess {
                    Log.d("RateOrderViewModel", "✅ Avaliação enviada com sucesso!")
                    _uiState.value = RatingUiState.Success
                }.onFailure { error ->
                    Log.e("RateOrderViewModel", "❌ Erro ao enviar avaliação: ${error.message}", error)
                    _uiState.value = RatingUiState.Error(
                        error.message ?: "Erro desconhecido ao enviar avaliação"
                    )
                }
                
            } catch (e: Exception) {
                Log.e("RateOrderViewModel", "❌ Exceção ao enviar avaliação: ${e.message}", e)
                _uiState.value = RatingUiState.Error(e.message ?: "Erro inesperado")
            }
        }
    }
    
    /**
     * Reseta o estado da UI
     */
    fun resetState() {
        _uiState.value = RatingUiState.Idle
    }
}

/**
 * Estados possíveis da UI de avaliação
 */
sealed class RatingUiState {
    object Idle : RatingUiState()
    object Loading : RatingUiState()
    object Success : RatingUiState()
    data class Error(val message: String) : RatingUiState()
}
