@file:OptIn(ExperimentalTextApi::class)

package com.bernportal.ehr.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernportal.ehr.R
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private fun seg(t: Float, start: Float, end: Float) = ((t - start) / (end - start)).coerceIn(0f, 1f)
private fun easeOut(x: Float) = 1 - (1 - x) * (1 - x) * (1 - x)
private fun backOut(x: Float): Float { val c1 = 1.70158f; val c3 = c1 + 1; return 1 + c3 * (x - 1) * (x - 1) * (x - 1) + c1 * (x - 1) * (x - 1) }

private data class Star(val x: Float, val y: Float, val z: Float, val hue: Int)

/**
 * Cinematic 3D opening: starfield warp, perspective cyber-grid, the Bernportal emblem flying in with a full 3D spin,
 * a gold orbit ring that passes behind and in front of the emblem, a shockwave, a light sweep across the emblem,
 * 3D letter-flip company title, typed app title, year badge, tagline and a boot-up loader. Tap or "Skip" to continue.
 * Honours the learner's animation setting (Full / Reduced / Off).
 */
@Composable
fun IntroScreen(onDone: () -> Unit) {
    val motion = LocalMotion.current
    val total = when (motion.level) { "Full" -> 6200f; "Reduced" -> 1800f; else -> 700f }
    val clock = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var finished by remember { mutableStateOf(false) }
    val stars = remember { val r = Random(7); List(140) { Star(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat(), r.nextInt(3)) } }

    fun finish() {
        if (finished) return
        finished = true
        scope.launch {
            exit.animateTo(1f, tween(if (motion.enabled) 420 else 1, easing = FastOutSlowInEasing))
            onDone()
        }
    }

    LaunchedEffect(Unit) {
        clock.animateTo(total, tween(total.toInt(), easing = LinearEasing))
        finish()
    }

    // Map real time onto the 6.2s storyboard so Reduced mode plays the same story faster.
    val t = clock.value / total * 6200f
    val full = motion.full

    val emblemIn = easeOut(seg(t, 0f, 1700f))
    val ringIn = seg(t, 500f, 1500f)
    val shock = seg(t, 1650f, 2500f)
    val sweep = seg(t, 1900f, 2900f)
    val company = seg(t, 1800f, 3200f)
    val presents = seg(t, 3000f, 3400f)
    val title = seg(t, 3300f, 4500f)
    val year = backOut(seg(t, 4300f, 4900f))
    val tagline = seg(t, 4700f, 5700f)
    val loader = seg(t, 0f, 5900f)

    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF0F1E4D), Color(0xFF060C26), Color(0xFF02030A)), radius = 1600f))
            .clickable(remember { MutableInteractionSource() }, indication = null) { if (t > 900f || !motion.enabled) finish() }
            .graphicsLayer { alpha = 1f - exit.value; val sc = 1f + exit.value * 0.15f; scaleX = sc; scaleY = sc }
            .semantics { contentDescription = "Bernportal System Innovations presents Ethical Hacking Roadmap 2026/2027. Tap to continue." }
    ) {
        val w = constraints.maxWidth.toFloat()
        val emblemWidth = (maxWidth * 0.72f).coerceAtMost(360.dp)

        // ---------- Starfield warp + perspective grid ----------
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height * 0.42f)
            stars.forEach { s ->
                val z = if (full) ((s.z - t / 6200f * 0.9f) % 1f + 1f) % 1f + 0.05f else s.z + 0.05f
                val px = c.x + s.x / z * size.width * 0.18f
                val py = c.y + s.y / z * size.height * 0.18f
                if (px in 0f..size.width && py in 0f..size.height) {
                    val col = when (s.hue) { 0 -> Brand.Cyan; 1 -> Brand.Violet; else -> Color.White }
                    val r = (1.6f / z).coerceAtMost(4.5f)
                    drawCircle(col.copy(alpha = (0.25f + (1 - z) * 0.6f).coerceIn(0f, 1f)), r, Offset(px, py))
                    if (full && z < 0.35f) drawLine(col.copy(alpha = 0.25f), Offset(px, py), Offset(c.x + (px - c.x) * 0.92f, c.y + (py - c.y) * 0.92f), r * 0.8f)
                }
            }
            // perspective floor
            val horizon = size.height * 0.72f
            val gridAlpha = 0.35f * seg(t, 200f, 1400f)
            val lineCol = Brand.Cyan.copy(alpha = gridAlpha)
            for (i in -12..12) {
                drawLine(lineCol, Offset(c.x + i * 18f, horizon), Offset(c.x + i * size.width * 0.22f, size.height), 1.2f)
            }
            val scroll = if (full) (t / 900f) % 1f else 0f
            for (k in 0 until 14) {
                val f = ((k + scroll) / 14f)
                val yy = horizon + (size.height - horizon) * f * f
                drawLine(Brand.Violet.copy(alpha = gridAlpha * f), Offset(0f, yy), Offset(size.width, yy), 1.2f)
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Brand.CyberBlue.copy(alpha = 0.18f * gridAlpha * 3), Color.Transparent), horizon - 60f, horizon + 60f),
                topLeft = Offset(0f, horizon - 60f), size = Size(size.width, 120f))
        }

        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            // ---------- Emblem with 3D orbit ----------
            Box(Modifier.width(emblemWidth).height(emblemWidth * 0.62f), contentAlignment = Alignment.Center) {
                val orbitAngle = (t / 6200f) * 720f
                // glow + shockwave behind
                Canvas(Modifier.fillMaxSize()) {
                    drawCircle(Brush.radialGradient(listOf(Brand.CyberBlue.copy(alpha = 0.55f * emblemIn), Color.Transparent), center, size.width * 0.55f), size.width * 0.55f)
                    if (shock > 0f && shock < 1f) {
                        drawCircle(Brand.Gold.copy(alpha = (1 - shock) * 0.8f), size.width * (0.25f + shock * 0.6f), style = Stroke(6f * (1 - shock) + 1f))
                        drawCircle(Brand.Cyan.copy(alpha = (1 - shock) * 0.5f), size.width * (0.18f + shock * 0.8f), style = Stroke(3f))
                    }
                    drawOrbit(orbitAngle, ringIn, back = true)
                }
                Image(
                    painterResource(R.drawable.brand_emblem), null,
                    Modifier.fillMaxWidth(0.92f)
                        .graphicsLayer {
                            alpha = emblemIn
                            val sc = 0.15f + 0.85f * emblemIn
                            scaleX = sc; scaleY = sc
                            if (motion.effects3d) {
                                rotationY = if (full) 540f * (1 - emblemIn) + sin(t / 900f) * 8f * emblemIn else 0f
                                rotationX = if (full) 25f * (1 - emblemIn) else 0f
                            }
                            translationY = (1 - emblemIn) * -120f + (if (full) sin(t / 700f) * 6f * emblemIn else 0f)
                            cameraDistance = 18f * density
                            compositingStrategy = CompositingStrategy.Offscreen
                        }
                        .drawWithContent {
                            drawContent()
                            if (sweep in 0.01f..0.99f) {
                                val x = -size.width + sweep * size.width * 3
                                drawRect(
                                    Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.75f), Color.Transparent), Offset(x, 0f), Offset(x + size.width * 0.45f, size.height)),
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                    contentScale = ContentScale.FillWidth
                )
                Canvas(Modifier.fillMaxSize()) { drawOrbit(orbitAngle, ringIn, back = false) }
            }

            Spacer(Modifier.height(18.dp))

            // ---------- Company title: 3D letter flip ----------
            val companyText = "BERNPORTAL SYSTEM INNOVATIONS"
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                companyText.forEachIndexed { i, ch ->
                    val f = easeOut(seg(company * companyText.length * 1.6f, i.toFloat(), i + 3f))
                    Text(
                        ch.toString(),
                        style = TextStyle(
                            brush = Brush.verticalGradient(listOf(Color(0xFFFFF1B8), Brand.Gold, Brand.GoldDeep)),
                            fontSize = if (maxWidth < 380.dp) 15.sp else 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.5.sp,
                            shadow = Shadow(Brand.Gold.copy(alpha = 0.6f), Offset(0f, 0f), 14f)
                        ),
                        modifier = Modifier.graphicsLayer {
                            alpha = f
                            if (motion.effects3d) rotationX = 90f * (1 - f)
                            translationY = (1 - f) * 24f
                            cameraDistance = 8f * density
                        }
                    )
                }
            }
            Box(Modifier.padding(top = 6.dp).height(2.dp).fillMaxWidth(0.6f * easeOut(company)).background(Brush.horizontalGradient(listOf(Color.Transparent, Brand.Gold, Color.Transparent))))
            Text("presents", color = Brand.Muted, fontSize = 13.sp, letterSpacing = 4.sp, modifier = Modifier.padding(top = 8.dp).graphicsLayer { alpha = presents })

            Spacer(Modifier.height(18.dp))

            // ---------- App title: typed with glowing cursor ----------
            val appTitle = "ETHICAL HACKING ROADMAP"
            val shown = (appTitle.length * title).toInt()
            val cursorOn = ((t / 260f).toInt() % 2 == 0) && title > 0f && tagline < 1f
            Text(
                appTitle.take(shown) + if (cursorOn) "▌" else " ",
                textAlign = TextAlign.Center,
                style = TextStyle(
                    brush = Brush.horizontalGradient(listOf(Brand.Cyan, Color.White, Brand.Violet)),
                    fontSize = if (maxWidth < 380.dp) 25.sp else 30.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.SansSerif,
                    shadow = Shadow(Brand.Cyan.copy(alpha = 0.8f), Offset(0f, 0f), 22f), letterSpacing = 1.sp
                ),
                modifier = Modifier.graphicsLayer { alpha = if (title > 0f) 1f else 0f }
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "2026 / 2027",
                color = Color(0xFF1B1200), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                modifier = Modifier.graphicsLayer { scaleX = year; scaleY = year; alpha = year.coerceIn(0f, 1f); if (motion.effects3d) rotationZ = (1 - year) * -12f }
                    .clip(RoundedCornerShape(50)).background(Brush.horizontalGradient(listOf(Brand.GoldDeep, Brand.Gold, Color(0xFFFFE08A))))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Learn.", "Practice.", "Secure.", "Become.").forEachIndexed { i, wd ->
                    val f = easeOut(seg(tagline * 4f, i.toFloat(), i + 1f))
                    Text(wd, color = if (i == 3) Brand.Neon else Brand.Soft, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        modifier = Modifier.graphicsLayer { alpha = f; translationY = (1 - f) * 18f })
                }
            }
        }

        // ---------- Boot loader ----------
        Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp).fillMaxWidth(0.72f), horizontalAlignment = Alignment.CenterHorizontally) {
            val msgs = listOf("Initialising secure learning environment", "Loading 16 modules and 84 lessons", "Preparing simulated labs", "Calibrating AI tutor", "Encrypting your workspace")
            Text(msgs[(loader * (msgs.size - 1)).toInt().coerceIn(0, msgs.size - 1)] + "...", color = Brand.Muted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF1B2547))) {
                Box(Modifier.fillMaxWidth(LinearOutSlowInEasing.transform(loader)).height(4.dp).background(Brush.horizontalGradient(listOf(Brand.Cyan, Brand.Violet, Brand.Gold))))
            }
        }
        TextButton(onClick = { finish() }, modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp)) {
            Text("Skip", color = Brand.Soft)
        }
        Text("Bernportal System Innovations", color = Brand.Muted.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 18.dp))
        if (w < 0f) Spacer(Modifier.size(0.dp))
    }
}

/** Draws half of the 3D gold orbit ellipse (back half behind the emblem, front half over it) plus the comet head. */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOrbit(angleDeg: Float, appear: Float, back: Boolean) {
    if (appear <= 0f) return
    val cx = size.width / 2; val cy = size.height / 2
    val rx = size.width * 0.50f; val ry = size.height * 0.20f
    val tilt = -0.32f
    val steps = 90
    fun point(a: Double): Offset {
        val x = rx * cos(a); val y = ry * sin(a)
        return Offset((cx + x * cos(tilt.toDouble()) - y * sin(tilt.toDouble())).toFloat(), (cy + x * sin(tilt.toDouble()) + y * cos(tilt.toDouble())).toFloat())
    }
    val sweep = 2 * PI * appear
    for (i in 0 until steps) {
        val a0 = i * sweep / steps; val a1 = (i + 1) * sweep / steps
        val isBack = sin(a0) < 0
        if (isBack != back) continue
        val depth = ((sin(a0) + 1) / 2).toFloat()
        drawLine(
            Brush.linearGradient(listOf(Color(0xFFFFE08A), Brand.Gold, Brand.GoldDeep)), point(a0), point(a1),
            strokeWidth = 3f + depth * 7f, cap = androidx.compose.ui.graphics.StrokeCap.Round, alpha = 0.55f + depth * 0.45f
        )
    }
    val head = Math.toRadians(angleDeg.toDouble())
    if ((sin(head) < 0) == back) {
        val p = point(head)
        drawCircle(Brush.radialGradient(listOf(Color.White, Brand.Gold.copy(alpha = 0.6f), Color.Transparent), p, 40f), 40f, p)
    }
}
