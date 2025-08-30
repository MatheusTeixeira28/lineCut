package com.br.linecut.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodePixScreen(
    totalAmount: Double = 39.90,
    onBackClick: () -> Unit = {},
    onFinishPaymentClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header simples baseado no padrão das outras telas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp) // Mesma altura da NotificationsScreen
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
                    .padding(start = 24.dp, top = 60.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = LineCutRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "QR Code PIX"
            Text(
                text = "QR Code PIX",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 60.dp, top = 20.dp)
            )
        }

        // Conteúdo principal
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(LineCutDesignSystem.screenBackgroundColor)
        ) {
            // Texto "Escaneie para pagar em seu banco"
            Text(
                text = "Escaneie para pagar em seu banco",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF515050),
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 70.dp, top = 86.dp, end = 70.dp)
            )
            
            // Espaço para QR Code - centralizado
            Box(
                modifier = Modifier
                    .size(236.dp)
                    .padding(top = 202.dp) // 328dp - 126dp = 202dp
                    .align(Alignment.TopCenter)
                    .background(
                        Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "QR CODE\nPlaceholder",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF9E9E9E),
                        fontSize = 16.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
            
            // Card com valor a ser pago - centralizado
            Box(
                modifier = Modifier
                    .padding(top = 531.dp) // 657dp - 126dp = 531dp
                    .width(339.dp)
                    .height(97.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = 1.dp, // Apenas linha vermelha, sem sombra
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
                        text = "Valor a ser pago:",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "R$ ${String.format("%.2f", totalAmount).replace(".", ",")}",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 32.sp
                        )
                    )
                }
            }
        }
        
        // Área inferior com shadow e botão
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
                .shadow(
                    elevation = 4.dp,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onFinishPaymentClick,
                colors = ButtonDefaults.buttonColors(containerColor = LineCutRed),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(343.dp)
                    .height(28.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Finalizar pagamento",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

// Preview
@Preview(
    name = "QR Code PIX Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun QRCodePixScreenPreview() {
    LineCutTheme {
        QRCodePixScreen(
            totalAmount = 39.90
        )
    }
}
