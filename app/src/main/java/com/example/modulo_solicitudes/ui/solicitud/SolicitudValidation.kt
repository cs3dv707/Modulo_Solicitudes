package com.example.modulo_solicitudes.ui.solicitud

data class SolicitudValidationResult(
    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val servicioError: String? = null
) {
    val isValid: Boolean
        get() = tituloError == null &&
            descripcionError == null &&
            categoriaError == null &&
            servicioError == null
}

object SolicitudValidation {
    fun validate(
        titulo: String,
        descripcion: String,
        categoria: String,
        servicio: String
    ): SolicitudValidationResult {
        return SolicitudValidationResult(
            tituloError = if (titulo.isBlank()) "El título es obligatorio" else null,
            descripcionError = when {
                descripcion.isBlank() -> "La descripción es obligatoria"
                descripcion.trim().length < 10 -> "La descripción debe tener al menos 10 caracteres"
                else -> null
            },
            categoriaError = if (categoria.isBlank()) "La categoría es obligatoria" else null,
            servicioError = if (servicio.isBlank()) "El servicio relacionado es obligatorio" else null
        )
    }
}
