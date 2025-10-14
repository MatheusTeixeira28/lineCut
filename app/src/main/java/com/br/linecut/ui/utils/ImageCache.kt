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
    
    /**
     * Busca no cache por URL completa ou por caminho parcial
     * Útil quando a URL vem como caminho relativo mas foi salva como URL completa
     */
    fun findByPath(urlOrPath: String): Bitmap? {
        // Tentar busca direta primeiro
        cache[urlOrPath]?.let { return it }
        
        // Se não encontrou e é um caminho relativo, buscar por correspondência parcial
        if (!urlOrPath.startsWith("http")) {
            // Normalizar o caminho para comparação
            val normalizedPath = urlOrPath.replace("%2F", "/")
            
            cache.entries.forEach { (key, bitmap) ->
                // Verificar se a chave contém o caminho
                if (key.contains(normalizedPath) || key.contains(urlOrPath)) {
                    return bitmap
                }
            }
        }
        
        return null
    }
}