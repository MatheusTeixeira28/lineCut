package com.br.linecut.data.repository

import android.util.Log
import com.br.linecut.ui.screens.profile.Notification
import com.br.linecut.ui.screens.profile.NotificationType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class NotificationRepository {
    private val database = FirebaseDatabase.getInstance()
    private val notificationsRef = database.getReference("notificacoes")
    
    /**
     * Cria uma notificação de pedido realizado
     */
    suspend fun createOrderPlacedNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PLACED",
                "title" to "Pedido realizado",
                "message" to "Recebemos seu pedido na $storeName. Em breve ele será preparado!",
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação criada: $notificationId para pedido $orderId")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido em preparo
     */
    suspend fun createOrderPreparingNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PREPARING",
                "title" to "Pedido em preparo",
                "message" to "Seu pedido na $storeName está sendo preparado!",
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de preparo criada para pedido $orderId")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de preparo", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido pronto
     */
    suspend fun createOrderReadyNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_READY",
                "title" to "Pedido pronto para retirada",
                "message" to "Seu pedido está pronto! Retire no balcão quando quiser.",
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de pedido pronto criada para $orderId")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de pedido pronto", e)
            false
        }
    }
    
    /**
     * Cria uma notificação de pedido retirado
     */
    suspend fun createOrderPickedUpNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "ORDER_PICKED_UP",
                "title" to "Pedido retirado",
                "message" to "Você retirou seu pedido na $storeName. Aproveite sua refeição!",
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to false
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de pedido retirado criada para $orderId")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de pedido retirado", e)
            false
        }
    }
    
    /**
     * Cria uma notificação para avaliar pedido
     */
    suspend fun createRatingNotification(
        userId: String,
        orderId: String,
        storeName: String
    ): Boolean {
        return try {
            val notificationId = notificationsRef.push().key ?: return false
            val currentTime = System.currentTimeMillis()
            
            val notification = mapOf(
                "id" to notificationId,
                "userId" to userId,
                "orderId" to orderId,
                "storeName" to storeName,
                "type" to "RATING",
                "title" to "Avalie seu pedido",
                "message" to "O que achou do seu pedido?\nSua opinião é muito importante!",
                "timestamp" to currentTime,
                "read" to false,
                "showRating" to true
            )
            
            notificationsRef.child(userId).child(notificationId).setValue(notification).await()
            Log.d("NotificationRepository", "✅ Notificação de avaliação criada para pedido $orderId")
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "❌ Erro ao criar notificação de avaliação", e)
            false
        }
    }
    
    /**
     * Observa notificações do usuário em tempo real
     */
    fun getUserNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notifications = mutableListOf<Notification>()
                
                for (child in snapshot.children) {
                    try {
                        val id = child.child("id").getValue(String::class.java) ?: continue
                        val title = child.child("title").getValue(String::class.java) ?: ""
                        val message = child.child("message").getValue(String::class.java) ?: ""
                        val timestamp = child.child("timestamp").getValue(Long::class.java) ?: 0L
                        val typeStr = child.child("type").getValue(String::class.java) ?: "ORDER_PLACED"
                        val showRating = child.child("showRating").getValue(Boolean::class.java) ?: false
                        
                        val type = when (typeStr) {
                            "RATING" -> NotificationType.RATING
                            "ORDER_PICKED_UP" -> NotificationType.ORDER_PICKED_UP
                            "ORDER_READY" -> NotificationType.ORDER_READY
                            "ORDER_PREPARING" -> NotificationType.ORDER_PREPARING
                            else -> NotificationType.ORDER_PLACED
                        }
                        
                        val time = formatTimestamp(timestamp)
                        
                        notifications.add(
                            Notification(
                                id = id,
                                title = title,
                                message = message,
                                time = time,
                                type = type,
                                showRating = showRating
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("NotificationRepository", "Erro ao converter notificação", e)
                    }
                }
                
                // Ordenar por timestamp (mais recente primeiro)
                notifications.sortByDescending { 
                    when {
                        it.time.contains("agora") -> System.currentTimeMillis()
                        it.time.contains("min atrás") -> System.currentTimeMillis() - (it.time.split(" ")[0].toIntOrNull() ?: 0) * 60000L
                        it.time.contains("h atrás") -> System.currentTimeMillis() - (it.time.split(" ")[0].toIntOrNull() ?: 0) * 3600000L
                        else -> 0L
                    }
                }
                
                Log.d("NotificationRepository", "✅ ${notifications.size} notificações carregadas")
                trySend(notifications)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Log.e("NotificationRepository", "Erro ao observar notificações", error.toException())
                close(error.toException())
            }
        }
        
        notificationsRef.child(userId).orderByChild("timestamp").addValueEventListener(listener)
        
        awaitClose {
            notificationsRef.child(userId).removeEventListener(listener)
        }
    }
    
    /**
     * Formata timestamp para exibição
     */
    private fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60000 -> "Agora"
            diff < 3600000 -> "${diff / 60000} min atrás"
            diff < 86400000 -> "${diff / 3600000}h atrás"
            else -> {
                val sdf = SimpleDateFormat("dd/MM 'às' HH:mm", Locale("pt", "BR"))
                sdf.format(Date(timestamp))
            }
        }
    }
}
