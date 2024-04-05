package com.pru.diceapp.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import com.pru.diceapp.R

class DiceLoadingView(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ViewGroup(context, attrs, defStyleAttr) {
    /**
     * The real width of this view.
     */
    private var mRealWidth = 0

    /**
     * The real height of this view.
     */
    private var mRealHeight = 0

    /**
     * The width of child, need to subtract [.getPaddingLeft] ()}, [.getPaddingRight].
     */
    private var mChildWidth = 0

    /**
     * The height of child, need to subtract [.getPaddingTop], [.getPaddingBottom].
     */
    private var mChildHeight = 0
    private var mFirstSideDiceNumber = DiceView.NUMBER_ONE
    private var mFirstSidePointColor = DiceView.DEFAULT_COLOR
    private var mFirstSideDiceBgColor = DiceView.DEFAULT_BG_COLOR
    private var mFirstSideDiceBorderColor = DiceView.DEFAULT_COLOR
    private var mSecondSideDiceNumber = DiceView.NUMBER_FIVE
    private var mSecondSidePointColor = DiceView.DEFAULT_COLOR
    private var mSecondSideDiceBgColor = DiceView.DEFAULT_BG_COLOR
    private var mSecondSideDiceBorderColor = DiceView.DEFAULT_COLOR
    private var mThirdSideDiceNumber = DiceView.NUMBER_SIX
    private var mThirdSidePointColor = DiceView.DEFAULT_COLOR
    private var mThirdSideDiceBgColor = DiceView.DEFAULT_BG_COLOR
    private var mThirdSideDiceBorderColor = DiceView.DEFAULT_COLOR
    private var mFourthSideDiceNumber = DiceView.NUMBER_TWO
    private var mFourthSidePointColor = DiceView.DEFAULT_COLOR
    private var mFourthSideDiceBgColor = DiceView.DEFAULT_BG_COLOR
    private var mFourthSideDiceBorderColor = DiceView.DEFAULT_COLOR
    private var mCamera: Camera? = null
    private var mMatrix: Matrix? = null
    private var mValueAnimator: ValueAnimator? = null
    private var mAnimatedValue = 0
    private var mAnimatorPlayTime: Long = 0
    private val mDiceViews = arrayOfNulls<DiceView>(FIXED_CHILD_COUNT)
    private var mInterpolator: TimeInterpolator = AccelerateDecelerateInterpolator()
    private var mDuration = DEFAULT_DURATION.toLong()

    init {
        parseAttr(attrs)
        initialize()
    }

    private fun parseAttr(attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DiceLoadingView)
        mDuration =
            typedArray.getInteger(R.styleable.DiceLoadingView_animDuration, DEFAULT_DURATION)
                .toLong()
        if (mDuration < 0) {
            mDuration = DEFAULT_DURATION.toLong()
        }
        when (typedArray.getInt(R.styleable.DiceLoadingView_animInterpolator, 0)) {
            0 -> mInterpolator = LinearInterpolator()
            1 -> mInterpolator = AccelerateInterpolator()
            2 -> mInterpolator = DecelerateInterpolator()
            3 -> mInterpolator = BounceInterpolator()
            4 -> mInterpolator = CycleInterpolator(0.5f)
            5 -> mInterpolator = AccelerateDecelerateInterpolator()
            6 -> mInterpolator = AnticipateOvershootInterpolator()
            7 -> mInterpolator = AnticipateInterpolator()
            8 -> mInterpolator = OvershootInterpolator()
            else -> Unit
        }
        mFirstSideDiceNumber = typedArray.getInteger(
            R.styleable.DiceLoadingView_firstSideDiceNumber,
            DiceView.NUMBER_ONE
        )
        mFirstSidePointColor = typedArray.getColor(
            R.styleable.DiceLoadingView_firstSideDicePointColor,
            DiceView.DEFAULT_COLOR
        )
        mFirstSideDiceBgColor = typedArray.getColor(
            R.styleable.DiceLoadingView_firstSideDiceBgColor,
            DiceView.DEFAULT_BG_COLOR
        )
        mFirstSideDiceBorderColor = typedArray.getColor(
            R.styleable.DiceLoadingView_firstSideDiceBorderColor,
            DiceView.DEFAULT_COLOR
        )
        mSecondSideDiceNumber = typedArray.getInteger(
            R.styleable.DiceLoadingView_secondSideDiceNumber,
            DiceView.NUMBER_FIVE
        )
        mSecondSidePointColor = typedArray.getColor(
            R.styleable.DiceLoadingView_secondSideDicePointColor,
            DiceView.DEFAULT_COLOR
        )
        mSecondSideDiceBgColor = typedArray.getColor(
            R.styleable.DiceLoadingView_secondSideDiceBgColor,
            DiceView.DEFAULT_BG_COLOR
        )
        mSecondSideDiceBorderColor = typedArray.getColor(
            R.styleable.DiceLoadingView_secondSideDiceBorderColor,
            DiceView.DEFAULT_COLOR
        )
        mThirdSideDiceNumber = typedArray.getInteger(
            R.styleable.DiceLoadingView_thirdSideDiceNumber,
            DiceView.NUMBER_SIX
        )
        mThirdSidePointColor = typedArray.getColor(
            R.styleable.DiceLoadingView_thirdSideDicePointColor,
            DiceView.DEFAULT_COLOR
        )
        mThirdSideDiceBgColor = typedArray.getColor(
            R.styleable.DiceLoadingView_thirdSideDiceBgColor,
            DiceView.DEFAULT_BG_COLOR
        )
        mThirdSideDiceBorderColor = typedArray.getColor(
            R.styleable.DiceLoadingView_thirdSideDiceBorderColor,
            DiceView.DEFAULT_COLOR
        )
        mFourthSideDiceNumber = typedArray.getInteger(
            R.styleable.DiceLoadingView_fourthSideDiceNumber,
            DiceView.NUMBER_TWO
        )
        mFourthSidePointColor = typedArray.getColor(
            R.styleable.DiceLoadingView_fourthSideDicePointColor,
            DiceView.DEFAULT_COLOR
        )
        mFourthSideDiceBgColor = typedArray.getColor(
            R.styleable.DiceLoadingView_fourthSideDiceBgColor,
            DiceView.DEFAULT_BG_COLOR
        )
        mFourthSideDiceBorderColor = typedArray.getColor(
            R.styleable.DiceLoadingView_fourthSideDiceBorderColor,
            DiceView.DEFAULT_COLOR
        )
        typedArray.recycle()
    }

    private fun initialize() {
        mCamera = Camera()
        mMatrix = Matrix()
        addChildViews()
    }

    private fun addChildViews() {
        List(7) {
            val diceView = DiceView(context)
            diceView.number = it
            mDiceViews[it] = diceView
        }
    }

    fun buildViews() {
        this.removeAllViews()
        mDiceViews.shuffle()
        for (diceView in mDiceViews) {
            (diceView?.parent as ViewGroup?)?.removeAllViews()
            addView(diceView, -1)
        }
    }

    /*fun setView(position: Int) {
        this.removeAllViews()
        val diceView = mDiceViews[position]
//        (diceView?.parent as ViewGroup?)?.removeAllViews()
        addView(diceView, -1)
    }*/

    /**
     * In order not to cut off the dice border, check and set padding here.
     */
    private fun checkPadding(width: Int, height: Int) {
        val minSize = Math.min(width, height)
        var paddingLeft = paddingLeft
        var paddingRight = paddingRight
        var paddingTop = paddingTop
        var paddingBottom = paddingBottom
        val defPadding = minSize / 8
        if (paddingLeft < defPadding) {
            paddingLeft = defPadding
        }
        if (paddingRight < defPadding) {
            paddingRight = defPadding
        }
        if (paddingTop < defPadding) {
            paddingTop = defPadding
        }
        if (paddingBottom < defPadding) {
            paddingBottom = defPadding
        }
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }

    override fun addView(child: View, index: Int, params: LayoutParams) {
        if (!(child is DiceView)) {
            return
        }
        super.addView(child, index, params)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        var w = widthSpecSize
        var h = heightSpecSize
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = DEFAULT_SIZE
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            w = DEFAULT_SIZE
            h = heightSpecSize
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            w = widthSpecSize
            h = DEFAULT_SIZE
        }
        mRealWidth = w
        mRealHeight = h
        setMeasuredDimension(w, h)
        checkPadding(w, h)
        mChildWidth = w - paddingLeft - paddingRight
        mChildHeight = h - paddingTop - paddingBottom
        measureChildren(
            MeasureSpec.makeMeasureSpec(mRealWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(mRealHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(b: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var childLeft = paddingLeft
        val paddingTop = paddingTop
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != GONE) {
                child.layout(
                    childLeft, paddingTop,
                    childLeft + child.measuredWidth, paddingTop + child.measuredHeight
                )
                childLeft += child.measuredWidth
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        setupAnimator()
    }

    fun setupAnimator() {
        release()
        if (childCount < FIXED_CHILD_COUNT) {
            return
        }
        mValueAnimator = ValueAnimator.ofInt(0, mChildWidth * 4)
        mValueAnimator!!.addUpdateListener(AnimatorUpdateListener { valueAnimator ->
            mAnimatedValue = valueAnimator.animatedValue as Int
            scrollTo(mAnimatedValue + mChildWidth, 0)
            invalidate()
        })
        mValueAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                scrollTo(mChildWidth, 0)
            }
        })
        mValueAnimator!!.setInterpolator(mInterpolator)
        mValueAnimator!!.setDuration(mDuration)
        mValueAnimator!!.setRepeatMode(ValueAnimator.RESTART)
        mValueAnimator!!.setRepeatCount(ValueAnimator.INFINITE)
        mValueAnimator!!.start()
    }

    override fun dispatchDraw(canvas: Canvas) {
        for (i in 0 until childCount) {
            drawChild(canvas, i, getChildAt(i))
        }
    }

    /**
     * Draw the child.
     *
     * @param canvas
     * @param index
     * @param child
     */
    private fun drawChild(canvas: Canvas, index: Int, child: View) {
        val childLeft = mChildWidth * index + paddingLeft
        val scrollX = scrollX + paddingLeft
        if (scrollX + mChildWidth < childLeft) {
            return
        }
        if (childLeft < scrollX - mChildWidth) {
            return
        }
        val centerX =
            if (scrollX > childLeft) (childLeft + mChildWidth).toFloat() else childLeft.toFloat()
        val centerY = (mChildHeight / 2).toFloat()
        val degree = (-90 * (scrollX - childLeft) / mChildWidth).toFloat()
        if (degree > 90 || degree < -90) {
            return
        }
        canvas.save()
        mCamera!!.save()
        mCamera!!.rotateY(degree)
        mCamera!!.getMatrix(mMatrix)
        mCamera!!.restore()
        mMatrix!!.preTranslate(-centerX, -centerY)
        mMatrix!!.postTranslate(centerX, centerY)
        canvas.concat(mMatrix)
        drawChild(canvas, child, drawingTime)
        canvas.restore()
    }

    /**
     * Pause the animation.
     */
    fun pause() {
        if (mValueAnimator != null && mValueAnimator!!.isRunning) {
            mAnimatorPlayTime = mValueAnimator!!.currentPlayTime
            mValueAnimator!!.cancel()
        }
    }

    /**
     * Resume the animation.
     */
    fun resume() {
        if (mValueAnimator != null && !mValueAnimator!!.isRunning) {
            mValueAnimator!!.currentPlayTime = mAnimatorPlayTime
            mValueAnimator!!.start()
        }
    }

    /**
     * Start the animation.
     */
    fun start() {
        mAnimatorPlayTime = 0
        if (mValueAnimator != null) {
            mValueAnimator!!.start()
        }
    }

    /**
     * Cancel the animation.
     */
    fun stop() {
        if (mValueAnimator != null) {
            mValueAnimator!!.cancel()
        }
    }

    /**
     * Release this view when you do not need it.
     */
    fun release() {
        stop()
        if (mValueAnimator != null) {
            mValueAnimator!!.removeAllUpdateListeners()
            mValueAnimator!!.removeAllListeners()
        }
    }
    /**
     * Get the dice number of the first dice side.
     */
    /**
     * Set the dice number{1-6} of the first dice side.
     */
    var firstSideDiceNumber: Int
        get() = mFirstSideDiceNumber
        set(firstSideDiceNumber) {
            mFirstSideDiceNumber = firstSideDiceNumber
            mDiceViews[1]!!.number = mFirstSideDiceNumber
            mDiceViews[5]!!.number = mFirstSideDiceNumber
        }
    /**
     * Get the point color of the first dice side.
     */
    /**
     * Set the point color of the first dice side.
     */
    var firstSidePointColor: Int
        get() = mFirstSidePointColor
        set(firstSidePointColor) {
            mFirstSidePointColor = firstSidePointColor
            mDiceViews[1]!!.pointColor = mFirstSidePointColor
            mDiceViews[5]!!.pointColor = mFirstSidePointColor
        }
    /**
     * Get the bg color of the first dice side.
     */
    /**
     * Set the bg color of the first dice side.
     */
    var firstSideDiceBgColor: Int
        get() = mFirstSideDiceBgColor
        set(firstSideDiceBgColor) {
            mFirstSideDiceBgColor = firstSideDiceBgColor
            mDiceViews[1]!!.bgColor = mFirstSideDiceBgColor
            mDiceViews[5]!!.bgColor = mFirstSideDiceBgColor
        }
    /**
     * Get the border color of the first dice side.
     */
    /**
     * Set the border color of the first dice side.
     */
    var firstSideDiceBorderColor: Int
        get() = mFirstSideDiceBorderColor
        set(firstSideDiceBorderColor) {
            mFirstSideDiceBorderColor = firstSideDiceBorderColor
            mDiceViews[1]!!.borderColor = mFirstSideDiceBorderColor
            mDiceViews[5]!!.borderColor = mFirstSideDiceBorderColor
        }
    /**
     * Set the dice number of the second dice side.
     */
    /**
     * Set the dice number{1-6} of the second dice side.
     */
    var secondSideDiceNumber: Int
        get() = mSecondSideDiceNumber
        set(secondSideDiceNumber) {
            mSecondSideDiceNumber = secondSideDiceNumber
            mDiceViews[2]!!.number = mSecondSideDiceNumber
            mDiceViews[6]!!.number = mSecondSideDiceNumber
        }
    /**
     * Get the point color of the second dice side.
     */
    /**
     * Set the point color of the second dice side.
     */
    var secondSidePointColor: Int
        get() = mSecondSidePointColor
        set(secondSidePointColor) {
            mSecondSidePointColor = secondSidePointColor
            mDiceViews[2]!!.pointColor = mSecondSidePointColor
            mDiceViews[6]!!.pointColor = mSecondSidePointColor
        }
    /**
     * Get the bg color of the second dice side.
     */
    /**
     * Set the bg color of the second dice side.
     */
    var secondSideDiceBgColor: Int
        get() = mSecondSideDiceBgColor
        set(secondSideDiceBgColor) {
            mSecondSideDiceBgColor = secondSideDiceBgColor
            mDiceViews[2]!!.bgColor = mSecondSideDiceBgColor
            mDiceViews[6]!!.bgColor = mSecondSideDiceBgColor
        }
    /**
     * Get the border color of the second dice side.
     */
    /**
     * Set the border color of the second dice side.
     */
    var secondSideDiceBorderColor: Int
        get() = mSecondSideDiceBorderColor
        set(secondSideDiceBorderColor) {
            mSecondSideDiceBorderColor = secondSideDiceBorderColor
            mDiceViews[2]!!.borderColor = mSecondSideDiceBorderColor
            mDiceViews[6]!!.borderColor = mSecondSideDiceBorderColor
        }
    /**
     * Get the dice number of the third dice side.
     */
    /**
     * Set the dice number{1-6} of the third dice side.
     */
    var thirdSideDiceNumber: Int
        get() = mThirdSideDiceNumber
        set(thirdSideDiceNumber) {
            mThirdSideDiceNumber = thirdSideDiceNumber
            mDiceViews[3]!!.number = mThirdSideDiceNumber
        }
    /**
     * Get the point color of the third dice side.
     */
    /**
     * Set the point color of the third dice side.
     */
    var thirdSidePointColor: Int
        get() = mThirdSidePointColor
        set(thirdSidePointColor) {
            mThirdSidePointColor = thirdSidePointColor
            mDiceViews[3]!!.pointColor = mThirdSidePointColor
        }
    /**
     * Get the bg color of the third dice side.
     */
    /**
     * Set the bg color of the third dice side.
     */
    var thirdSideDiceBgColor: Int
        get() = mThirdSideDiceBgColor
        set(thirdSideDiceBgColor) {
            mThirdSideDiceBgColor = thirdSideDiceBgColor
            mDiceViews[3]!!.bgColor = mThirdSideDiceBgColor
        }
    /**
     * Get the border color of the third dice side.
     */
    /**
     * Set the border color of the third dice side.
     */
    var thirdSideDiceBorderColor: Int
        get() = mThirdSideDiceBorderColor
        set(thirdSideDiceBorderColor) {
            mThirdSideDiceBorderColor = thirdSideDiceBorderColor
            mDiceViews[3]!!.borderColor = mThirdSideDiceBorderColor
        }
    /**
     * Get the dice number of the fourth dice side.
     */
    /**
     * Set the dice number{1-6} of the fourth dice side.
     */
    var fourthSideDiceNumber: Int
        get() = mFourthSideDiceNumber
        set(fourthSideDiceNumber) {
            mFourthSideDiceNumber = fourthSideDiceNumber
            mDiceViews[0]!!.number = mFourthSideDiceNumber
            mDiceViews[4]!!.number = mFourthSideDiceNumber
        }
    /**
     * Get the point color of the fourth dice side.
     */
    /**
     * Set the point color of the fourth dice side.
     */
    var fourthSidePointColor: Int
        get() = mFourthSidePointColor
        set(fourthSidePointColor) {
            mFourthSidePointColor = fourthSidePointColor
            mDiceViews[0]!!.pointColor = mFourthSidePointColor
            mDiceViews[4]!!.pointColor = mFourthSidePointColor
        }
    /**
     * Get the bg color of the fourth dice side.
     */
    /**
     * Set the bg color of the fourth dice side.
     */
    var fourthSideDiceBgColor: Int
        get() = mFourthSideDiceBgColor
        set(fourthSideDiceBgColor) {
            mFourthSideDiceBgColor = fourthSideDiceBgColor
            mDiceViews[0]!!.bgColor = mFourthSideDiceBgColor
            mDiceViews[4]!!.bgColor = mFourthSideDiceBgColor
        }
    /**
     * Get the border color of the fourth dice side.
     */
    /**
     * Set the border color of the fourth dice side.
     */
    var fourthSideDiceBorderColor: Int
        get() = mFourthSideDiceBorderColor
        set(fourthSideDiceBorderColor) {
            mFourthSideDiceBorderColor = fourthSideDiceBorderColor
            mDiceViews[0]!!.borderColor = mFourthSideDiceBorderColor
            mDiceViews[4]!!.borderColor = mFourthSideDiceBorderColor
        }
    /**
     * Get the animation's interpolator.
     */
    /**
     * Set the animation's interpolator.
     */
    var interpolator: TimeInterpolator
        get() = mInterpolator
        set(mInterpolator) {
            this.mInterpolator = mInterpolator
            setupAnimator()
        }
    /**
     * Get the animation 's duration.
     */
    /**
     * Set the animation 's duration.
     */
    var duration: Long
        get() = mDuration
        set(duration) {
            mDuration = duration
            setupAnimator()
        }

    companion object {
        private const val DEFAULT_DURATION = 2490
        private const val DEFAULT_SIZE = 200 //200 px
        private const val FIXED_CHILD_COUNT = 7 //The fixed child count.
    }
}