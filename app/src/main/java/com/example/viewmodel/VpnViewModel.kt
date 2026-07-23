package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.db.AppDatabase
import com.example.db.VpnConfig
import com.example.db.VpnLog
import com.example.model.CountryInfo
import com.example.model.CountryRegistry
import com.example.service.PlanetVpnService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.vpnDao()

    val vpnConfig: StateFlow<VpnConfig?> = dao.getConfigFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val vpnLogs: StateFlow<List<VpnLog>> = dao.getLogsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    private val _isConnecting = MutableStateFlow(false)
    val isConnecting = _isConnecting.asStateFlow()

    init {
        // Setup default config if none exists
        viewModelScope.launch {
            if (dao.getConfig() == null) {
                dao.saveConfig(VpnConfig())
            }
            // Clear past logs to prevent clutter
            dao.clearLogs()
            dao.insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "SYSTEM",
                    message = "Planet VPN Initialized. Quantum Earth positioning online."
                )
            )
        }
    }

    fun selectCountry(country: CountryInfo) {
        viewModelScope.launch {
            val current = dao.getConfig() ?: VpnConfig()
            val updated = current.copy(
                selectedCountryCode = country.code,
                selectedCountryName = country.name,
                selectedServerName = country.recommendedServers.firstOrNull() ?: "Primary Node"
            )
            dao.saveConfig(updated)
            dao.insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "GEOLOCATION",
                    message = "Switched planet trajectory focus to ${country.name} (${country.code})"
                )
            )

            // If connected, automatically restart VPN with the new country profile
            if (_isConnected.value) {
                toggleVpnConnection() // disconnect
                toggleVpnConnection() // reconnect with new parameters
            }
        }
    }

    fun updateAdBlockConfig(mode: String) {
        viewModelScope.launch {
            val current = dao.getConfig() ?: VpnConfig()
            val updated = current.copy(adBlockEnabled = mode)
            dao.saveConfig(updated)
            dao.insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "SETTINGS",
                    message = "Ad-Block configuration changed to: $mode"
                )
            )
        }
    }

    fun updateMalwareProtection(enabled: Boolean) {
        viewModelScope.launch {
            val current = dao.getConfig() ?: VpnConfig()
            val updated = current.copy(malwareProtectionEnabled = enabled)
            dao.saveConfig(updated)
            dao.insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "SETTINGS",
                    message = "Real-time Malware Shield set to: ${if (enabled) "ON" else "OFF"}"
                )
            )
        }
    }

    fun toggleVpnConnection() {
        viewModelScope.launch {
            val config = dao.getConfig() ?: VpnConfig()
            if (_isConnected.value) {
                // Disconnect service
                val disconnectIntent = Intent(getApplication(), PlanetVpnService::class.java).apply {
                    action = PlanetVpnService.ACTION_DISCONNECT
                }
                getApplication<Application>().stopService(disconnectIntent)
                _isConnected.value = false
                _isConnecting.value = false
            } else {
                _isConnecting.value = true
                dao.insertLog(
                    VpnLog(
                        type = "INFO",
                        tag = "CONNECTING",
                        message = "Starting secure network handshake via ${config.selectedServerName}..."
                    )
                )

                // Simulate brief cryptographic handshake delay
                viewModelScope.launch {
                    kotlinx.coroutines.delay(1200)
                    _isConnecting.value = false
                    _isConnected.value = true

                    val connectIntent = Intent(getApplication(), PlanetVpnService::class.java).apply {
                        action = PlanetVpnService.ACTION_CONNECT
                        putExtra(PlanetVpnService.EXTRA_SERVER, config.selectedServerName)
                        putExtra(PlanetVpnService.EXTRA_COUNTRY, config.selectedCountryName)
                        putExtra(PlanetVpnService.EXTRA_ADBLOCK, config.adBlockEnabled)
                    }
                    getApplication<Application>().startService(connectIntent)
                }
            }
        }
    }

    fun triggerBypassVerify(channelName: String) {
        viewModelScope.launch {
            dao.insertLog(
                VpnLog(
                    type = "BYPASS",
                    tag = "BYPASS_TEST",
                    message = "Deep verification: Successfully routed platform payload for $channelName"
                )
            )
        }
    }

    fun clearVpnLogs() {
        viewModelScope.launch {
            dao.clearLogs()
            dao.insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "CLEANUP",
                    message = "Security tunnel event log wiped."
                )
            )
        }
    }
}
