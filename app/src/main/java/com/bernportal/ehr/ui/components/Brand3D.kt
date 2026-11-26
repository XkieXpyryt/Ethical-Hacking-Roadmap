@file:OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)

package com.bernportal.ehr.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bernportal.ehr.R
import com.bernportal.ehr.ui.theme.Brand
import com.bernportal.ehr.ui.theme.LocalMotion
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** The glossy Bernportal icon set (from the brand sheets) used in the orbit, roadmap and drawer. */
val BRAND_ICONS = listOf(
    R.drawable.icc_map, R.drawable.icc_book, R.drawable.icc_globe, R.drawable.icc_dict, R.drawable.icc_library,
    R.drawable.icc_notes, R.drawable.icc_cloud, R.drawable.icc_gear, R.drawable.icc_search, R.drawable.icc_sync,
    R.drawable.icc_info, R.drawable.icc_help
)

/**
 * Bernportal emblem with a ring of glossy 3D icons orbiting it on a tilted ellipse.
 * Icons behind the emblem are drawn first, then the emblem, then the icons in front, so the ring genuinely passes behind and in front.
 *
 * @param angleDeg current orbit rotation in degrees
 * @param appear 0..1 - icons rise from below and fan out into their slots
 * @param emblemAlpha 0..1 opacity of the emblem itself (the intro animates it separately)
 */
@Composable
fun BrandOrbit3D(
    width: Dp,
    angleDeg: Float,
    appear: Float,
    modifier: Modifier = Modifier,
    icons: List<Int> = BRAND_ICONS,
    iconSize: Dp = width * 0.17f,
    showEmblem: Boolean = true,
    emblemContent: (@Composable () -> Unit)? = null
) {
    val height = width * 0.78f
    Box(modifier.width(width).height(height), contentAlignment = Alignment.Center) {
        val rx = width.value * 0.50f
        val ry = height.value * 0.20f
        data class Slot(val res: Int, val x: Float, val y: Float, val depth: Float, val idx: Int)
        val n = icons.size
        val slots = icons.mapIndexed { i, res ->
            // fan out: each icon starts at the bottom centre and travels to its orbit slot, staggered
            val local = ((appear * (n + 4) - i) / 4f).coerceIn(0f, 1f)
            val e = 1 - (1 - local) * (1 - local) * (1 - local)
            val a = Math.toRadians((angleDeg + i * 360f / n).toDouble())
            val ox = (rx * cos(a)).toFloat(); val oy = (ry * sin(a)).toFloat() + height.value * 0.06f
            val sx = 0f; val sy = height.value * 0.55f
            Slot(res, sx + (ox - sx) * e, sy + (oy - sy) * e, ((sin(a) + 1) / 2).toFloat() * e + (1 - e) * 0.5f, i).let { it to local }
        }
        // glow pad + ring track
        Canvas(Modifier.fillMaxSize()) {
            drawOval(Brush.radialGradient(listOf(Brand.CyberBlue.copy(alpha = 0.40f), Color.Transparent)), topLeft = Offset(size.width * 0.05f, size.height * 0.1f),
                size = androidx.compose.ui.geometry.Size(size.width * 0.9f, size.height * 0.8f))
            if (appear > 0f) drawOval(
                Brush.horizontalGradient(listOf(Brand.Gold.copy(alpha = 0.05f), Brand.Cyan.copy(alpha = 0.35f * appear), Brand.Gold.copy(alpha = 0.05f))),
                topLeft = Offset(size.width / 2 - rx.dp.toPx(), size.height / 2 + height.toPx() * 0.06f - ry.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(rx.dp.toPx() * 2, ry.dp.toPx() * 2), style = Stroke(2.dp.toPx())
            )
        }
        @Composable fun IconAt(s: Slot, local: Float) {
            if (local <= 0f) return
            val scale = 0.55f + s.depth * 0.6f
            Image(
                painterResource(s.res), null,
                Modifier.size(iconSize).offset(s.x.dp, s.y.dp).graphicsLayer {
                    scaleX = scale * local; scaleY = scale * local
                    alpha = (0.45f + s.depth * 0.55f) * local
                    rotationY = (s.x / rx) * -35f
                    rotationZ = (1 - local) * 90f
                    shadowElevation = 12f * s.depth
                    cameraDistance = 10f * density
                },
                contentScale = ContentScale.Fit
            )
        }
        val back = slots.filter { it.first.depth < 0.5f }.sortedBy { it.first.depth }
        val front = slots.filter { it.first.depth >= 0.5f }.sortedBy { it.first.depth }
        back.forEach { (s, l) -> IconAt(s, l) }
        if (showEmblem) {
            if (emblemContent != null) emblemContent() else Image(
                painterResource(R.drawable.brand_emblem), "Bernportal System Innovations emblem",
                Modifier.fillMaxWidth(0.7f), contentScale = ContentScale.FillWidth
            )
        }
        front.forEach { (s, l) -> IconAt(s, l) }
    }
}

/** Self-animating version of [BrandOrbit3D] for onboarding / about screens. */
@Composable
fun BrandOrbitAnimated(width: Dp, modifier: Modifier = Modifier) {
    val motion = LocalMotion.current
    val tr = rememberInfiniteTransition(label = "orbit")
    val angle by tr.animateFloat(0f, 360f, infiniteRepeatable(tween(if (motion.full) 16000 else 40000, easing = LinearEasing)), label = "a")
    val bob by tr.animateFloat(-1f, 1f, infiniteRepeatable(tween(2600), RepeatMode.Reverse), label = "b")
    BrandOrbit3D(width, if (motion.enabled) angle else 30f, 1f, modifier) {
        Image(
            painterResource(R.drawable.brand_emblem), "Bernportal System Innovations emblem",
            Modifier.fillMaxWidth(0.7f).graphicsLayer {
                if (motion.enabled) { translationY = bob * 6f * density; if (motion.effects3d) rotationY = bob * 14f }
                cameraDistance = 14f * density
            },
            contentScale = ContentScale.FillWidth
        )
    }
}

/**
 * Text with a genuine extruded 3D look: a stack of offset depth layers in dark gold, then a bright bevelled face.
 * [progress] (0..1) flips the letters in one after another around the X axis.
 */
@Composable
fun ExtrudedText3D(
    text: String,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
    progress: Float = 1f,
    depth: Int = 6,
    face: List<Color> = listOf(Color(0xFFFFF4C2), Brand.Gold, Brand.GoldDeep),
    side: Color = Color(0xFF5A3A08),
    letterSpacing: TextUnit = 2.sp,
    flip3d: Boolean = true
) {
    Row(modifier, horizontalArrangement = Arrangement.Center) {
        val n = text.length.coerceAtLeast(1)
        text.forEachIndexed { i, ch ->
            val local = ((progress * (n + 4) - i) / 4f).coerceIn(0f, 1f)
            val e = 1 - (1 - local) * (1 - local) * (1 - local)
            Box(
                Modifier.wrapContentSize().graphicsLayer {
                    alpha = e
                    if (flip3d) rotationX = 100f * (1 - e)
                    translationY = (1 - e) * 30f
                    scaleX = 0.6f + 0.4f * e; scaleY = 0.6f + 0.4f * e
                    cameraDistance = 8f * density
                }
            ) {
                val style = TextStyle(fontSize = fontSize, fontWeight = FontWeight.Black, letterSpacing = letterSpacing)
                for (d in depth downTo 1) {
                    Text(ch.toString(), style = style.copy(color = side.copy(alpha = 0.55f + 0.45f * (1 - d / depth.toFloat()))),
                        modifier = Modifier.offset((d * 0.6f).dp, (d * 0.9f).dp))
                }
                Text(ch.toString(), style = style.copy(
                    brush = Brush.verticalGradient(face),
                    shadow = Shadow(face[1].copy(alpha = 0.55f), Offset(0f, 0f), 16f)
                ))
            }
        }
    }
}

/** A real 3D-projected cube (3 visible shaded faces) - the "pixel" blocks of the Bernportal logo. */
fun DrawScope.drawCube3D(center: Offset, sizePx: Float, rx: Float, ry: Float, base: Color, alpha: Float = 1f) {
    if (alpha <= 0f) return
    val h = sizePx / 2
    val verts = listOf(
        floatArrayOf(-h, -h, -h), floatArrayOf(h, -h, -h), floatArrayOf(h, h, -h), floatArrayOf(-h, h, -h),
        floatArrayOf(-h, -h, h), floatArrayOf(h, -h, h), floatArrayOf(h, h, h), floatArrayOf(-h, h, h)
    )
    val cx = cos(rx); val sx = sin(rx); val cy = cos(ry); val sy = sin(ry)
    val rot = verts.map { v ->
        val x1 = v[0] * cy + v[2] * sy; val z1 = -v[0] * sy + v[2] * cy
        val y2 = v[1] * cx - z1 * sx; val z2 = v[1] * sx + z1 * cx
        floatArrayOf(x1, y2, z2)
    }
    val f = sizePx * 6f
    val proj = rot.map { v -> val k = f / (f + v[2]); Offset(center.x + v[0] * k, center.y + v[1] * k) }
    val faces = listOf(intArrayOf(0, 1, 2, 3), intArrayOf(5, 4, 7, 6), intArrayOf(4, 0, 3, 7), intArrayOf(1, 5, 6, 2), intArrayOf(4, 5, 1, 0), intArrayOf(3, 2, 6, 7))
    val light = floatArrayOf(-0.4f, -0.7f, -0.6f).let { val l = sqrt(it[0] * it[0] + it[1] * it[1] + it[2] * it[2]); floatArrayOf(it[0] / l, it[1] / l, it[2] / l) }
    faces.map { fc ->
        val a = rot[fc[0]]; val b = rot[fc[1]]; val c = rot[fc[2]]
        val ux = b[0] - a[0]; val uy = b[1] - a[1]; val uz = b[2] - a[2]
        val vx = c[0] - a[0]; val vy = c[1] - a[1]; val vz = c[2] - a[2]
        var nx = uy * vz - uz * vy; var ny = uz * vx - ux * vz; var nz = ux * vy - uy * vx
        val l = sqrt(nx * nx + ny * ny + nz * nz).coerceAtLeast(1e-4f); nx /= l; ny /= l; nz /= l
        val z = fc.map { rot[it][2] }.average().toFloat()
        Triple(fc, floatArrayOf(nx, ny, nz), z)
    }.filter { it.second[2] < 0f }.sortedByDescending { it.third }.forEach { (fc, nrm, _) ->
        val shade = (0.45f + 0.75f * (-(nrm[0] * light[0] + nrm[1] * light[1] + nrm[2] * light[2])).coerceIn(0f, 1f)).coerceIn(0.35f, 1.25f)
        val col = Color((base.red * shade).coerceAtMost(1f), (base.green * shade).coerceAtMost(1f), (base.blue * shade).coerceAtMost(1f), alpha)
        val p = Path().apply { moveTo(proj[fc[0]].x, proj[fc[0]].y); for (k in 1..3) lineTo(proj[fc[k]].x, proj[fc[k]].y); close() }
        drawPath(p, col)
        drawPath(p, Color.White.copy(alpha = 0.25f * alpha), style = Stroke(1.2f))
    }
}

/** Small row of tumbling 3D logo cubes - used as a decorative divider. */
@Composable
fun CubeTrail(modifier: Modifier = Modifier, count: Int = 5) {
    val motion = LocalMotion.current
    val tr = rememberInfiniteTransition(label = "cubes")
    val t by tr.animateFloat(0f, (2 * PI).toFloat(), infiniteRepeatable(tween(5000, easing = LinearEasing)), label = "t")
    Canvas(modifier.fillMaxWidth().height(28.dp)) {
        val gap = size.width / (count + 1)
        for (i in 0 until count) {
            val ph = if (motion.enabled) t + i * 0.7f else 0.5f
            drawCube3D(Offset(gap * (i + 1), size.height / 2), 14.dp.toPx() * (0.7f + 0.3f * (i % 2)), ph * 0.8f, ph,
                if (i == count / 2) Brand.Gold else Brand.CyberBlue)
        }
    }
}

/** A glossy brand-icon image that tilts in 3D when pressed (used for quick-access tiles). */
@Composable
fun BrandIconButton(res: Int, size: Dp, label: String, onClick: () -> Unit) {
    val motion = LocalMotion.current
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    val tilt by animateFloatAsState(if (pressed && motion.effects3d) 18f else 0f, tween(motion.dur(180)), label = "tilt")
    Image(
        painterResource(res), label,
        Modifier.size(size)
            .graphicsLayer { rotationX = tilt; rotationY = -tilt * 0.7f; val s = 1f - tilt / 200f; scaleX = s; scaleY = s; cameraDistance = 12f * density }
            .clickable(interactionSource = src, indication = null, onClick = onClick),
        contentScale = ContentScale.Fit
    )
}
