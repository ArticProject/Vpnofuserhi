package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.VpnSession
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnSessionDao {
    @Query("SELECT * FROM vpn_sessions ORDER BY startTimeMillis DESC")
    fun getAllSessions(): Flow<List<VpnSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: VpnSession): Long

    @Query("DELETE FROM vpn_sessions")
    suspend fun clearAll()

    @Query("DELETE FROM vpn_sessions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
