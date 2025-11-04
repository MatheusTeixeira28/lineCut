package com.br.linecut.ui.screens.profile.settings

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*

@Composable
fun AccountClosedScreen(
    onLoginClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Conteúdo da tela com padding para não sobrepor o header
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxSize()
                .padding(top = 133.dp) // Padding para não sobrepor o header
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.5.dp) // 357px/2 = 178.5, offset de 4.5px = 34.5dp de cada lado
                .background(LineCutDesignSystem.screenBackgroundColor),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(26.dp)) // top-[26px] conforme CSS
            
            // Subtítulo
            Text(
                text = "Solicitação de Exclusão de Conta",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                    fontSize = 14.sp, // text-[14px] conforme CSS
                    lineHeight = 19.sp // h-[19px] conforme CSS
                ),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .width(254.dp) // w-[254px] conforme CSS
                    .padding(start = 11.dp) // left-[11px] conforme CSS
            )
            
            Spacer(modifier = Modifier.height(27.72.dp)) // top-[53.72px] - 26px = 27.72dp
            
            // Container do conteúdo principal
            Column(
                modifier = Modifier
                    .width(318.dp) // w-[318px] conforme CSS
                    .padding(start = 25.dp) // left-[25px] conforme CSS
            ) {
                // Texto inicial
                Text(
                    text = "Recebemos sua solicitação para exclusão da conta vinculada ao LineCut.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D), // text-[#7d7d7d] conforme CSS
                        fontWeight = FontWeight.Normal, // Poppins:Regular conforme CSS
                        fontSize = 12.sp, // text-[12px] conforme CSS
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify, // text-justify conforme CSS
                    modifier = Modifier.fillMaxWidth()
                )
                
                // "Informamos que:"
                Text(
                    text = "Informamos que:",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Lista de itens com bullet points
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Item 1
                    Text(
                        text = "• Todos os seus dados pessoais associados à conta foram removidos ou anonimizados, conforme previsto pela Lei Geral de Proteção de Dados (LGPD).",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF7D7D7D),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Item 2
                    Text(
                        text = "• O histórico de pedidos e informações de pagamento serão mantidos apenas quando exigido por obrigações legais ou fiscais, por um período determinado.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF7D7D7D),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Item 3
                    Text(
                        text = "• Caso deseje utilizar novamente o LineCut, será necessário criar uma nova conta.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF7D7D7D),
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Texto de confirmação em negrito
                Text(
                    text = "A exclusão foi realizada com sucesso conforme sua solicitação.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Bold, // Poppins:Bold conforme CSS
                        fontSize = 14.sp, // text-[14px] conforme CSS
                        lineHeight = 18.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Texto sobre dúvidas
                Text(
                    text = "Em caso de dúvidas, estamos à disposição pelo",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Email de contato
                Text(
                    buildAnnotatedString {
                        append("e-mail: ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = LineCutRed // #9c0202 conforme CSS
                            )
                        ) {
                            append("privacidade@linecut.app.br")
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Header com fundo branco e sombra - DEVE FICAR POR ÚLTIMO para ficar por cima
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .height(133.dp) // 206px - 73px = 133dp
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            // Título "Encerramento solicitado"
            Text(
                text = "Encerramento solicitado",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp // Conforme CSS: text-[22px]
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 30.dp, end = 34.dp, bottom = 16.dp),
            )
        }
    }
}

// Previews
@Preview(
    name = "Account Closed Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AccountClosedScreenPreview() {
    LineCutTheme {
        AccountClosedScreen()
    }
}

@Preview(
    name = "Account Closed Screen - Dark Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AccountClosedScreenDarkPreview() {
    LineCutTheme {
        AccountClosedScreen()
    }
}
