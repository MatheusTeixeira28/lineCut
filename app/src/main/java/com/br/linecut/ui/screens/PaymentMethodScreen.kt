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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    selectedPaymentMethod: PaymentMethod = PaymentMethod.PAY_BY_APP,
    selectedPaymentType: PaymentType = PaymentType.PIX,
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header branco com shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(206.dp)
                    .offset(y = (-75).dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(206.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(30.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    // Conteúdo do header
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 158.dp, start = 34.dp, end = 34.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onBackClick,
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Voltar",
                                    tint = LineCutRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "Forma de pagamento",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = LineCutRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            )
                        }
                    }
                }
            }
            
            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 34.dp)
                    .offset(y = (-75).dp)
            ) {
                Spacer(modifier = Modifier.height(259.dp))
                
                // Botões de forma de pagamento
                Row(
                    modifier = Modifier.fillMaxWidth()
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
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Linha sublinhada quando selecionado
                        if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                            Box(
                                modifier = Modifier
                                    .width(143.dp)
                                    .height(1.dp)
                                    .background(LineCutRed)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(32.dp))
                    
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
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Linha sublinhada quando selecionado
                        if (selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP) {
                            Box(
                                modifier = Modifier
                                    .width(167.dp)
                                    .height(1.dp)
                                    .background(LineCutRed)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(1.dp))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(57.dp))
                
                // Opção PIX (quando pagar pelo app)
                if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                    Card(
                        modifier = Modifier
                            .width(298.dp)
                            .height(36.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp),
                                spotColor = Color.Black.copy(alpha = 0.15f)
                            ),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
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
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            Text(
                                text = "Pagamento via PIX selecionado",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF515050),
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Bottom area com botão Confirmar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.White)
                .shadow(
                    elevation = 4.dp,
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .offset(y = (-44).dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = onConfirmClick,
                colors = ButtonDefaults.buttonColors(containerColor = LineCutRed),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(343.dp)
                    .height(28.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Confirmar",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                )
            }
        }
        
        // Bottom Navigation Bar
        LineCutBottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
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
