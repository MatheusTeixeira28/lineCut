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
    // Observar cache diretamente - mantém sincronizado com o mutableStateMapOf
    val cachedBitmap = ImageCache.get(imageUrl)
    var loadedBitmap by remember(imageUrl) { mutableStateOf<Bitmap?>(null) }
    
    // A imagem final é o cache (se existir) ou a carregada
    val imageBitmap = cachedBitmap ?: loadedBitmap
    
    // Carregar imagem do Firebase Storage apenas se não estiver no cache
    LaunchedEffect(imageUrl) {
        if (imageUrl.isNotEmpty() && ImageCache.get(imageUrl) == null) {
            // Só carregar se realmente não estiver no cache
            val bitmap = ImageLoader.loadImage(imageUrl)
            loadedBitmap = bitmap
        } else if (imageUrl.isEmpty()) {
            loadedBitmap = null
        }
    }
    
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap!!.asImageBitmap(),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )
        } else {
            // Placeholder enquanto carrega ou se URL vazia
            Image(
                painter = painterResource(id = placeholderRes),
                contentDescription = contentDescription,
                contentScale = contentScale,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
