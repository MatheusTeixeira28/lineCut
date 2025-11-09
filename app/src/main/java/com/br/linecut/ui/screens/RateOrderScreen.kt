package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.CachedAsyncImage
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme

data class OrderRatingData(
    val orderId: String,
    val orderNumber: String,
    val storeName: String,
    val storeCategory: String,
    val date: String,
    val storeImageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateOrderScreen(
    order: OrderRatingData,
    onBackClick: () -> Unit,
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSubmitRating: (overallRating: Int, qualityRating: Int, speedRating: Int, serviceRating: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estados para as avaliações
    var overallRating by remember { mutableStateOf(0) }
    var qualityRating by remember { mutableStateOf(0) }
    var speedRating by remember { mutableStateOf(0) }
    var serviceRating by remember { mutableStateOf(0) }
    
    Box(
        modifier = modifier
            .statusBarsPadding()
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
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, end = 34.dp, bottom = 16.dp),
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
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = "Avalie seu pedido",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LineCutRed
                    )
                }
            }
        }
        
        // Main content with scrolling
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 141.dp, bottom = 80.dp)
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
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Store image
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .shadow(4.41.dp, CircleShape)
                                .clip(CircleShape)
                                .background(Color(0xFF8B4513)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (order.storeImageUrl.isNotEmpty()) {
                                CachedAsyncImage(
                                    imageUrl = order.storeImageUrl,
                                    contentDescription = "Logo ${order.storeName}",
                                    modifier = Modifier.size(44.dp),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = order.storeName.firstOrNull()?.uppercase() ?: "L",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(14.dp))
                        
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = order.storeName,
                                fontSize = 16.54.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF515050).copy(alpha = 0.85f)
                            )
                            Text(
                                text = order.storeCategory,
                                fontSize = 14.33.sp,
                                color = Color(0xFF515050).copy(alpha = 0.85f)
                            )
                        }
                        
                        Text(
                            text = order.date,
                            fontSize = 11.66.sp,
                            color = Color(0xFF515050)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Order number
                    Text(
                        text = "Pedido nº ${order.orderNumber}",
                        fontSize = 11.sp,
                        color = Color(0xFF515050).copy(alpha = 0.85f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Subtitle
            Text(
                text = "Sua opinião é muito importante para melhorarmos!",
                fontSize = 13.sp,
                color = Color(0xFF515050),
                modifier = Modifier.padding(horizontal = 10.dp)
            )
            
            Spacer(modifier = Modifier.height(36.dp))
            
            // Overall rating section
            RatingSection(
                title = "O que achou do pedido?",
                subtitle = "Escolha de 1 a 5 estrelas para classificar",
                rating = overallRating,
                onRatingChange = { overallRating = it },
                starSize = 35.dp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Quality rating section
            RatingSection(
                title = "Qualidade do produto",
                subtitle = "Estava bem preparado, saboroso, embalado corretamente.",
                rating = qualityRating,
                onRatingChange = { qualityRating = it },
                starSize = 30.dp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Speed rating section
            RatingSection(
                title = "Velocidade de entrega",
                subtitle = "Tempo de espera, agilidade no preparo.",
                rating = speedRating,
                onRatingChange = { speedRating = it },
                starSize = 30.dp
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Service rating section
            RatingSection(
                title = "Atendimento",
                subtitle = "Comunicação, atenção aos detalhes, resolução de dúvidas",
                rating = serviceRating,
                onRatingChange = { serviceRating = it },
                starSize = 30.dp
            )
            
            Spacer(modifier = Modifier.height(56.dp))
            
            // Submit button
            Button(
                onClick = {
                    onSubmitRating(overallRating, qualityRating, speedRating, serviceRating)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LineCutRed
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(0.dp),
                enabled = overallRating > 0 // Apenas habilitar se houver avaliação geral
            ) {
                Text(
                    text = "Finalizar avaliação",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
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
private fun RatingSection(
    title: String,
    subtitle: String,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    starSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF515050)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = Color(0xFF515050),
            modifier = Modifier.padding(start = 5.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Stars
        Row(
            modifier = Modifier.padding(start = 1.dp),
            horizontalArrangement = Arrangement.spacedBy(
                if (starSize == 35.dp) 2.dp else 1.dp
            )
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (index < rating) Color(0xFFF2C12E) else Color(0xFFD1D1D1),
                    modifier = Modifier
                        .size(starSize)
                        .clickable { onRatingChange(index + 1) }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RateOrderScreenPreview() {
    LineCutTheme {
        RateOrderScreen(
            order = OrderRatingData(
                orderId = "1",
                orderNumber = "#1025",
                storeName = "Burger Queen",
                storeCategory = "Lanches e Salgados",
                date = "24/04/2025",
                storeImageUrl = ""
            ),
            onBackClick = {},
            onHomeClick = {},
            onSearchClick = {},
            onNotificationClick = {},
            onOrdersClick = {},
            onProfileClick = {},
            onSubmitRating = { _, _, _, _ -> }
        )
    }
}
