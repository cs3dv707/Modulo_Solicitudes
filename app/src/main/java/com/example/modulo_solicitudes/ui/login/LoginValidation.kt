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

    const val USER_USERNAME = "usuario"
    const val USER_USERNAME_ALT = "user"
    const val USER_EMAIL = "usuario@correo.com"
    const val USER_PASSWORD = "usuario"
    const val USER_PASSWORD_ALT = "usuario123"

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

    fun getRoleForCredentials(usuario: String, password: String): UserRole? {
        val trimmedUsuario = usuario.trim()
        val isAdminUser = trimmedUsuario.equals(ADMIN_USERNAME, ignoreCase = true) ||
                trimmedUsuario.equals(ADMIN_EMAIL, ignoreCase = true)
        val isAdminPass = password == ADMIN_PASSWORD || password == ADMIN_PASSWORD_ALT

        if (isAdminUser && isAdminPass) {
            return UserRole.ADMIN
        }

        val isStandardUser = trimmedUsuario.equals(USER_USERNAME, ignoreCase = true) ||
                trimmedUsuario.equals(USER_USERNAME_ALT, ignoreCase = true) ||
                trimmedUsuario.equals(USER_EMAIL, ignoreCase = true) ||
                trimmedUsuario.startsWith("user", ignoreCase = true) ||
                trimmedUsuario.startsWith("usuario", ignoreCase = true)
        val isUserPass = password == USER_PASSWORD || password == USER_PASSWORD_ALT ||
                password == "user" || password == "123456"

        if (isStandardUser && isUserPass) {
            return UserRole.USER
        }

        return null
    }

    fun validateCredentials(usuario: String, password: String): Boolean {
        return getRoleForCredentials(usuario, password) != null
    }
}
