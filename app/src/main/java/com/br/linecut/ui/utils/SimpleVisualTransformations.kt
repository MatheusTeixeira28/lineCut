package com.br.linecut.ui.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Visual Transformation simples para CPF que não interfere no cursor
 */
class SimpleCpfVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val formatted = when {
            digits.length <= 3 -> digits
            digits.length <= 6 -> "${digits.substring(0, 3)}.${digits.substring(3)}"
            digits.length <= 9 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6)}"
            else -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits.substring(9)}"
        }
        
        return TransformedText(
            AnnotatedString(formatted),
            SimpleCpfOffsetMapping(digits.length)
        )
    }
}

/**
 * Offset mapping simples para CPF
 */
private class SimpleCpfOffsetMapping(private val digitCount: Int) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return when {
            offset <= 3 -> offset
            offset <= 6 -> offset + 1
            offset <= 9 -> offset + 2
            else -> offset + 3
        }.coerceAtMost(getTransformedLength())
    }
    
    override fun transformedToOriginal(offset: Int): Int {
        return when {
            offset <= 3 -> offset
            offset <= 7 -> offset - 1
            offset <= 11 -> offset - 2
            else -> offset - 3
        }.coerceAtMost(digitCount)
    }
    
    private fun getTransformedLength(): Int = when {
        digitCount <= 3 -> digitCount
        digitCount <= 6 -> digitCount + 1
        digitCount <= 9 -> digitCount + 2
        else -> digitCount + 3
    }
}

/**
 * Visual Transformation simples para telefone que não interfere no cursor
 */
class SimplePhoneVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter { it.isDigit() }
        val formatted = when {
            digits.isEmpty() -> ""
            digits.length == 1 -> "(${digits}"
            digits.length <= 2 -> "(${digits})"
            digits.length <= 7 -> "(${digits.substring(0, 2)}) ${digits.substring(2)}"
            else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7)}"
        }
        
        return TransformedText(
            AnnotatedString(formatted),
            SimplePhoneOffsetMapping(digits.length)
        )
    }
}

/**
 * Offset mapping simples para telefone
 */
private class SimplePhoneOffsetMapping(private val digitCount: Int) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        return when {
            offset == 0 -> 1
            offset <= 2 -> offset + 1
            offset <= 7 -> offset + 3
            else -> offset + 4
        }.coerceAtMost(getTransformedLength())
    }
    
    override fun transformedToOriginal(offset: Int): Int {
        return when {
            offset <= 1 -> 0
            offset <= 3 -> offset - 1
            offset <= 10 -> offset - 3
            else -> offset - 4
        }.coerceAtMost(digitCount)
    }
    
    private fun getTransformedLength(): Int = when {
        digitCount == 0 -> 0
        digitCount == 1 -> 2
        digitCount <= 2 -> 3
        digitCount <= 7 -> digitCount + 3
        else -> digitCount + 4
    }
}
