package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.br.linecut.data.models.AuthResult
import com.br.linecut.ui.components.LineCutBodyText
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutLinkText
import com.br.linecut.ui.components.LineCutLogo
import com.br.linecut.ui.components.LineCutPrimaryButton
import com.br.linecut.ui.components.LineCutSpacer
import com.br.linecut.ui.components.LineCutSpacing
import com.br.linecut.ui.components.LineCutTextField
import com.br.linecut.ui.components.LineCutTitle
import com.br.linecut.ui.theme.LineCutTheme
import com.br.linecut.ui.theme.TextPrimary
import com.br.linecut.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    // Observar estados do ViewModel
    val loginState by authViewModel.loginState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    
    // Observar o estado do login
    LaunchedEffect(loginState) {
        val currentState = loginState
        when (currentState) {
            is AuthResult.Success -> {
                authViewModel.clearLoginState()
                onLoginSuccess()
            }
            is AuthResult.Error -> {
                errorMessage = currentState.message
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
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
            
            // Exibir mensagem de erro se houver
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                LineCutSpacer(LineCutSpacing.Medium)
            }
            
            // Login Button
            LineCutPrimaryButton(
                text = if (isLoading) "Entrando..." else "Entrar",
                onClick = { 
                    errorMessage = ""
                    authViewModel.loginUser(email, password)
                },
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
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
