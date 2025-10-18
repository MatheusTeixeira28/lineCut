package com.br.linecut.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*
import com.br.linecut.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRCodePixScreen(
    totalAmount: Double = 39.90,
    qrCodeBase64: String? = null,
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
                    text = "QR Code PIX",
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
            
        // QR Code - centralizado (Figma: 236x236) preenchendo sem distorcer
            Box(
                modifier = Modifier
                    .size(420.dp)
                    .padding(top = 175.dp) // 328dp - 126dp = 202dp
            .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                // Processar QR Code base64
                val qrBitmap = remember(qrCodeBase64) {
                    if (qrCodeBase64 != null && qrCodeBase64.isNotEmpty()) {
                        try {
                            // Remover prefixo data:image/png;base64, se existir
                            val base64String = if (qrCodeBase64.contains("base64,")) {
                                qrCodeBase64.substringAfter("base64,")
                            } else {
                                qrCodeBase64
                            }
                            
                            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        } catch (e: Exception) {
                            android.util.Log.e("QRCodePixScreen", "Erro ao decodificar base64", e)
                            null
                        }
                    } else {
                        null
                    }
                }
                
                // Exibir QR Code
                if (qrBitmap != null) {
                    Image(
                        bitmap = qrBitmap.asImageBitmap(),
                        contentDescription = "QR Code PIX",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback para imagem padrão
                    Image(
                        painter = painterResource(id = R.drawable.qr_code),
                        contentDescription = "QR Code PIX",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
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
                    ,
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
