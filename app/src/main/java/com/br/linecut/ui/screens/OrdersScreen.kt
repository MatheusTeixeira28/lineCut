package com.br.linecut.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
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
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header background (Rectangle 6)
        Box(
            modifier = Modifier
                .width(428.dp)
                .height(206.dp)
                .offset(x = (-8).dp, y = (-80).dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(30.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(30.dp)
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
        
        // Pedidos container
        Box(
            modifier = Modifier
                .width(407.dp)
                .height(725.71.dp)
                .offset(x = 18.dp, y = 147.dp)
        ) {
            // Order cards with exact positioning
            orders.forEachIndexed { index, order ->
                val topOffset = (index * 178).dp // Each card is 165dp + 13dp spacing
                OrderCardAbsolute(
                    order = order,
                    topOffset = topOffset,
                    onDetailsClick = { onOrderClick(order) }
                )
            }
        }
        
        // Bottom Navigation Bar
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
fun OrderCardAbsolute(
    order: Order,
    topOffset: Dp,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Rectangle 6 - Card background
    Box(
        modifier = modifier
            .width(368.dp)
            .height(165.dp)
            .offset(x = (-19.5).dp, y = topOffset)
            .shadow(
                elevation = 4.31.dp,
                shape = RoundedCornerShape(10.77.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.77.dp)
            )
            .clickable { onDetailsClick() }
    ) {
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
            Text(
                text = "M",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Store name "Museoh"
        Text(
            text = order.storeName,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Bold,
            fontSize = 15.07.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier.offset(x = 68.91.dp, y = 41.99.dp)
        )
        
        // Store category "Lanches e Salgados"
        Text(
            text = order.storeCategory,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.92.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier.offset(x = 68.91.dp, y = 61.37.dp)
        )
        
        // Order number
        Text(
            text = "Pedido nº ${order.orderNumber}",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = Color(0xFF515050).copy(alpha = 0.85f),
            modifier = Modifier.offset(x = 139.dp, y = 84.dp)
        )
        
        // Line divider
        Box(
            modifier = Modifier
                .width(347.78.dp)
                .height(1.08.dp)
                .offset(x = 11.84.dp, y = 109.83.dp)
                .background(Color(0xFFB9B9B9).copy(alpha = 0.14f))
        )
        
        // Status indicator - positioned exactly
        val (statusText, statusColor) = when (order.status) {
            OrderStatus.IN_PROGRESS -> Pair("Em andamento", Color(0xFFF2C12E))
            OrderStatus.COMPLETED -> Pair("Pedido concluído", Color(0xFF1CB456))
            OrderStatus.CANCELLED -> Pair("Cancelado", Color(0xFF9C0202))
        }
        
        // Status circle
        Box(
            modifier = Modifier
                .size(14.dp)
                .offset(x = 247.65.dp, y = 11.84.dp)
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
        
        // Status text
        Text(
            text = statusText,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 10.77.sp,
            color = Color(0xFF515050),
            modifier = Modifier.offset(x = 270.dp, y = 14.dp)
        )
        
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
        
        // Details button - positioned exactly
        Box(
            modifier = Modifier
                .width(124.9.dp)
                .height(20.46.dp)
                .offset(x = 226.11.dp, y = 129.21.dp)
                .shadow(2.15.dp, RoundedCornerShape(21.53.dp))
                .background(Color.Transparent, RoundedCornerShape(21.53.dp))
                .border(1.08.dp, Color(0xFF959595), RoundedCornerShape(21.53.dp))
                .clickable { onDetailsClick() },
            contentAlignment = Alignment.Center
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
        Box(modifier = Modifier.size(400.dp, 200.dp)) {
            OrderCardAbsolute(
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
                topOffset = 0.dp,
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
            OrderCardAbsolute(
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
                topOffset = 0.dp,
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
            OrderCardAbsolute(
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
                topOffset = 0.dp,
                onDetailsClick = {}
            )
        }
    }
}
