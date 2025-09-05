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
        }
    }
    
    /**
     * Limpa o estado de login
     */
    fun clearLoginState() {
        _loginState.value = null
    }
    
    /**
     * Faz logout do usuário
     */
    fun logoutUser() {
        authRepository.logoutUser()
        clearSignUpState()
        clearLoginState()
    }
    
    /**
     * Verifica se o usuário está logado
     */
    fun isUserLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
