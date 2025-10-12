package com.br.linecut.ui.screens

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
fun HowToOrderScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header seguindo o design do Figma - mesmo header do HelpScreen
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
                .fillMaxWidth()
                .weight(1f)
                .background(LineCutDesignSystem.screenBackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))
            
            // Título "Como fazer um pedido?" - posição baseada no Figma
            Text(
                text = "Como fazer um pedido?",
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
            
            // Lista de instruções numeradas
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                InstructionItem(
                    number = "1.",
                    text = "Acesse a lanchonete desejada na lista."
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InstructionItem(
                    number = "2.",
                    text = "Escolha os itens do cardápio e adicione ao carrinho."
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InstructionItem(
                    number = "3.",
                    text = "Finalize o pedido e aguarde a confirmação."
                )
            }
            
            Spacer(modifier = Modifier.height(60.dp)) // Espaço para o bottom navigation
        }

        // Bottom navigation
        LineCutBottomNavigationBar(
            selectedItem = NavigationItem.PROFILE,
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun InstructionItem(
    number: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Número da instrução - posição baseada no Figma
        Text(
            text = number,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LineCutRed,
            modifier = Modifier.padding(start = 19.dp, end = 28.dp)
        )
        
        // Texto da instrução - posição baseada no Figma
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            textAlign = TextAlign.Justify,
            modifier = Modifier.width(291.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HowToOrderScreenPreview() {
    LineCutTheme {
        HowToOrderScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun InstructionItemPreview() {
    LineCutTheme {
        InstructionItem(
            number = "1.",
            text = "Acesse a lanchonete desejada na lista."
        )
    }
}