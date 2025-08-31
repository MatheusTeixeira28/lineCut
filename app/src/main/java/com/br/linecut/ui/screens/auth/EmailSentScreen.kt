package com.br.linecut.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.*
import com.br.linecut.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSentScreen(
    email: String,
    onResendEmailClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LineCutDesignSystem.screenBackgroundColor)
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
                    .height(240.dp)
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
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                            .size(20.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter_arrow),
                            contentDescription = "Voltar",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
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
                            
                            // Success Message with highlighted email
                            Text(
                                text = buildAnnotatedString {
                                    append("Enviamos um link para redefinir sua senha para o e-mail ")
                                    withStyle(
                                        style = SpanStyle(
                                            color = LineCutRed
                                        )
                                    ) {
                                        append(email)
                                    }
                                    append(".")
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            LineCutSpacer(LineCutSpacing.Medium)
                            
                            // Additional instruction
                            Text(
                                text = "Verifique sua caixa de entrada.",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Resend Button
                        LineCutPrimaryButton(
                            text = "Reenviar e-mail",
                            onClick = onResendEmailClick,
                            modifier = Modifier.width(196.dp)
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
fun EmailSentScreenPreview() {
    LineCutTheme {
        EmailSentScreen(
            email = "usuario@gmail.com"
        )
    }
}
