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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header com fundo branco e sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp) // h-[206px] conforme CSS original
                .offset(y = (-75).dp) // top-[-75px] conforme CSS
                .background(
                    Color.White,
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
                    .padding(start = 34.dp, top = 158.dp) // left-[34px] top-[83px] + 75px = 158dp
                    .size(20.dp) // size-5 = 20dp conforme CSS
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = LineCutRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "Forma de pagamento"
            Text(
                text = "Forma de pagamento",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp // text-[20px] conforme CSS
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 80.dp, top = 153.dp) // left-20 = 80dp, top-[78px] + 75px = 153dp
            )
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
                Box(
                    modifier = Modifier
                        .width(298.dp) // w-[298px] conforme CSS
                        .height(36.dp) // h-9 = 36dp conforme CSS
                        .background(
                            Color.White,
                            shape = RoundedCornerShape(10.dp) // rounded-[10px] conforme CSS
                        )
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .offset(x = 11.dp) // left-[45px] - 34px = 11dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 6.dp), // left-[51px] - 45px = 6dp
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Ícone PIX
                        Box(
                            modifier = Modifier
                                .size(24.dp) // size-6 = 24dp conforme CSS
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
                        
                        Spacer(modifier = Modifier.width(6.dp)) // left-[81px] - 75px = 6dp
                        
                        Text(
                            text = "Pagamento via PIX selecionado",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF515050), // text-[#515050] conforme CSS
                                fontWeight = FontWeight.Normal, // Poppins:Regular conforme CSS
                                fontSize = 15.sp // text-[15px] conforme CSS
                            ),
                            modifier = Modifier.width(286.dp) // w-[286px] conforme CSS
                        )
                    }
                }
            }
            
            // Card de atenção (quando pagar na retirada)
            if (selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP) {
                Card(
                    modifier = Modifier
                        .width(339.dp) // w-[339px] conforme CSS do Figma
                        .height(97.dp) // h-[97px] conforme CSS do Figma
                        .offset(x = 3.dp), // left-[37px] - 34px = 3dp
                    shape = RoundedCornerShape(10.dp), // rounded-[10px] conforme CSS
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 17.5.dp, vertical = 11.5.dp), // Para centralizar o texto
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Atenção: o pagamento deverá ser efetuado no momento da retirada do produto na loja.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = Color(0xFF515050), // text-[#515050] conforme CSS
                                fontWeight = FontWeight.Normal, // Poppins:Regular conforme CSS
                                fontSize = 15.sp, // text-[15px] conforme CSS
                                lineHeight = 20.sp
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.width(304.dp) // w-[304px] conforme CSS
                        )
                    }
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
                    if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
                        // Navegar para a tela de QR Code PIX
                        onConfirmClick()
                    } else {
                        // Para pagamento na retirada, apenas confirmar
                        onConfirmClick()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = LineCutRed), // bg-[#9c0202]
                shape = RoundedCornerShape(20.dp), // rounded-[20px] conforme CSS
                modifier = Modifier
                    .width(343.dp) // w-[343px] conforme CSS
                    .height(28.098.dp) // h-[28.098px] conforme CSS
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
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
