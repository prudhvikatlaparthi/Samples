package com.pru.diceapp.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pru.diceapp.R
import kotlin.math.cos
import kotlin.math.sin


class DiceView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {
    /**
     * The number of dice, one to six.
     */
    private var mNumber = NUMBER_ONE

    /**
     * The border color.
     */
    private var mBorderColor = DEFAULT_COLOR

    /**
     * The point color.
     */
    private var mPointColor = DEFAULT_COLOR

    /**
     * The bg color.
     */
    private var mBgColor = DEFAULT_BG_COLOR
    private var mPointPaint: Paint? = null
    private var mBorderPaint: Paint? = null
    private var mBgPaint: Paint? = null
    private var mPointPath: Path? = null
    private var mBorderPath: Path? = null
    private var mBgPath: Path? = null
    private var mWidth = 0
    private var mHeight = 0

    init {
        parseAttrs(attrs)
        initialize()
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DiceView)
        mPointColor = typedArray.getColor(R.styleable.DiceView_pointColor, DEFAULT_COLOR)
        mBorderColor = typedArray.getColor(R.styleable.DiceView_borderColor, DEFAULT_COLOR)
        mBgColor = typedArray.getColor(R.styleable.DiceView_bgColor, DEFAULT_BG_COLOR)
        mNumber = typedArray.getInteger(R.styleable.DiceView_number, NUMBER_ONE)
        if (mNumber < 1) {
            mNumber = 1
        } else if (mNumber > 6) {
            mNumber = 6
        }
        typedArray.recycle()
    }

    private fun initialize() {
        mPointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPointPaint!!.color = mPointColor
        mPointPaint!!.style = Paint.Style.FILL
        mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBorderPaint!!.color = mBorderColor
        mBorderPaint!!.style = Paint.Style.STROKE
        mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint!!.color = mBgColor
        mBgPaint!!.style = Paint.Style.FILL
        mPointPath = Path()
        mBorderPath = Path()
        mBgPath = Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        setupDiceNumber(mWidth, mHeight)
    }

    private fun setupDiceNumber(w: Int, h: Int) {
        mPointPath!!.reset()
        mBorderPath!!.reset()
        mBgPath!!.reset()
        val centerX = w / 2
        val centerY = h / 2
        val minSize = w.coerceAtMost(h)
        val borderStrokeWidth = minSize / 15f
        val borderRectRadius = minSize / 2 - borderStrokeWidth / 2
        mBorderPaint!!.strokeWidth = borderStrokeWidth
        mBorderPath!!.addRect(
            RectF(
                centerX - borderRectRadius, centerY - borderRectRadius,
                centerX + borderRectRadius, centerY + borderRectRadius
            ), Path.Direction.CW
        )
        mBgPath!!.set(mBorderPath!!)
        var pointRadius = (minSize / 10).toFloat()
        when (mNumber) {
            NUMBER_ONE -> {
                pointRadius = (minSize / 8).toFloat()
                mPointPath!!.addCircle(
                    centerX.toFloat(),
                    centerY.toFloat(),
                    pointRadius,
                    Path.Direction.CW
                )
            }
            NUMBER_TWO -> {
                val tArc1 = 45 / 180.0 * Math.PI
                val tArc2 = -135 / 180.0 * Math.PI
                val tX1 = (cos(tArc1) * borderRectRadius / 2).toFloat() + centerX
                val tY1 = (sin(tArc1) * borderRectRadius / 2).toFloat() + centerY
                val tX2 = (cos(tArc2) * borderRectRadius / 2).toFloat() + centerX
                val tY2 = (sin(tArc2) * borderRectRadius / 2).toFloat() + centerY
                mPointPath!!.addCircle(tX1, tY1, pointRadius, Path.Direction.CW)
                mPointPath!!.addCircle(tX2, tY2, pointRadius, Path.Direction.CW)
            }
            NUMBER_THREE -> {
                val thArc1 = 45 / 180.0 * Math.PI
                val thArc2 = -135 / 180.0 * Math.PI
                val thX1 = (cos(thArc1) * borderRectRadius * 2 / 3).toFloat() + centerX
                val thY1 = (sin(thArc1) * borderRectRadius * 2 / 3).toFloat() + centerY
                val thX2 = (cos(thArc2) * borderRectRadius * 2 / 3).toFloat() + centerX
                val thY2 = (sin(thArc2) * borderRectRadius * 2 / 3).toFloat() + centerY
                mPointPath!!.addCircle(thX1, thY1, pointRadius, Path.Direction.CW)
                mPointPath!!.addCircle(
                    centerX.toFloat(),
                    centerY.toFloat(),
                    pointRadius,
                    Path.Direction.CW
                )
                mPointPath!!.addCircle(thX2, thY2, pointRadius, Path.Direction.CW)
            }
            NUMBER_FOUR -> {
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
            }
            NUMBER_FIVE -> {
                mPointPath!!.addCircle(
                    centerX.toFloat(),
                    centerY.toFloat(),
                    pointRadius,
                    Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
            }
            NUMBER_SIX -> {
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY.toFloat(),
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY.toFloat(),
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY - borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX - borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
                mPointPath!!.addCircle(
                    centerX + borderRectRadius / 2, centerY + borderRectRadius / 2,
                    pointRadius, Path.Direction.CW
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw the bg
        canvas.drawPath(mBgPath!!, mBgPaint!!)

        //draw the border
        canvas.drawPath(mBorderPath!!, mBorderPaint!!)
        canvas.drawPath(mPointPath!!, mPointPaint!!)
    }
    /**
     * Get current number of this 'Dice'.
     */
    /**
     * Set number of this 'Dice', must be 1-6.
     */
    var number: Int
        get() = mNumber
        set(number) {
            mNumber = number
            if (mNumber < 1) {
                mNumber = 1
            } else if (number > 6) {
                mNumber = 6
            }
            setupDiceNumber(mWidth, mHeight)
            postInvalidate()
        }
    /**
     * Get the bg color.
     */
    /**
     * Set the bg color.
     */
    var bgColor: Int
        get() = mBgColor
        set(bgColor) {
            mBgColor = bgColor
            mBgPaint!!.color = mBgColor
            postInvalidate()
        }
    /**
     * Get the point color
     */
    /**
     * Set the point color.
     */
    var pointColor: Int
        get() = mPointColor
        set(pointColor) {
            mPointColor = pointColor
            mPointPaint!!.color = mPointColor
            postInvalidate()
        }

    /**
     * Set the border color.
     */
    var borderColor: Int
        get() = mBorderColor
        set(borderColor) {
            mBorderColor = borderColor
            mBorderPaint!!.color = mBorderColor
            postInvalidate()
        }

    companion object {
        val DEFAULT_COLOR = Color.parseColor("#C6342B")
        val DEFAULT_BG_COLOR = Color.parseColor("#FFFFFF")
        const val NUMBER_ONE = 1
        const val NUMBER_TWO = 2
        const val NUMBER_THREE = 3
        const val NUMBER_FOUR = 4
        const val NUMBER_FIVE = 5
        const val NUMBER_SIX = 6
    }
}