package com.br.linecut.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*

enum class PaymentMethod {
    PAY_BY_APP,
    PAY_ON_PICKUP
}

enum class PaymentType {
    PIX,
    CREDIT_CARD,
    DEBIT_CARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSummaryScreen(
    store: Store,
    cartItems: List<CartItem> = emptyList(),
    estimatedTime: String = "10 a 15 minutos",
    selectedPaymentMethod: PaymentMethod = PaymentMethod.PAY_BY_APP,
    selectedPaymentType: PaymentType = PaymentType.PIX,
    onBackClick: () -> Unit = {},
    onClearCartClick: () -> Unit = {},
    onAddMoreItemsClick: () -> Unit = {},
    onPaymentMethodChange: (PaymentMethod) -> Unit = {},
    onPaymentTypeChange: (PaymentType) -> Unit = {},
    onFinishOrderClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val totalPrice = cartItems.sumOf { it.price * it.quantity }
    val totalItems = cartItems.sumOf { it.quantity }
    
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header da loja
            OrderSummaryHeader(
                store = store,
                onAddMoreItemsClick = onAddMoreItemsClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Título e botão voltar
            OrderSummaryTitle(
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
            
            // Conteúdo principal
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    // Tempo estimado
                    EstimatedTimeSection(
                        estimatedTime = estimatedTime
                    )
                }
                
                item {
                    // Lista de itens
                    OrderItemsList(
                        cartItems = cartItems,
                        totalPrice = totalPrice,
                        onClearCartClick = onClearCartClick
                    )
                }
                
                item {
                    // Forma de pagamento
                    PaymentMethodSection(
                        selectedPaymentMethod = selectedPaymentMethod,
                        selectedPaymentType = selectedPaymentType,
                        onPaymentMethodChange = onPaymentMethodChange,
                        onPaymentTypeChange = onPaymentTypeChange
                    )
                }
                
                item {
                    // Espaço para o resumo
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
        
        // Área do resumo e bottom nav
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            // Fundo branco para o resumo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp)
                    .background(Color.White)
            ) {
                OrderSummaryFooter(
                    total = totalPrice,
                    itemCount = totalItems,
                    onFinishOrderClick = onFinishOrderClick
                )
            }
            
            // Bottom Navigation
            LineCutBottomNavigationBar(
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick,
                onOrdersClick = onOrdersClick,
                onProfileClick = onProfileClick
            )
        }
    }
}

@Composable
private fun OrderSummaryHeader(
    store: Store,
    onAddMoreItemsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(225.dp)
            .offset(y = (-20).dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(30.dp)
            ),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Conteúdo principal
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 35.dp, top = 76.dp, end = 35.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo circular da loja
                Card(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(70.dp)
                        .shadow(4.dp, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = store.imageRes),
                        contentDescription = "Logo ${store.name}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.width(19.dp))
                
                // Informações da loja
                Column {
                    Text(
                        text = store.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = store.category,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF7D7D7D),
                            fontSize = 16.sp
                        )
                    )
                }
            }
            
            // Botão "Adicionar mais itens" no canto inferior direito
            Button(
                onClick = onAddMoreItemsClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 24.dp)
                    .height(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(15.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LineCutRed),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
            ) {
                Text(
                    text = "Adicionar mais itens",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LineCutRed,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun OrderSummaryTitle(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
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
            text = "Sacola",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = LineCutRed,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    }
}

@Composable
private fun EstimatedTimeSection(
    estimatedTime: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Tempo estimado para retirada:",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF515050),
                fontSize = 15.sp
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Relógio",
                tint = Color(0xFF736F6C),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = estimatedTime,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF736F6C),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Linha divisória
        Divider(
            color = Color(0xFFE0E0E0),
            thickness = 1.dp
        )
    }
}

@Composable
private fun OrderItemsList(
    cartItems: List<CartItem>,
    totalPrice: Double,
    onClearCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Cabeçalho "Itens adicionados" e "Limpar"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Itens adicionados",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF515050),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
            
            Text(
                text = "Limpar",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFB4381C),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                ),
                modifier = Modifier.clickable { onClearCartClick() }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Lista de itens
        cartItems.forEach { item ->
            OrderItemRow(
                item = item,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF515050),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            )
            
            Text(
                text = "R$ ${String.format("%.2f", totalPrice).replace(".", ",")}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color(0xFF515050),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            )
        }
    }
}

@Composable
private fun OrderItemRow(
    item: CartItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.name,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF515050),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${item.quantity}x",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF515050),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
        
        Text(
            text = "R$ ${String.format("%.2f", item.price * item.quantity).replace(".", ",")}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF515050),
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun PaymentMethodSection(
    selectedPaymentMethod: PaymentMethod,
    selectedPaymentType: PaymentType,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onPaymentTypeChange: (PaymentType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Selecionar forma de pagamento:",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF515050),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botões de método de pagamento
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PaymentMethodButton(
                text = "PAGUE PELO APP",
                isSelected = selectedPaymentMethod == PaymentMethod.PAY_BY_APP,
                onClick = { onPaymentMethodChange(PaymentMethod.PAY_BY_APP) }
            )
            
            PaymentMethodButton(
                text = "PAGUE NA RETIRADA",
                isSelected = selectedPaymentMethod == PaymentMethod.PAY_ON_PICKUP,
                onClick = { onPaymentMethodChange(PaymentMethod.PAY_ON_PICKUP) }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Opção PIX (quando pagar pelo app)
        if (selectedPaymentMethod == PaymentMethod.PAY_BY_APP) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F7))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Ícone PIX
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(
                                color = Color(0xFF32BCAD),
                                shape = RoundedCornerShape(2.dp)
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
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF515050),
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentMethodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(22.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Transparent else Color.Transparent
        ),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isSelected) Color(0xFFB4381C) else Color.Transparent
        ),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isSelected) Color(0xFFB4381C) else Color(0xFF959595),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        )
    }
}

@Composable
private fun OrderSummaryFooter(
    total: Double,
    itemCount: Int,
    onFinishOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 44.dp)
            .height(82.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Informações do total
        Column {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF515050),
                    fontSize = 12.sp
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R$ ${String.format("%.2f", total).replace(".", ",")}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color(0xFF515050),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = " / $itemCount itens",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF515050),
                        fontSize = 12.sp
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botão Finalizar Pedido
        Button(
            onClick = onFinishOrderClick,
            colors = ButtonDefaults.buttonColors(containerColor = LineCutRed),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .height(28.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
        ) {
            Text(
                text = "Finalizar Pedido",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
        }
    }
}

// Previews
@Preview(
    name = "Order Summary Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OrderSummaryScreenPreview() {
    LineCutTheme {
        OrderSummaryScreen(
            store = Store(
                id = "1",
                name = "Museoh",
                category = "Lanches e Salgados",
                location = "Praça 3 - Senac",
                distance = "150m"
            ),
            cartItems = listOf(
                CartItem(id = "1", name = "Açaí", price = 11.90, quantity = 1),
                CartItem(id = "2", name = "Pizza", price = 20.00, quantity = 2),
                CartItem(id = "3", name = "Coca-cola", price = 5.00, quantity = 1),
                CartItem(id = "4", name = "Suco", price = 6.00, quantity = 1)
            )
        )
    }
}

@Preview(
    name = "Payment Method Section",
    showBackground = true
)
@Composable
fun PaymentMethodSectionPreview() {
    LineCutTheme {
        PaymentMethodSection(
            selectedPaymentMethod = PaymentMethod.PAY_BY_APP,
            selectedPaymentType = PaymentType.PIX,
            onPaymentMethodChange = {},
            onPaymentTypeChange = {}
        )
    }
}

@Preview(
    name = "Order Items List",
    showBackground = true
)
@Composable
fun OrderItemsListPreview() {
    LineCutTheme {
        OrderItemsList(
            cartItems = listOf(
                CartItem(id = "1", name = "Açaí", price = 11.90, quantity = 1),
                CartItem(id = "2", name = "Pizza", price = 20.00, quantity = 2)
            ),
            totalPrice = 51.90,
            onClearCartClick = {}
        )
    }
}
