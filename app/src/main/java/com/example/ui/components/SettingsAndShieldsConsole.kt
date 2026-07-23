package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.db.VpnConfig
import com.example.ui.theme.*

@Composable
fun SettingsAndShieldsConsole(
    config: VpnConfig,
    onAdBlockChange: (String) -> Unit,
    onMalwareChange: (Boolean) -> Unit,
    onTriggerVerify: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("settings_shields_card"),
        colors = CardDefaults.cardColors(
            containerColor = CosmicSurface
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardStroke(1.dp, CosmicSurfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Shield Settings",
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DEFENSIVE SHIELDS & SECURE BYPASS",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DNS AdBlock selection row
            Text(
                text = "Ad-Blocker & Telemetry Protection",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("OFF", "Disabled", TextMuted),
                    Triple("DNS_FILTER", "Standard DNS", NeonCyan),
                    Triple("ULTRA", "Ultra AdBlock", NeonEmerald)
                ).forEach { (mode, label, color) ->
                    val isSelected = config.adBlockEnabled == mode
                    Button(
                        onClick = { onAdBlockChange(mode) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) CosmicSurfaceVariant else DeepSpace,
                            contentColor = if (isSelected) color else TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = CardStroke(1.dp, if (isSelected) color.copy(alpha = 0.5f) else Color.Transparent)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Malware and Deep Filter toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DeepSpace)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield",
                        tint = NeonEmerald,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Active Malware Shield", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Filters malicious domains & trackers", fontSize = 10.sp, color = TextMuted)
                    }
                }
                Switch(
                    checked = config.malwareProtectionEnabled,
                    onCheckedChange = onMalwareChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NeonEmerald,
                        checkedTrackColor = NeonEmerald.copy(alpha = 0.3f),
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = CosmicSurfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom specific bypass tools the user requested
            Text(
                text = "Media & Platform Tunnel Status",
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            listOf(
                Triple("BBC iPlayer & CBBC UK", "BYPASS ACTIVE", NeonCyan),
                Triple("Boomerang UK & US Networks", "BYPASS ACTIVE", NeonCyan),
                Triple("Pluto TV AdBlock & Stream Core", "BYPASS ACTIVE", NeonEmerald),
                Triple("RaiPlay & CBBC International", "TUNNEL BYPASS ACTIVE", NeonEmerald)
            ).forEach { (platform, status, badgeColor) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DeepSpace)
                        .clickable { onTriggerVerify(platform) }
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = platform,
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
