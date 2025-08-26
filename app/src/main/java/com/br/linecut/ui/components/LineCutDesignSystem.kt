package com.br.linecut.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.br.linecut.R
import com.br.linecut.ui.theme.*

/**
 * LineCut Design System Components
 * 
 * Este arquivo centraliza todos os componentes reutilizáveis do design system.
 * Mantenha a consistência visual usando estes componentes em todas as telas.
 */

/**
 * LineCut Design System
 * 
 * Objeto que centraliza as definições de design do app
 */
object LineCutDesignSystem {
    /**
     * Cor de background padrão para todas as telas
     */
    val screenBackgroundColor: Color = Color(0xFFFFFFFF) // Branco (#FFFFFF)
}

// Text Components
@Composable
fun LineCutTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineLarge.copy(
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold
        ),
        modifier = modifier
    )
}

@Composable
fun LineCutSubtitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            color = TextSecondary
        ),
        modifier = modifier
    )
}

@Composable
fun LineCutBodyText(
    text: String,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = TextPrimary
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = color
        ),
        modifier = modifier
    )
}

@Composable
fun LineCutLinkText(
    text: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    underline: Boolean = true
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall.copy(
            color = TextPrimary,
            fontWeight = FontWeight.Medium,
            textDecoration = if (underline) TextDecoration.Underline else TextDecoration.None
        ),
        modifier = modifier.clickableNoRipple { onClick() }
    )
}

// Logo Component
@Composable
fun LineCutLogo(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.linecut_login_icon),
        contentDescription = "Logo LineCut",
        modifier = Modifier
            .size(296.dp)
            .offset(x = 0.dp, y = 41.dp)
    )
}

// Spacing Components
@Composable
fun LineCutSpacer(size: LineCutSpacing) {
    Spacer(modifier = Modifier.height(size.value))
}

enum class LineCutSpacing(val value: androidx.compose.ui.unit.Dp) {
    XSmall(4.dp),
    Small(8.dp),
    Medium(16.dp),
    Large(24.dp),
    XLarge(32.dp),
    XXLarge(48.dp)
}

// Extension for clickable without ripple
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    ) { onClick() }
}
