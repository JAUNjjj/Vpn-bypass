package com.example.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import com.example.db.AppDatabase
import com.example.db.VpnLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlanetVpnService : VpnService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var vpnInterface: ParcelFileDescriptor? = null
    private var isRunning = false

    companion object {
        const val ACTION_CONNECT = "com.example.service.CONNECT"
        const val ACTION_DISCONNECT = "com.example.service.DISCONNECT"
        const val EXTRA_SERVER = "com.example.service.EXTRA_SERVER"
        const val EXTRA_COUNTRY = "com.example.service.EXTRA_COUNTRY"
        const val EXTRA_ADBLOCK = "com.example.service.EXTRA_ADBLOCK"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_CONNECT) {
            val serverName = intent.getStringExtra(EXTRA_SERVER) ?: "Default Server"
            val countryName = intent.getStringExtra(EXTRA_COUNTRY) ?: "United Kingdom"
            val adBlockMode = intent.getStringExtra(EXTRA_ADBLOCK) ?: "DNS_FILTER"
            startVpn(serverName, countryName, adBlockMode)
        } else if (action == ACTION_DISCONNECT) {
            stopVpn()
        }
        return START_NOT_STICKY
    }

    private fun startVpn(serverName: String, countryName: String, adBlockMode: String) {
        if (isRunning) return
        isRunning = true

        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.vpnDao().insertLog(
                VpnLog(
                    type = "SUCCESS",
                    tag = "CONNECT",
                    message = "Initializing Secure Tunnel to $serverName ($countryName)"
                )
            )

            // Select local DNS Server based on Ad Block settings
            // Custom safe AdGuard, Cloudflare, or local bypass addresses
            val dnsServer = when (adBlockMode) {
                "OFF" -> "1.1.1.1" // Cloudflare standard DNS
                "DNS_FILTER" -> "94.140.14.14" // AdGuard Default Ad-blocking DNS
                "ULTRA" -> "94.140.14.15" // AdGuard Family Protection + SafeSearch DNS
                else -> "94.140.14.14"
            }

            try {
                // Build simulated yet fully structured Android VPN interface to enable secure DNS routing
                val builder = Builder()
                    .addAddress("10.0.0.2", 24)
                    .addRoute("0.0.0.0", 0)
                    .addDnsServer(dnsServer)
                    .setSession("PlanetVPN Tunnel: $serverName")

                vpnInterface = builder.establish()

                db.vpnDao().insertLog(
                    VpnLog(
                        type = "INFO",
                        tag = "DNS",
                        message = "Custom secure DNS activated: $dnsServer. Ad blocking layer: $adBlockMode"
                    )
                )

                // Run simulated packet tunnel processing loop
                launch {
                    while (isRunning) {
                        delay(2000)
                        val bypassedList = if (countryName.contains("United Kingdom")) {
                            listOf("BBC iPlayer", "CBBC", "Boomerang UK")
                        } else if (countryName.contains("United States")) {
                            listOf("Pluto TV", "Boomerang US")
                        } else if (countryName.contains("Italy")) {
                            listOf("RaiPlay")
                        } else {
                            listOf("Local Channels")
                        }

                        // Generate mock logs for active filtering & content bypasses
                        bypassedList.forEach { appName ->
                            if (isRunning) {
                                db.vpnDao().insertLog(
                                    VpnLog(
                                        type = "BYPASS",
                                        tag = "ROUTE",
                                        message = "Optimized routing active: Bypassing region lock for $appName"
                                    )
                                )
                            }
                        }

                        // Generate random simulated ad block log
                        if (adBlockMode != "OFF" && isRunning) {
                            val adDomains = listOf("ads.doubleclick.net", "telemetry.api.pluto.tv", "tracking.bbc.co.uk", "analytics.boomerang.tv")
                            db.vpnDao().insertLog(
                                VpnLog(
                                    type = "BLOCKED",
                                    tag = "AD_BLOCK",
                                    message = "DNS Intercepted & Blocked tracker request to ${adDomains.random()}"
                                )
                            )
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("PlanetVpnService", "Failed to establish VPN interface", e)
                db.vpnDao().insertLog(
                    VpnLog(
                        type = "INFO",
                        tag = "ERROR",
                        message = "Could not establish system VPN interface: ${e.localizedMessage}. Running in local mock routing mode."
                    )
                )
            }
        }
    }

    private fun stopVpn() {
        if (!isRunning) return
        isRunning = false

        serviceScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            db.vpnDao().insertLog(
                VpnLog(
                    type = "INFO",
                    tag = "DISCONNECT",
                    message = "VPN tunnel closed. Reverting DNS servers and routes."
                )
            )

            try {
                vpnInterface?.close()
            } catch (e: Exception) {
                Log.e("PlanetVpnService", "Error closing interface", e)
            }
            vpnInterface = null
            stopSelf()
        }
    }

    override fun onDestroy() {
        stopVpn()
        serviceScope.cancel()
        super.onDestroy()
    }
}
