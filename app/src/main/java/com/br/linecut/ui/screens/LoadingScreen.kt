package com.br.linecut.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.br.linecut.ui.theme.LineCutTheme

/**
 * Tela de carregamento com animação circular
 * Baseada no design do Figma (node-id: 107-15)
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    // Animação infinita de rotação
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8F7)), // bg-[#fff8f7]
        contentAlignment = Alignment.Center
    ) {
        // Círculo de loading animado
        Canvas(
            modifier = Modifier.size(99.dp)
        ) {
            // Desenhar círculo de fundo (mais claro)
            drawArc(
                color = Color(0xFFE8B4A8), // Tom mais claro do vermelho
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
            
            // Desenhar arco animado (mais escuro)
            drawArc(
                color = Color(0xFFBE4A3D), // Tom mais escuro do vermelho LineCut
                startAngle = rotation,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(
                    width = 8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoadingScreenPreview() {
    LineCutTheme {
        LoadingScreen()
    }
}
