package com.br.linecut.ui.screens

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.utils.ImageLoader
import com.br.linecut.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

// Shared layout constant for category filter chip height (from Figma spec)
private val FiltersChipHeight = 23.07.dp

@Parcelize
data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageRes: Int = android.R.drawable.ic_menu_gallery,
    val imageUrl: String = "", // URL da imagem do Firebase
    var quantity: Int = 0
) : Parcelable

data class MenuCategory(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    store: Store,
    menuItems: List<MenuItem> = emptyList(),
    categories: List<MenuCategory> = emptyList(),
    initialCartItems: List<MenuItem> = emptyList(), // Itens do carrinho para restaurar estado
    onBackClick: () -> Unit = {},
    onCategoryClick: (MenuCategory) -> Unit = {},
    onAddItem: (MenuItem) -> Unit = {},
    onRemoveItem: (MenuItem) -> Unit = {},
    onViewCartClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    productViewModel: ProductViewModel = viewModel(),
    categoryViewModel: com.br.linecut.ui.viewmodel.ProductCategoryViewModel = viewModel()
) {
    // Carregar produtos do Firebase quando o ID da loja mudar
    LaunchedEffect(store.id) {
        productViewModel.loadProductsByRestaurant(store.id)
    }
    
    // Observar dados do Firebase
    val firebaseMenuItems by productViewModel.filteredMenuItems.collectAsState()
    val isLoadingProducts by productViewModel.isLoading.collectAsState()
    val errorProducts by productViewModel.error.collectAsState()
    
    // Observar categorias do Firebase
    val firebaseCategories by categoryViewModel.menuCategories.collectAsState()
    val selectedCategoryId by categoryViewModel.selectedCategoryId.collectAsState()
    
    // Lista completa de produtos para exibição com quantidades
    var displayMenuItems by remember { mutableStateOf<List<MenuItem>>(emptyList()) }
    
    // Criar uma chave única baseada no conteúdo do carrinho para detectar mudanças
    val cartStateKey = remember(initialCartItems) {
        initialCartItems.joinToString(",") { "${it.id}:${it.quantity}" }
    }
    
    // Mapa de quantidades por ID de produto (gerenciado localmente para UI)
    var itemQuantities by remember(store.id) { 
        mutableStateOf<Map<String, Int>>(emptyMap())
    }
    
    // Sincronizar itemQuantities quando initialCartItems mudar
    LaunchedEffect(cartStateKey) {
        if (initialCartItems.isNotEmpty()) {
            val newQuantities = initialCartItems.associate { it.id to it.quantity }
            itemQuantities = newQuantities
        } else {
            // Se initialCartItems está vazio, limpar todas as quantidades (zerar carrinho)
            itemQuantities = emptyMap()
        }
    }
    
    // Atualizar lista de produtos quando dados do Firebase mudarem
    LaunchedEffect(firebaseMenuItems, itemQuantities) {
        // Usar APENAS produtos do Firebase, nunca mocks
        displayMenuItems = firebaseMenuItems.map { item ->
            item.copy(quantity = itemQuantities[item.id] ?: 0)
        }
    }
    
    // Calcular total e quantidade de itens baseado nas quantidades locais
    val cartTotal = remember(itemQuantities) {
        displayMenuItems.filter { itemQuantities[it.id] ?: 0 > 0 }
            .sumOf { it.price * (itemQuantities[it.id] ?: 0) }
    }
    
    val cartItemCount = remember(itemQuantities) {
        itemQuantities.values.sum()
    }
    
    // Funções para gerenciar o carrinho
    val handleAddItem: (MenuItem) -> Unit = { item ->
        // Atualizar quantidades locais
        val newQuantity = (itemQuantities[item.id] ?: 0) + 1
        itemQuantities = itemQuantities + (item.id to newQuantity)
        
        // Atualizar a lista de exibição
        displayMenuItems = displayMenuItems.map { 
            if (it.id == item.id) it.copy(quantity = newQuantity) else it 
        }
        
        // Chamar callback para atualizar carrinho na navegação
        onAddItem(item)
    }
    
    val handleRemoveItem: (MenuItem) -> Unit = { item ->
        // Atualizar quantidades locais
        val newQuantity = ((itemQuantities[item.id] ?: 0) - 1).coerceAtLeast(0)
        itemQuantities = if (newQuantity > 0) {
            itemQuantities + (item.id to newQuantity)
        } else {
            itemQuantities - item.id
        }
        
        // Atualizar a lista de exibição
        displayMenuItems = displayMenuItems.map { 
            if (it.id == item.id) it.copy(quantity = newQuantity) else it 
        }
        
        // Chamar callback para atualizar carrinho na navegação
        onRemoveItem(item)
    }
    
    // Função para navegar para a tela do carrinho com os dados
    val handleViewCartClick: () -> Unit = {
        // Chama o callback que já possui a lógica de conversão na navegação
        onViewCartClick()
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
                .background(LineCutDesignSystem.screenBackgroundColor)
        ) {
            // Header da loja
            StoreHeader(
                store = store
            )
            
            // Filtros de categoria - usar categorias do Firebase
            CategoryFilters(
                categories = if (firebaseCategories.isNotEmpty()) firebaseCategories else categories,
                onCategoryClick = { category ->
                    // Se clicar na categoria já selecionada, desselecionar (mostrar todos)
                    if (selectedCategoryId == category.id) {
                        categoryViewModel.selectCategory("")
                        productViewModel.filterByCategory(null)
                    } else {
                        // Atualizar seleção no categoryViewModel
                        categoryViewModel.selectCategory(category.id)
                        // Filtrar produtos no productViewModel
                        productViewModel.filterByCategory(category.id)
                    }
                    // Chamar callback original se necessário
                    onCategoryClick(category)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Lista de produtos
            MenuItemsList(
                items = displayMenuItems,
                onAddItem = handleAddItem,
                onRemoveItem = handleRemoveItem,
                isLoading = isLoadingProducts,
                error = errorProducts,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = FiltersChipHeight / 2)
            )
            
            // Espaço para o carrinho e bottom nav
            Spacer(modifier = Modifier.height(116.dp))
        }
        
        // Área do carrinho e bottom nav
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Fundo branco para o carrinho
            if (cartItemCount > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp)
                        .background(Color.White)
                ) {
                    CartSummary(
                        total = cartTotal,
                        itemCount = cartItemCount,
                        onViewCartClick = handleViewCartClick
                    )
                }
            }
            
            // Bottom Navigation
            LineCutBottomNavigationBar(
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
private fun StoreHeader(
    store: Store,
    modifier: Modifier = Modifier
) {
    // Carregar imagem com cache - busca inteligente para evitar placeholder
    // Tentar obter do cache ANTES do LaunchedEffect (busca síncrona)
    val initialBitmap = if (store.imageUrl.isNotEmpty()) {
        com.br.linecut.ui.utils.ImageCache.findByPath(store.imageUrl)
    } else null
    
    var storeLogoImageBitmap by remember(store.imageUrl) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isLoading by remember(store.imageUrl) { mutableStateOf(false) }
    
    LaunchedEffect(store.imageUrl) {
        if (store.imageUrl.isNotEmpty() && storeLogoImageBitmap == null) {
            // Só carregar se ainda não temos a imagem
            // Normalizar a URL para o formato do cache
            val normalizedUrl = ImageLoader.normalizeUrl(store.imageUrl)
            
            // Verificar cache com a URL normalizada
            val cachedBitmap = com.br.linecut.ui.utils.ImageCache.get(normalizedUrl)
            
            if (cachedBitmap != null) {
                // Imagem no cache - usar diretamente sem loading
                storeLogoImageBitmap = cachedBitmap
                isLoading = false
            } else {
                // Não está no cache - mostrar loading e carregar
                isLoading = true
                val bitmap = ImageLoader.loadImage(store.imageUrl)
                storeLogoImageBitmap = bitmap
                isLoading = false
            }
        } else if (store.imageUrl.isEmpty()) {
            storeLogoImageBitmap = null
            isLoading = false
        }
    }
    
    val topInset = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Card(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(170.dp + topInset)
            .offset(y = -topInset)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
        colors = CardDefaults.cardColors(containerColor = LineCutDesignSystem.screenBackgroundColor)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Conteúdo principal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 35.dp, top = 60.dp, end = 35.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Logo circular da loja
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(119.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (storeLogoImageBitmap != null) {
                            Image(
                                bitmap = storeLogoImageBitmap!!.asImageBitmap(),
                                contentDescription = "Logo ${store.name}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = LineCutRed
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(19.dp))
                
                // Informações da loja
                Column(modifier = Modifier.weight(1f)) {
                    // Nome da loja
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        maxLines = 1
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Categoria
                    Text(
                        text = store.category,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF7D7D7D),
                            fontSize = 16.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Localização
                    Text(
                        text = store.location,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF7D7D7D),
                            fontSize = 12.sp
                        )
                    )
                }
            }
            // Status "Aberto" fixo no topo à direita, sem sobrepor o nome
            StatusBadge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 40.dp, end = 16.dp)
            )
        }
    }
}

@Composable
private fun StatusBadge(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .shadow(3.dp, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = Color(0xFF4CAF50),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Aberto",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LineCutRed,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
private fun CategoryFilters(
    categories: List<MenuCategory>,
    onCategoryClick: (MenuCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // Pull the filters up by half the chip height so they sit centered on the seam between header and list
    Row(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = -(FiltersChipHeight / 2)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (categories.isNotEmpty()) {
                    scope.launch {
                        val prev = (listState.firstVisibleItemIndex - 1).coerceAtLeast(0)
                        listState.animateScrollToItem(prev)
                    }
                }
            },
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_arrow),
                contentDescription = "Mover filtros",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
    LazyRow(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    onClick = { onCategoryClick(category) },
                    label = {
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.92.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    },
                    selected = category.isSelected,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = LineCutRed,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFB9B9B9),
                        labelColor = Color.White
                    ),
                    shape = RoundedCornerShape(18.46.dp),
                    modifier = Modifier
                        .shadow(
                            elevation = 3.69.dp,
                            shape = RoundedCornerShape(18.46.dp)
                        )
                        .height(FiltersChipHeight)
                )
            }
        }
    }
}

@Composable
private fun MenuItemsList(
    items: List<MenuItem>,
    onAddItem: (MenuItem) -> Unit,
    onRemoveItem: (MenuItem) -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    modifier: Modifier = Modifier
) {
    when {
        isLoading -> {
            // Estado de carregamento
            Box(
                modifier = modifier.fillMaxSize(),
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
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = LineCutRed,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Erro ao carregar produtos",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary
                        )
                    )
                }
            }
        }
        items.isEmpty() -> {
            // Estado vazio
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Nenhum produto disponível",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextSecondary
                        )
                    )
                }
            }
        }
        else -> {
            // Lista com dados
            LazyColumn(
                modifier = modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { item ->
                    MenuItemCard(
                        item = item,
                        onAddItem = { onAddItem(item) },
                        onRemoveItem = { onRemoveItem(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Carregar imagem com cache - busca inteligente para evitar placeholder
    // Tentar obter do cache ANTES do LaunchedEffect (busca síncrona)
    val initialBitmap = if (item.imageUrl.isNotEmpty()) {
        com.br.linecut.ui.utils.ImageCache.findByPath(item.imageUrl)
    } else null
    
    var menuItemImageBitmap by remember(item.imageUrl) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isLoading by remember(item.imageUrl) { mutableStateOf(false) }
    
    LaunchedEffect(item.imageUrl) {
        if (item.imageUrl.isNotEmpty() && menuItemImageBitmap == null) {
            // Só carregar se ainda não temos a imagem
            // Normalizar a URL para o formato do cache
            val normalizedUrl = ImageLoader.normalizeUrl(item.imageUrl)
            
            // Verificar cache com a URL normalizada
            val cachedBitmap = com.br.linecut.ui.utils.ImageCache.get(normalizedUrl)
            
            if (cachedBitmap != null) {
                // Imagem no cache - usar diretamente sem loading
                menuItemImageBitmap = cachedBitmap
                isLoading = false
            } else {
                // Não está no cache - mostrar loading e carregar
                isLoading = true
                val bitmap = ImageLoader.loadImage(item.imageUrl)
                menuItemImageBitmap = bitmap
                isLoading = false
            }
        } else if (item.imageUrl.isEmpty()) {
            menuItemImageBitmap = null
            isLoading = false
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(107.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(19.dp)
            ),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            // Imagem do produto - fills the full height of the card
            Box(
                modifier = Modifier
                    .size(width = 106.dp, height = 104.dp)
                    .clip(RoundedCornerShape(topStart = 19.dp, bottomStart = 19.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (menuItemImageBitmap != null) {
                    Image(
                        bitmap = menuItemImageBitmap!!.asImageBitmap(),
                        contentDescription = "Imagem ${item.name}",
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
            
            // Content area with padding
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp, top = 12.dp, bottom = 12.dp) // Further reduced end padding to prevent button cutoff
            ) {
                // Nome e preço na mesma linha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    
                    Text(
                        text = "R$ ${String.format("%.2f", item.price).replace(".", ",")}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                // Descrição
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF515050),
                        fontSize = 12.sp,
                        lineHeight = 12.sp
                    ),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(4.dp)) // Reduced to 4dp to give maximum space for buttons
            
            // Controles de quantidade (layout vertical)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 14.dp, end = 8.dp) // Increased padding to ensure buttons have enough margin from edge
            ) {
                // Botão adicionar (sempre visível)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = LineCutRed,
                            shape = CircleShape
                        )
                        .clickable { onAddItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                }
                
                // Quantidade (se maior que 0)
                if (item.quantity > 0) {
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    )
                    
                    // Botão remover
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = LineCutRed,
                                shape = CircleShape
                            )
                            .clickable { onRemoveItem() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Remover",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CartSummary(
    total: Double,
    itemCount: Int,
    onViewCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
            .height(65.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Informações do total
        Column {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF515050),
                    fontSize = 12.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R$ ${String.format("%.2f", total).replace(".", ",")}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = " / $itemCount itens",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF515050),
                        fontSize = 12.sp
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botão Ver sacola
        Box(
            modifier = Modifier
                .width(144.dp)
                .height(28.1.dp)
                .background(
                    color = LineCutRed,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onViewCartClick() }
        ) {
            // Texto "Ver sacola"
            Text(
                text = "Ver sacola",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 16.dp)
                    .width(84.dp)
            )
            
            // Ícone da sacola
            Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(11.415.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 13.dp)
            )
        }
    }
}

// Previews
@Preview(
    name = "Store Detail Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun StoreDetailScreenPreview() {
    LineCutTheme {
        StoreDetailScreen(
            store = Store(
                id = "1",
                name = "Museoh",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m"
            ),
            menuItems = getSampleMenuItems(),
            categories = getSampleCategories()
        )
    }
}

@Preview(
    name = "Store Header",
    showBackground = true
)
@Composable
fun StoreHeaderPreview() {
    LineCutTheme {
        StoreHeader(
            store = Store(
                id = "1",
                name = "Museoh",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m"
            )
        )
    }
}

@Preview(
    name = "Category Filters",
    showBackground = true
)
@Composable
fun CategoryFiltersPreview() {
    LineCutTheme {
        CategoryFilters(
            categories = getSampleCategories(),
            onCategoryClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(
    name = "Menu Item Card",
    showBackground = true
)
@Composable
fun MenuItemCardPreview() {
    LineCutTheme {
        MenuItemCard(
            item = MenuItem(
                id = "1",
                name = "Açaí",
                description = "Creme congelado feito da polpa do fruto açaí, geralmente servido com frutas, granola e outros acompanhamentos.",
                price = 11.90,
                category = "acompanhamentos",
                quantity = 1
            ),
            onAddItem = {},
            onRemoveItem = {}
        )
    }
}

@Preview(
    name = "Cart Summary",
    showBackground = true
)
@Composable
fun CartSummaryPreview() {
    LineCutTheme {
        CartSummary(
            total = 39.90,
            itemCount = 4,
            onViewCartClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

// Dados de exemplo
private fun getSampleMenuItems(): List<MenuItem> = listOf(
    MenuItem(
        id = "1",
        name = "Açaí",
        description = "Creme congelado feito da polpa do fruto açaí, geralmente servido com frutas, granola e outros acompanhamentos.",
        price = 11.90,
        category = "acompanhamentos",
        quantity = 1
    ),
    MenuItem(
        id = "2",
        name = "Croissant",
        description = "Massa folhada em formato de meia-lua, geralmente amanteigada e podendo ter recheios doces ou salgados.",
        price = 6.00,
        category = "acompanhamentos",
        quantity = 1
    ),
    MenuItem(
        id = "3",
        name = "Lanche Natural",
        description = "Sanduíche preparado com pão integral, recheios leves como queijo branco, peito de peru, salada e molhos leves.",
        price = 8.00,
        category = "lanches",
        quantity = 1
    ),
    MenuItem(
        id = "4",
        name = "Pão de Queijo",
        description = "Pequeno pão assado, feito com polvilho, queijo e outros ingredientes, resultando em uma textura macia e elástica.",
        price = 4.00,
        category = "acompanhamentos",
        quantity = 1
    )
)

private fun getSampleCategories(): List<MenuCategory> = listOf(
    MenuCategory("acompanhamentos", "Acompanhamentos", true),
    MenuCategory("bebidas", "Bebidas"),
    MenuCategory("combos", "Combos"),
    MenuCategory("doces", "Doces"),
    MenuCategory("lanches", "Lanches"),
    MenuCategory("pratos", "Pratos Principais"),
    MenuCategory("salgados", "Salgados"),
    MenuCategory("sobremesas", "Sobremesas")
)
