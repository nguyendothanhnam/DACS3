package com.hunglevi.expense_mdc.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val axisPaint = Paint().apply {
        color = Color.parseColor("#0E3E3E")
        strokeWidth = 5f
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#6DB6FE")
        textSize = 32f
    }

    private val incomePaint = Paint().apply {
        color = Color.parseColor("#00D09E")
        style = Paint.Style.FILL
    }

    private val expensePaint = Paint().apply {
        color = Color.parseColor("#0068FF")
        style = Paint.Style.FILL
    }

    private val barWidth = 50f
    private val barSpacing = 50f

    private var incomeData = listOf<Float>()
    private var expenseData = listOf<Float>()
    private var labels = listOf<String>()

    fun setData(income: List<Float>, expense: List<Float>, period: String) {
        val itemCount = minOf(income.size, expense.size)

        labels = when (period) {
            "Last 7 Days" -> List(itemCount) { "Day ${it + 1}" } // Generate labels for last 7 days
            else -> List(itemCount) { "Item ${it + 1}" } // Default fallback
        }

        incomeData = income.take(itemCount)
        expenseData = expense.take(itemCount)

        invalidate() // Trigger redraw
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (incomeData.isEmpty() && expenseData.isEmpty()) {
            canvas.drawText(
                "No data for Last 7 Days",
                width / 2f - textPaint.measureText("No data for Last 7 Days") / 2f,
                height / 2f,
                textPaint.apply { textSize = 40f; color = Color.GRAY }
            )
            return
        }
        val xStart = 100f
        val yEnd = height - 100f
        val chartHeight = yEnd - 200f
        val maxYValue = maxOf(incomeData.maxOrNull() ?: 1f, expenseData.maxOrNull() ?: 1f) // Ensure scaling handles empty datasets
        val yStep = maxYValue / 5 // Divide Y-axis into 5 steps

        // Draw Y-axis
        canvas.drawLine(xStart, yEnd, xStart, 100f, axisPaint)

        val dashPaint = Paint().apply {
            color = Color.GRAY
            style = Paint.Style.STROKE
            strokeWidth = 2f
            pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f)
        }

        // Draw Y-axis labels and dashed lines
        for (i in 0..5) {
            val y = yEnd - (chartHeight * i / 5)
            val label = (yStep * i).toInt().toString()

            canvas.drawText(label, xStart - 50f, y + 10f, textPaint)
            canvas.drawLine(xStart, y, width - 50f, y, dashPaint)
        }

        // Draw X-axis
        canvas.drawLine(xStart, yEnd, width - 50f, yEnd, axisPaint)

        // Ensure safe iteration through data lists
        val itemCount = minOf(incomeData.size, expenseData.size, labels.size)
        for (i in 0 until itemCount) {
            val x = xStart + i * (barWidth * 2 + barSpacing)

            val incomeHeight = chartHeight * (incomeData[i] / maxYValue)
            val expenseHeight = chartHeight * (expenseData[i] / maxYValue)

            canvas.drawRect(
                x,
                yEnd - incomeHeight,
                x + barWidth,
                yEnd,
                incomePaint
            )

            canvas.drawRect(
                x + barWidth + 10f,
                yEnd + expenseHeight,
                x + barWidth * 2 + 10f,
                yEnd,
                expensePaint
            )

            val label = labels.getOrElse(i) { "Item $i" }
            canvas.drawText(label, x + barWidth / 2f, yEnd + 40f, textPaint)
        }

        // Draw Chart Title
        canvas.drawText(
            "Thu Nhập & Chi Phí",
            width / 2f - 120f,
            50f,
            textPaint.apply { textSize = 40f; color = Color.BLACK }
        )
    }
}