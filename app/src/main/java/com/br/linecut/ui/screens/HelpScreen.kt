package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

data class HelpQuestion(
    val question: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBackClick: () -> Unit = {},
    onHowToOrderClick: () -> Unit = {},
    onTrackOrderClick: () -> Unit = {},
    onCancelOrderClick: () -> Unit = {},
    onNotPickedUpClick: () -> Unit = {},
    onContactSupportClick: () -> Unit = {},
    onFAQClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val helpQuestions = listOf(
        HelpQuestion(
            question = "Como fazer um pedido?",
            onClick = onHowToOrderClick
        ),
        HelpQuestion(
            question = "Como acompanhar meu pedido?",
            onClick = onTrackOrderClick
        ),
        HelpQuestion(
            question = "Como cancelar um pedido?",
            onClick = onCancelOrderClick
        ),
        HelpQuestion(
            question = "O que fazer se meu pedido não for retirado?",
            onClick = onNotPickedUpClick
        ),
        HelpQuestion(
            question = "Contato com o suporte?",
            onClick = onContactSupportClick
        ),
        HelpQuestion(
            question = "Dúvidas frequentes (FAQ)",
            onClick = onFAQClick
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header seguindo o design do Figma - 206dp de altura
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
                    .padding(start = 24.dp, top = 60.dp)
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
                    .align(Alignment.CenterStart)
                    .padding(start = 60.dp, top = 20.dp)
            )
        }

        // Lista de perguntas - seguindo o design do Figma com espaçamento menor
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(LineCutDesignSystem.screenBackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))
            
            helpQuestions.forEachIndexed { index, question ->
                HelpQuestionRow(
                    question = question.question,
                    onClick = question.onClick,
                    modifier = Modifier.fillMaxWidth()
                )
                
                if (index < helpQuestions.size - 1) {
                    Spacer(modifier = Modifier.height(9.dp))
                    
                    // Divider line
                    HorizontalDivider(
                        color = Color(0xFFE0E0E0),
                        thickness = 0.5.dp,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(9.dp))
                }
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
private fun HelpQuestionRow(
    question: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .height(36.dp), // Altura baseada no espaçamento do Figma
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(25.dp))
        
        Text(
            text = question,
            fontSize = 13.67.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = Modifier.weight(1f)
        )
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = LineCutRed,
            modifier = Modifier
                .size(12.dp)
                .padding(end = 12.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HelpScreenPreview() {
    LineCutTheme {
        HelpScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun HelpQuestionRowPreview() {
    LineCutTheme {
        HelpQuestionRow(
            question = "Como fazer um pedido?",
            onClick = {}
        )
    }
}
