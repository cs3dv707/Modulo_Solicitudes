package com.example.modulo_solicitudes.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onUsuarioChanged(nuevoUsuario: String) {
        _uiState.update {
            it.copy(
                usuario = nuevoUsuario,
                usuarioError = null,
                generalError = null
            )
        }
    }

    fun onPasswordChanged(nuevaPassword: String) {
        _uiState.update {
            it.copy(
                password = nuevaPassword,
                passwordError = null,
                generalError = null
            )
        }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun onLoginClick() {
        val currentState = _uiState.value
        val validationResult = LoginValidation.validateFields(
            currentState.usuario,
            currentState.password
        )

        if (!validationResult.isValid) {
            _uiState.update {
                it.copy(
                    usuarioError = validationResult.usuarioError,
                    passwordError = validationResult.passwordError,
                    generalError = null
                )
            }
            return
        }

        val areCredentialsValid = LoginValidation.validateCredentials(
            currentState.usuario,
            currentState.password
        )

        if (areCredentialsValid) {
            _uiState.update {
                it.copy(
                    usuarioError = null,
                    passwordError = null,
                    generalError = null,
                    isSuccess = true
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    generalError = "Credenciales incorrectas. Verifique el usuario o la contraseña."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
