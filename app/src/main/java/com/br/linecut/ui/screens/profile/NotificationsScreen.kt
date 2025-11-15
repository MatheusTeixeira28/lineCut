package com.br.linecut.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.viewmodel.NotificationViewModel

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
    RATING(Color(0xFFF2C12E), Icons.Default.Star),
    ORDER_PICKED_UP(Color(0xFF1CB456), Icons.Default.CheckCircle),
    ORDER_READY(Color(0xFFFF9500), Icons.Default.ShoppingBag),
    ORDER_PREPARING(Color(0xFFF2C12E), Icons.Default.Timer),
    ORDER_PLACED(Color(0xFF7ED321), Icons.Default.Assignment)
}

@Composable
fun NotificationsScreen(
    onBackClick: () -> Unit = {},
    onRatingClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    notificationViewModel: NotificationViewModel = viewModel()
) {
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()
    val error by notificationViewModel.error.collectAsState()
    
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header simples baseado no Figma
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
            // Botão voltar
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
                text = "Notificações",
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

        // Lista de notificações - começando mais próxima ao header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = LineCutRed,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = LineCutRed,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Erro ao carregar notificações",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LineCutRed
                            )
                        }
                    }
                }
                notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = Color(0xFFD1D1D1),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Nenhuma notificação",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF7D7D7D)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Você ainda não possui notificações",
                                fontSize = 14.sp,
                                color = Color(0xFF959595)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 24.dp, bottom = 20.dp)
                    ) {
                        items(notifications) { notification ->
                            NotificationCard(
                                notification = notification,
                                onRatingClick = { onRatingClick(notification.id) }
                            )
                        }
                    }
                }
            }
        }

        // Bottom Navigation
        LineCutBottomNavigationBar(
            selectedItem = NavigationItem.NOTIFICATIONS,
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
        colors = CardDefaults.cardColors(LineCutDesignSystem.screenBackgroundColor)
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
                    fontSize = 12.sp
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
                        color = if (notification.type == NotificationType.RATING) LineCutRed else notification.type.color,
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
                        fontSize = 14.sp,
                        lineHeight = 16.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                // Seção de avaliação (se aplicável)
                if (notification.showRating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRatingClick() }, // Tornar clicável
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
                            
                            // Estrelas de avaliação (vazias)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.StarBorder, // Estrela vazia
                                        contentDescription = "Estrela ${index + 1}",
                                        tint = Color(0xFFD1D1D1), // Cor cinza para estrelas vazias
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
