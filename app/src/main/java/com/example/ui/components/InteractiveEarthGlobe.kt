package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.CountryInfo
import com.example.model.CountryRegistry
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun InteractiveEarthGlobe(
    selectedCountry: CountryInfo,
    isConnected: Boolean,
    onCountrySelected: (CountryInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    // Rotation of the Earth
    var rotationX by remember { mutableStateOf(-15f) }
    var rotationY by remember { mutableStateOf(0f) }

    // Natural rotation animation when not dragged
    val infiniteTransition = rememberInfiniteTransition(label = "GlobeSpin")
    val autoSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinAngle"
    )

    // Glowing heartbeat pulse animation for active connections
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowPulse"
    )

    // Base current rotation combining auto spin and manual drag offsets
    val currentRotationY = rotationY + autoSpin

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    rotationY += dragAmount.x * 0.4f
                    rotationX = (rotationX - dragAmount.y * 0.4f).coerceIn(-60f, 60f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2.3f

            // 1. Draw Space Background Atmosphere & Radial Glow
            val spaceGlow = Brush.radialGradient(
                colors = listOf(
                    ElectricBlue.copy(alpha = 0.25f * glowPulse),
                    CosmicBackground.copy(alpha = 0.0f)
                ),
                center = center,
                radius = radius * 1.6f
            )
            drawCircle(brush = spaceGlow, radius = radius * 1.6f, center = center)

            // 2. Draw Earth Sphere Ocean Base
            val oceanBrush = Brush.linearGradient(
                colors = listOf(
                    Color(0xFF0A122C),
                    Color(0xFF030712)
                ),
                start = Offset(center.x - radius, center.y - radius),
                end = Offset(center.x + radius, center.y + radius)
            )
            drawCircle(brush = oceanBrush, radius = radius, center = center)

            // 3. Draw Grid Lines (Latitude / Longitude Meridians) to represent "Sentinel Network"
            val gridColor = if (isConnected) NeonEmerald.copy(alpha = 0.15f) else NeonCyan.copy(alpha = 0.1f)
            val gridStroke = Stroke(width = 1.5f)

            // Draw horizontal rings
            for (lat in -3..3) {
                val hFactor = lat * 0.25f
                val rLat = radius * cos(hFactor * PI.toFloat() / 2)
                val yOffset = center.y + radius * sin(hFactor * PI.toFloat() / 2)
                drawOval(
                    color = gridColor,
                    topLeft = Offset(center.x - rLat, yOffset - (rLat * 0.15f)),
                    size = androidx.compose.ui.geometry.Size(rLat * 2, rLat * 0.3f),
                    style = gridStroke
                )
            }

            // Draw vertical rings representing rotation
            for (longIdx in 0 until 6) {
                val baseLong = (longIdx * 30f + currentRotationY) % 360f
                val radLong = Math.toRadians(baseLong.toDouble()).toFloat()
                val visibleWidth = radius * cos(radLong)

                if (cos(radLong) > 0f) {
                    drawOval(
                        color = gridColor,
                        topLeft = Offset(center.x - visibleWidth, center.y - radius),
                        size = androidx.compose.ui.geometry.Size(visibleWidth * 2, radius * 2),
                        style = gridStroke
                    )
                }
            }

            // 4. Draw Landmasses (Geometric cyber points & connecting lines)
            // Draw standard cyber continent contours based on standard points
            val continents = listOf(
                // North America
                listOf(Offset(-0.6f, -0.4f), Offset(-0.4f, -0.6f), Offset(-0.2f, -0.5f), Offset(-0.3f, -0.2f), Offset(-0.6f, -0.2f)),
                // Europe
                listOf(Offset(-0.05f, -0.5f), Offset(0.2f, -0.6f), Offset(0.3f, -0.3f), Offset(0.1f, -0.2f), Offset(-0.05f, -0.3f)),
                // South America
                listOf(Offset(-0.4f, 0.1f), Offset(-0.2f, 0.2f), Offset(-0.1f, 0.6f), Offset(-0.3f, 0.7f), Offset(-0.45f, 0.4f)),
                // Asia
                listOf(Offset(0.3f, -0.5f), Offset(0.7f, -0.4f), Offset(0.8f, -0.1f), Offset(0.5f, 0.2f), Offset(0.2f, 0.0f)),
                // Africa
                listOf(Offset(0.0f, -0.1f), Offset(0.3f, 0.0f), Offset(0.4f, 0.3f), Offset(0.2f, 0.5f), Offset(0.1f, 0.3f))
            )

            continents.forEach { points ->
                val landPath = Path()
                var first = true

                points.forEach { point ->
                    // Rotate point based on current spherical angles
                    val rawX = point.x * radius
                    val rawY = point.y * radius
                    val rawZ = sqrt((radius * radius - rawX * rawX - rawY * rawY).coerceAtLeast(0f))

                    // Compute dynamic spherical position under Y & X rotation
                    val cosY = cos(Math.toRadians(currentRotationY.toDouble())).toFloat()
                    val sinY = sin(Math.toRadians(currentRotationY.toDouble())).toFloat()

                    val rotatedX = rawX * cosY - rawZ * sinY
                    val rotatedZ = rawX * sinY + rawZ * cosY

                    // Only draw if on the visible front hemisphere (rotatedZ > 0)
                    if (rotatedZ > 0) {
                        val screenX = center.x + rotatedX
                        val screenY = center.y + rawY

                        if (first) {
                            landPath.moveTo(screenX, screenY)
                            first = false
                        } else {
                            landPath.lineTo(screenX, screenY)
                        }
                    }
                }

                if (!first) {
                    landPath.close()
                    drawPath(
                        path = landPath,
                        color = if (isConnected) NeonEmerald.copy(alpha = 0.25f) else NeonCyan.copy(alpha = 0.15f),
                        style = Stroke(width = 2f)
                    )
                }
            }

            // 5. Draw Country Nodes & Target Flags
            CountryRegistry.countries.forEach { country ->
                // Map latitude & longitude to spherical 3D points
                // Standard mapping: lat = y, long = x
                val radLat = Math.toRadians(country.latitude.toDouble()).toFloat()
                val radLong = Math.toRadians((country.longitude + currentRotationY).toDouble()).toFloat()

                // Calculate 3D sphere coordinate
                val z = radius * cos(radLat) * cos(radLong)
                val x = radius * cos(radLat) * sin(radLong)
                val y = -radius * sin(radLat) // Screen Y is inverted from Cartesian

                // Render only if on the front face of the 3D globe (z > 0)
                if (z > 0) {
                    val screenX = center.x + x
                    val screenY = center.y + y

                    val isTarget = country.code == selectedCountry.code
                    val nodeColor = if (isConnected) NeonEmerald else country.primaryColor

                    // Draw orbital halo pulse
                    if (isTarget) {
                        drawCircle(
                            color = nodeColor.copy(alpha = 0.3f),
                            radius = 18f * glowPulse,
                            center = Offset(screenX, screenY),
                            style = Stroke(width = 2f)
                        )
                    }

                    // Draw actual point node
                    drawCircle(
                        color = nodeColor,
                        radius = if (isTarget) 10f else 6f,
                        center = Offset(screenX, screenY)
                    )

                    // Draw connecting line from core center to the active node
                    if (isTarget && isConnected) {
                        drawSegmentLine(
                            start = center,
                            end = Offset(screenX, screenY),
                            color = NeonEmerald.copy(alpha = 0.4f),
                            strokeWidth = 3f
                        )
                    }
                }
            }

            // 6. Draw Atmosphere Outer Shield Ring
            val shieldColor = if (isConnected) NeonEmerald else NeonCyan
            drawCircle(
                color = shieldColor.copy(alpha = 0.4f),
                radius = radius + 3f,
                center = center,
                style = Stroke(width = 3f)
            )

            // Neon glowing edge effect
            val shieldGlow = Brush.radialGradient(
                colors = listOf(
                    shieldColor.copy(alpha = 0.2f),
                    Color.Transparent
                ),
                center = center,
                radius = radius + 30f
            )
            drawCircle(brush = shieldGlow, radius = radius + 30f, center = center)
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSegmentLine(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth
    )
}
