package com.example.modulo_solicitudes.data.repository

import com.example.modulo_solicitudes.data.local.SolicitudDao
import com.example.modulo_solicitudes.data.local.SolicitudEntity
import kotlinx.coroutines.flow.Flow

class SolicitudRepository(private val solicitudDao: SolicitudDao) {
    val allSolicitudes: Flow<List<SolicitudEntity>> = solicitudDao.getAllSolicitudes()

    fun getSolicitudById(id: Long): Flow<SolicitudEntity?> {
        return solicitudDao.getSolicitudById(id)
    }

    fun getSolicitudByCodigo(codigo: String): Flow<SolicitudEntity?> {
        return solicitudDao.getSolicitudByCodigo(codigo)
    }

    suspend fun insertSolicitud(solicitud: SolicitudEntity): Result<Long> {
        return runCatching { solicitudDao.insertSolicitud(solicitud) }
    }

    suspend fun updateSolicitud(solicitud: SolicitudEntity) {
        solicitudDao.updateSolicitud(solicitud)
    }

    suspend fun deleteSolicitud(solicitud: SolicitudEntity): Result<Unit> {
        return runCatching { solicitudDao.deleteSolicitud(solicitud) }
    }
}
