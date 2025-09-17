package com.br.linecut.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.components.LineCutDesignSystem
import com.br.linecut.ui.components.LineCutBottomNavigationBar
import com.br.linecut.ui.theme.*

@Composable
fun TermsAndConditionsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(LineCutDesignSystem.screenBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Faixa branca superior (67px = ~67.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(67.dp)
                    .background(LineCutDesignSystem.screenBackgroundColor)
            )
            
            // Conteúdo principal com padding horizontal de 35px
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(22.dp)) // top-[89px] - 67px = 22dp
                
                // Título principal - conforme Figma: w-[326px] centralizado
                Text(
                    text = "TERMOS E CONDIÇÕES DE USO DO APLICATIVO LINECUT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LineCutRed, // #9c0202
                        fontWeight = FontWeight.SemiBold, // Poppins:SemiBold
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 7.5.dp) // Centralizar w-[326px] em w-[341px]
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Data da última atualização - alinhada à direita conforme Figma
                Text(
                    text = "Data da última atualização: 01/05/2025",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(15.dp)) // 35px - 20px = 15dp
                
                // Conteúdo principal dos termos - w-[341px] conforme Figma
                Text(
                    text = buildTermsContent(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D), // #7d7d7d
                        fontWeight = FontWeight.Normal, // Poppins:Regular
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Texto final
                Text(
                    text = "Por favor, leia os termos com atenção. Ao continuar, você concorda com as condições estabelecidas.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Espaço para o bottom navigation
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            // Faixa branca inferior (44px = ~44.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(LineCutDesignSystem.screenBackgroundColor)
            )
        }
        
        // Botão voltar - posição absoluta conforme Figma (left-[25px], top-[88px])
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(start = 25.dp, top = 88.dp)
                .size(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_filter_arrow),
                contentDescription = "Voltar",
                tint = Color.Unspecified,
                modifier = Modifier.size(20.dp)
            )
        }
        
        // Bottom Navigation Bar
        LineCutBottomNavigationBar(
            onHomeClick = onHomeClick,
            onSearchClick = onSearchClick,
            onNotificationClick = onNotificationClick,
            onOrdersClick = onOrdersClick,
            onProfileClick = onProfileClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

private fun buildTermsContent(): String {
    return """Este Termo de Uso ("Termo") regula o uso do aplicativo LineCut, desenvolvido para otimizar o atendimento em praças de alimentação, permitindo a gestão de filas e pedidos de forma digital, prática e eficiente. Ao utilizar o LineCut, o usuário declara ter lido, compreendido e aceitado integralmente os termos abaixo. Se você não concordar com estes termos, por favor, não utilize o aplicativo. — 1. OBJETO: O aplicativo LineCut oferece uma plataforma digital que permite: clientes realizarem pedidos antecipados em lanchonetes cadastradas; acompanharem sua posição na fila e o tempo estimado de atendimento; estabelecimentos gerenciarem seus pedidos, filas e estoques; administradores monitorarem o funcionamento do sistema e gerenciarem permissões. — 2. CADASTRO E ACESSO: O acesso ao LineCut requer a criação de uma conta, com informações pessoais verídicas, como nome, e-mail e número de telefone. O usuário é o único responsável pela veracidade dos dados fornecidos e pela guarda de suas credenciais de acesso. O uso do aplicativo por menores de 18 anos requer consentimento dos responsáveis legais. — 3. USO DA PLATAFORMA: É proibido: utilizar o LineCut para fins ilícitos; inserir informações falsas, ofensivas ou fraudulentas; comprometer a segurança do sistema; tentar obter acesso não autorizado a contas ou funcionalidades restritas. O LineCut pode utilizar notificações via aplicativo (push), e-mail ou SMS para informar o status de pedidos e atualizações importantes. — 4. PAGAMENTOS: O LineCut pode integrar sistemas de pagamento para facilitar as transações entre usuários e estabelecimentos. As taxas cobradas, formas de pagamento e políticas de reembolso serão informadas previamente, caso se apliquem. — 5. PRIVACIDADE E PROTEÇÃO DE DADOS: Ao utilizar o LineCut, o usuário concorda com a coleta e o tratamento de seus dados conforme a Política de Privacidade do aplicativo, em conformidade com a Lei Geral de Proteção de Dados (LGPD - Lei nº 13.709/18). Os dados serão utilizados apenas para fins de operação e aprimoramento do serviço, sendo armazenados com segurança. — 6. RESPONSABILIDADES: O LineCut não se responsabiliza por: atrasos ou falhas na entrega de pedidos causadas por terceiros; danos decorrentes do mau uso do aplicativo; informações falsas inseridas por estabelecimentos ou usuários. O LineCut se compromete a manter o sistema disponível, mas não garante funcionamento ininterrupto ou isento de erros. — 7. PROPRIEDADE INTELECTUAL: Todo o conteúdo do LineCut (marcas, layout, textos, códigos, imagens) é de propriedade exclusiva dos desenvolvedores, sendo proibida sua reprodução sem autorização prévia. — 8. MODIFICAÇÕES: O LineCut reserva-se o direito de alterar estes Termos a qualquer momento. Mudanças significativas serão comunicadas ao usuário. O uso contínuo do aplicativo após alterações será considerado como aceitação dos novos termos. — 9. CANCELAMENTO E ENCERRAMENTO: O usuário pode solicitar a exclusão de sua conta a qualquer momento. O LineCut também poderá encerrar contas que violem este Termo, sem aviso prévio. — 10. LEI APLICÁVEL E FORO: Este Termo é regido pelas leis da República Federativa do Brasil. Fica eleito o foro da comarca de [cidade/estado do desenvolvedor] para dirimir quaisquer controvérsias. — Ao clicar em "Aceito", você declara estar de acordo com os Termos e Condições de Uso do LineCut."""
}

@Preview(showBackground = true)
@Composable
fun TermsAndConditionsScreenPreview() {
    LineCutTheme {
        TermsAndConditionsScreen()
    }
}

@Preview(
    name = "Terms and Conditions - Light Mode",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TermsAndConditionsScreenLightPreview() {
    LineCutTheme {
        TermsAndConditionsScreen(
            onBackClick = { println("Back clicked") },
            onHomeClick = { println("Home clicked") },
            onSearchClick = { println("Search clicked") },
            onNotificationClick = { println("Notifications clicked") },
            onOrdersClick = { println("Orders clicked") },
            onProfileClick = { println("Profile clicked") }
        )
    }
}

@Preview(
    name = "Terms and Conditions - Content Focus",
    showBackground = true,
    widthDp = 360,
    heightDp = 800
)
@Composable
fun TermsAndConditionsContentPreview() {
    LineCutTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LineCutDesignSystem.screenBackgroundColor)
                .padding(35.dp)
        ) {
            Column {
                Text(
                    text = "TERMOS E CONDIÇÕES DE USO DO APLICATIVO LINECUT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = LineCutRed,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Data da última atualização: 01/05/2025",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(15.dp))
                
                Text(
                    text = buildTermsContent().take(500) + "...", // Mostra apenas parte do conteúdo
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF7D7D7D),
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
