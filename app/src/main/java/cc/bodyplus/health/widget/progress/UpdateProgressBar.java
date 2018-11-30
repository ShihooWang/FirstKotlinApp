package cc.bodyplus.health.widget.progress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Shihu.Wang on 2017/8/31.
 * Email shihu.wang@bodyplus.cc
 */

public class UpdateProgressBar extends View {
    private float maxCount;
    private float currentCount;
    private Paint mPaint;
    private int mWidth, mHeight;

    public UpdateProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public UpdateProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UpdateProgressBar(Context context) {
        super(context);
    }
    public void setMaxCount(float maxCount) {
        this.maxCount = maxCount;
    }
    public double getMaxCount() {
        return maxCount;
    }
    public void setCurrentCount(float currentCount) {
        this.currentCount = currentCount > maxCount ? maxCount : currentCount;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#e4e5e6"));
        int round = mHeight / 2;
        RectF rf = new RectF(0, 0, mWidth, mHeight);
        canvas.drawRoundRect(rf, round, round, mPaint);
        mPaint.setColor(Color.parseColor("#e4e5e6"));
        RectF rectBlackBg = new RectF(2, 2, mWidth - 2, mHeight - 2);
        canvas.drawRoundRect(rectBlackBg, round, round, mPaint);
        float section = currentCount / maxCount;
        RectF rectProgressBg = new RectF(3, 3, (mWidth - 3) * section, mHeight - 3);
        if (section != 0.0f) {
            mPaint.setColor(Color.parseColor("#02cd7b"));
        } else {
            mPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawRoundRect(rectProgressBg, round, round, mPaint);
    }

    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = widthSpecSize;
        } else {
            mWidth = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            mHeight = dipToPx(15);
        } else {
            mHeight = heightSpecSize;
        }
        setMeasuredDimension(mWidth, mHeight);
    }
}

