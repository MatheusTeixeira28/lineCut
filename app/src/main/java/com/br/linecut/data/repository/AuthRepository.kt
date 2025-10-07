package com.br.linecut.data.repository

import com.br.linecut.data.firebase.FirebaseConfig
import com.br.linecut.data.models.AuthResult
import com.br.linecut.data.models.User
import com.br.linecut.data.models.ValidationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import android.util.Patterns
import com.br.linecut.ui.utils.ImageCache
import com.br.linecut.ui.utils.ValidationUtils

/**
 * Repositório para gerenciar operações de autenticação e usuários no Firebase
 */
class AuthRepository {
    private val auth: FirebaseAuth = FirebaseConfig.auth
    private val realtimeDatabase: FirebaseDatabase = FirebaseConfig.realtimeDatabase
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    
    /**
     * Cadastra um novo usuário no Firebase
     */
    suspend fun signUpUser(
        fullName: String,
        cpf: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String
    ): AuthResult {
        return try {
            // Validar dados antes de enviar
            val validation = validateSignUpData(fullName, cpf, phone, email, password, confirmPassword)
            if (!validation.isValid) {
                return AuthResult.Error(validation.errorMessage ?: "Dados inválidos")
            }
            
            // Verificar se o email já existe no banco de dados
            val emailExists = checkEmailExists(email)
            if (emailExists) {
                return AuthResult.Error("Este email já está cadastrado")
            }
            
            // Criar conta no Firebase Auth
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Criar objeto User
                val user = User(
                    uid = firebaseUser.uid,
                    fullName = fullName,
                    cpf = cpf,
                    phone = phone,
                    email = email,
                    createdAt = System.currentTimeMillis()
                )
                
                // Salvar dados do usuário no Realtime Database
                // Estrutura: usuarios/{uid} (paralelo a empresas)
                realtimeDatabase.reference
                    .child("usuarios")
                    .child(firebaseUser.uid)
                    .setValue(user)
                    .await()
                
                AuthResult.Success(user)
            } else {
                AuthResult.Error("Erro ao criar conta no Firebase")
            }
        } catch (e: Exception) {
            AuthResult.Error(getErrorMessage(e))
        }
    }
    
    /**
     * Faz login de um usuário existente
     */
    suspend fun loginUser(email: String, password: String): AuthResult {
        return try {
            // Validar dados básicos
            if (email.isBlank() || password.isBlank()) {
                return AuthResult.Error("Email e senha são obrigatórios")
            }
            
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return AuthResult.Error("Email inválido")
            }
            
            // Fazer login no Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                // Buscar dados do usuário no Realtime Database
                val userRef = realtimeDatabase.getReference("usuarios").child(firebaseUser.uid)
                val userSnapshot = userRef.get().await()
                
                if (userSnapshot.exists()) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        AuthResult.Success(user)
                    } else {
                        AuthResult.Error("Erro ao carregar dados do usuário")
                    }
                } else {
                    AuthResult.Error("Usuário não encontrado no banco de dados")
                }
            } else {
                AuthResult.Error("Erro ao fazer login")
            }
        } catch (e: Exception) {
            AuthResult.Error(getLoginErrorMessage(e))
        }
    }
    
    /**
     * Faz logout do usuário atual
     */
    fun logoutUser() {
        auth.signOut()
        // Limpar o cache de imagens ao fazer logout
        ImageCache.clear()
    }
    
    /**
     * Verifica se o email já existe no banco de dados
     */
    private suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val query = realtimeDatabase.getReference("usuarios")
                .orderByChild("email")
                .equalTo(email)
            
            val snapshot = query.get().await()
            snapshot.exists()
        } catch (e: Exception) {
            false // Em caso de erro, permite continuar
        }
    }
    
    /**
     * Valida os dados do formulário de cadastro
     */
    private fun validateSignUpData(
        fullName: String,
        cpf: String,
        phone: String,
        email: String,
        password: String,
        confirmPassword: String
    ): ValidationResult {
        // Validar nome completo
        if (fullName.isBlank() || fullName.length < 2) {
            return ValidationResult(false, "Nome deve ter pelo menos 2 caracteres")
        }
        
        // Validar CPF usando ValidationUtils
        if (cpf.isBlank() || !ValidationUtils.isValidCpf(cpf)) {
            return ValidationResult(false, "CPF inválido")
        }
        
        // Validar telefone usando ValidationUtils
        if (phone.isBlank() || !ValidationUtils.isValidPhone(phone)) {
            return ValidationResult(false, "Telefone inválido")
        }
        
        // Validar email usando ValidationUtils
        if (!ValidationUtils.isValidEmail(email)) {
            return ValidationResult(false, "Email inválido")
        }
        
        // Validar senha
        if (password.isBlank() || password.length < 6) {
            return ValidationResult(false, "Senha deve ter pelo menos 6 caracteres")
        }
        
        // Validar confirmação de senha
        if (password != confirmPassword) {
            return ValidationResult(false, "Senhas não coincidem")
        }
        
        return ValidationResult(true)
    }
    
    /**
     * Converte exceções de login Firebase em mensagens de erro amigáveis
     */
    private fun getLoginErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("user-not-found") == true -> 
                "Usuário não encontrado"
            exception.message?.contains("wrong-password") == true -> 
                "Senha incorreta"
            exception.message?.contains("invalid-email") == true -> 
                "Email inválido"
            exception.message?.contains("user-disabled") == true -> 
                "Conta desabilitada"
            exception.message?.contains("too-many-requests") == true -> 
                "Muitas tentativas. Tente novamente mais tarde"
            exception.message?.contains("network") == true -> 
                "Erro de conexão. Verifique sua internet"
            exception.message?.contains("supplied auth credential is incorrect") == true -> 
            "Email ou senha incorretos"
            else -> exception.message ?: "Erro ao fazer login, tente novamente"
        }
    }
    
    /**
     * Converte exceções Firebase em mensagens de erro amigáveis
     */
    private fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("email address is already in use") == true -> 
                "Este email já está sendo usado por outra conta"
            exception.message?.contains("weak-password") == true -> 
                "A senha é muito fraca"
            exception.message?.contains("invalid-email") == true -> 
                "Email inválido"
            exception.message?.contains("network") == true -> 
                "Erro de conexão. Verifique sua internet"
            else -> exception.message ?: "Erro desconhecido ao criar conta"
        }
    }
    
    /**
     * Verifica se o usuário está logado
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Obtém o usuário atual do Firebase Auth
     */
    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? {
        return auth.currentUser
    }
    
    /**
     * Obtém os dados completos do usuário atual do Realtime Database
     */
    suspend fun getCurrentUser(): User? {
        return try {
            val currentFirebaseUser = auth.currentUser
            if (currentFirebaseUser != null) {
                val uid = currentFirebaseUser.uid
                val snapshot = realtimeDatabase.reference
                    .child("usuarios")
                    .child(uid)
                    .get()
                    .await()
                
                if (snapshot.exists()) {
                    val userData = snapshot.getValue(User::class.java)
                    userData
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Atualiza os dados do perfil do usuário no Firebase
     */
    suspend fun updateUserProfile(
        fullName: String,
        phone: String,
        email: String,
        profileImage: ByteArray? = null
    ): Boolean {
        return try {
            println("DEBUG: updateUserProfile called with name=$fullName, phone=$phone")
            val currentFirebaseUser = auth.currentUser
            if (currentFirebaseUser != null) {
                val uid = currentFirebaseUser.uid
                println("DEBUG: User UID = $uid")
                
                // Não vamos mais atualizar email no Firebase Auth para evitar erros
                
                // Upload da imagem se fornecida
                var profileImageUrl: String? = null
                if (profileImage != null) {
                    // Remover imagem antiga do cache se existir
                    val currentImageUrl = getCurrentUser()?.profileImageUrl
                    currentImageUrl?.let { oldUrl ->
                        ImageCache.remove(oldUrl)
                    }
                    
                    val imageRef = storage.reference.child("profile_images/$uid.jpg")
                    val uploadTask = imageRef.putBytes(profileImage).await()
                    profileImageUrl = imageRef.downloadUrl.await().toString()
                }
                
                // Preparar dados para atualização no Realtime Database
                val updates = mutableMapOf<String, Any>(
                    "fullName" to fullName,
                    "phone" to phone,
                    "email" to email
                )
                
                // Adicionar URL da imagem se foi feito upload
                if (profileImageUrl != null) {
                    updates["profileImageUrl"] = profileImageUrl
                }
                
                println("DEBUG: Updates to send = $updates")
                
                // Atualizar dados no Realtime Database
                val dbRef = realtimeDatabase.reference
                    .child("usuarios")
                    .child(uid)
                
                println("DEBUG: Database path = usuarios/$uid")
                
                dbRef.updateChildren(updates).await()
                
                println("DEBUG: Update completed successfully")
                true
            } else {
                println("DEBUG: No current user found")
                false
            }
        } catch (e: Exception) {
            println("DEBUG: Error updating profile: ${e.message}")
            e.printStackTrace()
            false
        }
    }
}
