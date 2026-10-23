package com.example.modulo_solicitudes.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val codigo: String,
    val titulo: String,
    val descripcion: String,
    val categoria: String,
    val servicio: String,
    val evidenciaUri: String? = null,
    val estado: String = "Recibida",
    val fechaHora: Long = System.currentTimeMillis(),
    val motivoCierre: String? = null
)
