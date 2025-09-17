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
    PAYMENT_METHOD,
    QR_CODE_PIX,
    PICKUP_QR,
    PROFILE,
    ACCOUNT_DATA,
    NOTIFICATIONS,
    PAYMENTS,
    ORDERS,
    ORDER_DETAILS,
    HELP,
    SETTINGS,
    TERMS_AND_CONDITIONS,
    PRIVACY_POLICY,
    CLOSE_ACCOUNT,
    ACCOUNT_CLOSED
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
