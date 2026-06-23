package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen() {
    var loadingStatus by remember { mutableStateOf("Initializing Security Nodes...") }
    
    // Scale and Fade animation for central lock-shield logo
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldPulse")
    val shieldScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShieldScale"
    )

    val shieldGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInElastic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    // Animate progress text
    LaunchedEffect(Unit) {
        delay(600)
        loadingStatus = "Synchronizing DNA DNS Hashes..."
        delay(700)
        loadingStatus = "Activating Real-Time Threat Inspection..."
        delay(700)
        loadingStatus = "Shield AI Active. Welcome to Secure Browsing."
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CyberDarkBg, CyberDeepNavy, CyberDeepNavy)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic background scanning lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeColor = Color(0xFF1E3A8A).copy(alpha = 0.15f)
            val step = 40.dp.toPx()
            for (x in 0..size.width.toInt() step step.toInt()) {
                drawLine(
                    color = strokeColor,
                    start = Offset(x.toFloat(), 0f),
                    end = Offset(x.toFloat(), size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            for (y in 0..size.height.toInt() step step.toInt()) {
                drawLine(
                    color = strokeColor,
                    start = Offset(0f, y.toFloat()),
                    end = Offset(size.width, y.toFloat()),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(shieldScale)
            ) {
                // Vector Shield drawing
                Canvas(modifier = Modifier.size(130.dp)) {
                    val path = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.05f)
                        lineTo(size.width * 0.9f, size.height * 0.15f)
                        quadraticTo(
                            size.width * 0.9f, size.height * 0.6f,
                            size.width * 0.5f, size.height * 0.95f
                        )
                        quadraticTo(
                            size.width * 0.1f, size.height * 0.6f,
                            size.width * 0.1f, size.height * 0.15f
                        )
                        close()
                    }

                    // Outer primary cyber gradient
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(CyberBluePrimary, CyberAccentCyan)
                        )
                    )

                    // Inner border layer
                    val innerPath = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.12f)
                        lineTo(size.width * 0.82f, size.height * 0.20f)
                        quadraticTo(
                            size.width * 0.82f, size.height * 0.58f,
                            size.width * 0.5f, size.height * 0.88f
                        )
                        quadraticTo(
                            size.width * 0.18f, size.height * 0.58f,
                            size.width * 0.18f, size.height * 0.20f
                        )
                        close()
                    }
                    drawPath(
                        path = innerPath,
                        color = CyberDeepNavy
                    )
                }

                // Cyber padlock symbol
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.offset(y = 4.dp)
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        // Shackle
                        drawCircle(
                            color = Color.White,
                            radius = size.width * 0.35f,
                            center = Offset(size.width * 0.5f, size.height * 0.35f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
                        )
                        // Padlock base
                        drawRoundRect(
                            color = CyberAccentCyan,
                            topLeft = Offset(size.width * 0.15f, size.height * 0.45f),
                            size = Size(size.width * 0.7f, size.height * 0.45f),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Tech Branding Text
            Text(
                text = "SHIELD AI",
                color = Color.White,
                fontSize = 28.sp,
                letterSpacing = 6.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "ROBUST CYBER ARMOR",
                color = CyberBlueSecondary,
                fontSize = 11.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .alpha(0.85f)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Pulse Spinner
            CircularProgressIndicator(
                color = CyberAccentCyan,
                strokeWidth = 3.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Animated Status Tick
            Text(
                text = loadingStatus,
                color = CyberTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.alpha(shieldGlowAlpha)
            )
        }
    }
}
