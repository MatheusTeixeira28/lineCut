package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
fun SignUpScreen(
    onSignUpClick: (String, String, String, String, String, String) -> Unit = { _, _, _, _, _, _ -> },
    onLoginClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var fullName by remember { mutableStateOf("") }
    var cpf by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var acceptTerms by remember { mutableStateOf(false) }
    var acceptPrivacy by remember { mutableStateOf(false) }

    val isFormValid = fullName.isNotBlank() && 
                     cpf.isNotBlank() && 
                     phone.isNotBlank() && 
                     email.isNotBlank() && 
                     password.isNotBlank() && 
                     confirmPassword.isNotBlank() && 
                     password == confirmPassword &&
                     acceptTerms && 
                     acceptPrivacy

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
            LineCutSpacer(LineCutSpacing.XLarge)
            
            // Logo
            Box(
                modifier = Modifier
                    .size(214.dp)
                    .padding(bottom = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                LineCutLogo()
            }
            
            LineCutSpacer(LineCutSpacing.Large)
            
            // Cadastro Title
            LineCutTitle(
                text = "Cadastro",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp)
            )
            
            LineCutSpacer(LineCutSpacing.XLarge)
            
            // Full Name Field
            LineCutTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = "Nome completo",
                leadingIcon = Icons.Outlined.Person,
                modifier = Modifier.fillMaxWidth()
            )
            
            LineCutSpacer(LineCutSpacing.Medium)
            
            // CPF Field
            LineCutTextField(
                value = cpf,
                onValueChange = { cpf = it },
                placeholder = "CPF",
                leadingIcon = Icons.Outlined.Person, // Usando Person como placeholder para ID
                keyboardType = KeyboardType.Number,
                modifier = Modifier.fillMaxWidth()
            )
            
            LineCutSpacer(LineCutSpacing.Medium)
            
            // Phone Field
            LineCutTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "Telefone",
                leadingIcon = Icons.Outlined.Phone,
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )
            
            LineCutSpacer(LineCutSpacing.Medium)
            
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
            
            LineCutSpacer(LineCutSpacing.Medium)
            
            // Confirm Password Field
            LineCutTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirmar senha",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true,
                keyboardType = KeyboardType.Password,
                modifier = Modifier.fillMaxWidth()
            )
            
            LineCutSpacer(LineCutSpacing.Large)
            
            // Terms and Privacy Checkboxes
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Terms Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acceptTerms,
                        onCheckedChange = { acceptTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = LineCutRed,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Li e concordo com os ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary
                        )
                    )
                    Text(
                        text = "Termos e Condições",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LineCutRed,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable { onTermsClick() }
                    )
                }
                
                LineCutSpacer(LineCutSpacing.Small)
                
                // Privacy Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = acceptPrivacy,
                        onCheckedChange = { acceptPrivacy = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = LineCutRed,
                            uncheckedColor = TextSecondary
                        ),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aceito a ",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextSecondary
                        )
                    )
                    Text(
                        text = "Política de Privacidade",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = LineCutRed,
                            textDecoration = TextDecoration.Underline
                        ),
                        modifier = Modifier.clickable { onPrivacyClick() }
                    )
                }
            }
            
            LineCutSpacer(LineCutSpacing.XLarge)
            
            // Sign Up Button
            LineCutPrimaryButton(
                text = "Cadastrar",
                onClick = { 
                    onSignUpClick(fullName, cpf, phone, email, password, confirmPassword)
                },
                enabled = isFormValid,
                modifier = Modifier.width(196.dp)
            )
            
            LineCutSpacer(LineCutSpacing.Large)
            
            // Login Link
            Text(
                text = "Já possuo conta",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable { onLoginClick() }
            )
            
            LineCutSpacer(LineCutSpacing.XLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    LineCutTheme {
        SignUpScreen()
    }
}
