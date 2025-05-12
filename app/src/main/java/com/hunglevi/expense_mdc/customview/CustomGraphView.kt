package com.hunglevi.expense_mdc.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomGraphView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axisPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
    }

    private val pointPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 10f
    }

    private val linePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 6f
    }

    private val textPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 30f
    }

    private val graphData = listOf(
        Pair(1f, 1000f),
        Pair(2f, 1200f),
        Pair(3f, 800f),
        Pair(4f, 1500f)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw axes
        canvas.drawLine(100f, height - 100f, width.toFloat() - 50f, height - 100f, axisPaint) // X-axis
        canvas.drawLine(100f, height - 100f, 100f, 50f, axisPaint) // Y-axis

        // Draw graph points and lines
        for (i in graphData.indices) {
            val x = 100f + graphData[i].first * 150f
            val y = height - 100f - graphData[i].second / 10f

            canvas.drawCircle(x, y, 10f, pointPaint)
            canvas.drawText("(${graphData[i].first}, ${graphData[i].second.toInt()})", x + 10f, y - 10f, textPaint)

            if (i < graphData.size - 1) {
                val nextX = 100f + graphData[i + 1].first * 150f
                val nextY = height - 100f - graphData[i + 1].second / 10f
                canvas.drawLine(x, y, nextX, nextY, linePaint)
            }
        }
    }
}