package com.br.linecut.data.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.content.Context

/**
 * Configuração central do Firebase
 * Inicializa e gerencia todas as instâncias dos serviços Firebase
 */
object FirebaseConfig {
    
    // Instâncias dos serviços Firebase
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val realtimeDatabase: FirebaseDatabase by lazy { FirebaseDatabase.getInstance() }
    
    /**
     * Inicializa o Firebase
     * Deve ser chamado no Application ou MainActivity
     */
    fun initialize(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            
            // Configurações opcionais
            configureRealtimeDatabase()
            configureAuth()
            
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Configurações específicas do Realtime Database
     */
    private fun configureRealtimeDatabase() {
        // Configurar cache offline
        realtimeDatabase.setPersistenceEnabled(true)
    }
    
    /**
     * Configurações específicas do Auth
     */
    private fun configureAuth() {
        // Configurações específicas de autenticação podem ser adicionadas aqui
        // Por exemplo: definir idioma, configurar provedores personalizados, etc.
    }
    
    /**
     * Verifica se o Firebase está inicializado
     */
    fun isInitialized(context: Context): Boolean {
        return FirebaseApp.getApps(context).isNotEmpty()
    }
}
