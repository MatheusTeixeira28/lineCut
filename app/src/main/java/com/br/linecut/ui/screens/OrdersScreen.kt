package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.ui.components.CachedAsyncImage
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.viewmodel.OrderViewModel

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val storeName: String,
    val storeCategory: String,
    val status: OrderStatus,
    val total: Double,
    val rating: Int? = null,
    val canRate: Boolean = true,
    val storeImageUrl: String = ""
)

enum class OrderStatus {
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onOrderClick: (Order) -> Unit = {},
    modifier: Modifier = Modifier,
    orderViewModel: OrderViewModel = viewModel()
) {
    // Carregar pedidos do Firebase quando a tela for aberta
    LaunchedEffect(Unit) {
        orderViewModel.loadUserOrders()
    }
    
    // Observar estados do ViewModel
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val error by orderViewModel.error.collectAsState()
    
    Box(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header background (Rectangle 6)
        Box(
            modifier = Modifier
                .width(430.dp)
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
        
        // Title "Meus Pedidos"
        Text(
            text = "Meus Pedidos",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = LineCutRed,
            modifier = Modifier
                .offset(x = 30.dp, y = 80.dp)
        )
        
        // Pedidos container with LazyColumn for scrolling
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 147.dp, bottom = 80.dp) // Space for header and bottom nav
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Mostrar indicador de carregamento
            if (isLoading && orders.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Carregando pedidos...",
                            color = Color(0xFF959595),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Mostrar mensagem de erro se houver
            if (error != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Erro ao carregar pedidos",
                            color = Color(0xFF9C0202),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Mostrar mensagem se não houver pedidos
            if (!isLoading && orders.isEmpty() && error == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Você ainda não fez nenhum pedido",
                            color = Color(0xFF959595),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            
            // Listar os pedidos
            items(orders) { order ->
                OrderCard(
                    order = order,
                    onDetailsClick = { onOrderClick(order) },
                    modifier = Modifier.padding(bottom = 13.dp)
                )
            }
        }
        
        // Bottom Navigation Bar
        LineCutBottomNavigationBar(
            selectedItem = NavigationItem.ORDERS,
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun OrderCard(
    order: Order,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Card background with dynamic width
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(165.dp)
            .shadow(
                elevation = 4.31.dp,
                shape = RoundedCornerShape(10.77.dp)
            )
            .clickable { onDetailsClick() },
        shape = RoundedCornerShape(10.77.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        // Date - positioned exactly as in CSS
        Text(
            text = order.date,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 11.84.sp,
            color = Color(0xFF515050),
            modifier = Modifier.offset(x = 11.84.dp, y = 9.69.dp)
        )
        
        // Store image - circular with exact positioning
        Box(
            modifier = Modifier
                .size(36.61.dp)
                .offset(x = 18.3.dp, y = 43.07.dp)
                .shadow(4.31.dp, CircleShape)
                .clip(CircleShape)
                .background(Color(0xFF8B4513)),
            contentAlignment = Alignment.Center
        ) {
            if (order.storeImageUrl.isNotEmpty()) {
                CachedAsyncImage(
                    imageUrl = order.storeImageUrl,
                    contentDescription = "Logo ${order.storeName}",
                    modifier = Modifier.size(36.61.dp),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                // Fallback para primeira letra do nome
                Text(
                    text = order.storeName.firstOrNull()?.uppercase() ?: "L",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Store name
        Text(
            text = order.storeName,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 15.07.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier.offset(x = 68.91.dp, y = 41.99.dp)
        )
        
        // Store category
        Text(
            text = order.storeCategory,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.92.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier.offset(x = 68.91.dp, y = 61.37.dp)
        )
        
        // Order number - alinhado à direita
        Text(
            text = "Pedido nº ${order.orderNumber}",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 84.dp, end = 11.dp)
        )
        
        // Line divider
        Box(
            modifier = Modifier
                .width(347.78.dp)
                .height(1.08.dp)
                .offset(x = 11.84.dp, y = 109.83.dp)
                .background(Color(0xFFB9B9B9).copy(alpha = 0.14f))
        )
        
        // Status indicator - positioned at top right dynamically
        val (statusText, statusColor) = when (order.status) {
            OrderStatus.IN_PROGRESS -> Pair("Em andamento", Color(0xFFF2C12E))
            OrderStatus.COMPLETED -> Pair("Pedido concluído", Color(0xFF1CB456))
            OrderStatus.CANCELLED -> Pair("Cancelado", Color(0xFF9C0202))
        }
        
        // Status row at top right
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 14.dp, end = 11.dp)
        ) {
            // Status circle
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .shadow(4.31.dp, CircleShape)
                    .background(statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                when (order.status) {
                    OrderStatus.IN_PROGRESS -> {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(9.69.dp)
                        )
                    }
                    OrderStatus.COMPLETED -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.8.dp)
                        )
                    }
                    OrderStatus.CANCELLED -> {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(7.56.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(6.dp))
            
            // Status text
            Text(
                text = statusText,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = Color(0xFF515050)
            )
        }
        
        // "Avaliação" label
        Text(
            text = "Avaliação",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 11.84.sp,
            color = Color(0xFF515050),
            modifier = Modifier.offset(x = 22.32.dp, y = 116.29.dp)
        )
        
        // Rating stars or "Indisponível"
        if (order.status == OrderStatus.CANCELLED || !order.canRate) {
            Text(
                text = "Indisponível",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                color = Color(0xFF959595),
                modifier = Modifier.offset(x = 21.92.dp, y = 138.dp)
            )
        } else {
            // Star rating with exact positioning
            StarRatingAbsolute(
                rating = order.rating ?: 0,
                modifier = Modifier.offset(x = 19.38.dp, y = 138.77.dp)
            )
        }
        
        // Details button - positioned at bottom right dynamically
        OutlinedButton(
            onClick = onDetailsClick,
            shape = RoundedCornerShape(21.53.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF959595)
            ),
            border = androidx.compose.foundation.BorderStroke(1.08.dp, Color(0xFF959595)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 11.dp, bottom = 13.dp)
                .height(20.46.dp)
        ) {
            Text(
                text = "Detalhes do Pedido",
                fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 10.77.sp,
                color = Color(0xFF959595)
            )
        }
        }
    }
}

@Composable
fun StarRatingAbsolute(
    rating: Int,
    maxStars: Int = 5,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // Star positions based on CSS: left positions are 0, 16.15, 32.3, 48.45, 64.6
        val starPositions = listOf(0.dp, 16.15.dp, 32.3.dp, 48.45.dp, 64.6.dp)
        
        repeat(maxStars) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFF2C12E) else Color(0xFFD1D1D1),
                modifier = Modifier
                    .size(17.23.dp)
                    .offset(x = starPositions[index])
            )
        }
    }
}

@Composable
fun StarRating(
    rating: Int,
    maxStars: Int = 5,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(maxStars) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFF2C12E) else Color(0xFFD1D1D1),
                modifier = Modifier.size(17.23.dp)
            )
        }
    }
}

@Preview(
    name = "Orders Screen - Light",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OrdersScreenPreview() {
    LineCutTheme {
        OrdersScreen()
    }
}

@Preview(
    name = "Order Card - In Progress",
    showBackground = true
)
@Composable
fun OrderCardInProgressPreview() {
    LineCutTheme {
        Box(modifier = Modifier.size(400.dp, 200.dp)) {
            OrderCard(
                order = Order(
                    id = "1",
                    orderNumber = "#1025",
                    date = "24 Abril 2025",
                    storeName = "Museoh",
                    storeCategory = "Lanches e Salgados",
                    status = OrderStatus.IN_PROGRESS,
                    total = 42.90,
                    rating = null,
                    canRate = true,
                    storeImageUrl = ""
                ),
                onDetailsClick = {}
            )
        }
    }
}

@Preview(
    name = "Order Card - Completed",
    showBackground = true
)
@Composable
fun OrderCardCompletedPreview() {
    LineCutTheme {
        Box(modifier = Modifier.size(400.dp, 200.dp)) {
            OrderCard(
                order = Order(
                    id = "2",
                    orderNumber = "#1020",
                    date = "13 Abril 2025",
                    storeName = "Museoh",
                    storeCategory = "Lanches e Salgados",
                    status = OrderStatus.COMPLETED,
                    total = 39.90,
                    rating = 5,
                    canRate = true,
                    storeImageUrl = ""
                ),
                onDetailsClick = {}
            )
        }
    }
}

@Preview(
    name = "Order Card - Cancelled",
    showBackground = true
)
@Composable
fun OrderCardCancelledPreview() {
    LineCutTheme {
        Box(modifier = Modifier.size(400.dp, 200.dp)) {
            OrderCard(
                order = Order(
                    id = "3",
                    orderNumber = "#1013",
                    date = "10 Abril 2025",
                    storeName = "Museoh",
                    storeCategory = "Lanches e Salgados",
                    status = OrderStatus.CANCELLED,
                    total = 28.75,
                    rating = null,
                    canRate = false,
                    storeImageUrl = ""
                ),
                onDetailsClick = {}
            )
        }
    }
}
