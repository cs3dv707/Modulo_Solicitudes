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
        val trimmedTitulo = titulo.trim()
        val trimmedDescripcion = descripcion.trim()
        val trimmedCategoria = categoria.trim()
        val trimmedServicio = servicio.trim()

        return SolicitudValidationResult(
            tituloError = when {
                trimmedTitulo.isEmpty() -> "El título es obligatorio"
                trimmedTitulo.length < 5 -> "El título debe tener al menos 5 caracteres"
                trimmedTitulo.length > 60 -> "El título no puede exceder los 60 caracteres"
                else -> null
            },
            categoriaError = when {
                trimmedCategoria.isEmpty() -> "La categoría es obligatoria"
                trimmedCategoria.length < 3 -> "La categoría debe tener al menos 3 caracteres"
                else -> null
            },
            servicioError = when {
                trimmedServicio.isEmpty() -> "El servicio relacionado es obligatorio"
                trimmedServicio.length < 3 -> "El servicio relacionado debe tener al menos 3 caracteres"
                else -> null
            },
            descripcionError = when {
                trimmedDescripcion.isEmpty() -> "La descripción es obligatoria"
                trimmedDescripcion.length < 10 -> "La descripción debe tener al menos 10 caracteres"
                trimmedDescripcion.length > 500 -> "La descripción no puede exceder los 500 caracteres"
                else -> null
            }
        )
    }
}
