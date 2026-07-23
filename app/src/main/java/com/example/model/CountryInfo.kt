package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.WarningRed

data class CountryInfo(
    val code: String, // ISO Code
    val name: String,
    val flagEmoji: String,
    val latitude: Float, // For drawing coordinates relative to standard Mercator / spherical globe projections
    val longitude: Float,
    val primaryColor: Color = NeonCyan,
    val description: String,
    val recommendedServers: List<String>,
    val channelBypasses: List<ChannelBypassInfo>
)

data class ChannelBypassInfo(
    val name: String,
    val isBypassing: Boolean,
    val targetPlatform: String,
    val unlockCode: String
)

object CountryRegistry {
    val countries = listOf(
        CountryInfo(
            code = "GB",
            name = "United Kingdom",
            flagEmoji = "🇬🇧",
            latitude = 55.3781f,
            longitude = -3.4360f,
            primaryColor = NeonCyan,
            description = "High-speed optical servers. Full bypass for BBC iPlayer, CBBC, Boomerang UK, ITVX, Channel 4.",
            recommendedServers = listOf("London - Sentinel Grid", "Manchester - Obsidian Pillar", "Edinburgh - Aegis Node"),
            channelBypasses = listOf(
                ChannelBypassInfo("BBC iPlayer", true, "BBC Network", "BYPASS-UK-BBCI-2026"),
                ChannelBypassInfo("CBBC", true, "BBC Kids", "BYPASS-UK-CBBC-2026"),
                ChannelBypassInfo("Boomerang UK", true, "Turner Broadcasting", "BYPASS-UK-BOOM-2026")
            )
        ),
        CountryInfo(
            code = "US",
            name = "United States",
            flagEmoji = "🇺🇸",
            latitude = 37.0902f,
            longitude = -95.7129f,
            primaryColor = ElectricBlue,
            description = "Ultra-low latency grids. Full bypass for Pluto TV, Boomerang US, Tubi, Hulu, Max.",
            recommendedServers = listOf("New York - Horizon Array", "San Francisco - Silicon Bastion", "Miami - Vortex Grid"),
            channelBypasses = listOf(
                ChannelBypassInfo("Pluto TV", true, "Paramount Global", "BYPASS-US-PLUT-2026"),
                ChannelBypassInfo("Boomerang US", true, "Warner Bros.", "BYPASS-US-BOOM-2026")
            )
        ),
        CountryInfo(
            code = "IT",
            name = "Italy",
            flagEmoji = "🇮🇹",
            latitude = 41.8719f,
            longitude = 12.5674f,
            primaryColor = NeonEmerald,
            description = "Mediterranean routing tunnels. Access RaiPlay, Mediaset Infinity, and local streaming blocks.",
            recommendedServers = listOf("Milan - Alpha Gateway", "Rome - Tiberius Core"),
            channelBypasses = listOf(
                ChannelBypassInfo("RaiPlay", true, "Rai Network", "BYPASS-IT-RAIP-2026"),
                ChannelBypassInfo("Rai 1 / Rai Due", true, "Rai Broadcasters", "BYPASS-IT-RAIL-2026")
            )
        ),
        CountryInfo(
            code = "JP",
            name = "Japan",
            flagEmoji = "🇯🇵",
            latitude = 36.2048f,
            longitude = 138.2529f,
            primaryColor = WarningRed,
            description = "Subsea fiber-optic ring routing. Access NHK, ABEMA, TVer, and bypass region-locked platforms.",
            recommendedServers = listOf("Tokyo - Sakura Hub", "Osaka - Shogun Core", "Sapporo - Aurora Gateway"),
            channelBypasses = listOf(
                ChannelBypassInfo("NHK World", true, "NHK", "BYPASS-JP-NHKW-2026"),
                ChannelBypassInfo("ABEMA TV", true, "CyberAgent", "BYPASS-JP-ABEM-2026")
            )
        ),
        CountryInfo(
            code = "BR",
            name = "Brazil",
            flagEmoji = "🇧🇷",
            latitude = -14.2350f,
            longitude = -51.9253f,
            primaryColor = NeonEmerald,
            description = "South American high-capacity gateway. Access Globoplay, RecordTV, and local streaming tunnels.",
            recommendedServers = listOf("São Paulo - Amazonia Grid", "Rio de Janeiro - Copacabana Node"),
            channelBypasses = listOf(
                ChannelBypassInfo("Globoplay", true, "Globo", "BYPASS-BR-GLOB-2026"),
                ChannelBypassInfo("SBT Streaming", true, "SBT", "BYPASS-BR-SBTS-2026")
            )
        )
    )

    fun getCountryByCode(code: String): CountryInfo {
        return countries.firstOrNull { it.code.uppercase() == code.uppercase() } ?: countries[0]
    }
}
