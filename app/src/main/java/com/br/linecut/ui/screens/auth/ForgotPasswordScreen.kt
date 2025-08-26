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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.br.linecut.ui.components.*
import com.br.linecut.ui.theme.*
import com.br.linecut.ui.theme.LineCutRed
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.theme.TextSecondary

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onSendEmailClick: (String) -> Unit = { _ -> },
    onCancelClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    
    val isEmailValid = email.isNotBlank() && email.contains("@")

    Box(
        modifier = modifier
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
                            onValueChange = { email = it },
                            placeholder = "Email",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Send Button
                        LineCutPrimaryButton(
                            text = "Enviar",
                            onClick = { onSendEmailClick(email) },
                            enabled = isEmailValid,
                            modifier = Modifier.width(196.dp)
                        )
                        
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
