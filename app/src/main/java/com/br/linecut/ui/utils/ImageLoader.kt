package com.br.linecut.ui.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/**
 * Utilitário para carregamento de imagens com sistema de cache
 */
object ImageLoader {
    
    /**
     * Carrega uma imagem da URL, usando cache quando disponível
     * @param url URL da imagem a ser carregada
     * @return Bitmap da imagem ou null se houver erro
     */
    suspend fun loadImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            // Verificar se a imagem já está no cache
            ImageCache.get(url)?.let { cachedBitmap ->
                return@withContext cachedBitmap
            }
            
            // Se não estiver no cache, baixar da internet
            val connection = URL(url).openConnection()
            connection.doInput = true
            connection.connect()
            
            val inputStream = connection.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            // Salvar no cache se o bitmap foi criado com sucesso
            bitmap?.let { ImageCache.put(url, it) }
            
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}