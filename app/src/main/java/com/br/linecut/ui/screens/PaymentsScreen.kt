package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.LineCutTheme

@Composable
fun PaymentsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top section with title and back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp)
                .offset(y = (-73).dp)
        ) {
            // White rounded background with shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(206.dp)
                    .offset(x = (-8).dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = Color.Black.copy(alpha = 0.25f),
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {}
            
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .offset(x = 34.dp, y = 88.dp)
                    .size(20.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF9C0202),
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Title
            Text(
                text = "Pagamentos",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9C0202),
                modifier = Modifier.offset(x = 80.dp, y = 82.dp)
            )
        }
        
        // White stripe background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .offset(y = 123.dp)
                .background(Color.White)
        )
        
        // History section title
        Text(
            text = "Histórico de Transação",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9C0202),
            modifier = Modifier.offset(x = 30.dp, y = 157.dp)
        )
        
        // Scrollable payments list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(651.dp)
                .offset(x = 2.5.dp, y = 189.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(29.dp))
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 39,90",
                paymentMethod = "PIX",
                orderNumber = "1024",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "20/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 39,90",
                paymentMethod = "PIX",
                orderNumber = "1024",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "24/04/2025"
            )
            
            PaymentDivider()
            
            PaymentItem(
                storeName = "Museoh",
                amount = "R$ 19,90",
                paymentMethod = "Pago na entrega",
                orderNumber = "950",
                date = "24/04/2025"
            )
        }
        
        // Bottom navigation
        LineCutBottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun PaymentItem(
    storeName: String,
    amount: String,
    paymentMethod: String,
    orderNumber: String,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Store indicator dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(Color(0xFFD3D3D3), CircleShape)
                .padding(end = 16.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Payment details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = storeName,
                fontSize = 13.67.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7D7D7D)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = paymentMethod,
                fontSize = 11.96.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB3B3B3)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "Pedido nº $orderNumber",
                fontSize = 11.96.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB3B3B3)
            )
        }
        
        // Amount and date
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = amount,
                fontSize = 12.81.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7D7D7D)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = date,
                fontSize = 11.11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFB3B3B3)
            )
        }
    }
}

@Composable
private fun PaymentDivider() {
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(
        color = Color(0xFFE0E0E0),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true)
@Composable
fun PaymentsScreenPreview() {
    LineCutTheme {
        PaymentsScreen()
    }
}

@Preview(showBackground = true, name = "Payment Item Preview")
@Composable
fun PaymentItemPreview() {
    LineCutTheme {
        PaymentItem(
            storeName = "Museoh",
            amount = "R$ 39,90",
            paymentMethod = "PIX",
            orderNumber = "1024",
            date = "24/04/2025"
        )
    }
}
