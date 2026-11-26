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
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.bernportal.ehr.ui.components.BrandOrbit3D
import com.bernportal.ehr.ui.components.ExtrudedText3D
import com.bernportal.ehr.ui.components.drawCube3D
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
private data class Cube(val sx: Float, val sy: Float, val sz: Float, val tx: Float, val ty: Float, val size: Float, val gold: Boolean, val spin: Float)

/**
 * Cinematic 3D opening (about 9 s at Full motion, shorter on Reduced / Off):
 *  Act 1  Boot - warp starfield, binary rain, scan line, and 3D logo cubes flying in from depth and assembling.
 *  Act 2  Emblem - the Bernportal "B" spins in on two axes, the gold orbit ring draws itself, a shockwave and a light sweep.
 *  Act 3  Icon orbit - the glossy Bernportal icon set rises and orbits the emblem in 3D (passing behind and in front).
 *  Act 4  Company - "BERNPORTAL / SYSTEM INNOVATIONS" in extruded 3D gold letters flipping in, "presents".
 *  Act 5  Product - app title typed with a glitch/RGB split, 3D-spinning year badge, tagline.
 *  Act 6  Finale - orbit accelerates, flare burst, then a zoom-through into the app.
 * Tap anywhere (after the first second) or "Skip" to continue. Honours Full / Reduced / Off motion and the 3D toggle.
 */
@Composable
fun IntroScreen(onDone: () -> Unit) {
    val motion = LocalMotion.current
    val story = 9000f
    val total = when (motion.level) { "Full" -> story; "Reduced" -> 2600f; else -> 800f }
    val clock = remember { Animatable(0f) }
    val exit = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var finished by remember { mutableStateOf(false) }
    val stars = remember { val r = Random(7); List(160) { Star(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, r.nextFloat(), r.nextInt(3)) } }
    // Pixel cubes from the logo: start far away at random positions, end in the trail to the left of the "B"
    val cubes = remember {
        val r = Random(11)
        val trail = listOf(-0.62f to -0.30f, -0.50f to -0.18f, -0.62f to -0.06f, -0.38f to -0.30f, -0.38f to -0.06f, -0.50f to 0.06f,
            -0.26f to -0.18f, -0.26f to 0.06f, -0.74f to -0.18f, -0.38f to 0.18f, -0.14f to -0.06f, -0.86f to -0.06f)
        trail.mapIndexed { i, (x, y) -> Cube(r.nextFloat() * 2 - 1, r.nextFloat() * 2 - 1, 4f + r.nextFloat() * 6f, x, y, if (i % 3 == 0) 1f else 0.78f, i == 4, 2f + r.nextFloat() * 3f) }
    }
    val rain = remember { val r = Random(3); List(26) { Triple(r.nextFloat(), r.nextFloat(), 0.6f + r.nextFloat()) } }

    fun finish() {
        if (finished) return
        finished = true
        scope.launch {
            exit.animateTo(1f, tween(if (motion.enabled) 650 else 1, easing = FastOutSlowInEasing))
            onDone()
        }
    }

    LaunchedEffect(Unit) {
        clock.animateTo(total, tween(total.toInt(), easing = LinearEasing))
        finish()
    }

    // Map real time onto the 9 s storyboard so Reduced mode plays the same story faster.
    val t = clock.value / total * story
    val full = motion.full
    val fx3d = motion.effects3d

    val boot = seg(t, 0f, 1400f)
    val cubesIn = easeOut(seg(t, 150f, 1500f))
    val cubesOut = seg(t, 1500f, 2300f)
    val emblemIn = easeOut(seg(t, 1100f, 2700f))
    val ringIn = seg(t, 1500f, 2600f)
    val shock = seg(t, 2550f, 3400f)
    val sweep = seg(t, 2800f, 3700f)
    val iconsIn = seg(t, 2600f, 4600f)
    val company = seg(t, 3300f, 4900f)
    val presents = seg(t, 4800f, 5200f)
    val title = seg(t, 5100f, 6400f)
    val year = seg(t, 6200f, 6900f)
    val tagline = seg(t, 6700f, 7700f)
    val finale = seg(t, 7700f, 8600f)
    val loader = seg(t, 0f, 8800f)
    // orbit speeds up in the finale
    val orbitAngle = t * 0.045f + finale * finale * 260f

    BoxWithConstraints(
        Modifier.fillMaxSize()
            .background(Brush.radialGradient(listOf(Color(0xFF0F1E4D), Color(0xFF060C26), Color(0xFF02030A)), radius = 1600f))
            .clickable(remember { MutableInteractionSource() }, indication = null) { if (t > 1000f || !motion.enabled) finish() }
            .graphicsLayer { alpha = 1f - exit.value; val sc = 1f + exit.value * exit.value * 1.6f; scaleX = sc; scaleY = sc }
            .semantics { contentDescription = "Bernportal System Innovations presents Ethical Hacking Roadmap 2026/2027. Tap to continue." }
    ) {
        val emblemWidth = (maxWidth * 0.86f).coerceAtMost(420.dp)
        val narrow = maxWidth < 380.dp

        // ---------- Act 1: starfield warp, binary rain, perspective grid, scan line, flying 3D cubes ----------
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height * 0.36f)
            val warp = if (full) 1f + (1 - boot) * 3f + finale * 4f else 1f
            stars.forEach { s ->
                val z = if (full) ((s.z - t / story * 1.4f * warp) % 1f + 1f) % 1f + 0.05f else s.z + 0.05f
                val px = c.x + s.x / z * size.width * 0.18f
                val py = c.y + s.y / z * size.height * 0.18f
                if (px in 0f..size.width && py in 0f..size.height) {
                    val col = when (s.hue) { 0 -> Brand.Cyan; 1 -> Brand.Violet; else -> Color.White }
                    val r = (1.6f / z).coerceAtMost(4.5f)
                    drawCircle(col.copy(alpha = (0.25f + (1 - z) * 0.6f).coerceIn(0f, 1f)), r, Offset(px, py))
                    if (full && z < 0.45f) drawLine(col.copy(alpha = 0.3f), Offset(px, py), Offset(c.x + (px - c.x) * (1 - 0.06f * warp), c.y + (py - c.y) * (1 - 0.06f * warp)), r * 0.8f)
                }
            }
            // binary rain (fades out once the emblem lands)
            val rainAlpha = (1f - seg(t, 2200f, 3600f)) * seg(t, 0f, 400f)
            if (rainAlpha > 0f && motion.enabled) drawIntoCanvas { cv ->
                val paint = android.graphics.Paint().apply { isAntiAlias = true; textSize = 13.dp.toPx(); typeface = android.graphics.Typeface.MONOSPACE }
                rain.forEachIndexed { k, (fx, ph, spd) ->
                    val x = fx * size.width
                    val head = ((ph + t / 1000f * spd * 0.6f) % 1.3f) * size.height
                    for (j in 0 until 10) {
                        val y = head - j * 15.dp.toPx()
                        if (y < 0f || y > size.height) continue
                        paint.color = (if (j == 0) Color.White else if (k % 4 == 0) Brand.Gold else Brand.Cyan).copy(alpha = rainAlpha * (1f - j / 10f) * 0.55f).toArgb()
                        cv.nativeCanvas.drawText(if ((k * 7 + j * 3 + (t / 120f).toInt()) % 2 == 0) "1" else "0", x, y, paint)
                    }
                }
            }
            // perspective floor
            val horizon = size.height * 0.74f
            val gridAlpha = 0.38f * seg(t, 200f, 1400f)
            for (i in -12..12) drawLine(Brand.Cyan.copy(alpha = gridAlpha), Offset(c.x + i * 18f, horizon), Offset(c.x + i * size.width * 0.22f, size.height), 1.2f)
            val scroll = if (full) (t / 700f * (1f + finale * 3f)) % 1f else 0f
            for (k in 0 until 14) {
                val f = ((k + scroll) / 14f)
                val yy = horizon + (size.height - horizon) * f * f
                drawLine(Brand.Violet.copy(alpha = gridAlpha * f), Offset(0f, yy), Offset(size.width, yy), 1.2f)
            }
            drawRect(Brush.verticalGradient(listOf(Color.Transparent, Brand.CyberBlue.copy(alpha = 0.5f * gridAlpha), Color.Transparent), horizon - 60f, horizon + 60f),
                topLeft = Offset(0f, horizon - 60f), size = Size(size.width, 120f))
            // scan line sweeping down during boot
            if (boot in 0.01f..0.99f) {
                val yy = boot * size.height
                drawRect(Brush.verticalGradient(listOf(Color.Transparent, Brand.Cyan.copy(alpha = 0.35f), Color.Transparent), yy - 40f, yy + 40f), topLeft = Offset(0f, yy - 40f), size = Size(size.width, 80f))
                drawLine(Brand.Cyan.copy(alpha = 0.8f), Offset(0f, yy), Offset(size.width, yy), 2f)
            }
            // 3D logo cubes: fly in from depth, assemble into the pixel trail, then stream into the emblem
            if (cubesOut < 1f) {
                val ew = emblemWidth.toPx()
                cubes.forEachIndexed { i, cb ->
                    val local = easeOut(seg(cubesIn * 1.3f - i * 0.025f, 0f, 1f))
                    val z = cb.sz * (1 - local) + 1f
                    val tx = c.x + cb.tx * ew * 0.5f; val ty = c.y + cb.ty * ew * 0.5f + ew * 0.02f
                    val sx = c.x + cb.sx * size.width * 0.5f; val sy = c.y + cb.sy * size.height * 0.4f
                    var px = sx + (tx - sx) * local; var py = sy + (ty - sy) * local
                    // stream into the emblem centre
                    val o = easeOut(seg(cubesOut * 1.4f - i * 0.03f, 0f, 1f))
                    px += (c.x + ew * 0.08f - px) * o; py += (c.y - py) * o
                    val s = ew * 0.075f * cb.size / z * (1 - o * 0.7f)
                    val spin = if (fx3d) (1 - local) * cb.spin * 6f + t / 900f else 0.6f
                    drawCube3D(Offset(px, py), s, spin * 0.7f + 0.5f, spin + 0.6f, if (cb.gold) Brand.Gold else Brand.CyberBlue, (local * (1 - o)).coerceIn(0f, 1f))
                }
            }
        }

        Column(
            Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
        ) {
            // ---------- Acts 2 + 3: emblem with gold orbit, shockwave, sweep, and the orbiting 3D icon ring ----------
            Box(contentAlignment = Alignment.Center) {
                Canvas(Modifier.width(emblemWidth).height(emblemWidth * 0.78f)) {
                    if (shock > 0f && shock < 1f) {
                        drawCircle(Brand.Gold.copy(alpha = (1 - shock) * 0.8f), size.width * (0.2f + shock * 0.6f), style = Stroke(7f * (1 - shock) + 1f))
                        drawCircle(Brand.Cyan.copy(alpha = (1 - shock) * 0.5f), size.width * (0.14f + shock * 0.85f), style = Stroke(3f))
                    }
                    if (finale > 0f) {
                        val fl = sin(finale * PI).toFloat()
                        drawCircle(Brush.radialGradient(listOf(Color.White.copy(alpha = 0.7f * fl), Brand.Gold.copy(alpha = 0.35f * fl), Color.Transparent), center, size.width * 0.6f), size.width * 0.6f)
                        for (k in 0 until 12) {
                            val a = k * PI / 6 + finale * 0.6
                            drawLine(Brand.Gold.copy(alpha = 0.5f * fl), center, Offset(center.x + (cos(a) * size.width * 0.7f * fl).toFloat(), center.y + (sin(a) * size.width * 0.7f * fl).toFloat()), 3f)
                        }
                    }
                }
                BrandOrbit3D(emblemWidth, orbitAngle, if (motion.enabled) iconsIn else 1f) {
                    Box(Modifier.fillMaxWidth(0.72f), contentAlignment = Alignment.Center) {
                        Canvas(Modifier.matchParentSize()) { drawOrbit(orbitAngle * 2.2f, ringIn, back = true) }
                        Image(
                            painterResource(R.drawable.brand_emblem), null,
                            Modifier.fillMaxWidth()
                                .graphicsLayer {
                                    alpha = emblemIn
                                    val sc = 0.1f + 0.9f * emblemIn + sin(finale * PI).toFloat() * 0.08f
                                    scaleX = sc; scaleY = sc
                                    if (fx3d && full) {
                                        rotationY = 720f * (1 - emblemIn) + sin(t / 900f) * 10f * emblemIn
                                        rotationX = 50f * (1 - emblemIn) + cos(t / 1300f) * 4f * emblemIn
                                        rotationZ = -20f * (1 - emblemIn)
                                    }
                                    translationY = (1 - emblemIn) * -160f + (if (full) sin(t / 700f) * 6f * emblemIn else 0f)
                                    cameraDistance = 16f * density
                                    compositingStrategy = CompositingStrategy.Offscreen
                                }
                                .drawWithContent {
                                    drawContent()
                                    if (sweep in 0.01f..0.99f) {
                                        val x = -size.width + sweep * size.width * 3
                                        drawRect(
                                            Brush.linearGradient(listOf(Color.Transparent, Color.White.copy(alpha = 0.8f), Color.Transparent), Offset(x, 0f), Offset(x + size.width * 0.45f, size.height)),
                                            blendMode = BlendMode.SrcAtop
                                        )
                                    }
                                },
                            contentScale = ContentScale.FillWidth
                        )
                        Canvas(Modifier.matchParentSize()) { drawOrbit(orbitAngle * 2.2f, ringIn, back = false) }
                    }
                }
            }

            // ---------- Act 4: company title in extruded 3D gold ----------
            ExtrudedText3D("BERNPORTAL", if (narrow) 30.sp else 36.sp, progress = seg(company, 0f, 0.65f), depth = 7, letterSpacing = 3.sp, flip3d = fx3d)
            ExtrudedText3D(
                "SYSTEM INNOVATIONS", if (narrow) 15.sp else 18.sp, progress = seg(company, 0.3f, 1f), depth = 4, letterSpacing = 2.5.sp, flip3d = fx3d,
                face = listOf(Color.White, Color(0xFF9CC9FF), Brand.CyberBlue), side = Color(0xFF0A1B4A)
            )
            Box(Modifier.padding(top = 6.dp).height(2.dp).fillMaxWidth(0.7f * easeOut(company)).background(Brush.horizontalGradient(listOf(Color.Transparent, Brand.Gold, Color.Transparent))))
            Text("p r e s e n t s", color = Brand.Muted, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp).graphicsLayer { alpha = presents; translationY = (1 - presents) * 10f })

            Spacer(Modifier.height(12.dp))

            // ---------- Act 5: app title typed with glitch / RGB split ----------
            val appTitle = "ETHICAL HACKING ROADMAP"
            val shown = (appTitle.length * title).toInt()
            val cursorOn = ((t / 260f).toInt() % 2 == 0) && title > 0f && tagline < 1f
            val glitching = full && title in 0.02f..0.98f
            val jitter = if (glitching) ((t / 45f).toInt() % 5 - 2).toFloat() else 0f
            val titleStyle = TextStyle(fontSize = if (narrow) 23.sp else 28.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.SansSerif, letterSpacing = 1.sp, textAlign = TextAlign.Center)
            val shownText = appTitle.take(shown) + if (cursorOn) "▌" else " "
            Box(Modifier.graphicsLayer { alpha = if (title > 0f) 1f else 0f }, contentAlignment = Alignment.Center) {
                if (glitching) {
                    Text(shownText, style = titleStyle.copy(color = Color(0xFFFF2E88).copy(alpha = 0.7f)), modifier = Modifier.graphicsLayer { translationX = jitter * 2.5f })
                    Text(shownText, style = titleStyle.copy(color = Brand.Cyan.copy(alpha = 0.7f)), modifier = Modifier.graphicsLayer { translationX = -jitter * 2.5f })
                }
                Text(shownText, style = titleStyle.copy(brush = Brush.horizontalGradient(listOf(Brand.Cyan, Color.White, Brand.Violet)), shadow = Shadow(Brand.Cyan.copy(alpha = 0.8f), Offset(0f, 0f), 22f)))
            }
            Spacer(Modifier.height(10.dp))
            val yb = backOut(year)
            Text(
                "2026 / 2027",
                color = Color(0xFF1B1200), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp,
                modifier = Modifier.graphicsLayer {
                    scaleX = yb; scaleY = yb; alpha = year.coerceIn(0f, 1f)
                    if (fx3d) rotationY = (1 - easeOut(year)) * 360f
                    cameraDistance = 10f * density
                }
                    .shadow(10.dp, RoundedCornerShape(50), spotColor = Brand.Gold)
                    .clip(RoundedCornerShape(50)).background(Brush.verticalGradient(listOf(Color(0xFFFFE9A8), Brand.Gold, Brand.GoldDeep)))
                    .padding(horizontal = 18.dp, vertical = 6.dp)
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Learn.", "Practice.", "Secure.", "Become.").forEachIndexed { i, wd ->
                    val f = easeOut(seg(tagline * 4f, i.toFloat(), i + 1f))
                    Text(wd, color = if (i == 3) Brand.Neon else Brand.Soft, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                        modifier = Modifier.graphicsLayer { alpha = f; translationY = (1 - f) * 18f; if (fx3d) rotationX = (1 - f) * 80f })
                }
            }
            Spacer(Modifier.height(70.dp))
        }

        // ---------- Boot loader ----------
        Column(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 40.dp).fillMaxWidth(0.72f), horizontalAlignment = Alignment.CenterHorizontally) {
            val msgs = listOf("Booting Bernportal secure core", "Assembling the learning environment", "Loading 16 modules and 84 lessons", "Preparing simulated labs",
                "Calibrating AI tutor", "Encrypting your workspace", "Ready")
            Text(msgs[(loader * (msgs.size - 1)).toInt().coerceIn(0, msgs.size - 1)] + if (loader < 1f) "..." else "", color = Brand.Muted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFF1B2547))) {
                Box(Modifier.fillMaxWidth(LinearOutSlowInEasing.transform(loader)).height(4.dp).background(Brush.horizontalGradient(listOf(Brand.Cyan, Brand.Violet, Brand.Gold))))
            }
        }
        TextButton(onClick = { finish() }, modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(8.dp)) {
            Text("Skip", color = Brand.Soft)
        }
        Text("© 2026 Bernportal System Innovations", color = Brand.Muted.copy(alpha = 0.6f), fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 14.dp))
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
