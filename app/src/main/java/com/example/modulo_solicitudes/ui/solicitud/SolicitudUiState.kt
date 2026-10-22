package com.example.modulo_solicitudes.ui.solicitud

import com.example.modulo_solicitudes.data.local.SolicitudEntity

data class SolicitudUiState(
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val servicio: String = "",
    val evidenciaUri: String? = null,

    val tituloError: String? = null,
    val descripcionError: String? = null,
    val categoriaError: String? = null,
    val servicioError: String? = null,

    val guardando: Boolean = false,
    val guardadoExitosamente: Boolean = false,
    val guardadoError: String? = null,
    val solicitudSeleccionada: SolicitudEntity? = null
)
