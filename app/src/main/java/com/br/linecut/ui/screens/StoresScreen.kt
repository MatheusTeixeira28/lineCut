package com.br.linecut.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutTitle
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

data class Store(
    val id: String,
    val name: String,
    val category: String,
    val location: String,
    val distance: String,
    val imageRes: Int = android.R.drawable.ic_menu_gallery, // placeholder
    val isFavorite: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoresScreen(
    stores: List<Store> = emptyList(),
    currentAddress: String = "Av. Eng. Eusébio Stevaux, 823",
    onStoreClick: (Store) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    
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
                .height(184.dp)
        ) {
            // Fundo branco arredondado que se estende além das bordas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp)
                    .offset(y = (-73).dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(30.dp)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {}
            
            // Conteúdo do header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 82.dp)
            ) {
                // Título "Lojas"
                LineCutTitle(
                    text = "Lojas"
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Endereço centralizado
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(10.dp)
                            ),
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
            
            // Barra de busca - posicionada com CSS especificado
            if (isSearchVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 140.dp),
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
        if (filteredStores.isEmpty()) {
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
        } else {
            // Lista com dados
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 23.dp),
                verticalArrangement = Arrangement.spacedBy(19.dp)
            ) {
                // Primeiro item com espaço extra do header
                item {
                    Spacer(modifier = Modifier.height(if (isSearchVisible) 36.dp else 4.dp))
                }
                
                items(filteredStores) { store ->
                    StoreCard(
                        store = store,
                        onStoreClick = { onStoreClick(store) },
                        onFavoriteClick = { /* TODO: Toggle favorite */ }
                    )
                }
                
                // Spacer para o bottom navigation
                item {
                    Spacer(modifier = Modifier.height(20.dp))
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
                if (!isSearchVisible) searchQuery = ""
            },
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
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
                    Image(
                        painter = painterResource(id = store.imageRes),
                        contentDescription = "Imagem do ${store.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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

// Previews
@Preview(
    name = "Stores Screen - Com Dados",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StoresScreenPreview() {
    LineCutTheme {
        StoresScreen(
            stores = getSampleStores()
        )
    }
}

@Preview(
    name = "Stores Screen - Com Busca",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StoresScreenSearchPreview() {
    LineCutTheme {
        StoresScreenWithSearch(
            stores = getSampleStores()
        )
    }
}

// Componente auxiliar para preview com busca visível
@Composable
private fun StoresScreenWithSearch(
    stores: List<Store> = emptyList(),
    currentAddress: String = "Senac Campinas",
    onStoreClick: (Store) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchVisible by remember { mutableStateOf(true) } // Forçado como true para preview
    var searchQuery by remember { mutableStateOf("lanches") } // Exemplo de busca
    
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
            .background(Color.White)
    ) {
        // Header com fundo arredondado - mais fiel ao Figma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(184.dp)
        ) {
            // Fundo branco arredondado que se estende além das bordas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp)
                    .offset(y = (-73).dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(30.dp)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {}
            
            // Conteúdo do header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, top = 82.dp)
            ) {
                // Título "Lojas"
                LineCutTitle(
                    text = "Lojas"
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Endereço centralizado
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(10.dp)
                            ),
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
            
            // Barra de busca - posicionada com CSS especificado
            if (isSearchVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 140.dp),
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
        if (filteredStores.isEmpty()) {
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
        } else {
            // Lista com dados
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 23.dp),
                verticalArrangement = Arrangement.spacedBy(19.dp)
            ) {
                // Primeiro item com espaço extra do header
                item {
                    Spacer(modifier = Modifier.height(if (isSearchVisible) 36.dp else 4.dp))
                }
                
                items(filteredStores) { store ->
                    StoreCard(
                        store = store,
                        onStoreClick = { onStoreClick(store) },
                        onFavoriteClick = { /* TODO: Toggle favorite */ }
                    )
                }
                
                // Spacer para o bottom navigation
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@Preview(
    name = "Stores Screen - Vazio",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StoresScreenEmptyPreview() {
    LineCutTheme {
        StoresScreen(
            stores = emptyList()
        )
    }
}

@Preview(
    name = "Store Card",
    showBackground = true
)
@Composable
fun StoreCardPreview() {
    LineCutTheme {
        StoreCard(
            store = Store(
                id = "1",
                name = "Museoh",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m"
            ),
            onStoreClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(
    name = "Store Card - Favorito",
    showBackground = true
)
@Composable
fun StoreCardFavoritePreview() {
    LineCutTheme {
        StoreCard(
            store = Store(
                id = "1",
                name = "Vila Sabor",
                category = "Refeições variadas",
                location = "Praça 1 - Senac",
                distance = "300m",
                isFavorite = true
            ),
            onStoreClick = {},
            onFavoriteClick = {}
        )
    }
}

// Função auxiliar para dados de exemplo
private fun getSampleStores(): List<Store> = listOf(
    Store(
        id = "1",
        name = "Museoh",
        category = "Lanches e Salgados",
        location = "Praça 3 - Senac",
        distance = "150m"
    ),
    Store(
        id = "2",
        name = "Sabor & Companhia",
        category = "Refeições variadas",
        location = "Praça 3 - Senac",
        distance = "200m"
    ),
    Store(
        id = "3",
        name = "Cafezin",
        category = "Café gourmet",
        location = "Praça 2 - Senac",
        distance = "240m"
    ),
    Store(
        id = "4",
        name = "Sanduba Burguer",
        category = "Lanches variados",
        location = "Praça 2 - Senac",
        distance = "260m",
        isFavorite = true
    ),
    Store(
        id = "5",
        name = "Vila Sabor",
        category = "Refeições variadas",
        location = "Praça 1 - Senac",
        distance = "300m",
        isFavorite = true
    ),
    Store(
        id = "6",
        name = "Varanda",
        category = "Snacks",
        location = "Praça 1 - Senac",
        distance = "320m"
    ),
    Store(
        id = "7",
        name = "Urban Food",
        category = "Refeição rápida",
        location = "Praça 1 - Senac",
        distance = "400m"
    )
)
