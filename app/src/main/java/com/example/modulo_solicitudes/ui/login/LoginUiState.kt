package com.example.modulo_solicitudes.ui.login

data class LoginUiState(
    val usuario: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val usuarioError: String? = null,
    val passwordError: String? = null,
    val generalError: String? = null,
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val loggedRole: UserRole = UserRole.USER
)
