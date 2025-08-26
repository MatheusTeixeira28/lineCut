package com.br.linecut.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.theme.*

data class ProfileMenuItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userEmail: String = "Hannah.Montana@gmail.com",
    userName: String = "Hannah Montana",
    onAccountDataClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onPaymentsClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onHelpClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val menuItems = listOf(
        ProfileMenuItem(
            icon = Icons.Outlined.Person,
            title = "Dados da conta",
            onClick = onAccountDataClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Notifications,
            title = "Notificações",
            onClick = onNotificationsClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.CreditCard,
            title = "Pagamentos",
            onClick = onPaymentsClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.FavoriteBorder,
            title = "Favoritos",
            onClick = onFavoritesClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Help,
            title = "Ajuda",
            onClick = onHelpClick
        ),
        ProfileMenuItem(
            icon = Icons.Outlined.Settings,
            title = "Configurações",
            onClick = onSettingsClick
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header com shadow
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 82.dp, start = 30.dp)
                ) {
                    Text(
                        text = "Perfil",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = LineCutRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    )
                }
            }
            
            // Conteúdo principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 23.dp)
                    .padding(top = 45.dp, bottom = 70.dp)
            ) {
                
                // Card do perfil com foto e nome
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(59.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(8.dp),
                            spotColor = Color.Black.copy(alpha = 0.25f)
                        ),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Foto de perfil
                        Box(
                            modifier = Modifier
                                .size(61.dp)
                                .offset(x = (-16).dp)
                        ) {
                            // Círculo de fundo
                            Box(
                                modifier = Modifier
                                    .size(73.dp)
                                    .background(
                                        color = Color(0xFFE0E0E0),
                                        shape = CircleShape
                                    )
                            )
                            
                            // Foto do usuário (placeholder)
                            Box(
                                modifier = Modifier
                                    .size(57.dp)
                                    .offset(x = 8.dp, y = 2.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCCCCCC))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Foto do perfil",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Informações do usuário
                        Column {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    color = Color(0xFF7D7D7D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color(0xFF7D7D7D),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Lista de itens do menu
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    menuItems.forEachIndexed { index, item ->
                        ProfileMenuItemRow(
                            icon = item.icon,
                            title = item.title,
                            onClick = item.onClick,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        if (index < menuItems.size - 1) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Linha divisória
                            Divider(
                                color = Color(0xFFE0E0E0),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(horizontal = 19.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
        
        // Bottom Navigation - Fixa na parte inferior
        BottomNavigationBar(
            currentScreen = "profile",
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ProfileMenuItemRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 19.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF7D7D7D),
            modifier = Modifier.size(18.dp)
        )
        
        Spacer(modifier = Modifier.width(24.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = Color(0xFF7D7D7D),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun BottomNavigationBar(
    currentScreen: String = "",
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
                    tint = if (currentScreen == "home") Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = if (currentScreen == "search") Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificações",
                    tint = if (currentScreen == "notifications") Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = onOrdersClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingBag,
                    contentDescription = "Pedidos",
                    tint = if (currentScreen == "orders") Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(25.dp)
                )
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = if (currentScreen == "profile") Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(25.dp)
                )
            }
        }
    }
}

// Previews
@Preview(
    name = "Profile Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ProfileScreenPreview() {
    LineCutTheme {
        ProfileScreen()
    }
}

@Preview(
    name = "Profile Menu Item",
    showBackground = true
)
@Composable
fun ProfileMenuItemPreview() {
    LineCutTheme {
        ProfileMenuItemRow(
            icon = Icons.Outlined.Person,
            title = "Dados da conta",
            onClick = {}
        )
    }
}
