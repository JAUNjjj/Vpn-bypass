package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CountryInfo
import com.example.ui.theme.*

@Composable
fun MainConnectionConsole(
    selectedCountry: CountryInfo,
    isConnected: Boolean,
    isConnecting: Boolean,
    onToggleConnection: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSpin")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        ),
        label = "Spin"
    )

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CosmicSurface
        ),
        shape = RoundedCornerShape(20.dp),
        border = CardStroke(1.dp, CosmicSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isConnected) "SECURE TUNNEL ESTABLISHED" else if (isConnecting) "ALIGNING NETWORK GRID..." else "TUNNEL DISCONNECTED",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isConnected) NeonEmerald else if (isConnecting) GlowAmber else TextMuted,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main Cyber Connect Button with animated state
            Button(
                onClick = onToggleConnection,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) WarningRed else if (isConnecting) GlowAmber else NeonCyan,
                    contentColor = CosmicBackground
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isConnecting) {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "Handshake",
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(spinAngle)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "CONNECTING...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else if (isConnected) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Shield Off",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "DISCONNECT FROM EARTH",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.NetworkCheck,
                            contentDescription = "Shield Connect",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ACTIVATE PLANET SHIELD",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connection metrics simulation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ACTIVE GATEWAY", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = "${selectedCountry.flagEmoji} ${selectedCountry.name}",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text("CYBER CORE ROUTE", fontSize = 11.sp, color = TextMuted)
                    Text(
                        text = selectedCountry.recommendedServers.firstOrNull() ?: "London - Sentinel Grid",
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DeepSpace)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = selectedCountry.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}
