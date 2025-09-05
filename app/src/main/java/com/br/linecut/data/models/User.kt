package com.br.linecut.data.models

/**
 * Modelo de dados do usuário para Firebase
 */
data class User(
    val uid: String = "",
    val fullName: String = "",
    val cpf: String = "",
    val phone: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Resultado das operações de autenticação
 */
sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

/**
 * Estados de validação dos formulários
 */
data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
