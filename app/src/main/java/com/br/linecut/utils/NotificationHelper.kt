package com.br.linecut.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.br.linecut.MainActivity
import com.br.linecut.R

/**
 * Helper para gerenciar notificações do sistema Android
 */
object NotificationHelper {
    
    private const val CHANNEL_ID_ORDERS = "order_updates"
    private const val CHANNEL_NAME = "Atualizações de Pedidos"
    private const val CHANNEL_DESCRIPTION = "Notificações sobre o status dos seus pedidos"
    
    /**
     * Cria o canal de notificações (necessário para Android 8+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID_ORDERS,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    /**
     * Mostra uma notificação do sistema Android
     */
    fun showNotification(
        context: Context,
        notificationId: Int,
        title: String,
        message: String,
        priority: Int = NotificationCompat.PRIORITY_HIGH
    ) {
        // Criar intent para abrir o app quando clicar na notificação
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        // Construir notificação
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ORDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use seu ícone aqui
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(priority)
            .setAutoCancel(true) // Remove ao clicar
            .setContentIntent(pendingIntent)
            .build()
        
        // Mostrar notificação
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }
    
    /**
     * Gera um ID único para cada notificação baseado no timestamp
     */
    fun generateNotificationId(): Int {
        return System.currentTimeMillis().toInt()
    }
}
