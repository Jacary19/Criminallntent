package com.example.criminallntent

import android.content.Context
import androidx.room.Room


object CrimeDatabaseProvider {

    @Volatile
    private var INSTANCE: CrimeDatabase? = null

    fun getDatabase(context: Context): CrimeDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                CrimeDatabase::class.java,
                "criminalintent-database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}