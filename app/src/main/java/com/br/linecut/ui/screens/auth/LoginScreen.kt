package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.linecut.ui.components.*
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 58.dp)
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
            
            LineCutSpacer(LineCutSpacing.Large)
            
            // Login Title
            LineCutTitle(
                text = "Login",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp)
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
            
            LineCutSpacer(LineCutSpacing.Medium)
            
            // Password Field
            LineCutTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Senha",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )
            
            LineCutSpacer(LineCutSpacing.Small)
            
            // Forgot Password Link
            Text(
                text = "Esqueceu sua senha?",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextPrimary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onForgotPasswordClick() }
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )
            
            LineCutSpacer(LineCutSpacing.XLarge)
            
            // Login Button
            LineCutPrimaryButton(
                text = "Entrar",
                onClick = { onLoginClick(email, password) },
                enabled = email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.width(196.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Sign Up Link
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LineCutBodyText(
                    text = "Não possui uma conta? ",
                    color = TextPrimary
                )
                LineCutLinkText(
                    text = "Cadastre-se",
                    onClick = onSignUpClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LineCutTheme {
        LoginScreen()
    }
}
