package com.br.linecut.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*

// Data classes para order details
data class OrderDetailItem(
    val name: String,
    val quantity: Int,
    val price: Double
)

data class OrderDetail(
    val orderId: String,
    val storeName: String,
    val storeType: String,
    val date: String,
    val status: String,
    val items: List<OrderDetailItem>,
    val total: Double,
    val paymentMethod: String,
    val pickupLocation: String,
    val rating: Int? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    order: OrderDetail,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onRateOrderClick: () -> Unit,
    onAddToCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header with back button and title
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp),
            shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 34.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar",
                        tint = LineCutRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(34.dp))
                
                Text(
                    text = "Detalhes do pedido",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = LineCutRed
                )
            }
        }
        
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 141.dp, bottom = 60.dp)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Store information card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Store image placeholder
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(14.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = order.storeName,
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF515050).copy(alpha = 0.85f)
                            )
                            Text(
                                text = order.storeType,
                                fontSize = 14.3.sp,
                                color = Color(0xFF515050).copy(alpha = 0.85f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pedido nº ${order.orderId}",
                                fontSize = 11.sp,
                                color = Color(0xFF515050).copy(alpha = 0.85f)
                            )
                        }
                        
                        Text(
                            text = order.date,
                            fontSize = 11.66.sp,
                            color = Color(0xFF515050)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Status badge
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFB9B9B9).copy(alpha = 0.14f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = order.status,
                                fontSize = 11.66.sp,
                                color = Color(0xFF515050)
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Order progress tracker for in-progress orders
            if (order.status != "Pedido concluído") {
                OrderProgressTracker(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Order summary
            Text(
                text = "Resumo do pedido",
                fontSize = 13.23.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF515050)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Order items
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        fontSize = 12.13.sp,
                        color = Color(0xFF515050),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${item.quantity}x",
                        fontSize = 12.13.sp,
                        color = Color(0xFF515050)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "R$ %.2f".format(item.price),
                        fontSize = 12.13.sp,
                        color = Color(0xFF515050)
                    )
                }
                Divider(
                    color = Color(0xFFE0E0E0),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontSize = 13.23.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF515050)
                )
                Text(
                    text = "R$ %.2f".format(order.total),
                    fontSize = 13.23.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF515050)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Payment method
            Text(
                text = "Pagamento",
                fontSize = 13.23.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF515050)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = Color(0xFF515050),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.paymentMethod,
                    fontSize = 13.23.sp,
                    color = Color(0xFF515050).copy(alpha = 0.85f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Pickup location
            Text(
                text = "Local de retirada",
                fontSize = 13.23.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF515050)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = null,
                    tint = Color(0xFF515050),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = order.pickupLocation,
                    fontSize = 13.23.sp,
                    color = Color(0xFF515050).copy(alpha = 0.85f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Rating section (only if order is completed)
            if (order.status == "Pedido concluído") {
                Text(
                    text = "Sua Avaliação",
                    fontSize = 13.23.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF515050)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StarRating(rating = order.rating ?: 0)
                    
                    OutlinedButton(
                        onClick = onRateOrderClick,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF959595)
                        ),
                        modifier = Modifier.height(21.dp)
                    ) {
                        Text(
                            text = "Avaliar pedido",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Add to cart button
            OutlinedButton(
                onClick = onAddToCartClick,
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LineCutRed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(23.dp)
            ) {
                Text(
                    text = "Adicionar à sacola",
                    fontSize = 13.23.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
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
private fun StarRating(
    rating: Int,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

// Sample data for preview
fun getSampleOrderDetail() = OrderDetail(
    orderId = "#1020",
    storeName = "Museoh",
    storeType = "Lanches e Salgados",
    date = "24/04/2025",
    status = "Pedido concluído",
    items = listOf(
        OrderDetailItem("Açaí", 1, 11.90),
        OrderDetailItem("Pizza", 2, 20.00),
        OrderDetailItem("Coca-cola", 1, 5.00),
        OrderDetailItem("Suco", 1, 6.00)
    ),
    total = 39.90,
    paymentMethod = "PIX",
    pickupLocation = "Praça 3 - Senac",
    rating = 5
)

// Previews
@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenPreview() {
    LineCutTheme {
        OrderDetailsScreen(
            order = getSampleOrderDetail(),
            onBackClick = { },
            onHomeClick = { },
            onSearchClick = { },
            onNotificationClick = { },
            onOrdersClick = { },
            onProfileClick = { },
            onRateOrderClick = { },
            onAddToCartClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailsScreenWithoutRatingPreview() {
    LineCutTheme {
        OrderDetailsScreen(
            order = getSampleOrderDetail().copy(
                status = "Em preparo",
                rating = null
            ),
            onBackClick = { },
            onHomeClick = { },
            onSearchClick = { },
            onNotificationClick = { },
            onOrdersClick = { },
            onProfileClick = { },
            onRateOrderClick = { },
            onAddToCartClick = { }
        )
    }
}
// Data classes for progress tracking
data class ProgressStep(
    val title: String,
    val description: String? = null,
    val icon: ImageVector,
    val isCompleted: Boolean,
    val isActive: Boolean = false
)

@Composable
fun OrderProgressTracker(
    modifier: Modifier = Modifier
) {
    val progressSteps = listOf(
        ProgressStep(
            title = "Pedido realizado",
            description = "Seu pedido foi recebido com sucesso!",
            icon = Icons.Default.ShoppingCart,
            isCompleted = true
        ),
        ProgressStep(
            title = "Preparando pedido",
            description = null,
            icon = Icons.Default.Schedule,
            isCompleted = true,
            isActive = true
        ),
        ProgressStep(
            title = "Pronto para retirada",
            description = null,
            icon = Icons.Default.ShoppingBag,
            isCompleted = false
        ),
        ProgressStep(
            title = "Retirado",
            description = null,
            icon = Icons.Default.CheckCircle,
            isCompleted = false
        )
    )
    
    Column(modifier = modifier) {
        // "Acompanhe seu pedido!" section
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "Acompanhe seu pedido!",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF515050)
            )
        }
        
        // Time estimate
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = Color(0xFF666666),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "10 a 15 minutos",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
        
        // Progress steps
        progressSteps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Icon and line column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icon circle
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (step.isCompleted || step.isActive) LineCutRed
                                else Color(0xFFE0E0E0)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = step.icon,
                            contentDescription = null,
                            tint = if (step.isCompleted || step.isActive) Color.White 
                                   else Color(0xFF999999),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    // Connecting line (except for the last item)
                    if (index < progressSteps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(40.dp)
                                .background(
                                    if (progressSteps[index + 1].isCompleted) LineCutRed
                                    else Color(0xFFE0E0E0)
                                )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Text content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp, bottom = if (index < progressSteps.size - 1) 24.dp else 0.dp)
                ) {
                    Text(
                        text = step.title,
                        fontSize = 14.sp,
                        fontWeight = if (step.isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = Color(0xFF515050)
                    )
                    
                    step.description?.let { desc ->
                        Text(
                            text = desc,
                            fontSize = 12.sp,
                            color = Color(0xFF666666),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
