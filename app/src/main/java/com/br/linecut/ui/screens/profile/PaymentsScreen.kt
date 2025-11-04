package com.br.linecut.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme

// Data class para representar um pagamento
data class Payment(
    val id: String,
    val storeName: String,
    val amount: String,
    val paymentMethod: String,
    val orderNumber: String,
    val date: String
)

// Função para gerar dados de exemplo
private fun getSamplePayments(): List<Payment> {
    return listOf(
        Payment("1", "Burger Queen", "R$ 39,90", "PIX", "1024", "24/04/2025"),
        Payment("2", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "20/04/2025"),
        Payment("3", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "24/04/2025"),
        Payment("4", "Burger Queen", "R$ 39,90", "PIX", "1024", "24/04/2025"),
        Payment("5", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "24/04/2025"),
        Payment("6", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "24/04/2025"),
        Payment("7", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "24/04/2025"),
        Payment("8", "Burger Queen", "R$ 19,90", "Pago na entrega", "950", "24/04/2025")
    )
}

@Composable
fun PaymentsScreen(
    payments: List<Payment> = getSamplePayments(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header seguindo o design do Figma - sem background para manter status bar transparente
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
            // Botão voltar - posição ajustada para não invadir status bar
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

            // Título "Notificações"
            Text(
                text = "Pagamentos",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 50.dp, end = 34.dp, bottom = 16.dp),
            )
        }
        // Título "Histórico de Transação" - posição baseada no Figma
        Text(
            text = "Histórico de Transação",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF9C0202),
            modifier = Modifier.padding(start = 30.dp, top = 34.dp)
        )
        // Lista de pagamentos - seguindo o design do Figma
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(LineCutDesignSystem.screenBackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp)
        ) {
            Spacer(modifier = Modifier.height(29.dp))
            
            payments.forEachIndexed { index, payment ->
                PaymentItem(
                    storeName = payment.storeName,
                    amount = payment.amount,
                    paymentMethod = payment.paymentMethod,
                    orderNumber = payment.orderNumber,
                    date = payment.date
                )
                
                if (index < payments.size - 1) {
                    PaymentDivider()
                }
            }
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

@Composable
private fun PaymentItem(
    storeName: String,
    amount: String,
    paymentMethod: String,
    orderNumber: String,
    date: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(57.dp)
    ) {
        // Store indicator dot - posição baseada no Figma
        Box(
            modifier = Modifier
                .offset(x = 0.dp, y = 8.dp)
                .size(10.dp)
                .background(Color(0xFFD3D3D3), CircleShape)

        )
        
        // Nome da loja - posição baseada no Figma
        Text(
            text = storeName,
            fontSize = 13.67.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = Modifier
                .padding(start = 25.43.dp, top = 0.dp)
        )
        
        // Valor - canto superior direito
        Text(
            text = amount,
            fontSize = 12.81.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 16.dp)
        )
        
        // Método de pagamento
        Text(
            text = paymentMethod,
            fontSize = 11.96.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB3B3B3),
            modifier = Modifier
                .padding(start = 25.dp, top = 22.dp)
        )
        
        // Número do pedido
        Text(
            text = "Pedido nº $orderNumber",
            fontSize = 11.96.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB3B3B3),
            modifier = Modifier
                .padding(start = 25.dp, top = 39.dp)
        )
        
        // Data - canto inferior direito
        Text(
            text = date,
            fontSize = 11.11.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFB3B3B3),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 0.dp)
        )
    }
}

@Composable
private fun PaymentDivider() {
    Spacer(modifier = Modifier.height(15.dp))
    HorizontalDivider(
        color = Color(0xFFE0E0E0),
        thickness = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
    )
    Spacer(modifier = Modifier.height(15.dp))
}

@Preview(showBackground = true)
@Composable
fun PaymentsScreenPreview() {
    LineCutTheme {
        PaymentsScreen()
    }
}

@Preview(showBackground = true, name = "Payment Item Preview")
@Composable
fun PaymentItemPreview() {
    LineCutTheme {
        PaymentItem(
            storeName = "Burger Queen",
            amount = "R$ 39,90",
            paymentMethod = "PIX",
            orderNumber = "1024",
            date = "24/04/2025"
        )
    }
}
