package com.br.linecut.ui.utils

import android.util.Patterns

/**
 * Utilitários para validação e formatação de campos
 */
object ValidationUtils {
    
    /**
     * Formata CPF com máscara XXX.XXX.XXX-XX
     * Recebe apenas dígitos como entrada
     */
    fun formatCpf(digits: String): String {
        return when (digits.length) {
            0 -> ""
            1 -> digits
            2 -> digits
            3 -> digits
            4 -> "${digits.substring(0, 3)}.${digits[3]}"
            5 -> "${digits.substring(0, 3)}.${digits.substring(3, 5)}"
            6 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}"
            7 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits[6]}"
            8 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 8)}"
            9 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}"
            10 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits[9]}"
            11 -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits.substring(9, 11)}"
            else -> "${digits.substring(0, 3)}.${digits.substring(3, 6)}.${digits.substring(6, 9)}-${digits.substring(9, 11)}"
        }
    }
    
    /**
     * Formata telefone com máscara (XX) XXXXX-XXXX
     * Recebe apenas dígitos como entrada
     */
    fun formatPhone(digits: String): String {
        return when (digits.length) {
            0 -> ""
            1 -> "($digits"
            2 -> "(${digits}) "
            3 -> "(${digits.substring(0, 2)}) ${digits[2]}"
            4 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 4)}"
            5 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 5)}"
            6 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 6)}"
            7 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}"
            8 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits[7]}"
            9 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7, 9)}"
            10 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7, 10)}"
            11 -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7, 11)}"
            else -> "(${digits.substring(0, 2)}) ${digits.substring(2, 7)}-${digits.substring(7, 11)}"
        }
    }
    
    /**
     * Valida CPF usando algoritmo oficial
     */
    fun isValidCpf(cpf: String): Boolean {
        val digitsOnly = cpf.filter { it.isDigit() }
        
        if (digitsOnly.length != 11) return false
        
        // Verifica se todos os dígitos são iguais
        if (digitsOnly.all { it == digitsOnly[0] }) return false
        
        // Calcula primeiro dígito verificador
        val firstDigit = calculateCpfDigit(digitsOnly.substring(0, 9))
        if (firstDigit != digitsOnly[9].digitToInt()) return false
        
        // Calcula segundo dígito verificador
        val secondDigit = calculateCpfDigit(digitsOnly.substring(0, 10))
        if (secondDigit != digitsOnly[10].digitToInt()) return false
        
        return true
    }
    
    private fun calculateCpfDigit(cpfPartial: String): Int {
        val weights = if (cpfPartial.length == 9) {
            listOf(10, 9, 8, 7, 6, 5, 4, 3, 2)
        } else {
            listOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
        }
        
        val sum = cpfPartial.mapIndexed { index, char ->
            char.digitToInt() * weights[index]
        }.sum()
        
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }
    
    /**
     * Valida telefone brasileiro
     * Recebe dígitos puros como entrada
     */
    fun isValidPhone(digits: String): Boolean {
        return digits.length == 10 || digits.length == 11
    }
    
    /**
     * Valida email
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    /**
     * Remove formatação do CPF
     */
    fun cleanCpf(cpf: String): String {
        return cpf.filter { it.isDigit() }
    }
    
    /**
     * Remove formatação do telefone
     */
    fun cleanPhone(phone: String): String {
        return phone.filter { it.isDigit() }
    }
}
