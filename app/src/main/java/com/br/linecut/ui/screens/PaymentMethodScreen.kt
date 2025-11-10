package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    selectedPaymentMethod: PaymentMethod = PaymentMethod.PAY_BY_APP,
    selectedPaymentType: PaymentType = PaymentType.PIX,
    hasItemsInCart: Boolean = true,
    onBackClick: () -> Unit = {},
    onPaymentMethodChange: (PaymentMethod) -> Unit = {},
    onPaymentTypeChange: (PaymentType) -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header com fundo branco e sombra
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
                    text = "Forma de pagamento",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        // Conteúdo principal
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 34.dp) // left-[34px] conforme CSS
        ) {
            Spacer(modifier = Modifier.height(78.dp)) // top-[184px] - 206px + 100dp = 78dp
            
            // Botões de forma de pagamento
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // PAGUE PELO APP
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.clickable { 
                        onPaymentMethodChange(PaymentMethod.PAY_BY_APP) 
                    }
                ) {
                    Text(
                        text = "PAGUE PELO APP",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) LineCutRed else Color(0xFF959595),
                            fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                            fontSize = 16.sp // text-[16px] conforme CSS
                        ),
                        modifier = Modifier.width(143.dp) // w-[143px] conforme CSS
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp)) // top-[213px] - 201px = 12dp
                    
                    // Linha sublinhada quando selecionado
                    if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                        Box(
                            modifier = Modifier
                                .width(98.dp) // w-[98px] conforme CSS
                                .height(1.dp)
                                .background(LineCutRed)
                                .offset(x = 15.dp) // Centralizar com o texto
                        )
                    } else {
                        Spacer(modifier = Modifier.height(1.dp))
                    }
                }
                
                // PAGUE NA RETIRADA
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.clickable { 
                        onPaymentMethodChange(PaymentMethod.PAY_ON_PICKUP) 
                    }
                ) {
                    Text(
                        text = "PAGUE NA RETIRADA",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = if (selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP) LineCutRed else Color(0xFF959595),
                            fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                            fontSize = 16.sp // text-[16px] conforme CSS
                        ),
                        modifier = Modifier.width(167.dp) // w-[167px] conforme CSS
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Linha sublinhada quando selecionado
                    if (selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP) {
                        Box(
                            modifier = Modifier
                                .width(98.dp)
                                .height(1.dp)
                                .background(LineCutRed)
                                .offset(x = 35.dp) // Ajustar posicionamento
                        )
                    } else {
                        Spacer(modifier = Modifier.height(1.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(57.dp)) // top-[270px] - 213px = 57dp
            
            // Opção PIX (quando pagar pelo app)
            if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                NoticeCard(
                    width = 298.dp,
                    height = 36.dp,
                    offsetX = 11.dp,
                    contentPadding = PaddingValues(start = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone PIX
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(
                                    color = Color(0xFF32BCAD),
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "PIX",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Pagamento via PIX selecionado",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF515050),
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        )
                    }
                }
            }
            
            // Card de atenção (quando pagar na retirada)
            if (selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP) {
                NoticeCard() {
                    Text(
                        text = "Atenção: o pagamento deverá ser efetuado no momento da retirada do produto na loja.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF515050),
                            fontWeight = FontWeight.Normal,
                            fontSize = 15.sp,
                            lineHeight = 20.sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.width(304.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Área inferior com shadow e botão
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp) // h-[100px] conforme CSS
                .background(Color.White)
                .shadow(
                    elevation = 4.dp,
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                ),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    if (hasItemsInCart) {
                        if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                            // Navegar para a tela de QR Code PIX
                            onConfirmClick()
                        } else {
                            // Para pagamento na retirada, apenas confirmar
                            onConfirmClick()
                        }
                    }
                },
                enabled = hasItemsInCart,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LineCutRed,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(20.dp), // rounded-[20px] conforme CSS
                modifier = Modifier
                    .width(343.dp) // w-[343px] conforme CSS
                    .height(28.098.dp) // h-[28.098px] conforme CSS
                    ,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Confirmar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium, // Poppins:Medium conforme CSS
                        fontSize = 14.sp // text-[14px] conforme CSS
                    )
                )
            }
        }
    }
}

@Composable
private fun NoticeCard(
    modifier: Modifier = Modifier,
    width: Dp = 339.dp,
    height: Dp = 97.dp,
    offsetX: Dp = 3.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 17.5.dp, vertical = 11.5.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Card(
        modifier = modifier
            .width(width)
            .height(height)
            .offset(x = offsetX),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}


// Previews
@Preview(
    name = "Payment Method Screen - PAY BY APP",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PaymentMethodScreenPreview() {
    LineCutTheme {
        PaymentMethodScreen(
            selectedPaymentMethod = PaymentMethod.PAY_BY_APP,
            selectedPaymentType = PaymentType.PIX
        )
    }
}

@Preview(
    name = "Payment Method Screen - PAY ON PICKUP",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PaymentMethodScreenPayOnPickupPreview() {
    LineCutTheme {
        PaymentMethodScreen(
            selectedPaymentMethod = PaymentMethod.PAY_ON_PICKUP,
            selectedPaymentType = PaymentType.PIX
        )
    }
}
