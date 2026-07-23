package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vpn_config")
data class VpnConfig(
    @PrimaryKey val id: Int = 1,
    val selectedCountryCode: String = "GB",
    val selectedCountryName: String = "United Kingdom",
    val selectedServerName: String = "London - Sentinel Grid",
    val adBlockEnabled: String = "DNS_FILTER", // "OFF", "DNS_FILTER", "ULTRA"
    val malwareProtectionEnabled: Boolean = true,
    val bypassBbcIplayer: Boolean = true,
    val bypassBoomerang: Boolean = true,
    val bypassPlutoTv: Boolean = true,
    val bypassRaiPlay: Boolean = true,
    val bypassCbbc: Boolean = true
)

@Entity(tableName = "vpn_log")
data class VpnLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String, // "INFO", "SUCCESS", "BLOCKED", "BYPASS"
    val tag: String,  // e.g. "DNS", "BYPASS", "CONNECT"
    val message: String
)
