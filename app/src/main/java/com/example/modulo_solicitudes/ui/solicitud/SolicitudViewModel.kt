package com.example.modulo_solicitudes.ui.solicitud

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.modulo_solicitudes.data.local.SolicitudEntity
import com.example.modulo_solicitudes.data.repository.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SolicitudViewModel(private val repository: SolicitudRepository) : ViewModel() {

    val solicitudes: StateFlow<List<SolicitudEntity>> = repository.allSolicitudes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow(SolicitudUiState())
    val uiState: StateFlow<SolicitudUiState> = _uiState.asStateFlow()

    fun onTituloChange(titulo: String) {
        _uiState.update { currentState ->
            currentState.copy(
                titulo = titulo,
                tituloError = SolicitudValidation.validate(
                    titulo, currentState.descripcion, currentState.categoria, currentState.servicio
                ).tituloError,
                guardadoError = null
            )
        }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update { currentState ->
            val error = SolicitudValidation.validate(
                currentState.titulo, descripcion, currentState.categoria, currentState.servicio
            ).descripcionError
            currentState.copy(
                descripcion = descripcion,
                descripcionError = error,
                guardadoError = null
            )
        }
    }

    fun onCategoriaChange(categoria: String) {
        _uiState.update { currentState ->
            currentState.copy(
                categoria = categoria,
                categoriaError = SolicitudValidation.validate(
                    currentState.titulo, currentState.descripcion, categoria, currentState.servicio
                ).categoriaError,
                guardadoError = null
            )
        }
    }

    fun onServicioChange(servicio: String) {
        _uiState.update { currentState ->
            currentState.copy(
                servicio = servicio,
                servicioError = SolicitudValidation.validate(
                    currentState.titulo, currentState.descripcion, currentState.categoria, servicio
                ).servicioError,
                guardadoError = null
            )
        }
    }

    fun onEvidenciaUriChange(uriString: String?) {
        _uiState.update { currentState ->
            currentState.copy(evidenciaUri = uriString)
        }
    }

    fun validarFormulario(): Boolean {
        val state = _uiState.value

        val result = SolicitudValidation.validate(
            state.titulo, state.descripcion, state.categoria, state.servicio
        )

        _uiState.update {
            it.copy(
                tituloError = result.tituloError,
                descripcionError = result.descripcionError,
                categoriaError = result.categoriaError,
                servicioError = result.servicioError
            )
        }

        return result.isValid
    }

    fun guardarSolicitud(onSuccess: () -> Unit) {
        if (!validarFormulario()) return

        val state = _uiState.value
        _uiState.update { it.copy(guardando = true, guardadoError = null) }

        viewModelScope.launch {
            val codigoGenerado = "SOL-${System.currentTimeMillis().toString().takeLast(5)}"
            val nuevaSolicitud = SolicitudEntity(
                codigo = codigoGenerado,
                titulo = state.titulo.trim(),
                descripcion = state.descripcion.trim(),
                categoria = state.categoria.trim(),
                servicio = state.servicio.trim(),
                evidenciaUri = state.evidenciaUri,
                estado = "Recibida",
                fechaHora = System.currentTimeMillis()
            )

            val result = repository.insertSolicitud(nuevaSolicitud)
            result.onSuccess {
                _uiState.update {
                    it.copy(guardando = false, guardadoExitosamente = true)
                }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        guardando = false,
                        guardadoError = error.localizedMessage
                            ?: "No se pudo guardar la solicitud. Intenta nuevamente."
                    )
                }
            }
        }
    }

    fun seleccionarSolicitud(solicitud: SolicitudEntity) {
        _uiState.update { it.copy(solicitudSeleccionada = solicitud) }
    }

    fun cargarSolicitudPorId(id: Long) {
        viewModelScope.launch {
            repository.getSolicitudById(id).collect { solicitud ->
                _uiState.update { it.copy(solicitudSeleccionada = solicitud) }
            }
        }
    }

    fun eliminarSolicitud(solicitud: SolicitudEntity) {
        viewModelScope.launch {
            repository.deleteSolicitud(solicitud)
        }
    }

    fun resetFormulario() {
        _uiState.update {
            SolicitudUiState()
        }
    }

    companion object {
        fun provideFactory(repository: SolicitudRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(SolicitudViewModel::class.java)) {
                        return SolicitudViewModel(repository) as T
                    }
                    throw IllegalArgumentException("ViewModel desconocida")
                }
            }
    }
}
