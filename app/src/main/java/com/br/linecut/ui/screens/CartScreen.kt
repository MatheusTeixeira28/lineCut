package com.br.linecut.ui.screens

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.utils.ImageCache
import com.br.linecut.ui.utils.ImageLoader
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val imageRes: Int = android.R.drawable.ic_menu_gallery,
    val imageUrl: String = "", // URL da imagem do Firebase
    var quantity: Int = 1
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    store: Store,
    cartItems: List<CartItem> = emptyList(),
    onBackClick: () -> Unit = {},
    onClearCartClick: () -> Unit = {},
    onAddMoreItemsClick: () -> Unit = {},
    onAddItem: (CartItem) -> Unit = {},
    onRemoveItem: (CartItem) -> Unit = {},
    onContinueClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    val totalItems = cartItems.sumOf { it.quantity }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LineCutDesignSystem.screenBackgroundColor)
        ) {
            // Header da loja
            CartHeader(
                store = store
            )
            
            // Título e botão voltar
            CartTitle(
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp) // Reduced from 16dp to 8dp
            )
            
            // Lista de itens do carrinho
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {
                // Cabeçalho "Itens adicionados" e "Limpar"
                CartItemsHeader(
                    onClearCartClick = onClearCartClick,
                    modifier = Modifier.padding(bottom = 12.dp) // Reduced from 16dp to 12dp
                )
                
                // Lista de itens
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp), // Reduced from 12dp to 8dp
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemCard(
                            item = item,
                            onAddItem = { onAddItem(item) },
                            onRemoveItem = { onRemoveItem(item) }
                        )
                    }

                    if (cartItems.isNotEmpty()) {
                        item {
                Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 8.dp), // Reduced from top=10dp, bottom=12dp
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                AddMoreItemsButton(
                                    onClick = onAddMoreItemsClick
                                )
                            }
                        }
                    }
                }
            }
            
            // (removido) Espaço fixo substituído por contentPadding no LazyColumn
        }
        
        // Área do resumo e bottom nav
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Fundo branco para o resumo
            if (cartItems.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .background(Color.White)
                ) {
                    CartSummary(
                        total = totalPrice,
                        itemCount = totalItems,
                        onContinueClick = onContinueClick
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
private fun CartHeader(
    store: Store,
    modifier: Modifier = Modifier
) {
    // Carregar imagem com cache - busca inteligente para evitar placeholder
    val initialBitmap = if (store.imageUrl.isNotEmpty()) {
        ImageCache.findByPath(store.imageUrl)
    } else null
    
    var storeImageBitmap by remember(store.imageUrl) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isLoading by remember(store.imageUrl) { mutableStateOf(false) }
    
    LaunchedEffect(store.imageUrl) {
        if (store.imageUrl.isNotEmpty() && storeImageBitmap == null) {
            // Só carregar se ainda não temos a imagem
            val normalizedUrl = ImageLoader.normalizeUrl(store.imageUrl)
            val cachedBitmap = ImageCache.get(normalizedUrl)
            
            if (cachedBitmap != null) {
                storeImageBitmap = cachedBitmap
                isLoading = false
            } else {
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
    
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(145.dp + statusBarHeight)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ),
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
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
                    .size(70.dp)
                    .shadow(4.dp, CircleShape)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (storeImageBitmap != null) {
                        Image(
                            bitmap = storeImageBitmap!!.asImageBitmap(),
                            contentDescription = "Logo ${store.name}",
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
            
            Spacer(modifier = Modifier.width(19.dp))
            
            // Informações da loja
            Column {
                Text(
                    text = store.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = store.category,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF7D7D7D),
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun CartTitle(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_arrow),
                contentDescription = "Voltar",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = "Sacola",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = LineCutRed,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
private fun CartItemsHeader(
    onClearCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Itens adicionados",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF515050),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        )
        
        Text(
            text = "Limpar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LineCutRed,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            modifier = Modifier.clickable { onClearCartClick() }
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onAddItem: () -> Unit,
    onRemoveItem: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Carregar imagem com cache - busca inteligente para evitar placeholder
    val initialBitmap = if (item.imageUrl.isNotEmpty()) {
        ImageCache.findByPath(item.imageUrl)
    } else null
    
    var itemImageBitmap by remember(item.imageUrl) { mutableStateOf<Bitmap?>(initialBitmap) }
    var isLoading by remember(item.imageUrl) { mutableStateOf(false) }
    
    LaunchedEffect(item.imageUrl) {
        if (item.imageUrl.isNotEmpty() && itemImageBitmap == null) {
            // Só carregar se ainda não temos a imagem
            val normalizedUrl = ImageLoader.normalizeUrl(item.imageUrl)
            val cachedBitmap = ImageCache.get(normalizedUrl)
            
            if (cachedBitmap != null) {
                itemImageBitmap = cachedBitmap
                isLoading = false
            } else {
                isLoading = true
                val bitmap = ImageLoader.loadImage(item.imageUrl)
                itemImageBitmap = bitmap
                isLoading = false
            }
        } else if (item.imageUrl.isEmpty()) {
            itemImageBitmap = null
            isLoading = false
        }
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem do produto
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (itemImageBitmap != null) {
                        Image(
                            bitmap = itemImageBitmap!!.asImageBitmap(),
                            contentDescription = "Imagem ${item.name}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = LineCutRed
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Informações do produto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
                
                Text(
                    text = "R$ ${String.format("%.2f", item.price).replace(".", ",")}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF515050),
                        fontSize = 13.sp
                    )
                )
            }
            
            // Controles de quantidade
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Botão remover
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = LineCutRed,
                            shape = CircleShape
                        )
                        .shadow(3.dp, CircleShape)
                        .clickable { onRemoveItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Remover",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
                
                // Quantidade
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(12.dp)
                )
                
                // Botão adicionar
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .background(
                            color = LineCutRed,
                            shape = CircleShape
                        )
                        .shadow(3.dp, CircleShape)
                        .clickable { onAddItem() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Adicionar",
                        tint = Color.White,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddMoreItemsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .width(170.dp)
            .height(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(19.84.dp),
        border = androidx.compose.foundation.BorderStroke(0.94.dp, LineCutRed),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
    ) {
        Text(
            text = "Adicionar mais itens",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = LineCutRed,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun CartSummary(
    total: Double,
    itemCount: Int,
    onContinueClick: () -> Unit,
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
        
        // Botão Continuar
        Button(
            onClick = onContinueClick,
            colors = ButtonDefaults.buttonColors(containerColor = LineCutRed),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .width(144.dp)
                .height(28.1.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Continuar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
        }
    }
}

// Função auxiliar para dados de exemplo
private fun getSampleCartItems(): List<CartItem> = listOf(
    CartItem(
        id = "1",
        name = "Açaí",
        price = 11.90,
        quantity = 1,
        imageRes = R.drawable.acai
    ),
    CartItem(
        id = "2",
        name = "Pizza",
        price = 20.00,
        quantity = 2,
        imageRes = R.drawable.pizza
    ),
    CartItem(
        id = "3",
        name = "Coca-cola",
        price = 5.00,
        quantity = 1,
        imageRes = R.drawable.coca_cola
    ),
    CartItem(
        id = "4",
        name = "Suco",
        price = 5.00,
        quantity = 1,
        imageRes = R.drawable.suco
    )
)

// Previews
@Preview(
    name = "Cart Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CartScreenPreview() {
    LineCutTheme {
        CartScreen(
            store = Store(
                id = "1",
                name = "Burguer Queen",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m",
                imageRes = R.drawable.burger_queen
            ),
            cartItems = getSampleCartItems()
        )
    }
}

@Preview(
    name = "Cart Item Card",
    showBackground = true
)
@Composable
fun CartItemCardPreview() {
    LineCutTheme {
        CartItemCard(
            item = CartItem(
                id = "1",
                name = "Açaí",
                price = 11.90,
                quantity = 1
            ),
            onAddItem = {},
            onRemoveItem = {}
        )
    }
}

@Preview(
    name = "Empty Cart",
    showBackground = true
)
@Composable
fun EmptyCartScreenPreview() {
    LineCutTheme {
        CartScreen(
            store = Store(
                id = "1",
                name = "Burguer Queen",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m"
            ),
            cartItems = emptyList()
        )
    }
}
