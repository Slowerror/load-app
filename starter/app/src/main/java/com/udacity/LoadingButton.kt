package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
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

    private var buttonProgress = 0f
    private lateinit var textDownloadButton: String
    private lateinit var textLoadingButton: String
    private val textBounds = Rect()
    private var valueAnimator = ValueAnimator.ofFloat(0f, 1f)

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Loading -> {
                showAnimation()
            }
            ButtonState.Completed -> {
                stopAnimation()
            }
            ButtonState.Clicked -> {
                isEnabled = true
                return@observable
            }
        }
    }

    private val rect = RectF(0f, 0f, 0f, 0f)

    private lateinit var paintBackground: Paint
    private lateinit var textPaint: TextPaint
    private lateinit var paintProgressCircle: Paint

    init {

        if (attrs != null) {
            initAttributes(attrs)
        }

        initPaint()
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
        paintBackground = Paint((Paint.ANTI_ALIAS_FLAG)).apply {
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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackgroundProgress(canvas)

        when (buttonState) {
            ButtonState.Loading -> {
                drawBackgroundProgress(canvas, buttonProgress)

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

                drawText(canvas, textLoadingButton)
            }
            else -> {
                drawText(canvas, textDownloadButton)
            }
        }
        /*if (buttonState == ButtonState.Loading) {


        } else {

        }*/

    }

    /*override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h
        rect.set(0f, 0f, widthSize.toFloat(), heightSize.toFloat())

    }*/

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    private fun drawBackgroundProgress(canvas: Canvas, progress: Float? = null) {
        if (progress != null) {
            paintBackground.alpha = 200
        }
        canvas.drawRect(0f, 0f,
            widthSize.toFloat() * (progress ?: 1f),
            heightSize.toFloat(),
            paintBackground
        )

    }

    private fun drawText(canvas: Canvas, text: String) {
        val xPos = widthSize / 2f
        val yPos = heightSize / 2f - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)
    }

    private fun showAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                buttonProgress = animatedValue as Float
                invalidate()
            }
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            duration = 3000
            start()
        }
        isEnabled = false

    }

    private fun stopAnimation() {
        valueAnimator.cancel()
        invalidate()
        isEnabled = true
        buttonState = ButtonState.Clicked
    }

    fun setupButtonState(state: ButtonState) {
        buttonState = state
    }
}