package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutDesignSystem
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
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with back button and title
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp),
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 34.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = LineCutRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(80.dp))
                    
                    Text(
                        text = "Configurações",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = LineCutRed
                    )
                }
            }
            
            // White band section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(88.dp)
                    .background(Color.White)
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
                        Spacer(modifier = Modifier.height(18.dp))
                        
                        // Divider line
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 0.5.dp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
        
        // Bottom Navigation
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
        // Show logout icon for "Sair" option, otherwise use spacing
        if (icon != null) {
            Spacer(modifier = Modifier.width(14.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF7D7D7D),
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(8.43.dp))
        } else {
            Spacer(modifier = Modifier.width(25.43.dp))
        }
        
        Text(
            text = title,
            fontSize = 13.67.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF7D7D7D),
            modifier = Modifier.weight(1f)
        )
        
        // Only show arrow for options without icons
        if (icon == null) {
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
