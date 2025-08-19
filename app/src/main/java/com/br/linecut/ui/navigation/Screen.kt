package com.br.linecut.ui.navigation

/**
 * Enum que define as telas disponíveis na aplicação
 */
enum class Screen {
    LOGIN,
    SIGNUP,
    FORGOT_PASSWORD,
    EMAIL_SENT,
    STORES,
    STORE_DETAIL,
    CART,
    ORDER_SUMMARY,
    PAYMENT_METHOD
}

/**
 * Classe para gerenciar a navegação entre telas
 */
class NavigationState {
    companion object {
        const val LOGIN = "login"
        const val SIGNUP = "signup"
        const val FORGOT_PASSWORD = "forgot_password"
        const val EMAIL_SENT = "email_sent"
    }
}
