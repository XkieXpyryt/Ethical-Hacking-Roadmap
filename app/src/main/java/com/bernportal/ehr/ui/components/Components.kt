@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.bernportal.ehr.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernportal.ehr.R
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun iconFor(name: String): ImageVector = when (name) {
    "computer" -> Icons.Filled.Computer; "network" -> Icons.Filled.Hub; "terminal" -> Icons.Filled.Terminal
    "code" -> Icons.Filled.Code; "shield" -> Icons.Filled.Shield; "radar" -> Icons.Filled.Radar; "scan" -> Icons.Filled.TrackChanges
    "bug" -> Icons.Filled.BugReport; "web" -> Icons.Filled.Language; "target" -> Icons.Filled.GpsFixed; "wifi" -> Icons.Filled.Wifi
    "bolt" -> Icons.Filled.Bolt; "layers" -> Icons.Filled.Layers; "report" -> Icons.Filled.Description; "flask" -> Icons.Filled.Science
    "briefcase" -> Icons.Filled.Work; else -> Icons.Filled.School
}

/** Palette per module index so every module has a distinctive glossy 3D tile. */
fun paletteFor(i: Int): List<Color> = listOf(
    listOf(Color(0xFF1E40AF), Color(0xFF2563EB)), listOf(Color(0xFF0E7490), Color(0xFF22D3EE)), listOf(Color(0xFF166534), Color(0xFF22C55E)),
    listOf(Color(0xFF6D28D9), Color(0xFFA78BFA)), listOf(Color(0xFF1D4ED8), Color(0xFF38BDF8)), listOf(Color(0xFF9D174D), Color(0xFFF472B6)),
    listOf(Color(0xFFB45309), Color(0xFFFBBF24)), listOf(Color(0xFF7F1D1D), Color(0xFFF87171))
)[((i % 8) + 8) % 8]

/** Static circuit-grid background (no continuous animation, so screens can go idle and save battery). */
@Composable
fun AppBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val bg = MaterialTheme.colorScheme.background
    val accent = MaterialTheme.colorScheme.primary
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(bg, bg.copy(red = bg.red * 0.8f, green = bg.green * 0.85f, blue = (bg.blue * 1.15f).coerceAtMost(1f)))))
            .drawBehind {
                val step = 64.dp.toPx()
                val line = accent.copy(alpha = 0.05f)
                var x = 0f
                while (x < size.width) { drawLine(line, Offset(x, 0f), Offset(x, size.height), 1f); x += step }
                var y = 0f
                while (y < size.height) { drawLine(line, Offset(0f, y), Offset(size.width, y), 1f); y += step }
                for (k in 0 until 6) {
                    val row = ((k * 3 + 1) * step) % size.height
                    val px = ((0.13f + k * 0.17f) % 1f) * size.width
                    drawCircle(accent.copy(alpha = 0.35f), 3.dp.toPx(), Offset(px, row))
                    drawLine(Brush.horizontalGradient(listOf(Color.Transparent, accent.copy(alpha = 0.25f)), px - 120f, px), Offset(px - 120f, row), Offset(px, row), 2f)
                }
                drawCircle(Brush.radialGradient(listOf(Brand.Violet.copy(alpha = 0.10f), Color.Transparent), Offset(size.width * 0.9f, size.height * 0.1f), size.width * 0.7f), size.width * 0.7f, Offset(size.width * 0.9f, size.height * 0.1f))
                drawCircle(Brush.radialGradient(listOf(accent.copy(alpha = 0.08f), Color.Transparent), Offset(0f, size.height), size.width * 0.8f), size.width * 0.8f, Offset(0f, size.height))
            },
        content = content
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    glow: Color = MaterialTheme.colorScheme.primary,
    onClick: (() -> Unit)? = null,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val motion = LocalMotion.current
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed && motion.enabled) 0.97f else 1f, tween(motion.dur(160)), label = "press")
    val shape = RoundedCornerShape(20.dp)
    val surface = MaterialTheme.colorScheme.surface
    Column(
        modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; if (motion.effects3d && pressed) rotationX = 4f }
            .shadow(10.dp, shape, ambientColor = glow.copy(alpha = 0.25f), spotColor = glow.copy(alpha = 0.35f))
            .clip(shape)
            .background(Brush.linearGradient(listOf(surface.copy(alpha = 0.92f), surface.copy(alpha = 0.70f))))
            .border(1.dp, Brush.linearGradient(listOf(glow.copy(alpha = 0.55f), Color.White.copy(alpha = 0.08f), glow.copy(alpha = 0.20f))), shape)
            .then(if (onClick != null) Modifier.clickable(interactionSource = src, indication = androidx.compose.material3.ripple(), onClick = onClick) else Modifier)
            .padding(padding),
        content = content
    )
}

/** Glossy, bevelled 3D icon tile in the Bernportal brand style (gold rim, specular highlight, depth shadow, tilt on press). */
@Composable
fun Glossy3DIcon(icon: ImageVector, colors: List<Color>, size: Dp = 56.dp, contentDescription: String? = null, onClick: (() -> Unit)? = null) {
    val motion = LocalMotion.current
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val tilt by animateFloatAsState(if (pressed && motion.effects3d) 14f else 0f, tween(motion.dur(180)), label = "tilt")
    val shape = RoundedCornerShape(size * 0.28f)
    Box(
        Modifier
            .size(size)
            .graphicsLayer { rotationX = tilt; rotationY = -tilt * 0.6f; cameraDistance = 12f * density }
            .shadow(if (motion.effects3d) 10.dp else 2.dp, shape, spotColor = colors.last())
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.last(), colors.first()), Offset.Zero, Offset(0f, Float.POSITIVE_INFINITY)))
            .border(2.dp, Brush.linearGradient(listOf(Brand.Gold, Color(0xFFFFF1B8), Brand.GoldDeep)), shape)
            .then(if (onClick != null) Modifier.clickable(interactionSource = src, indication = null, onClick = onClick) else Modifier)
            .semantics { if (contentDescription != null) this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = this.size.width; val h = this.size.height
            drawRoundRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.45f), Color.Transparent), 0f, h * 0.55f),
                topLeft = Offset(w * 0.08f, h * 0.06f), size = Size(w * 0.84f, h * 0.42f),
                cornerRadius = CornerRadius(w * 0.22f))
        }
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(size * 0.52f).graphicsLayer { shadowElevation = 6f })
    }
}

@Composable
fun BrandImage(res: Int, size: Dp, modifier: Modifier = Modifier, description: String? = null) {
    Image(painterResource(res), description, modifier.size(size), contentScale = ContentScale.Fit)
}

/** Floating 3D emblem with gentle rotation and glow. */
@Composable
fun Emblem3D(width: Dp, modifier: Modifier = Modifier) {
    val motion = LocalMotion.current
    val t = rememberInfiniteTransition(label = "emblem")
    val float by t.animateFloat(-6f, 6f, infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "f")
    val rot by t.animateFloat(-12f, 12f, infiniteRepeatable(tween(4200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "r")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(width * 0.9f, width * 0.5f)) {
            drawOval(Brush.radialGradient(listOf(Brand.CyberBlue.copy(alpha = 0.45f), Color.Transparent)), size = size)
        }
        Image(
            painterResource(R.drawable.brand_emblem), "Bernportal System Innovations emblem",
            Modifier.width(width).graphicsLayer {
                if (motion.enabled) { translationY = float * density; if (motion.effects3d) rotationY = rot }
                cameraDistance = 14f * density
            },
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun ProgressRing(value: Float, size: Dp = 64.dp, stroke: Dp = 7.dp, color: Color = MaterialTheme.colorScheme.primary, label: String? = null) {
    val motion = LocalMotion.current
    val anim by animateFloatAsState(value.coerceIn(0f, 1f), tween(motion.dur(900)), label = "ring")
    val track = MaterialTheme.colorScheme.surfaceVariant
    Box(Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val s = stroke.toPx()
            drawArc(track, -90f, 360f, false, style = Stroke(s, cap = StrokeCap.Round), topLeft = Offset(s / 2, s / 2), size = Size(this.size.width - s, this.size.height - s))
            drawArc(Brush.sweepGradient(listOf(color, Brand.Violet, color)), -90f, 360f * anim, false, style = Stroke(s, cap = StrokeCap.Round),
                topLeft = Offset(s / 2, s / 2), size = Size(this.size.width - s, this.size.height - s))
        }
        Text(label ?: "${(value * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SectionTitle(text: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(width = 4.dp, height = 18.dp).clip(RoundedCornerShape(2.dp)).background(Brush.verticalGradient(listOf(Brand.Cyan, Brand.Violet))))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (action != null && onAction != null) TextButton(onClick = onAction) { Text(action) }
    }
}

@Composable
fun Breadcrumb(parts: List<String>) {
    Row(Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        parts.forEachIndexed { i, p ->
            Text(p, style = MaterialTheme.typography.labelMedium, color = if (i == parts.lastIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            if (i < parts.lastIndex) Icon(Icons.Filled.ChevronRight, null, Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun Pill(text: String, color: Color = MaterialTheme.colorScheme.primary, modifier: Modifier = Modifier) {
    Text(
        text, style = MaterialTheme.typography.labelSmall, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis,
        modifier = modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.14f)).border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
fun ConnectivityChip(online: Boolean, syncing: Boolean = false) {
    val (label, color, icon) = when {
        syncing -> Triple("Syncing - optional update", Brand.Warn, Icons.Filled.Sync)
        online -> Triple("Online - AI tutor available", Brand.Neon, Icons.Filled.CloudDone)
        else -> Triple("Offline - learning available", Brand.Cyan, Icons.Filled.CloudOff)
    }
    Row(
        Modifier.clip(RoundedCornerShape(50)).background(color.copy(alpha = 0.12f)).padding(horizontal = 10.dp, vertical = 4.dp)
            .semantics(mergeDescendants = true) {}, verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(14.dp), tint = color); Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun MonoBlock(text: String, modifier: Modifier = Modifier, color: Color = Color(0xFF9EF0B5)) {
    SelectionContainer {
        Text(
            text, fontFamily = FontFamily.Monospace, fontSize = 12.5.sp, lineHeight = 18.sp, color = color,
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF02060F))
                .border(1.dp, Color(0xFF1E3A5F), RoundedCornerShape(12.dp)).horizontalScroll(rememberScrollState()).padding(12.dp)
        )
    }
}

@Composable
fun StatTile(value: String, label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier, glow = color, padding = 12.dp) {
        Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
    }
}

@Composable
fun EmptyState(icon: ImageVector, title: String, body: String) {
    Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Glossy3DIcon(icon, paletteFor(3), 64.dp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun BulletList(items: List<String>, color: Color = MaterialTheme.colorScheme.primary) {
    Column {
        items.forEach {
            Row(Modifier.padding(vertical = 3.dp)) {
                Box(Modifier.padding(top = 7.dp).size(6.dp).clip(CircleShape).background(color))
                Spacer(Modifier.width(10.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun ChipRow(items: List<String>, onClick: ((String) -> Unit)? = null) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items.forEach { Pill(it, modifier = if (onClick != null) Modifier.clickable { onClick(it) } else Modifier) }
    }
}

/** Animated "packet journey" diagram used as the animated visual for every lesson. */
@Composable
fun FlowDiagram(steps: List<String>, modifier: Modifier = Modifier) {
    val motion = LocalMotion.current
    var revealed by remember(steps) { mutableIntStateOf(if (motion.enabled) 0 else steps.size) }
    LaunchedEffect(steps, motion.level) {
        if (motion.enabled) for (i in 1..steps.size) { kotlinx.coroutines.delay(motion.dur(420).toLong()); revealed = i } else revealed = steps.size
    }
    val t = rememberInfiniteTransition(label = "packet")
    val pkt by t.animateFloat(0f, 1f, infiniteRepeatable(tween(2400 + steps.size * 250, easing = LinearEasing)), label = "pkt")
    val primary = MaterialTheme.colorScheme.primary
    Column(modifier) {
        steps.forEachIndexed { i, s ->
            val a by animateFloatAsState(if (i < revealed) 1f else 0f, tween(motion.dur(400)), label = "a$i")
            Row(Modifier.fillMaxWidth().graphicsLayer { alpha = a; translationX = (1 - a) * 60f; if (motion.effects3d) rotationY = (1 - a) * 25f }, verticalAlignment = Alignment.CenterVertically) {
                Glossy3DIcon(Icons.Filled.Memory, paletteFor(i + 1), 40.dp)
                Spacer(Modifier.width(12.dp))
                GlassCard(Modifier.weight(1f), glow = paletteFor(i + 1).last(), padding = 10.dp) { Text("${i + 1}. $s", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium) }
            }
            if (i < steps.lastIndex) {
                Box(Modifier.padding(start = 19.dp).height(26.dp).width(2.dp)) {
                    Canvas(Modifier.fillMaxSize()) {
                        drawLine(primary.copy(alpha = 0.35f), Offset(1f, 0f), Offset(1f, size.height), 2f)
                        val seg = 1f / steps.size
                        val local = (pkt - i * seg) / seg
                        if (motion.enabled && local in 0f..1f) drawCircle(Brand.Gold, 5f, Offset(1f, size.height * local))
                    }
                }
            }
        }
    }
}

@Composable
fun RadarChart(labels: List<String>, values: List<Float>, modifier: Modifier = Modifier) {
    val primary = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val motion = LocalMotion.current
    val anim by animateFloatAsState(1f, tween(motion.dur(1000)), label = "radar")
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2, size.height / 2)
            val r = minOf(size.width, size.height) / 2 * 0.72f
            val n = labels.size
            fun pt(i: Int, f: Float): Offset { val ang = -PI / 2 + 2 * PI * i / n; return Offset(c.x + (r * f * cos(ang)).toFloat(), c.y + (r * f * sin(ang)).toFloat()) }
            for (ring in 1..4) {
                val p = Path(); for (i in 0 until n) { val o = pt(i, ring / 4f); if (i == 0) p.moveTo(o.x, o.y) else p.lineTo(o.x, o.y) }; p.close()
                drawPath(p, grid, style = Stroke(1f))
            }
            for (i in 0 until n) drawLine(grid, c, pt(i, 1f), 1f)
            val v = Path()
            values.forEachIndexed { i, f -> val o = pt(i, (0.05f + f.coerceIn(0f, 1f) * 0.95f) * anim); if (i == 0) v.moveTo(o.x, o.y) else v.lineTo(o.x, o.y) }
            v.close()
            drawPath(v, Brush.radialGradient(listOf(primary.copy(alpha = 0.55f), Brand.Violet.copy(alpha = 0.35f)), c, r))
            drawPath(v, primary, style = Stroke(3f))
        }
        labels.forEachIndexed { i, l ->
            val ang = -PI / 2 + 2 * PI * i / labels.size
            Text(
                l, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).width(84.dp).radarLabel(ang)
            )
        }
    }
}

private fun Modifier.radarLabel(ang: Double) = this.graphicsLayer {
    translationX = (cos(ang) * 135.dp.toPx()).toFloat()
    translationY = (sin(ang) * 118.dp.toPx()).toFloat()
}

/** 3D medal for achievements. */
@Composable
fun Medal(title: String, earned: Boolean, progress: Float, size: Dp = 64.dp) {
    val motion = LocalMotion.current
    val spin = if (earned && motion.full && motion.effects3d) {
        val t = rememberInfiniteTransition(label = "medal")
        t.animateFloat(0f, 360f, infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "spin").value
    } else 0f
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(size + 24.dp)) {
        Box(
            Modifier.size(size).graphicsLayer { rotationY = spin; cameraDistance = 10f * density }
                .shadow(if (earned) 12.dp else 0.dp, CircleShape, spotColor = Brand.Gold).clip(CircleShape)
                .background(
                    if (earned) Brush.radialGradient(listOf(Color(0xFFFFF1B8), Brand.Gold, Brand.GoldDeep))
                    else Brush.radialGradient(listOf(Color(0xFF3B4A6B), Color(0xFF1B2440)))
                )
                .border(3.dp, if (earned) Color(0xFFFFE08A) else Color(0xFF34436B), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (earned) Icon(Icons.Filled.EmojiEvents, null, tint = Color(0xFF6B3E00), modifier = Modifier.size(size * 0.5f))
            else ProgressRing(progress, size * 0.8f, 4.dp, Brand.Cyan)
        }
        Spacer(Modifier.height(4.dp))
        Text(title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
