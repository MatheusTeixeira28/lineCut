package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.ui.components.*
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.theme.TextSecondary
import com.br.linecut.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onSendEmailClick: (String) -> Unit = { _ -> },
    onCancelClick: () -> Unit = {},
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    
    val isEmailValid = email.isNotBlank() && email.contains("@")

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        // Logo posicionado como no Figma
        Box(
            modifier = Modifier
                .size(296.dp)
                .offset(x = 58.dp, y = 41.dp),
            contentAlignment = Alignment.Center
        ) {
            LineCutLogo()
        }
        
        // Card Container na posição exata do Figma
        Card(
            modifier = Modifier
                .fillMaxWidth() // Usar fillMaxWidth para garantir responsividade
                .height(300.dp) // h-[300px] - altura exata do Figma
                .padding(horizontal = 24.dp) // Padding para simular left-6 do Figma
                .offset(y = 318.dp) // top-[318px] - posição vertical exata do Figma
                .drawBehind {
                    // Sombra sutil conforme Figma: apenas lado direito e embaixo, bem leve
                    drawRoundRect(
                        color = Color.Black.copy(alpha = 0.15f), // Opacidade reduzida para efeito mais sutil
                        topLeft = Offset(2.dp.toPx(), 3.dp.toPx()), // Offset menor e mais sutil
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(10.6.dp.toPx())
                    )
                },
            shape = RoundedCornerShape(10.6.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundPrimary
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            )
        ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Title
                        Text(
                            text = "Esqueci minha senha",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                        
                        LineCutSpacer(LineCutSpacing.Large)
                        
                        // Description
                        Text(
                            text = "Informe seu e-mail cadastrado para enviarmos um link de redefinição de senha.",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = TextSecondary,
                                fontSize = 12.sp
                            ),
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        LineCutSpacer(LineCutSpacing.XLarge)
                        
                        // Email Field
                        LineCutTextField(
                            value = email,
                            onValueChange = { 
                                email = it
                                // Limpar mensagens quando o usuário digitar
                                if (errorMessage.isNotEmpty()) errorMessage = ""
                                if (successMessage.isNotEmpty()) successMessage = ""
                            },
                            placeholder = "Email",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        LineCutSpacer(LineCutSpacing.Small)
                        
                        // Mensagem de erro
                        if (errorMessage.isNotEmpty()) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LineCutRed,
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Mensagem de sucesso
                        if (successMessage.isNotEmpty()) {
                            Text(
                                text = successMessage,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF4CAF50), // Verde
                                    fontSize = 11.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Send Button com loading
                        LineCutPrimaryButton(
                            text = if (isLoading) "" else "Enviar",
                            onClick = {
                                android.util.Log.d("PASSWORD_RESET", "Screen: Botão 'Enviar' clicado para email: $email")
                                coroutineScope.launch {
                                    isLoading = true
                                    errorMessage = ""
                                    successMessage = ""

                                    val result = authViewModel.sendPasswordResetEmail(email)
                                    
                                    result.onSuccess { message ->
                                        successMessage = message
                                        onSendEmailClick(email)
                                    }.onFailure { error ->
                                        errorMessage = error.message ?: "Erro ao enviar email"
                                    }
                                    isLoading = false
                                }
                            },
                            enabled = isEmailValid && !isLoading,
                            modifier = Modifier.width(196.dp)
                        )
                        
                        // Loading indicator dentro do botão
                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .width(196.dp)
                                    .height(48.dp)
                                    .offset(y = (-48).dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                        
                        LineCutSpacer(LineCutSpacing.Medium)
                        
                        // Cancel Link
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = LineCutRed,
                                fontSize = 12.sp,
                                textDecoration = TextDecoration.Underline
                            ),
                            modifier = Modifier.clickable { onCancelClick() },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    LineCutTheme {
        ForgotPasswordScreen()
    }
}
