package com.br.linecut.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.components.NavigationItem
import com.br.linecut.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header com fundo branco e sombra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp)
                .background(
                    Color.White,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
                    ambientColor = Color.Black.copy(alpha = 0.25f),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
        ) {
            // Botão voltar
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = 24.dp, top = 60.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = LineCutRed,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "Política de Privacidade"
            Text(
                text = "Política de Privacidade",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 60.dp, top = 20.dp)
            )
        }

        // Conteúdo da Política de Privacidade
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Texto introdutório
            Text(
                text = "Última atualização: 27 de agosto de 2025",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF666666),
                    fontSize = 12.sp
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Seção 1: Introdução
            PrivacySection(
                title = "1. Introdução",
                content = "Esta Política de Privacidade descreve como o LineCut coleta, usa e protege suas informações pessoais quando você utiliza nosso aplicativo de delivery de comida. Ao usar o LineCut, você concorda com a coleta e uso de informações de acordo com esta política."
            )

            // Seção 2: Informações que Coletamos
            PrivacySection(
                title = "2. Informações que Coletamos",
                content = "• Informações de cadastro: nome, e-mail, telefone, CPF\n• Dados de localização para delivery\n• Histórico de pedidos e preferências\n• Informações de pagamento (processadas com segurança)\n• Dados de uso do aplicativo para melhorar nossos serviços"
            )

            // Seção 3: Como Usamos suas Informações
            PrivacySection(
                title = "3. Como Usamos suas Informações",
                content = "Utilizamos suas informações para:\n• Processar e entregar seus pedidos\n• Comunicar sobre o status dos pedidos\n• Melhorar nossos serviços e experiência do usuário\n• Enviar notificações relevantes\n• Oferecer suporte ao cliente\n• Cumprir obrigações legais"
            )

            // Seção 4: Compartilhamento de Informações
            PrivacySection(
                title = "4. Compartilhamento de Informações",
                content = "Seus dados são compartilhados apenas quando necessário:\n• Com restaurantes parceiros para processar pedidos\n• Com processadores de pagamento seguros\n• Com autoridades legais quando exigido por lei\n• Nunca vendemos suas informações a terceiros"
            )

            // Seção 5: Segurança
            PrivacySection(
                title = "5. Segurança dos Dados",
                content = "Implementamos medidas de segurança técnicas e organizacionais para proteger suas informações contra acesso não autorizado, alteração, divulgação ou destruição. Utilizamos criptografia e outras tecnologias de segurança padrão da indústria."
            )

            // Seção 6: Seus Direitos
            PrivacySection(
                title = "6. Seus Direitos",
                content = "Você tem o direito de:\n• Acessar suas informações pessoais\n• Corrigir dados incorretos\n• Solicitar exclusão de seus dados\n• Restringir o processamento de informações\n• Portabilidade dos dados\n• Revogar consentimento a qualquer momento"
            )

            // Seção 7: Retenção de Dados
            PrivacySection(
                title = "7. Retenção de Dados",
                content = "Mantemos suas informações apenas pelo tempo necessário para cumprir os propósitos descritos nesta política, ou conforme exigido por lei. Dados de pedidos são mantidos por até 5 anos para fins fiscais e histórico."
            )

            // Seção 8: Contato
            PrivacySection(
                title = "8. Entre em Contato",
                content = "Se você tiver dúvidas sobre esta Política de Privacidade ou sobre como tratamos seus dados pessoais, entre em contato conosco:\n\nE-mail: privacidade@linecut.com.br\nTelefone: (11) 1234-5678\nEndereço: Av. Exemplo, 123 - São Paulo, SP"
            )

            // Espaço extra para o bottom navigation
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Bottom Navigation
        LineCutBottomNavigationBar(
            selectedItem = NavigationItem.PROFILE,
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun PrivacySection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(bottom = 20.dp)
    ) {
        // Título da seção
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = LineCutRed,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Conteúdo da seção
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF333333),
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            textAlign = TextAlign.Justify
        )
    }
}

// Previews
@Preview(
    name = "Privacy Policy Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PrivacyPolicyScreenPreview() {
    LineCutTheme {
        PrivacyPolicyScreen()
    }
}

@Preview(
    name = "Privacy Section",
    showBackground = true
)
@Composable
fun PrivacySectionPreview() {
    LineCutTheme {
        PrivacySection(
            title = "1. Introdução",
            content = "Esta Política de Privacidade descreve como o LineCut coleta, usa e protege suas informações pessoais quando você utiliza nosso aplicativo de delivery de comida."
        )
    }
}
