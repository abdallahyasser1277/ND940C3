package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnRepeat
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var progress = 0
    private var backColor = 0
    private var textColor = 0

    private val loadingRect = Rect()

    private val textPaint = Paint().apply {
        textAlign = Paint.Align.CENTER
        textSize = 45F
        color = resources.getColor(R.color.white, null)
    }

    private val rectPaint = Paint().apply {
        color = resources.getColor(R.color.colorPrimary, null)
    }

    private val loadingRectPaint = Paint().apply {
        color = resources.getColor(R.color.colorPrimaryDark, null)
    }

    private val arcPaint = Paint().apply {
        color = resources.getColor(R.color.colorAccent, null)
    }

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        if (new == ButtonState.Loading) {
            valueAnimator = ValueAnimator.ofInt(0,60).setDuration(3000).apply {
                addUpdateListener {
                    progress = it.animatedValue as Int
                    invalidate()
                }

                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.RESTART

                doOnRepeat {
                    buttonState = ButtonState.Completed
                    isClickable = true
                    invalidate()
                }

                start()
            }
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0) {
            backColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
        }

        isClickable = true
        buttonState = ButtonState.Completed
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (buttonState) {
            ButtonState.Loading -> {
                onDrawLoadingButton(canvas)
            }

            ButtonState.Completed -> {
                onDrawClickedButton(canvas)
            }

            ButtonState.Clicked -> {
                onDrawClickedButton(canvas)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()

        isClickable = false
        buttonState = ButtonState.Loading
        invalidate()

        return true
    }

    fun onDrawClickedButton(canvas: Canvas){
        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            rectPaint
        )
        canvas.drawText(
            "Download",
            (width / 2).toFloat(),
            ((height / 2)+10f),
            textPaint
        )
    }
    fun onDrawLoadingButton(canvas: Canvas){

        canvas.drawRect(
            0f,
            0f,
            width.toFloat(),
            height.toFloat(),
            rectPaint
        )
        loadingRect.set(0, 0, width * progress /60, height)
        canvas.drawRect(loadingRect, loadingRectPaint)

        canvas.drawArc(
            (widthSize - 92f),
            (heightSize / 2) - 30f,
            (widthSize - 30f),
            (heightSize / 2) + 30f,
            0f,
            progress*6f,
            true,
            arcPaint
        )

        canvas.drawText(
            "We wre downloading",
            (width / 2)-20f,
            ((height / 2)+10f),
            textPaint
        )
    }

    fun changeButtonState(newButtonState: ButtonState) {
        buttonState = newButtonState
    }

}