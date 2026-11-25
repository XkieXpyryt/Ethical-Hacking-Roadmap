package com.bernportal.ehr.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.bernportal.ehr.R
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Minimal multi-page PDF writer built on Android's free, built-in PdfDocument API (no third-party libraries). */
class PdfBuilder(private val context: Context, private val docTitle: String, private val landscape: Boolean = false) {
    private val w = if (landscape) 842 else 595
    private val h = if (landscape) 595 else 842
    private val margin = 48f
    private val doc = PdfDocument()
    private var page: PdfDocument.Page? = null
    private var canvas: Canvas? = null
    private var y = 0f
    private var pageNo = 0
    private val emblem: Bitmap? = try { BitmapFactory.decodeResource(context.resources, R.drawable.brand_emblem) } catch (e: Exception) { null }

    private val navy = Color.rgb(8, 16, 46)
    private val cyan = Color.rgb(14, 165, 233)
    private val gold = Color.rgb(212, 160, 23)

    private fun paint(size: Float, bold: Boolean = false, color: Int = Color.rgb(20, 24, 38)) = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = size; this.color = color; typeface = Typeface.create(Typeface.SANS_SERIF, if (bold) Typeface.BOLD else Typeface.NORMAL)
    }

    private fun newPage() {
        finishPage()
        pageNo++
        page = doc.startPage(PdfDocument.PageInfo.Builder(w, h, pageNo).create())
        canvas = page!!.canvas
        val c = canvas!!
        val band = Paint().apply { shader = LinearGradient(0f, 0f, w.toFloat(), 0f, navy, Color.rgb(30, 64, 175), Shader.TileMode.CLAMP) }
        c.drawRect(0f, 0f, w.toFloat(), 40f, band)
        emblem?.let { c.drawBitmap(it, null, RectF(margin, 6f, margin + 52f, 34f), null) }
        c.drawText("Bernportal System Innovations  |  $docTitle", margin + 60f, 25f, paint(9f, true, Color.WHITE))
        c.drawText("Page $pageNo", w - margin - 40f, h - 20f, paint(8f, color = Color.GRAY))
        c.drawText("Generated offline by Ethical Hacking Roadmap 2026/2027", margin, h - 20f, paint(8f, color = Color.GRAY))
        y = 64f
    }

    private fun finishPage() { page?.let { doc.finishPage(it) }; page = null }

    private fun ensure(space: Float) { if (page == null || y + space > h - 44f) newPage() }

    private fun layout(text: String, p: TextPaint, width: Int) =
        StaticLayout.Builder.obtain(text, 0, text.length, p, width).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(2f, 1f).build()

    fun text(text: String, size: Float = 10.5f, bold: Boolean = false, color: Int = Color.rgb(20, 24, 38), indent: Float = 0f): PdfBuilder {
        if (text.isBlank()) return this
        val p = paint(size, bold, color)
        val width = (w - 2 * margin - indent).toInt()
        // split into chunks of paragraphs so long text can flow across pages
        text.split("\n").forEach { para ->
            val sl = layout(para.ifBlank { " " }, p, width)
            for (line in 0 until sl.lineCount) {
                val lh = (sl.getLineBottom(line) - sl.getLineTop(line)).toFloat()
                ensure(lh)
                val start = sl.getLineStart(line); val end = sl.getLineEnd(line)
                canvas!!.drawText(para.ifBlank { " " }, start, end, margin + indent, y + lh * 0.8f, p)
                y += lh
            }
        }
        y += 4f
        return this
    }

    fun title(text: String, subtitle: String = ""): PdfBuilder {
        ensure(80f)
        text(text, 22f, true, navy)
        if (subtitle.isNotBlank()) text(subtitle, 11f, false, Color.DKGRAY)
        canvas!!.drawRect(margin, y, margin + 120f, y + 3f, Paint().apply { color = gold }); y += 14f
        return this
    }

    fun heading(text: String): PdfBuilder {
        ensure(40f); y += 6f
        text(text, 14f, true, cyan)
        canvas!!.drawLine(margin, y, w - margin, y, Paint().apply { color = Color.rgb(220, 226, 236); strokeWidth = 1f }); y += 6f
        return this
    }

    fun bullet(items: List<String>): PdfBuilder { items.forEach { text("•  $it", indent = 8f) }; return this }

    fun kv(key: String, value: String): PdfBuilder { if (value.isNotBlank()) text("$key: $value"); return this }

    fun table(headers: List<String>, rows: List<List<String>>, weights: List<Float>): PdfBuilder {
        val total = weights.sum(); val avail = w - 2 * margin
        val cols = weights.map { it / total * avail }
        fun row(cells: List<String>, bold: Boolean, fill: Int?) {
            val p = paint(9f, bold, if (bold) Color.WHITE else Color.rgb(20, 24, 38))
            val layouts = cells.mapIndexed { i, s -> layout(s, p, (cols[i] - 8).toInt().coerceAtLeast(20)) }
            val rh = (layouts.maxOf { it.height } + 8).toFloat()
            ensure(rh)
            val c = canvas!!
            if (fill != null) c.drawRect(margin, y, w - margin, y + rh, Paint().apply { color = fill })
            var x = margin
            layouts.forEachIndexed { i, sl -> c.save(); c.translate(x + 4, y + 4); sl.draw(c); c.restore(); x += cols[i] }
            c.drawLine(margin, y + rh, w - margin, y + rh, Paint().apply { color = Color.rgb(220, 226, 236) })
            y += rh
        }
        row(headers, true, navy)
        rows.forEachIndexed { i, r -> row(r, false, if (i % 2 == 0) Color.rgb(244, 247, 252) else null) }
        y += 8f
        return this
    }

    fun space(px: Float = 10f): PdfBuilder { y += px; return this }

    /** A one-page, landscape certificate design. */
    fun certificate(learner: String, title: String, detail: String, skills: String, score: String, date: String, certId: String): PdfBuilder {
        newPage()
        val c = canvas!!
        c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), Paint().apply { color = Color.WHITE })
        val border = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 10f; shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), gold, Color.rgb(255, 225, 120), Shader.TileMode.MIRROR) }
        c.drawRect(18f, 18f, w - 18f, h - 18f, border)
        c.drawRect(32f, 32f, w - 32f, h - 32f, Paint().apply { style = Paint.Style.STROKE; strokeWidth = 1.5f; color = navy })
        emblem?.let { c.drawBitmap(it, null, RectF(w / 2f - 70f, 46f, w / 2f + 70f, 122f), null) }
        fun center(t: String, yy: Float, size: Float, bold: Boolean, color: Int) {
            val p = paint(size, bold, color); c.drawText(t, (w - p.measureText(t)) / 2f, yy, p)
        }
        center("BERNPORTAL SYSTEM INNOVATIONS", 146f, 12f, true, gold)
        center("Certificate of Completion", 186f, 30f, true, navy)
        center("This certifies that", 216f, 12f, false, Color.DKGRAY)
        center(learner.ifBlank { "Learner" }, 254f, 28f, true, Color.rgb(30, 64, 175))
        center("has successfully completed", 280f, 12f, false, Color.DKGRAY)
        center(title, 310f, 18f, true, navy)
        center(detail, 334f, 10.5f, false, Color.DKGRAY)
        center("Skills demonstrated: $skills".take(150), 360f, 10f, false, Color.DKGRAY)
        center("Assessment result: $score    |    Completed: $date", 386f, 10.5f, true, Color.rgb(20, 24, 38))
        center("Certificate ID: $certId", 412f, 9.5f, false, Color.GRAY)
        center("Ethical Hacking Roadmap 2026/2027 - an in-app curriculum certificate. Not an official third-party certification.", h - 52f, 8.5f, false, Color.GRAY)
        y = h.toFloat()
        return this
    }

    fun build(): ByteArray {
        if (page == null && pageNo == 0) newPage()
        finishPage()
        val out = ByteArrayOutputStream()
        doc.writeTo(out); doc.close()
        return out.toByteArray()
    }

    companion object {
        fun today(): String = SimpleDateFormat("d MMMM yyyy", Locale.UK).format(Date())
        fun date(ms: Long): String = SimpleDateFormat("d MMM yyyy", Locale.UK).format(Date(ms))
    }
}
