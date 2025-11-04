package com.br.linecut.ui.screens.profile.help

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header seguindo o design do Figma - mesmo header do HelpScreen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            // Botão voltar - posição baseada no Figma
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 16.dp)
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_arrow),
                    contentDescription = "Voltar",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "Ajuda" - centralizado horizontalmente como no Figma
            Text(
                text = "Ajuda",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 50.dp, end = 34.dp, bottom = 16.dp),
            )
        }

        // Conteúdo da tela - seguindo o design do Figma
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 126.dp) // Padding para não sobrepor o header
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp)
                .padding(bottom = 80.dp) // Bottom padding para o navigation
        ) {
            Spacer(modifier = Modifier.height(26.dp))
            
            // Título "Como acompanhar meu pedido?" - posição baseada no Figma
            Text(
                text = "Como acompanhar meu pedido?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = LineCutRed,
                modifier = Modifier.padding(start = 11.43.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Linha divisória abaixo do título
            HorizontalDivider(
                color = Color(0xFFE0E0E0),
                thickness = 1.dp,
                modifier = Modifier
                    .width(145.dp)
                    .padding(start = 11.43.dp)
            )
            
            Spacer(modifier = Modifier.height(28.dp))
            
            // Conteúdo da explicação
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 23.dp, end = 23.dp)
            ) {
                // Texto introdutório
                Text(
                    text = "Após fazer o pedido, vá até a aba \"Pedidos\".\nVocê verá o status atualizado em tempo real, incluindo:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7D7D7D),
                    textAlign = TextAlign.Justify,
                    lineHeight = 18.sp,
                    modifier = Modifier.width(306.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Lista de itens com bullets
                TrackOrderBulletItem(text = "Posição na fila")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TrackOrderBulletItem(text = "Tempo estimado de preparo")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TrackOrderBulletItem(text = "Notification sobre o andamento (ex: \"em preparo\", \"pronto para retirada\")")
            }
        }

        // Bottom navigation
        LineCutBottomNavigationBar(
            selectedItem = NavigationItem.PROFILE,
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
private fun TrackOrderBulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Bullet point
        Text(
            text = "•",
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = Modifier.padding(end = 8.dp)
        )
        
        // Texto do item
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            textAlign = TextAlign.Justify,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackOrderScreenPreview() {
    LineCutTheme {
        TrackOrderScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun TrackOrderBulletItemPreview() {
    LineCutTheme {
        TrackOrderBulletItem(
            text = "Posição na fila"
        )
    }
}