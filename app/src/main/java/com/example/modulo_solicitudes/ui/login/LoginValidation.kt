package com.example.modulo_solicitudes.ui.login

data class LoginValidationResult(
    val usuarioError: String? = null,
    val passwordError: String? = null
) {
    val isValid: Boolean
        get() = usuarioError == null && passwordError == null
}

object LoginValidation {
    const val ADMIN_USERNAME = "admin"
    const val ADMIN_EMAIL = "admin@admin.com"
    const val ADMIN_PASSWORD = "admin"
    const val ADMIN_PASSWORD_ALT = "admin123"

    fun validateFields(usuario: String, password: String): LoginValidationResult {
        val trimmedUsuario = usuario.trim()

        return LoginValidationResult(
            usuarioError = when {
                trimmedUsuario.isEmpty() -> "El usuario o correo es obligatorio"
                trimmedUsuario.length < 3 -> "El usuario debe tener al menos 3 caracteres"
                else -> null
            },
            passwordError = when {
                password.isEmpty() -> "La contraseña es obligatoria"
                password.length < 4 -> "La contraseña debe tener al menos 4 caracteres"
                else -> null
            }
        )
    }

    fun validateCredentials(usuario: String, password: String): Boolean {
        val trimmedUsuario = usuario.trim()
        val isCorrectUser = trimmedUsuario.equals(ADMIN_USERNAME, ignoreCase = true) ||
                trimmedUsuario.equals(ADMIN_EMAIL, ignoreCase = true)
        val isCorrectPassword = password == ADMIN_PASSWORD || password == ADMIN_PASSWORD_ALT

        return isCorrectUser && isCorrectPassword
    }
}
