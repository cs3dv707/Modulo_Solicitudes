package com.example.modulo_solicitudes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SolicitudEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE solicitudes ADD COLUMN evidenciaUri TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "solicitudes_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
