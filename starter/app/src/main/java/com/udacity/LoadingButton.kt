package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var backgroundColorButton by Delegates.notNull<Int>()
    private var backgroundColorProgress by Delegates.notNull<Int>()

    private var textButton: String
    private lateinit var textDownloadButton: String
    private lateinit var textLoadingButton: String

    private val textBounds = Rect()

    private var buttonProgress = 0f

    private lateinit var paintRect: Paint
    private lateinit var textPaint: TextPaint
    private lateinit var paintProgressCircle: Paint
    private lateinit var paintLoad: Paint

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> { buttonState = ButtonState.Loading }
            ButtonState.Loading -> { showAnimation() }
            ButtonState.Completed -> { cancelAnimation() }
        }
    }

    init {
        isClickable = true
        initAttributes(attrs)
        initPaint()
        textButton = textDownloadButton
    }

    private fun initAttributes(attrs: AttributeSet?) {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            backgroundColorButton = getColor(R.styleable.LoadingButton_downloadColorButton, Color.GRAY)
            backgroundColorProgress = getColor(R.styleable.LoadingButton_loadingColorButton, Color.DKGRAY)
            textDownloadButton = getString(R.styleable.LoadingButton_downloadTextButton).toString()
            textLoadingButton = getString(R.styleable.LoadingButton_loadingTextButton).toString()
        }
    }

    private fun initPaint() {
        paintRect = Paint((Paint.ANTI_ALIAS_FLAG)).apply {
            color = backgroundColorButton
            style = Paint.Style.FILL
        }

        textPaint = TextPaint((Paint.ANTI_ALIAS_FLAG)).apply {
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                20f,
                resources.displayMetrics
            )
            typeface = Typeface.DEFAULT_BOLD
        }

        paintProgressCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        paintLoad = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = backgroundColorProgress
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.Clicked
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintRect)

        drawBackgroundProgress(canvas)
        drawText(canvas, textButton)
        drawCircleProgress(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawBackgroundProgress(canvas: Canvas) {
        canvas.drawRect(0f, 0f,
               widthSize.toFloat() * buttonProgress,
               heightSize.toFloat(),
            paintLoad
        )
    }

    private fun drawText(canvas: Canvas, text: String) {
        val xPos = widthSize / 2f
        val yPos = heightSize / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)
    }

    private fun drawCircleProgress(canvas: Canvas) {
        textPaint.getTextBounds(textLoadingButton, 0, textLoadingButton.length, textBounds)
        val start = (widthSize / 2 + textBounds.exactCenterX()) + textBounds.height() / 2

        canvas.drawArc(
            start,
            (heightSize / 2 - textBounds.height() / 2).toFloat(),
            start + textBounds.height(),
            (heightSize / 2 + textBounds.height() / 2).toFloat(),
            0F,
            buttonProgress * 360,
            true,
            paintProgressCircle
        )
    }

    private fun showAnimation() {
        isEnabled = false
        textButton = textLoadingButton
        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 2000
        valueAnimator.addUpdateListener { animation ->
            buttonProgress = animation.animatedValue as Float
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                buttonProgress = 0f
                if (buttonState == ButtonState.Loading) {
                    buttonState = ButtonState.Completed
                }
            }
        })
        valueAnimator.start()
    }

    private fun cancelAnimation() {
        valueAnimator.cancel()
        isEnabled = true
        textButton = textDownloadButton
        invalidate()
    }

}