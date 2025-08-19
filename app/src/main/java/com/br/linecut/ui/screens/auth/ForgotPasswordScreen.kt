package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onSendEmailClick: (String) -> Unit = { _ -> },
    onCancelClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    
    val isEmailValid = email.isNotBlank() && email.contains("@")

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LineCutSpacer(LineCutSpacing.XXLarge)
            
            // Logo
            Box(
                modifier = Modifier
                    .size(296.dp)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                LineCutLogo()
            }
            
            LineCutSpacer(LineCutSpacing.XXLarge)
            
            // Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(10.6.dp),
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    ),
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
            
            LineCutSpacer(LineCutSpacing.XXLarge)
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
