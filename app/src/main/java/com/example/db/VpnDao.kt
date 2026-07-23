package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface VpnDao {
    @Query("SELECT * FROM vpn_config WHERE id = 1 LIMIT 1")
    fun getConfigFlow(): Flow<VpnConfig?>

    @Query("SELECT * FROM vpn_config WHERE id = 1 LIMIT 1")
    suspend fun getConfig(): VpnConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: VpnConfig)

    @Query("SELECT * FROM vpn_log ORDER BY timestamp DESC LIMIT 200")
    fun getLogsFlow(): Flow<List<VpnLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: VpnLog)

    @Query("DELETE FROM vpn_log")
    suspend fun clearLogs()
}
