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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
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
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 16.dp)
                    .size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter_arrow),
                    contentDescription = "Voltar",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            // Título "Política de Privacidade"
            Text(
                text = "Política de Privacidade",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = LineCutRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 50.dp, end = 34.dp, bottom = 16.dp),
            )
        }

        // Conteúdo da Política de Privacidade
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 34.dp, vertical = 24.dp)
        ) {
            // Seção 1: Dados Coletados
            PrivacySection(
                title = "1. Dados Coletados",
                content = "Ao utilizar o LineCut, podemos coletar os seguintes dados:\n\n•  Dados pessoais: Nome, e-mail, número de telefone, CPF (se exigido por lei ou para emissão de nota fiscal);\n• Dados de uso: Informações sobre os pedidos realizados, tempo na fila, preferências, histórico de compras;\n• Dados de localização: Opcional, utilizado apenas para mostrar estabelecimentos próximos (se autorizado);\n• Dados de pagamento: Informações de transações via terceiros (ex: PIX), nunca armazenamos os dados completos do cartão;\n• Dados técnicos: IP, tipo de dispositivo, sistema operacional e versão do app."
            )

            // Seção 2: Uso das Informações
            PrivacySection(
                title = "2. Uso das Informações",
                content = "As informações são utilizadas para:\n\n• Realizar e gerenciar pedidos nas lanchonetes cadastradas;\n• Melhorar a experiência do usuário;\n• Enviar notificações sobre status dos pedidos;\n• Garantir segurança e prevenir fraudes;\n• Cumprir obrigações legais e regulatórias;\n• Realizar análises estatísticas (sempre de forma anonimizada)."
            )

            // Seção 3: Compartilhamento de Dados
            PrivacySection(
                title = "3. Compartilhamento de Dados",
                content = "Seus dados podem ser compartilhados com:\n\n• Lanchonetes parceiras, para execução do pedido;\n• Serviços de pagamento, para processar as transações;\n• Serviços de notificação, para envio de alertas (push/SMS);\n• Autoridades legais, quando exigido por lei;\n\nNão vendemos seus dados pessoais a terceiros."
            )

            // Seção 4: Armazenamento e Segurança
            PrivacySection(
                title = "4. Armazenamento e Segurança",
                content = "Seus dados são armazenados de forma segura em servidores com criptografia, controle de acesso e práticas atualizadas de segurança. Apenas pessoas autorizadas podem acessá-los."
            )

            // Seção 5: Seus Direitos (conforme LGPD)
            PrivacySection(
                title = "5. Seus Direitos (conforme LGPD)",
                content = "Você tem o direito de:\n\n• Solicitar acesso aos seus dados pessoais;\n• Corrigir dados incompletos, inexatos ou desatualizados;\n• Solicitar a exclusão de dados desnecessários, excessivos;\n• Solicitar a portabilidade dos dados;\n• Revogar o consentimento;\n• Obter informações sobre o compartilhamento dos dados."
            )

            // Seção 6: Cookies e Tecnologias de Rastreamento
            PrivacySection(
                title = "6. Cookies e Tecnologias de Rastreamento",
                content = "Utilizamos tecnologias como cookies e identificadores de dispositivo para:\n\n• Manter você logado;\n• Personalizar a experiência;\n• Analisar o uso do app;\n• Melhorar a funcionalidade;\n\nVocê pode desativar cookies nas configurações do seu dispositivo."
            )

            // Seção 7: Contato
            PrivacySection(
                title = "7. Contato",
                content = "Em caso de dúvidas sobre esta Política de Privacidade, entre em contato pelo e-mail:\nprivacidade@linecut.app.br"
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
        
        // Conteúdo da seção - processado para lidar com bullet points
        val lines = content.split("\n")
        lines.forEach { line ->
            if (line.trim().startsWith("•")) {
                // Para linhas com bullet points, usar Row para alinhamento
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = line.trim().removePrefix("•").trim(),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF333333),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.weight(1f)
                    )
                }
            } else if (line.isNotBlank()) {
                // Para linhas normais, usar Text normal
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF333333),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(bottom = if (line.trim().isEmpty()) 0.dp else 4.dp)
                )
            } else {
                // Para linhas vazias, adicionar espaço
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
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
