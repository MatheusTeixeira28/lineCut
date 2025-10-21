package com.br.linecut.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.br.linecut.data.models.AuthResult
import com.br.linecut.data.models.User
import com.br.linecut.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gerenciar o estado de autenticação
 */
class AuthViewModel : ViewModel() {
    
    private val authRepository = AuthRepository()
    
    // Estado do processo de cadastro
    private val _signUpState = MutableStateFlow<AuthResult?>(null)
    val signUpState: StateFlow<AuthResult?> = _signUpState.asStateFlow()
    
    // Estado do processo de login
    private val _loginState = MutableStateFlow<AuthResult?>(null)
    val loginState: StateFlow<AuthResult?> = _loginState.asStateFlow()
    
    // Estado de carregamento
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Estado do usuário atual
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    /**
     * Realiza o cadastro do usuário
     */
    fun signUpUser(
        fullName: String,
        cpf: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _signUpState.value = AuthResult.Loading
            
            val result = authRepository.signUpUser(
                fullName = fullName,
                cpf = cpf,
                phone = phone,
                email = email,
                password = password,
                confirmPassword = confirmPassword
            )
            
            _signUpState.value = result
            _isLoading.value = false
        }
    }
    
    /**
     * Limpa o estado de cadastro
     */
    fun clearSignUpState() {
        _signUpState.value = null
    }
    
    /**
     * Realiza o login do usuário
     */
    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loginState.value = AuthResult.Loading
            
            val result = authRepository.loginUser(email, password)
            
            _loginState.value = result
            _isLoading.value = false
            
            // Se o login foi bem-sucedido, carregar os dados do usuário
            if (result is AuthResult.Success) {
                loadCurrentUser()
            }
        }
    }
    
    /**
     * Limpa o estado de login
     */
    fun clearLoginState() {
        _loginState.value = null
    }
    
    /**
     * Envia email de redefinição de senha
     * Retorna true se o email foi enviado com sucesso, false caso contrário
     */
    suspend fun sendPasswordResetEmail(email: String): Result<String> {
        return try {
            _isLoading.value = true
            val result = authRepository.sendPasswordResetEmail(email)
            _isLoading.value = false
            
            result
        } catch (e: Exception) {
            _isLoading.value = false
            Result.failure(e)
        }
    }
    
    /**
     * Faz logout do usuário
     */
    fun logoutUser() {
        authRepository.logoutUser()
        clearSignUpState()
        clearLoginState()
        clearCurrentUser()
    }
    
    /**
     * Verifica se o usuário está logado
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
    
    /**
     * Carrega os dados do usuário atual
     */
    fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
            _currentUser.value = user
        }
    }
    
    /**
     * Limpa os dados do usuário atual
     */
    fun clearCurrentUser() {
        _currentUser.value = null
    }
    
    /**
     * Atualiza os dados do perfil do usuário
     */
    fun updateUserProfile(
        fullName: String,
        phone: String,
        email: String,
        profileImage: ByteArray? = null,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = authRepository.updateUserProfile(fullName, phone, email, profileImage)
                if (success) {
                    // Recarregar dados do usuário após atualização
                    loadCurrentUser()
                }
                onResult(success)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}
