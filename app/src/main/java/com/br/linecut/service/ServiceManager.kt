package com.br.linecut.service

import android.app.ActivityManager
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

/**
 * Gerenciador para controlar o OrderStatusService
 * 
 * Responsável por:
 * - Iniciar o service quando o usuário faz login
 * - Parar o service quando o usuário faz logout
 * - Verificar se o service está rodando
 */
object ServiceManager {
    
    private const val TAG = "ServiceManager"
    
    /**
     * Inicia o OrderStatusService se o usuário estiver autenticado
     */
    fun startOrderMonitoringIfLoggedIn(context: Context) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            Log.d(TAG, "Usuário autenticado, iniciando OrderStatusService")
            OrderStatusService.start(context.applicationContext)
        } else {
            Log.d(TAG, "Usuário não autenticado, não iniciando service")
        }
    }
    
    /**
     * Para o OrderStatusService
     */
    fun stopOrderMonitoring(context: Context) {
        Log.d(TAG, "Parando OrderStatusService")
        OrderStatusService.stop(context.applicationContext)
    }
    
    /**
     * Verifica se o OrderStatusService está rodando
     */
    fun isServiceRunning(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        
        @Suppress("DEPRECATION")
        val runningServices = activityManager.getRunningServices(Integer.MAX_VALUE)
        
        return runningServices.any { serviceInfo ->
            serviceInfo.service.className == OrderStatusService::class.java.name
        }
    }
    
    /**
     * Reinicia o service se necessário (útil após atualizações)
     */
    fun restartServiceIfNeeded(context: Context) {
        if (isServiceRunning(context)) {
            Log.d(TAG, "Service já está rodando, reiniciando...")
            stopOrderMonitoring(context)
            Thread.sleep(500) // Pequeno delay para garantir que parou
        }
        startOrderMonitoringIfLoggedIn(context)
    }
}
