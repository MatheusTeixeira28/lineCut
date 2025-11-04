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
fun FAQScreen(
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
            
            // Título "Dúvidas frequentes (FAQ)" - posição baseada no Figma
            Text(
                text = "Dúvidas frequentes (FAQ)",
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
            
            // Lista de perguntas e respostas
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                FAQItem(
                    number = "1.",
                    question = "Posso alterar meu pedido depois de enviar?",
                    answer = "Não, após confirmado, ele vai direto para a fila da lanchonete."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                FAQItem(
                    number = "2.",
                    question = "Posso pedir em mais de uma lanchonete ao mesmo tempo?",
                    answer = "Sim, desde que elas estejam disponíveis na praça."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                FAQItem(
                    number = "3.",
                    question = "Recebo notificação quando meu pedido estiver pronto?",
                    answer = "Sim! Você será avisado por push."
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                FAQItem(
                    number = "4.",
                    question = "Como sei que meu pagamento foi aprovado?",
                    answer = "Você receberá a confirmação imediatamente no app."
                )
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
private fun FAQItem(
    number: String,
    question: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        // Número da pergunta - posição baseada no Figma
        Text(
            text = number,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LineCutRed,
            modifier = Modifier.padding(start = 8.dp, end = 19.dp, top = 0.dp)
        )
        
        // Pergunta e resposta
        Column(
            modifier = Modifier.width(318.dp)
        ) {
            // Pergunta em negrito
            Text(
                text = question,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF7D7D7D),
                textAlign = TextAlign.Justify,
                lineHeight = 18.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Resposta normal
            Text(
                text = answer,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF7D7D7D),
                textAlign = TextAlign.Justify,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FAQScreenPreview() {
    LineCutTheme {
        FAQScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun FAQItemPreview() {
    LineCutTheme {
        FAQItem(
            number = "1.",
            question = "Posso alterar meu pedido depois de enviar?",
            answer = "Não, após confirmado, ele vai direto para a fila da lanchonete."
        )
    }
}