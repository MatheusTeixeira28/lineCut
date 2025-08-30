package com.br.linecut.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    orderNumber: String = "#1024",
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
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header identical to QRCodePixScreen - same proportions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .background(
                    LineCutDesignSystem.screenBackgroundColor,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
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
                    text = "QR de Retirada",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Conteúdo principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(LineCutDesignSystem.screenBackgroundColor)
        ) {
            // Texto "Apresente este QR Code no balcão"
            Text(
                text = "Apresente este QR Code no balcão",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF515050),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 72.dp, top = 89.dp, end = 72.dp) // 215px - 126px = 89dp
            )
            
            // QR Code - 236x236 centralizado (mesmo do QRCodePixScreen)
            Box(
                modifier = Modifier
                    .size(236.dp)
                    .offset(y = 213.dp) // 339px - 126px = 213dp
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qr_code),
                    contentDescription = "QR Code de Retirada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Card com número do pedido - 339x97 centralizado
            Box(
                modifier = Modifier
                    .offset(y = 550.dp) // 676px - 126px = 550dp
                    .width(339.dp)
                    .height(97.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = LineCutRed,
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Número do pedido:",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = orderNumber,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 32.sp
                        )
                    )
                }
            }
        }
        
        // Bottom Navigation - seguindo o padrão especificado
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
            orderNumber = "#1024"
        )
    }
}
