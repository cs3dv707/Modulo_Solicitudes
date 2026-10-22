package com.example.modulo_solicitudes

import com.example.modulo_solicitudes.ui.login.LoginValidation
import com.example.modulo_solicitudes.ui.login.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginValidationTest {

    @Test
    fun emptyFields_reportsErrors() {
        val result = LoginValidation.validateFields("", "")

        assertEquals("El usuario o correo es obligatorio", result.usuarioError)
        assertEquals("La contraseña es obligatoria", result.passwordError)
        assertFalse(result.isValid)
    }

    @Test
    fun shortUsuarioAndPassword_reportsMinLengthErrors() {
        val result = LoginValidation.validateFields("ad", "123")

        assertEquals("El usuario debe tener al menos 3 caracteres", result.usuarioError)
        assertEquals("La contraseña debe tener al menos 4 caracteres", result.passwordError)
        assertFalse(result.isValid)
    }

    @Test
    fun validFieldFormats_hasNoErrors() {
        val result = LoginValidation.validateFields("admin", "admin")

        assertNull(result.usuarioError)
        assertNull(result.passwordError)
        assertTrue(result.isValid)
    }

    @Test
    fun validAdminUsernameCredentials_accepted() {
        assertTrue(LoginValidation.validateCredentials("admin", "admin"))
        assertTrue(LoginValidation.validateCredentials("ADMIN", "admin123"))
    }

    @Test
    fun validAdminEmailCredentials_accepted() {
        assertTrue(LoginValidation.validateCredentials("admin@admin.com", "admin"))
        assertTrue(LoginValidation.validateCredentials("ADMIN@ADMIN.COM", "admin123"))
    }

    @Test
    fun invalidUserOrPassword_rejected() {
        assertFalse(LoginValidation.validateCredentials("user", "admin"))
        assertFalse(LoginValidation.validateCredentials("admin", "wrongpass"))
        assertFalse(LoginValidation.validateCredentials("user", "wrongpass"))
    }

    @Test
    fun viewModel_loginSuccessWithAdmin() {
        val viewModel = LoginViewModel()
        viewModel.onUsuarioChanged("admin")
        viewModel.onPasswordChanged("admin")
        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertTrue(state.isSuccess)
        assertNull(state.generalError)
        assertNull(state.usuarioError)
        assertNull(state.passwordError)
    }

    @Test
    fun viewModel_loginFailureWithInvalidCredentials() {
        val viewModel = LoginViewModel()
        viewModel.onUsuarioChanged("usuarioInvalido")
        viewModel.onPasswordChanged("claveInvalida")
        viewModel.onLoginClick()

        val state = viewModel.uiState.value
        assertFalse(state.isSuccess)
        assertEquals("Credenciales incorrectas. Verifique el usuario o la contraseña.", state.generalError)
    }
}
