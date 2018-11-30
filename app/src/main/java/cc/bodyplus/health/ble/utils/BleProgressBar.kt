package cc.bodyplus.health.ble.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Created by shihoo.wang on 2018/4/24.
 * Email shihu.wang@bodyplus.cc
 */
class BleProgressBar : View {

    private var maxCount: Int = 0
    private var currentCount: Int = 0
    private var mPaint: Paint? = null
    private var mWidth: Int = 0
    private var mHeight:Int = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setMaxCount(maxCount: Int) {
        this.maxCount = maxCount
    }

    fun getMaxCount(): Double {
        return maxCount.toDouble()
    }

    fun setCurrentCount(currentCount: Int) {
        this.currentCount = if (currentCount > maxCount) maxCount else currentCount
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint = Paint()
        mPaint?.isAntiAlias = true
        mPaint?.color = Color.parseColor("#e4e5e6")
        val round = mHeight / 2
        val rf = RectF(0f, 0f, mWidth.toFloat(), mHeight.toFloat())
        canvas.drawRoundRect(rf, round.toFloat(), round.toFloat(), mPaint)
        mPaint?.color = Color.parseColor("#e4e5e6")
        val rectBlackBg = RectF(2f, 2f, (mWidth - 2).toFloat(), (mHeight - 2).toFloat())
        canvas.drawRoundRect(rectBlackBg, round.toFloat(), round.toFloat(), mPaint)
        val section = currentCount*1f / maxCount
        val rectProgressBg = RectF(3f, 3f, (mWidth - 3) * section, (mHeight - 3).toFloat())
        if (section != 0.0f) {
            mPaint?.color = Color.parseColor("#31D4D1")
        } else {
            mPaint?.color = Color.TRANSPARENT
        }
        canvas.drawRoundRect(rectProgressBg, round.toFloat(), round.toFloat(), mPaint)
    }

    private fun dipToPx(dip: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dip * scale + 0.5f * if (dip >= 0) 1 else -1).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getSize(heightMeasureSpec)
        if (widthSpecMode == View.MeasureSpec.EXACTLY || widthSpecMode == View.MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize
        } else {
            mWidth = 0
        }
        if (heightSpecMode == View.MeasureSpec.AT_MOST || heightSpecMode == View.MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15)
        } else {
            mHeight = heightSpecSize
        }
        setMeasuredDimension(mWidth, mHeight)
    }
}