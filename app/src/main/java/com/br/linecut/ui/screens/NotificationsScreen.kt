package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*

// Data class para representar uma notificação
data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val showRating: Boolean = false
)

// Enum para os tipos de notificação
enum class NotificationType(
    val color: Color,
    val icon: ImageVector
) {
    RATING(Color(0x99D9009C02), Icons.Default.Star),
    ORDER_PICKED_UP(Color(0xFF1CB456), Icons.Default.CheckCircle),
    ORDER_READY(Color(0xFFFF9500), Icons.Default.ShoppingBag),
    ORDER_PREPARING(Color(0xFFF2C12E), Icons.Default.Timer),
    ORDER_PLACED(Color(0xFF7ED321), Icons.Default.Assignment)
}

@Composable
fun NotificationsScreen(
    notifications: List<Notification> = getSampleNotifications(),
    onBackClick: () -> Unit = {},
    onRatingClick: (String) -> Unit = {},
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
    ) {
        // Header com fundo arredondado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(206.dp)
                .background(LineCutDesignSystem.screenBackgroundColor)
        ) {
            // Fundo branco arredondado
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp)
                    .offset(y = (-73).dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(30.dp)
                    ),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {}
            
            // Título "Notificações"
            Text(
                text = "Notificações",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier
                    .padding(start = 30.dp, top = 82.dp)
            )
        }

        // Lista de notificações
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 22.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 20.dp)
        ) {
            items(notifications) { notification ->
                NotificationCard(
                    notification = notification,
                    onRatingClick = { onRatingClick(notification.id) }
                )
            }
        }

        // Bottom Navigation
        BottomNavigationBar(
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun NotificationCard(
    notification: Notification,
    onRatingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(if (notification.showRating) 125.dp else 96.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(10.77.dp)
            ),
        shape = RoundedCornerShape(10.77.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Ícone
            Icon(
                imageVector = notification.type.icon,
                contentDescription = null,
                tint = notification.type.color,
                modifier = Modifier
                    .size(20.dp)
                    .offset(x = (-1).dp, y = 8.dp)
            )
            
            // Horário (canto superior direito)
            Text(
                text = notification.time,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0x99515050),
                    fontSize = 11.sp
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp)
            )
            
            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp, top = 8.dp)
            ) {
                // Título
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = notification.type.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Mensagem
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0x99515050),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Seção de avaliação (se aplicável)
                if (notification.showRating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            // Texto "Avaliar agora"
                            Text(
                                text = "Avaliar agora",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF515050),
                                    fontSize = 10.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Estrelas de avaliação
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Estrela ${index + 1}",
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(11.63.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = LineCutRed)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onHomeClick) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onOrdersClick) {
                Icon(
                    imageVector = Icons.Default.Receipt,
                    contentDescription = "Pedidos",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
            
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.White,
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

// Função para gerar dados de exemplo das notificações
private fun getSampleNotifications(): List<Notification> = listOf(
    Notification(
        id = "1",
        title = "Avalie seu pedido",
        message = "O que achou do seu pedido?\nSua opinião é muito importante!",
        time = "Hoje às 17:30",
        type = NotificationType.RATING,
        showRating = true
    ),
    Notification(
        id = "2",
        title = "Pedido retirado",
        message = "Você retirou seu pedido na Lanchonete Museoh. Aproveite sua refeição!",
        time = "Hoje às 17:20",
        type = NotificationType.ORDER_PICKED_UP
    ),
    Notification(
        id = "3",
        title = "Pedido pronto para retirada",
        message = "Seu pedido está pronto! Retire no balcão quando quiser.",
        time = "Hoje às 17:15",
        type = NotificationType.ORDER_READY
    ),
    Notification(
        id = "4",
        title = "Pedido em preparo",
        message = "Seu pedido na Lanchonete Museoh está sendo preparado!",
        time = "Hoje às 17:05",
        type = NotificationType.ORDER_PREPARING
    ),
    Notification(
        id = "5",
        title = "Pedido realizado",
        message = "Recebemos seu pedido na Lanchonete Museoh. Em breve ele será preparado!",
        time = "Hoje às 17:00",
        type = NotificationType.ORDER_PLACED
    )
)

// Previews
@Preview(
    name = "Notifications Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun NotificationsScreenPreview() {
    LineCutTheme {
        NotificationsScreen()
    }
}

@Preview(
    name = "Notification Card - Rating",
    showBackground = true
)
@Composable
fun NotificationCardRatingPreview() {
    LineCutTheme {
        NotificationCard(
            notification = Notification(
                id = "1",
                title = "Avalie seu pedido",
                message = "O que achou do seu pedido?\nSua opinião é muito importante!",
                time = "Hoje às 17:30",
                type = NotificationType.RATING,
                showRating = true
            ),
            onRatingClick = {}
        )
    }
}

@Preview(
    name = "Notification Card - Order Status",
    showBackground = true
)
@Composable
fun NotificationCardOrderPreview() {
    LineCutTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            NotificationCard(
                notification = Notification(
                    id = "2",
                    title = "Pedido retirado",
                    message = "Você retirou seu pedido na Lanchonete Museoh. Aproveite sua refeição!",
                    time = "Hoje às 17:20",
                    type = NotificationType.ORDER_PICKED_UP
                ),
                onRatingClick = {}
            )
            
            NotificationCard(
                notification = Notification(
                    id = "3",
                    title = "Pedido pronto para retirada",
                    message = "Seu pedido está pronto! Retire no balcão quando quiser.",
                    time = "Hoje às 17:15",
                    type = NotificationType.ORDER_READY
                ),
                onRatingClick = {}
            )
        }
    }
}
