package com.br.linecut.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
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
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickupQRScreen(
    orderNumber: String,
    storeName: String,
    storeType: String,
    date: String,
    status: String,
    items: List<OrderDetailItem>,
    total: Double,
    paymentMethod: String,
    imageRes: Int,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSearchVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header - Código de Retirada
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp).shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .background(
                    LineCutDesignSystem.screenBackgroundColor,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            // Linha inferior do header com voltar + título
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 34.dp, end = 34.dp, bottom = 16.dp),
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
                    text = "Código de Retirada",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Conteúdo principal com scroll
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(34.dp))
                
                // Store card (Rectangle 6 - igual ao OrderDetailsScreen)
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
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
                                    .clip(CircleShape)
                                    .shadow(
                                        elevation = 4.dp,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = imageRes),
                                    contentDescription = "Logo $storeName",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = storeName,
                                    fontSize = 16.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF515050).copy(alpha = 0.85f)
                                )
                                Text(
                                    text = storeType,
                                    fontSize = 14.sp,
                                    color = Color(0xFF515050).copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Pedido nº $orderNumber",
                                    fontSize = 11.sp,
                                    color = Color(0xFF515050).copy(alpha = 0.85f)
                                )
                            }
                            
                            Text(
                                text = date,
                                fontSize = 11.66.sp,
                                color = Color(0xFF515050)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
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
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA500),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = status,
                                    fontSize = 10.5.sp,
                                    color = Color(0xFF515050)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(44.dp))
                
                // Card grande com número do pedido (bordado em vermelho)
                Card(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .height(208.dp)
                        .border(
                            width = 2.dp,
                            color = LineCutRed,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "APRESENTE ESTE CÓDIGO NO BALCÃO",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LineCutRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = orderNumber,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LineCutRed
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Resumo do pedido
                Column(
                    modifier = Modifier.padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = "Resumo do pedido",
                        fontSize = 13.23.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF515050)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Order items
                    items.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
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
                                color = Color(0xFF515050),
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            
                            Text(
                                text = "R$ %.2f".format(item.price),
                                fontSize = 12.13.sp,
                                color = Color(0xFF515050)
                            )
                        }
                        
                        // Divider
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = Color(0xFFB9B9B9).copy(alpha = 0.3f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Total
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total",
                            fontSize = 13.23.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF515050)
                        )
                        
                        Text(
                            text = "R$ %.2f".format(total),
                            fontSize = 13.23.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF515050)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Pagamento
                Column(
                    modifier = Modifier.padding(horizontal = 29.dp)
                ) {
                    Text(
                        text = "Pagamento",
                        fontSize = 12.98.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF515050)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payment,
                            contentDescription = "PIX",
                            tint = Color(0xFF515050),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Text(
                            text = paymentMethod,
                            fontSize = 12.98.sp,
                            color = Color(0xFF515050).copy(alpha = 0.85f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Bottom Navigation
            LineCutBottomNavigationBar(
                selectedItem = if (isSearchVisible) NavigationItem.SEARCH else NavigationItem.HOME,
                onHomeClick = {
                    if (isSearchVisible) {
                        isSearchVisible = false
                    } else {
                        onHomeClick()
                    }
                },
                onSearchClick = {
                    isSearchVisible = true
                    if (!isSearchVisible) {
                        // This condition will never be true, but keeping the pattern
                    }
                    onSearchClick()
                },
                onNotificationClick = onNotificationClick,
                onOrdersClick = onOrdersClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

// Preview
@Preview(
    name = "Pickup QR Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PickupQRScreenPreview() {
    LineCutTheme {
        PickupQRScreen(
            orderNumber = "#1025",
            storeName = "Burger Queen",
            storeType = "Lanches e Salgados",
            date = "24/04/2025",
            status = "Em andamento",
            items = listOf(
                OrderDetailItem("Açaí", 1, 11.90),
                OrderDetailItem("Pizza", 2, 20.00),
                OrderDetailItem("Coca-cola", 1, 5.00),
                OrderDetailItem("Suco", 1, 6.00)
            ),
            total = 39.90,
            paymentMethod = "PIX",
            imageRes = R.drawable.burger_queen
        )
    }
}
