package com.example.criminallntent

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crimes")
    suspend fun getCrimes(): List<Crime>

    @Query("SELECT * FROM crimes WHERE id = :id")
    suspend fun getCrime(id: String): Crime?

    @Insert
    suspend fun insertCrime(crime: Crime)

    @Update
    suspend fun updateCrime(crime: Crime)

    @Delete
    suspend fun deleteCrime(crime: Crime)
}