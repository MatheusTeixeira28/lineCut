package com.br.linecut.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Utilitário para carregamento de imagens com sistema de cache
 */
object ImageLoader {
    
    /**
     * Normaliza a URL para o formato que será salvo no cache
     * Converte caminhos relativos em URLs completas do Firebase Storage
     */
    suspend fun normalizeUrl(url: String): String = withContext(Dispatchers.IO) {
        try {
            var imageUrl = url
            
            // Se a URL não começar com http/https/gs, é um caminho relativo
            if (!url.startsWith("http") && !url.startsWith("gs://")) {
                try {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child(url)
                    imageUrl = storageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext url
                }
            }
            // Se a URL for uma referência do Storage (gs://), obter URL de download atual
            else if (url.startsWith("gs://")) {
                try {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReferenceFromUrl(url)
                    imageUrl = storageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext url
                }
            }
            
            imageUrl
        } catch (e: Exception) {
            e.printStackTrace()
            url
        }
    }
    
    /**
     * Carrega uma imagem da URL, usando cache quando disponível
     * @param url URL da imagem a ser carregada
     * @return Bitmap da imagem ou null se houver erro
     */
    suspend fun loadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            var imageUrl = url
            
            // Se a URL não começar com http/https/gs, é um caminho relativo
            if (!url.startsWith("http") && !url.startsWith("gs://")) {
                try {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child(url)
                    imageUrl = storageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext null
                }
            }
            // Se a URL for uma referência do Storage (gs://), obter URL de download atual
            else if (url.startsWith("gs://")) {
                try {
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.getReferenceFromUrl(url)
                    imageUrl = storageRef.downloadUrl.await().toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext null
                }
            }
            
            // Verificar se a imagem já está no cache COM A URL NORMALIZADA
            ImageCache.get(imageUrl)?.let { cachedBitmap ->
                return@withContext cachedBitmap
            }
            
            // Tentar baixar a imagem
            val bitmap = downloadImage(imageUrl)
            
            // Se falhar por token expirado e a URL tiver um caminho de storage, tentar novamente
            if (bitmap == null && imageUrl.contains("firebasestorage.googleapis.com")) {
                // Extrair o caminho do arquivo da URL
                val pathMatch = Regex("/o/(.+?)\\?").find(imageUrl)
                if (pathMatch != null) {
                    val path = pathMatch.groupValues[1].replace("%2F", "/")
                    try {
                        val storage = FirebaseStorage.getInstance()
                        val storageRef = storage.reference.child(path)
                        val freshUrl = storageRef.downloadUrl.await().toString()
                        return@withContext downloadImage(freshUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private suspend fun downloadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection()
            connection.doInput = true
            connection.connect()
            
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            
            // Salvar no cache
            bitmap?.let { ImageCache.put(url, it) }
            
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}