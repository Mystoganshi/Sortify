package com.example.sortify.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

data class DrawItem(val rect: RectF, val label: String, val color: Int)

class OverlayView @JvmOverloads constructor(
    ctx: Context, attrs: AttributeSet? = null
) : View(ctx, attrs) {

    private var items: List<DrawItem> = emptyList()
    private var srcW = 1
    private var srcH = 1
    private var isFront = false

    private val pBox = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }
    private val pFill = Paint().apply {
        style = Paint.Style.FILL
        color = 0xAA000000.toInt()
        isAntiAlias = true
    }
    private val pText = Paint().apply {
        color = Color.WHITE
        textSize = 36f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
    }

    fun setSourceInfo(imageWidth: Int, imageHeight: Int, rotationDegrees: Int, frontFacing: Boolean) {
        if (rotationDegrees == 90 || rotationDegrees == 270) {
            srcW = imageHeight.coerceAtLeast(1)
            srcH = imageWidth.coerceAtLeast(1)
        } else {
            srcW = imageWidth.coerceAtLeast(1)
            srcH = imageHeight.coerceAtLeast(1)
        }
        isFront = frontFacing
    }

    fun setResults(newItems: List<DrawItem>) {
        items = newItems
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (srcW <= 0 || srcH <= 0) return

        val scaleX = width.toFloat() / srcW
        val scaleY = height.toFloat() / srcH
        val margin = 6f
        val pad = 8f

        for (it in items) {
            val l = it.rect.left * scaleX
            val t = it.rect.top * scaleY
            val r = it.rect.right * scaleX
            val b = it.rect.bottom * scaleY

            val dl = if (isFront) width - r else l
            val dr = if (isFront) width - l else r
            val rect = RectF(dl, t, dr, b)

            pBox.color = it.color
            canvas.drawRect(rect, pBox)

            val text = it.label
            val tw = pText.measureText(text)
            val th = pText.textSize

            var labelLeft = rect.left
            if (labelLeft + tw + 2 * pad > width - margin) {
                labelLeft = (width - margin - tw - 2 * pad).coerceAtLeast(margin)
            }
            if (labelLeft < margin) labelLeft = margin

            val bgRect = RectF()
            val belowTop = rect.bottom + pad
            val belowBottom = belowTop + th + 2 * pad
            val drawBelow = belowBottom + margin <= height

            if (drawBelow) {
                bgRect.left = labelLeft
                bgRect.top = belowTop
                bgRect.right = labelLeft + tw + 2 * pad
                bgRect.bottom = belowBottom
            } else {
                val aboveBottom = rect.top - pad
                val aboveTop = (aboveBottom - th - 2 * pad).coerceAtLeast(margin)
                bgRect.left = labelLeft
                bgRect.top = aboveTop
                bgRect.right = labelLeft + tw + 2 * pad
                bgRect.bottom = aboveBottom
            }

            canvas.drawRoundRect(bgRect, 8f, 8f, pFill)
            canvas.drawText(text, bgRect.left + pad, bgRect.bottom - pad, pText)
        }
    }
}
