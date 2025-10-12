package com.br.linecut.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.utils.ImageCache
import com.br.linecut.ui.utils.ImageLoader

/**
 * Componente que carrega imagens com cache otimizado
 * Observa o cache reativamente para evitar animações desnecessárias
 */
@Composable
fun CachedAsyncImage(
    imageUrl: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderRes: Int = android.R.drawable.ic_menu_gallery,
    loadingIndicatorSize: androidx.compose.ui.unit.Dp = 24.dp
) {
    // Ler o cache FORA do remember para criar observação de estado
    val cachedBitmap = if (imageUrl.isNotEmpty()) ImageCache.get(imageUrl) else null
    
    var imageBitmap by remember(imageUrl) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(imageUrl) { mutableStateOf(false) }
    
    // Se encontrou no cache, usar diretamente
    val displayBitmap = cachedBitmap ?: imageBitmap
    
    // Só carregar se não estiver no cache
    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty() && cachedBitmap == null && imageBitmap == null) {
            isLoading = true
            val bitmap = ImageLoader.loadImage(imageUrl)
            imageBitmap = bitmap
            isLoading = false
        }
    }
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            displayBitmap != null -> {
                Image(
                    bitmap = displayBitmap.asImageBitmap(),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.matchParentSize()
                )
            }
            isLoading -> {
                CircularProgressIndicator(
                    color = LineCutRed,
                    modifier = Modifier.size(loadingIndicatorSize)
                )
            }
            else -> {
                // Placeholder para URL vazia ou falha no carregamento
                Image(
                    painter = painterResource(id = placeholderRes),
                    contentDescription = contentDescription,
                    contentScale = contentScale,
                    modifier = Modifier.matchParentSize()
                )
            }
        }
    }
}
