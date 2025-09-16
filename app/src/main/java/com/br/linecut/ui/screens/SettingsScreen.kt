package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*

data class SettingsOption(
    val title: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit = {}
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    onCloseAccountClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settingsOptions = listOf(
        SettingsOption(
            title = "Termos e condições de uso",
            onClick = onTermsClick
        ),
        SettingsOption(
            title = "Política de privacidade",
            onClick = onPrivacyClick
        ),
        SettingsOption(
            title = "Encerrar conta",
            onClick = onCloseAccountClick
        ),
        SettingsOption(
            title = "Sair",
            icon = Icons.Default.Logout,
            onClick = onLogoutClick
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header igual ao da HelpScreen
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
                // Botão voltar - posição igual à HelpScreen
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .padding(start = 24.dp, top = 60.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_filter_arrow),
                        contentDescription = "Voltar",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Título "Configurações" - posição igual à HelpScreen
                Text(
                    text = "Configurações",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 60.dp, top = 20.dp)
                )
            }
            
            // White band section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(LineCutDesignSystem.screenBackgroundColor)
            )
            
            // Main content - Settings options list
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp)
                    .padding(bottom = 60.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                settingsOptions.forEachIndexed { index, option ->
                    SettingsOptionRow(
                        title = option.title,
                        icon = option.icon,
                        onClick = option.onClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    if (index < settingsOptions.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Divider line
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
        
        // Bottom Navigation Bar
        LineCutBottomNavigationBar(
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
private fun SettingsOptionRow(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Espaçamento inicial consistente
        Spacer(modifier = Modifier.width(23.43.dp))
        
        Text(
            text = title,
            fontSize = 13.67.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = if (icon != null) Modifier else Modifier.weight(1f)
        )
        
        // Mostrar ícone próximo ao texto para "Sair" ou seta para outras opções
        if (icon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LineCutRed,
                modifier = Modifier.size(17.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = LineCutRed,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    LineCutTheme {
        SettingsScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsOptionRowPreview() {
    LineCutTheme {
        Column {
            SettingsOptionRow(
                title = "Termos e condições de uso",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsOptionRow(
                title = "Sair",
                icon = Icons.Default.Logout,
                onClick = {}
            )
        }
    }
}
