package com.br.linecut.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    pixCopiaCola: String? = null,
    onBackClick: () -> Unit = {},
    onFinishPaymentClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Log para debug
    LaunchedEffect(qrCodeBase64, pixCopiaCola) {
        android.util.Log.d("QRCodePixScreen", "qrCodeBase64: ${if (qrCodeBase64.isNullOrEmpty()) "VAZIO" else "${qrCodeBase64.length} chars"}")
        android.util.Log.d("QRCodePixScreen", "pixCopiaCola: ${if (pixCopiaCola.isNullOrEmpty()) "VAZIO" else pixCopiaCola}")
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .background(
                    color = LineCutDesignSystem.screenBackgroundColor,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            // Linha inferior do header com voltar + título
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 18.dp, end = 34.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Pagamento",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conteúdo principal com shadow
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .shadow(
                    elevation = 4.dp,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                
                // Pagamento Total
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pagamento Total",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "R$ ${String.format("%.2f", totalAmount).replace(".", ",")}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Pague em até
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pague em até",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp
                        )
                    )
                    Text(
                        text = "10 minutos",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    )
                }
                
                Spacer(modifier = Modifier.height(30.dp))
                
                // Linha divisória horizontal
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFFE0E0E0))
                )
                
                Spacer(modifier = Modifier.height(30.dp))
                
                // Texto "Escaneie para pagar em seu banco"
                Text(
                    text = "Escaneie para pagar em seu banco",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(36.dp))
                
                // QR Code - sempre mostrar, mesmo que seja null (para debug)
                if (qrCodeBase64 != null) {
                    val qrBitmap = remember(qrCodeBase64) {
                        try {
                            val base64String = if (qrCodeBase64.contains("base64,")) {
                                qrCodeBase64.substringAfter("base64,")
                            } else {
                                qrCodeBase64
                            }
                            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        } catch (e: Exception) {
                            android.util.Log.e("QRCodePixScreen", "Erro ao decodificar QR Code", e)
                            null
                        }
                    }
                    
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR Code PIX",
                            modifier = Modifier.size(236.dp),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        // Se houver erro ao decodificar, mostrar placeholder para debug
                        Box(
                            modifier = Modifier
                                .size(236.dp)
                                .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Erro ao carregar\nQR Code",
                                textAlign = TextAlign.Center,
                                color = Color(0xFF515050)
                            )
                        }
                    }
                } else {
                    // Se não houver QR Code, mostrar placeholder para debug
                    Box(
                        modifier = Modifier
                            .size(236.dp)
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aguardando\nQR Code",
                            textAlign = TextAlign.Center,
                            color = Color(0xFF515050)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(38.dp))
                
                // Código PIX
                Text(
                    text = "Código PIX",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp
                    ),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Box com o código PIX (fundo cinza)
                Box(
                    modifier = Modifier
                        .width(294.dp)
                        .height(35.dp)
                        .background(
                            Color(0xFFD1D1D1),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pixCopiaCola?.take(30)?.plus("...") ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Spacer(modifier = Modifier.height(30.dp))
                
                // Botão Copiar Código PIX
                Button(
                    onClick = {
                        pixCopiaCola?.let { codigo ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Código PIX", codigo)
                            clipboard.setPrimaryClip(clip)
                            android.util.Log.d("QRCodePixScreen", "Código PIX copiado: $codigo")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LineCutDesignSystem.screenBackgroundColor
                    ),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(1.dp, LineCutRed),
                    modifier = Modifier
                        .width(163.dp)
                        .height(24.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Copiar Código PIX",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Normal,
                            fontSize = 16.sp
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
                .shadow(
                    elevation = 4.dp,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onFinishPaymentClick,
                colors = ButtonDefaults.buttonColors(containerColor = LineCutRed),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(343.dp)
                    .height(28.dp),
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
            totalAmount = 39.90,
            pixCopiaCola = "34195.42689 31456.700000 12345.678901 23456 78901234567890"
        )
    }
}
