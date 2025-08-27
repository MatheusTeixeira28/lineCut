package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and title
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp),
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
                    
                    Spacer(modifier = Modifier.width(126.dp))
                    
                    Text(
                        text = "Ajuda",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LineCutRed
                    )
                }
            }
            
            // White band section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(Color.White)
            )
            
            // Main content - Questions list
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 60.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                helpQuestions.forEachIndexed { index, question ->
                    HelpQuestionRow(
                        question = question.question,
                        onClick = question.onClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (index < helpQuestions.size - 1) {
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        // Divider line
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
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
private fun HelpQuestionRow(
    question: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
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
            modifier = Modifier.size(24.dp)
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
