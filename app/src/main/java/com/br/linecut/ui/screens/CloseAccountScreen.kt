package com.br.linecut.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

@Composable
fun CloseAccountScreen(
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onConfirmCloseClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header com fundo branco e sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(133.dp) // 206px - 73px = 133dp
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
            // Botão voltar
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = 24.dp, top = 50.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = LineCutRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "Encerrar sua conta"
            Text(
                text = "Encerrar sua conta",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp // Conforme CSS: text-[22px]
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 30.dp, top = 10.dp) // left-[30px] conforme CSS
            )
        }

        // Conteúdo da tela
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 34.5.dp) // 357px/2 = 178.5, offset de 4.5px = 34.5dp de cada lado
                .background(LineCutDesignSystem.screenBackgroundColor),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(26.dp)) // top-[26px] conforme CSS
            
            // Pergunta principal
            Text(
                text = "Você tem certeza que deseja excluir sua conta?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                    fontSize = 14.sp, // text-[14px] conforme CSS
                    lineHeight = 18.sp // Ajuste para o layout
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 11.dp) // left-[11px] conforme CSS
            )
            
            Spacer(modifier = Modifier.height(30.dp)) // Espaço para a linha divisória
            
            // Texto explicativo - Primeiro parágrafo
            Text(
                text = "Ao confirmar, todos os seus dados pessoais serão removidos de forma permanente, conforme a Lei Geral de Proteção de Dados (LGPD).",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF7D7D7D), // text-[#7d7d7d] conforme CSS
                    fontWeight = FontWeight.Normal, // Poppins:Regular conforme CSS
                    fontSize = 12.sp, // text-[12px] conforme CSS
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Justify, // text-justify conforme CSS
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.57.dp) // left-[24.57px] conforme CSS
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Texto sobre irreversibilidade - Segundo parágrafo
            Text(
                buildAnnotatedString {
                    append("Essa ação é ")
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append("irreversível")
                    pop()
                    append(" e você perderá acesso ao histórico de pedidos, preferências e informações associadas à sua conta.")
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF7D7D7D),
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                textAlign = TextAlign.Justify,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.57.dp)
            )
            
            Spacer(modifier = Modifier.height(48.dp)) // Espaço até os botões
            
            // Botões de ação
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp), // left-6 = 24dp conforme CSS
                horizontalArrangement = Arrangement.spacedBy(24.dp) // Espaço entre botões
            ) {
                // Botão Cancelar (Verde) - #1cb456 conforme CSS
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier
                        .width(144.dp) // w-36 = 144dp conforme CSS
                        .height(20.dp), // h-5 = 20dp conforme CSS
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1CB456) // bg-[#1cb456] conforme CSS
                    ),
                    shape = RoundedCornerShape(20.dp), // rounded-[20px] conforme CSS
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                            fontSize = 14.sp // text-[14px] conforme CSS
                        )
                    )
                }
                
                // Botão Excluir conta (Vermelho) - #9c0202 conforme CSS
                Button(
                    onClick = onConfirmCloseClick,
                    modifier = Modifier
                        .width(144.dp) // w-36 = 144dp conforme CSS
                        .height(20.dp), // h-5 = 20dp conforme CSS
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9C0202) // bg-[#9c0202] conforme CSS (LineCutRed)
                    ),
                    shape = RoundedCornerShape(20.dp), // rounded-[20px] conforme CSS
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Excluir conta",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                            fontSize = 14.sp // text-[14px] conforme CSS
                        )
                    )
                }
            }
        }
    }
}

// Previews
@Preview(
    name = "Close Account Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CloseAccountScreenPreview() {
    LineCutTheme {
        CloseAccountScreen()
    }
}

@Preview(
    name = "Close Account Screen - Dark Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun CloseAccountScreenDarkPreview() {
    LineCutTheme {
        CloseAccountScreen()
    }
}
