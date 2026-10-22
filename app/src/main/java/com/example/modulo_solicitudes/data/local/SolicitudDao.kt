package com.example.modulo_solicitudes.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudDao {
    @Query("SELECT * FROM solicitudes ORDER BY fechaHora DESC")
    fun getAllSolicitudes(): Flow<List<SolicitudEntity>>

    @Query("SELECT * FROM solicitudes WHERE id = :id")
    fun getSolicitudById(id: Long): Flow<SolicitudEntity?>

    @Query("SELECT * FROM solicitudes WHERE codigo = :codigo")
    fun getSolicitudByCodigo(codigo: String): Flow<SolicitudEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSolicitud(solicitud: SolicitudEntity): Long

    @Update
    suspend fun updateSolicitud(solicitud: SolicitudEntity)

    @Delete
    suspend fun deleteSolicitud(solicitud: SolicitudEntity)
}
