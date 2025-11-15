package com.br.linecut.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.repository.NotificationRepository
import com.br.linecut.ui.screens.profile.Notification
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private val notificationRepository = NotificationRepository()
    private val auth = FirebaseAuth.getInstance()
    
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    /**
     * Carrega notificações do usuário logado
     */
    private fun loadNotifications() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Log.e("NotificationViewModel", "Usuário não autenticado")
                    _error.value = "Usuário não autenticado"
                    _notifications.value = emptyList()
                    return@launch
                }
                
                Log.d("NotificationViewModel", "Carregando notificações para userId: $userId")
                
                // Desativa loading antes do collect (que é infinito)
                _isLoading.value = false
                
                notificationRepository.getUserNotifications(userId).collect { notificationsList ->
                    _notifications.value = notificationsList
                    Log.d("NotificationViewModel", "Notificações atualizadas: ${notificationsList.size}")
                }
                
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Erro ao carregar notificações", e)
                _error.value = e.message ?: "Erro desconhecido"
                _notifications.value = emptyList()
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Recarrega as notificações
     */
    fun refresh() {
        loadNotifications()
    }
    
    /**
     * Deleta todas as notificações do usuário
     */
    fun deleteAllNotifications() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId == null) {
                    Log.e("NotificationViewModel", "Usuário não autenticado")
                    _error.value = "Usuário não autenticado"
                    return@launch
                }
                
                Log.d("NotificationViewModel", "Deletando todas as notificações para userId: $userId")
                
                val success = notificationRepository.deleteAllNotifications(userId)
                if (success) {
                    Log.d("NotificationViewModel", "✅ Notificações deletadas com sucesso")
                    _notifications.value = emptyList()
                } else {
                    Log.e("NotificationViewModel", "❌ Falha ao deletar notificações")
                    _error.value = "Erro ao deletar notificações"
                }
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Erro ao deletar notificações", e)
                _error.value = e.message ?: "Erro desconhecido"
            }
        }
    }
}

