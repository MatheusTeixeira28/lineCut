package com.br.linecut.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// Remove the wildcard import to avoid conflicts with Material3 NavigationBar
// import com.br.linecut.ui.components.*
import com.br.linecut.ui.theme.*

data class Order(
    val id: String,
    val orderNumber: String,
    val date: String,
    val storeName: String,
    val storeCategory: String,
    val status: OrderStatus,
    val total: Double,
    val rating: Int? = null,
    val canRate: Boolean = true
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
    modifier: Modifier = Modifier
) {
    val orders = remember { getSampleOrders() }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(206.dp)
                    .offset(y = (-80).dp)
            ) {
                // White rounded background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(206.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(30.dp)
                        )
                )
                
                // Title
                Text(
                    text = "Meus Pedidos",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LineCutRed,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = 80.dp)
                )
            }
            
            // Orders List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 18.dp)
                    .offset(y = (-80).dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                items(orders) { order ->
                    OrderCard(
                        order = order,
                        onDetailsClick = {
                            onOrderClick(order)
                        }
                    )
                }
            }
        }
        
        // Bottom Navigation
        LineCutBottomNavigationBar(
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(165.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(11.dp)
            ),
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Date (top left)
            Text(
                text = order.date,
                fontSize = 12.sp,
                color = Color(0xFF515050),
                modifier = Modifier.align(Alignment.TopStart)
            )
            
            // Status indicator (top right)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                val (statusText, statusColor, statusIcon) = when (order.status) {
                    OrderStatus.IN_PROGRESS -> Triple("Em andamento", Color(0xFFFFA500), Icons.Default.Schedule)
                    OrderStatus.COMPLETED -> Triple("Pedido concluído", Color(0xFF4CAF50), Icons.Default.CheckCircle)
                    OrderStatus.CANCELLED -> Triple("Cancelado", Color(0xFFF44336), Icons.Default.Cancel)
                }
                
                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(10.dp)
                )
                
                Spacer(modifier = Modifier.width(4.dp))
                
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    color = Color(0xFF515050)
                )
            }
            
            // Store info (middle section)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = (-10).dp)
            ) {
                // Restaurant image
                Box(
                    modifier = Modifier
                        .size(37.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8B4513))
                        .shadow(4.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = order.storeName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF515050).copy(alpha = 0.85f)
                    )
                    Text(
                        text = order.storeCategory,
                        fontSize = 13.sp,
                        color = Color(0xFF515050).copy(alpha = 0.85f)
                    )
                }
            }
            
            // Order number (below store info)
            Text(
                text = "Pedido nº ${order.orderNumber}",
                fontSize = 11.sp,
                color = Color(0xFF515050).copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = 25.dp)
            )
            
            // Divider
            Divider(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(y = 40.dp)
                    .fillMaxWidth(),
                color = Color(0xFF515050).copy(alpha = 0.3f),
                thickness = 1.dp
            )
            
            // Bottom section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rating section
                Column {
                    Text(
                        text = "Avaliação",
                        fontSize = 12.sp,
                        color = Color(0xFF515050)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    when {
                        order.status == OrderStatus.CANCELLED || !order.canRate -> {
                            Text(
                                text = "Indisponível",
                                fontSize = 10.sp,
                                color = Color(0xFF959595)
                            )
                        }
                        order.rating != null -> {
                            StarRating(rating = order.rating)
                        }
                        else -> {
                            StarRating(rating = 0)
                        }
                    }
                }
                
                // Details button
                OutlinedButton(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, Color(0xFF959595)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF959595)
                    ),
                    modifier = Modifier
                        .height(21.dp)
                        .width(125.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Detalhes do Pedido",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
                tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
fun LineCutBottomNavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(LineCutRed)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 37.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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
                    tint = Color.White, // Highlighted since we're on orders screen
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

fun getSampleOrders(): List<Order> {
    return listOf(
        Order(
            id = "1",
            orderNumber = "#1025",
            date = "24 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.IN_PROGRESS,
            total = 42.90,
            rating = null,
            canRate = true
        ),
        Order(
            id = "2",
            orderNumber = "#1024",
            date = "24 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.IN_PROGRESS,
            total = 35.50,
            rating = null,
            canRate = true
        ),
        Order(
            id = "3",
            orderNumber = "#1020",
            date = "13 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.COMPLETED,
            total = 39.90,
            rating = 5,
            canRate = true
        ),
        Order(
            id = "4",
            orderNumber = "#1013",
            date = "10 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.CANCELLED,
            total = 28.75,
            rating = null,
            canRate = false
        ),
        Order(
            id = "5",
            orderNumber = "#1013",
            date = "5 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.COMPLETED,
            total = 55.80,
            rating = 5,
            canRate = true
        ),
        Order(
            id = "6",
            orderNumber = "#1013",
            date = "2 Abril 2025",
            storeName = "Museoh",
            storeCategory = "Lanches e Salgados",
            status = OrderStatus.COMPLETED,
            total = 31.20,
            rating = 5,
            canRate = true
        )
    )
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
                canRate = true
            ),
            onDetailsClick = {}
        )
    }
}

@Preview(
    name = "Order Card - Completed",
    showBackground = true
)
@Composable
fun OrderCardCompletedPreview() {
    LineCutTheme {
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
                canRate = true
            ),
            onDetailsClick = {}
        )
    }
}

@Preview(
    name = "Order Card - Cancelled",
    showBackground = true
)
@Composable
fun OrderCardCancelledPreview() {
    LineCutTheme {
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
                canRate = false
            ),
            onDetailsClick = {}
        )
    }
}
