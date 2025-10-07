package com.br.linecut.ui.utils

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateMapOf

/**
 * Cache singleton para armazenar imagens de perfil em memória durante a sessão
 */
object ImageCache {
    private val cache = mutableStateMapOf<String, Bitmap>()
    
    /**
     * Obtém uma imagem do cache
     */
    fun get(url: String): Bitmap? = cache[url]
    
    /**
     * Armazena uma imagem no cache
     */
    fun put(url: String, bitmap: Bitmap) {
        cache[url] = bitmap
    }
    
    /**
     * Remove uma imagem específica do cache
     */
    fun remove(url: String) {
        cache.remove(url)
    }
    
    /**
     * Limpa todo o cache (útil no logout)
     */
    fun clear() {
        cache.clear()
    }
    
    /**
     * Verifica se uma URL está no cache
     */
    fun contains(url: String): Boolean = cache.containsKey(url)
}