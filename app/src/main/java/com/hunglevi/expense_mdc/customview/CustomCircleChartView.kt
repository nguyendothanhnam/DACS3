package com.hunglevi.expense_mdc.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomCircleChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var percentage: Float = 50f // Default progress percentage
    private var label: String = "Progress"
    private var circleColor: Int = Color.parseColor("#6DB6FE") // Outer circle color
    private var progressColor: Int = Color.parseColor("#0068FF") // Progress color
    private var unprogressColor: Int = Color.WHITE // Default unprogressed color
    private var labelColor: Int = Color.WHITE

    private val baseCirclePaint = Paint().apply {
        color = unprogressColor
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {
        color = progressColor
        style = Paint.Style.STROKE
        strokeWidth = 20f
        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {
        color = labelColor
        textSize = 50f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // Method to set data dynamically
    fun setData(percentage: Float, label: String, circleColor: Int, progressColor: Int) {
        this.percentage = percentage.coerceIn(0f, 100f) // Ensure percentage stays within 0-100%
        this.label = label
        this.circleColor = circleColor
        this.progressColor = progressColor
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate dimensions
        val centerX = width / 2f
        val centerY = height / 2f
        val radius = (width.coerceAtMost(height) / 2f) - 40f

        // Draw base circle (unprogressed part)
        baseCirclePaint.color = unprogressColor
        canvas.drawCircle(centerX, centerY, radius, baseCirclePaint)

        // Draw progress arc (progressed part)
        val startAngle = -90f // Start at top (12 o'clock)
        val sweepAngle = (percentage / 100f) * 360f
        progressPaint.color = progressColor
        canvas.drawArc(
            centerX - radius, // Left
            centerY - radius, // Top
            centerX + radius, // Right
            centerY + radius, // Bottom
            startAngle,
            sweepAngle,
            false,
            progressPaint
        )

        // Draw percentage label inside the circle
        labelPaint.color = labelColor
        canvas.drawText("$percentage%", centerX, centerY, labelPaint)

        // Draw additional label below the progress
        canvas.drawText(label, centerX, centerY + 70f, labelPaint.apply { textSize = 40f })
    }
}