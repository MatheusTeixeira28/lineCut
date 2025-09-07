package com.br.linecut.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*

@Composable
fun AccountDataScreen(
    userName: String = "Hannah Montana",
    userCpf: String = "482.392.103-25",
    userPhone: String = "(11) 97283-1931",
    userEmail: String = "Hannah.Montana@gmail.com",
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Header com fundo arredondado - mesmas proporções da QRCodePixScreen
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
                    text = "Meus dados",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp)) // Reduzido para aproximar do design do Figma

        // Seção de dados do usuário
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Foto do usuário
            Box(
                modifier = Modifier
                    .size(135.dp)
                    .offset(y = (-10).dp) // Ajuste para seguir o CSS
            ) {
                // Círculo de fundo da foto
                Box(
                    modifier = Modifier
                        .size(135.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = CircleShape
                        )
                        .background(
                            color = Color(0xFFE8E8E8),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                ) {
                    // Foto do usuário (placeholder)
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Placeholder
                        contentDescription = "Foto do usuário",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            
            // Botão editar posicionado no canto superior direito conforme Figma
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp) // Container para posicionamento absoluto
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = 283.dp, y = (-157).dp) // Posição baseada no Figma: left-[283px] top-[-187px] + ajuste para nossa estrutura
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = LineCutDesignSystem.screenBackgroundColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .border(
                            BorderStroke(1.dp, LineCutRed),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onEditClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.editar),
                        contentDescription = "Editar foto",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(15.dp)) // Reduzido para aproximar do design do Figma

            // Campos de dados
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Nome completo
                AccountDataField(
                    icon = Icons.Default.Person,
                    value = userName,
                    placeholder = "Nome completo"
                )

                // CPF
                AccountDataField(
                    icon = Icons.Default.CreditCard,
                    value = userCpf,
                    placeholder = "CPF"
                )

                // Telefone
                AccountDataField(
                    icon = Icons.Default.Phone,
                    value = userPhone,
                    placeholder = "Telefone"
                )

                // Email
                AccountDataField(
                    icon = Icons.Default.Email,
                    value = userEmail,
                    placeholder = "Email"
                )

                // Senha
                AccountDataField(
                    icon = Icons.Default.Lock,
                    value = "**********",
                    placeholder = "Senha",
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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

@Composable
private fun AccountDataField(
    icon: ImageVector,
    value: String,
    placeholder: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityChange: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(43.dp),
        shape = RoundedCornerShape(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFD1D1D1))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícone
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Texto do campo
            Text(
                text = if (isPassword && !isPasswordVisible) "**********" else value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.weight(1f)
            )
            
            // Botão de visibilidade da senha
            if (isPassword) {
                IconButton(
                    onClick = onPasswordVisibilityChange,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha",
                        tint = TextSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}


// Previews
@Preview(
    name = "Account Data Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AccountDataScreenPreview() {
    LineCutTheme {
        AccountDataScreen()
    }
}

@Preview(
    name = "Account Data Field",
    showBackground = true
)
@Composable
fun AccountDataFieldPreview() {
    LineCutTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AccountDataField(
                icon = Icons.Default.Person,
                value = "Hannah Montana",
                placeholder = "Nome completo"
            )
            
            AccountDataField(
                icon = Icons.Default.Lock,
                value = "**********",
                placeholder = "Senha",
                isPassword = true
            )
        }
    }
}
