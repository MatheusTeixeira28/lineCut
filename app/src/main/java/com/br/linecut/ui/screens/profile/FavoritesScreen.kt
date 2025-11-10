package com.br.linecut.ui.screens.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.screens.Store
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.theme.TextSecondary
import com.br.linecut.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackClick: () -> Unit = {},
    onStoreClick: (Store) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = viewModel()
) {
    // Carregar lojas favoritas
    LaunchedEffect(Unit) {
        Log.d("FavoritesScreen", "==== CARREGANDO FAVORITOS ====")
        userViewModel.loadFavoriteStores()
    }
    
    val favoriteStores by userViewModel.favoriteStores.collectAsState()
    val isLoading by userViewModel.isLoadingFavorites.collectAsState()
    val error by userViewModel.favoritesError.collectAsState()
    
    Log.d("FavoritesScreen", "State - Loading: $isLoading, Error: $error, Stores count: ${favoriteStores.size}")
    favoriteStores.forEach { store ->
        Log.d("FavoritesScreen", "  Store: ${store.name} (${store.id})")
    }
    
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        )
        
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .offset(x = 34.dp, y = 82.dp)
                .size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_arrow),
                contentDescription = "Voltar",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Title "Favoritos"
        Text(
            text = "Favoritos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = LineCutRed,
            modifier = Modifier.offset(x = 60.dp, y = 82.dp)
        )
        
        // Lista de lojas favoritas
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 140.dp,
                    bottom = 44.dp // Espaço para bottom navigation
                )
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LineCutRed,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = LineCutRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Erro ao carregar favoritos",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LineCutRed
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = error ?: "Tente novamente mais tarde",
                                fontSize = 14.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                favoriteStores.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = Color(0xFFD1D1D1),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhum favorito ainda",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7D7D7D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Adicione suas lanchonetes favoritas",
                                fontSize = 14.sp,
                                color = Color(0xFF959595)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = 28.dp,
                            end = 28.dp,
                            top = 0.dp,
                            bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(19.dp)
                    ) {
                        items(favoriteStores) { store ->
                            FavoriteStoreCard(
                                store = store,
                                onStoreClick = { onStoreClick(store) },
                                onFavoriteClick = {
                                    userViewModel.toggleFavorite(store.id)
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Bottom Navigation
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            LineCutBottomNavigationBar(
                selectedItem = NavigationItem.PROFILE,
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick,
                onOrdersClick = onOrdersClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun FavoriteStoreCard(
    store: Store,
    onStoreClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Usar o mesmo card de StoresScreen
    com.br.linecut.ui.screens.StoreCard(
        store = store,
        onStoreClick = onStoreClick,
        onFavoriteClick = onFavoriteClick,
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesScreenPreview() {
    LineCutTheme {
        FavoritesScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenEmptyPreview() {
    LineCutTheme {
        FavoritesScreen()
    }
}
