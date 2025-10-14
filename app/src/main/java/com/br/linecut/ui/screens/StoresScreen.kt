package com.br.linecut.ui.screens

import android.Manifest
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.utils.ImageLoader
import com.br.linecut.ui.viewmodel.CompanyViewModel
import android.os.Parcelable
import com.br.linecut.ui.utils.ImageCache
import kotlinx.parcelize.Parcelize

@Parcelize
data class Store(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val distance: String,
    val imageRes: Int = android.R.drawable.ic_menu_gallery, // placeholder
    val imageUrl: String = "", // URL da imagem do Firebase
    val isFavorite: Boolean = false
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    currentAddress: String = "Av. Eng. Eusébio Stevaux, 823",
    onStoreClick: (Store) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    showSearchBar: Boolean = false,
    modifier: Modifier = Modifier,
    companyViewModel: CompanyViewModel = viewModel()
) {
    var isSearchVisible by remember { mutableStateOf(showSearchBar) }
    var searchQuery by remember { mutableStateOf("") }
    var showLocationDialog by remember { mutableStateOf(false) }
    
    // Launcher para solicitar permissão de localização
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Permissão concedida - aqui você pode adicionar lógica futura
            showLocationDialog = false
        } else {
            // Permissão negada
            showLocationDialog = false
        }
    }
    
    // Observar dados do Firebase
    val stores by companyViewModel.stores.collectAsState()
    val isLoading by companyViewModel.isLoading.collectAsState()
    val error by companyViewModel.error.collectAsState()
    
    // Filtrar lojas baseado na busca
    val filteredStores = remember(stores, searchQuery) {
        if (searchQuery.isBlank()) {
            stores
        } else {
            stores.filter { store ->
                store.name.contains(searchQuery, ignoreCase = true) ||
                store.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header com fundo arredondado - mais fiel ao Figma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            // Header simples baseado no Figma
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(126.dp)
                    .background(
                        LineCutDesignSystem.screenBackgroundColor,
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                    )
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    )
            ) { }
            
            // Conteúdo do header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 82.dp)
            ) {
                // Título "Lojas"

                Text(
                    text = "Lojas",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                // Endereço centralizado - visível apenas quando busca não está ativa
                if (!isSearchVisible) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 23.dp), // Ajusta a distância da borda direita
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Card(
                                modifier = Modifier
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        showLocationDialog = true
                                    },
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Localização",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = currentAddress,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Barra de busca - posicionada com CSS especificado
            if (isSearchVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 135.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .width(343.dp)
                            .height(28.1.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        shape = RoundedCornerShape(15.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xFFE0E0E0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = TextPlaceholder,
                                modifier = Modifier.size(16.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier.weight(1f),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                ),
                                singleLine = true,
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text(
                                            text = "Buscar lojas...",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = TextPlaceholder,
                                                fontSize = 13.sp
                                            )
                                        )
                                    }
                                    innerTextField()
                                }
                            )
                            
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpar busca",
                                        tint = TextPlaceholder,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Lista de lojas
        when {
            isLoading -> {
                // Estado de carregamento
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LineCutRed,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            error != null -> {
                // Estado de erro
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = LineCutRed,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Erro ao carregar lojas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { companyViewModel.refresh() },
                            colors = ButtonDefaults.buttonColors(containerColor = LineCutRed)
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            filteredStores.isEmpty() -> {
                // Estado vazio
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.StoreMallDirectory,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Nenhuma loja encontrada" else "Nenhuma loja encontrada",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextSecondary
                            )
                        )
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Tente buscar por outro termo" else "Tente ajustar os filtros ou sua localização",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextPlaceholder
                            )
                        )
                    }
                }
            }
            else -> {
                // Lista com dados
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 23.dp),
                    verticalArrangement = Arrangement.spacedBy(19.dp)
                ) {

                    
                    items(filteredStores) { store ->
                        StoreCard(
                            store = store,
                            onStoreClick = { onStoreClick(store) },
                            onFavoriteClick = { /* TODO: Toggle favorite */ }
                        )
                    }
                    

                }
            }
        }
        
        // Bottom Navigation
        LineCutBottomNavigationBar(
            selectedItem = if (isSearchVisible) NavigationItem.SEARCH else NavigationItem.HOME,
            onHomeClick = {
                if (isSearchVisible) {
                    isSearchVisible = false
                    searchQuery = ""
                } else {
                    onHomeClick()
                }
            },
            onSearchClick = {
                isSearchVisible = true
                onSearchClick()
            },
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
    
    // Dialog de solicitação de localização
    if (showLocationDialog) {
        LocationPermissionDialog(
            onDismiss = { showLocationDialog = false },
            onAllowAccess = {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        )
    }
}

@Composable
private fun StoreCard(
    store: Store,
    onStoreClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Carregar imagem com cache - busca inteligente para evitar placeholder
    // Tentar obter do cache ANTES do LaunchedEffect (busca síncrona)
    val initialBitmap = if (store.imageUrl.isNotEmpty()) {
        ImageCache.findByPath(store.imageUrl)
    } else null
    
    var storeImageBitmap by remember(store.imageUrl) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isLoading by remember(store.imageUrl) { mutableStateOf(false) }
    
    LaunchedEffect(store.imageUrl) {
        if (store.imageUrl.isNotEmpty() && storeImageBitmap == null) {
            // Só carregar se ainda não temos a imagem
            // Normalizar a URL para o formato do cache
            val normalizedUrl = ImageLoader.normalizeUrl(store.imageUrl)
            
            // Verificar cache com a URL normalizada
            val cachedBitmap = ImageCache.get(normalizedUrl)
            
            if (cachedBitmap != null) {
                // Imagem no cache - usar diretamente sem loading
                storeImageBitmap = cachedBitmap
                isLoading = false
            } else {
                // Não está no cache - mostrar loading e carregar
                isLoading = true
                val bitmap = ImageLoader.loadImage(store.imageUrl)
                storeImageBitmap = bitmap
                isLoading = false
            }
        } else if (store.imageUrl.isEmpty()) {
            storeImageBitmap = null
            isLoading = false
        }
    }
    
    Card(
        onClick = onStoreClick,
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .shadow(
                elevation = 3.dp,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagem do restaurante - proporções exatas do Figma
                Card(
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(width = 94.dp, height = 68.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (storeImageBitmap != null) {
                            Image(
                                bitmap = storeImageBitmap!!.asImageBitmap(),
                                contentDescription = "Imagem do ${store.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = LineCutRed
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Informações do restaurante
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Nome do restaurante
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(2.dp))
                    
                    // Categoria
                    Text(
                        text = store.category,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        ),
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    // Localização e distância
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = store.location,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextPlaceholder,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Ícone de pessoa e distância
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = store.distance,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = LineCutRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            // Ícone de favorito - posicionado no canto superior direito
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
            ) {
                Icon(
                    imageVector = if (store.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (store.isFavorite) "Remover dos favoritos" else "Adicionar aos favoritos",
                    tint = if (store.isFavorite) LineCutRed else TextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onAllowAccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .width(380.dp)
                .height(260.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                
                // Título com ícone
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = LineCutRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Defina sua Localização",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF7D7D7D),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Descrição
                Text(
                    text = "Permita o acesso à sua localização para encontrar lojas próximas.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 20.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Botão "Permitir acesso"
                Button(
                    onClick = onAllowAccess,
                    modifier = Modifier
                        .width(200.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(86.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1CB456)
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = "Permitir acesso",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Botão "Cancelar"
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = LineCutRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}