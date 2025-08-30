package com.br.linecut.ui.screens

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

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val imageRes: Int = android.R.drawable.ic_menu_gallery,
    var quantity: Int = 1
)

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
                store = store,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Título e botão voltar
            CartTitle(
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
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
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Lista de itens
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
                    .padding(top = 10.dp, bottom = 12.dp),
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(206.dp)
            .offset(y = (-18).dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 35.dp, top = 76.dp, end = 35.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo circular da loja
            Card(
                shape = CircleShape,
                modifier = Modifier
                    .size(70.dp)
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
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Voltar",
                tint = LineCutRed,
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
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = "Imagem ${item.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
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
                .height(28.dp),
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
        quantity = 1
    ),
    CartItem(
        id = "2",
        name = "Pizza",
        price = 20.00,
        quantity = 2
    ),
    CartItem(
        id = "3",
        name = "Coca-cola",
        price = 5.00,
        quantity = 1
    ),
    CartItem(
        id = "4",
        name = "Suco",
        price = 5.00,
        quantity = 1
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
