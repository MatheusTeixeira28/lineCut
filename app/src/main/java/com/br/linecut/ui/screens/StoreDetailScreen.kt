package com.br.linecut.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.theme.*

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageRes: Int = android.R.drawable.ic_menu_gallery,
    var quantity: Int = 0
)

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
    cartTotal: Double = 0.0,
    cartItemCount: Int = 0,
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
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header da loja
            StoreHeader(
                store = store,
                onBackClick = onBackClick
            )
            
            // Filtros de categoria
            CategoryFilters(
                categories = categories,
                onCategoryClick = onCategoryClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
            
            // Lista de produtos
            MenuItemsList(
                items = menuItems,
                onAddItem = onAddItem,
                onRemoveItem = onRemoveItem,
                modifier = Modifier.weight(1f)
            )
            
            // Espaço para o carrinho e bottom nav
            Spacer(modifier = Modifier.height(126.dp))
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
                        .height(82.dp)
                        .background(Color.White)
                ) {
                    CartSummary(
                        total = cartTotal,
                        itemCount = cartItemCount,
                        onViewCartClick = onViewCartClick
                    )
                }
            }
            
            // Bottom Navigation
            BottomNavigationBar(
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
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(235.dp)
            .offset(y = (-13).dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Botão voltar
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = LineCutRed
                )
            }
            
            // Conteúdo principal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 35.dp, top = 87.dp, end = 35.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Logo circular da loja
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(119.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = store.imageRes),
                        contentDescription = "Logo ${store.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.width(19.dp))
                
                // Informações da loja
                Column {
                    // Nome da loja
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
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
            
            // Status "Aberto" no canto superior direito
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 73.dp, end = 16.dp)
                    .shadow(3.dp, RoundedCornerShape(20.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
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
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryFilters(
    categories: List<MenuCategory>,
    onCategoryClick: (MenuCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
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
                    .height(23.07.dp)
            )
        }
    }
}

@Composable
private fun MenuItemsList(
    items: List<MenuItem>,
    onAddItem: (MenuItem) -> Unit,
    onRemoveItem: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
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

@Composable
private fun MenuItemCard(
    item: MenuItem,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(19.dp)
            ),
        shape = RoundedCornerShape(19.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Imagem do produto
            Card(
                shape = RoundedCornerShape(9.dp),
                modifier = Modifier.size(width = 106.dp, height = 80.dp)
            ) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = "Imagem ${item.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Informações do produto
            Column(
                modifier = Modifier.weight(1f)
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
                        fontSize = 9.sp,
                        lineHeight = 11.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Controles de quantidade (layout vertical)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
            .height(82.dp),
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
                .shadow(
                    elevation = 4.dp,
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
                    .align(Alignment.CenterStart)
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

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = LineCutRed)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onOrdersClick) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = "Pedidos",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
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
            categories = getSampleCategories(),
            cartTotal = 39.90,
            cartItemCount = 4
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
            ),
            onBackClick = {}
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
