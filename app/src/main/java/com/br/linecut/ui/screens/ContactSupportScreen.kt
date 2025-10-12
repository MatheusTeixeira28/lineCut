package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
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
fun ContactSupportScreen(
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
            
            // Título "Contato com o suporte" - posição baseada no Figma
            Text(
                text = "Contato com o suporte",
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
                // Texto inicial
                Text(
                    text = "Precisa de ajuda? Estamos aqui:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF7D7D7D),
                    textAlign = TextAlign.Justify,
                    lineHeight = 18.sp,
                    modifier = Modifier.width(318.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Texto "E-mail:" normal + endereço copiável
                Row(
                    modifier = Modifier.width(318.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Texto "E-mail:" normal (não clicável)
                    Text(
                        text = "E-mail: ",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7D7D7D),
                        lineHeight = 18.sp
                    )
                    
                    // Endereço de email copiável
                    val annotatedText = buildAnnotatedString {
                        pushStringAnnotation(tag = "EMAIL", annotation = "suporte@linecut.app.br")
                        withStyle(
                            style = SpanStyle(
                                color = LineCutRed,
                                fontWeight = FontWeight.Medium
                            )
                        ) {
                            append("suporte@linecut.app.br")
                        }
                        pop()
                    }
                    
                    ClickableText(
                        text = annotatedText,
                        style = androidx.compose.ui.text.TextStyle(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = LineCutRed,
                            lineHeight = 18.sp
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "EMAIL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    // Aqui poderia copiar o email para clipboard
                                    // ClipboardManager para copiar o endereço
                                }
                        }
                    )
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

@Preview(showBackground = true)
@Composable
fun ContactSupportScreenPreview() {
    LineCutTheme {
        ContactSupportScreen()
    }
}