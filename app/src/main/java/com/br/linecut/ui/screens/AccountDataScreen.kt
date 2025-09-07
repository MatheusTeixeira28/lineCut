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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.viewmodel.AuthViewModel
import com.br.linecut.ui.utils.SimpleCpfVisualTransformation
import com.br.linecut.ui.utils.SimplePhoneVisualTransformation

@Composable
fun AccountDataScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    // Estados dos dados do usuário do Firebase
    val currentUser by authViewModel.currentUser.collectAsState()
    
    // Carregar dados do usuário quando a tela for aberta
    LaunchedEffect(Unit) {
        authViewModel.loadCurrentUser()
    }
    
    // Formatação dos dados para exibição
    val formattedCpf = currentUser?.cpf?.let { cpf ->
        val digits = cpf.filter { it.isDigit() }
        when {
            digits.length <= 3 -> digits
            digits.length <= 6 -> "${digits.substring(0, 3)}.${digits.substring(3)}"
            digits.length <= 9 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6)}"
            else -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits.substring(9)}"
        }
    } ?: ""
    
    val formattedPhone = currentUser?.phone?.let { phone ->
        val digits = phone.filter { it.isDigit() }
        when {
            digits.isEmpty() -> ""
            digits.length == 1 -> "(${digits}"
            digits.length <= 2 -> "(${digits})"
            digits.length <= 7 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
            else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        }
    } ?: ""

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

        // Espaço para centralizar a foto entre header e cards
        Spacer(modifier = Modifier.height(40.dp))

        // Container centralizado para foto e botão editar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp), // Altura suficiente para a foto e espaçamento
            contentAlignment = Alignment.Center
        ) {
            // Foto do usuário centralizada
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
            
            // Botão editar posicionado no canto superior direito da foto
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = 50.dp, y = (-50).dp) // Posicionamento relativo à foto centralizada
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
        
        // Espaço antes dos cards
        Spacer(modifier = Modifier.height(20.dp))

        // Seção de dados do usuário
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 45.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (currentUser == null) {
                // Estado de carregamento
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = LineCutRed,
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else {
                // Campos de dados com dados do usuário
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                // Nome completo
                AccountDataField(
                    icon = Icons.Default.Person,
                    value = currentUser?.fullName ?: "",
                    placeholder = "Nome completo"
                )

                // CPF
                AccountDataField(
                    icon = Icons.Default.CreditCard,
                    value = formattedCpf,
                    placeholder = "CPF"
                )

                // Telefone
                AccountDataField(
                    icon = Icons.Default.Phone,
                    value = formattedPhone,
                    placeholder = "Telefone"
                )

                // Email
                AccountDataField(
                    icon = Icons.Default.Email,
                    value = currentUser?.email ?: "",
                    placeholder = "Email"
                )

                // Senha
                AccountDataField(
                    icon = Icons.Default.Lock,
                    value = "••••••••", // Senha mascarada por segurança
                    placeholder = "Senha",
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityChange = { isPasswordVisible = !isPasswordVisible }
                )
                }
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
                text = if (isPassword) {
                    if (isPasswordVisible) "minhasenha123" else "••••••••"
                } else {
                    value
                },
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
