package cc.bodyplus.health.widget.progress;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.text.DecimalFormat;

import cc.bodyplus.health.R;


/**
 */
public class RoundProgressBar extends View {

    private Paint mPaint;

    private int mRoundColor;
    private int mRoundPrOneColor;
    private int mRoundPrTwoColor;
    private int mRoundPrThreeColor;

    private int mTopTextColor;
    private int mBottomTextColor;

    private float mTopTextSize;
    private float mBottomTextSize;

    private float mRoundWidth;
    private float mRoundPrOneWidth;
    private float mRoundPrTwoWidth;
    private float mRoundPrThreeWidth;

    private float mSmallRoundWith;

    private double mProgressMax;
    private double mProgress;
    private Typeface mTypeface;
    private float mCurrentAngle;
    private float mRadius;
    private double mPercent;
    private boolean mIsShowSmallCicle = false;
    private Paint paintm;
    private Paint smallCirclePaint2;
    private float smallRoundTwoStrokeWidth;
    private float smallRoundTreeStrokeWidth;
    private int smallRoundThreeColor;
    private int smallRoundTwoColor;
    private Paint smallCirclePaint3;
    private Paint mPaint1;
    private int count = 60;
    private Bitmap smallCircleProgress;

    public RoundProgressBar(Context context) {
        this(context, null);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DIN.ttf");
        setCirclePaint();
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DIN.ttf");
        setCirclePaint();
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/DIN.ttf");
        setCirclePaint();
        mPaint = new Paint();
        smallCircleProgress = BitmapFactory.decodeResource(getResources(), R.drawable.progress_thumb);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        mRadius = mTypedArray.getDimension(R.styleable.RoundProgressBar_radius, 75);
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, 0XFFFFFF);
        mRoundPrOneColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressOneColor, 0XFFFFFF);
        mRoundPrTwoColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressTwoColor, 0XFFFFFF);
        mRoundPrThreeColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressThreeColor, 0X000000);

        mTopTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_topTextColor, 0XFFFFFF);
        mBottomTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_bottomTextColor, 0XFFFFFF);

        mTopTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_topTextSize, 15);
        mBottomTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_bottomTextSize, 5);

        mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 100);
        mRoundPrOneWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundProgressOneWidth, 5);
        mRoundPrTwoWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundProgressTwoWidth, 5);
        mRoundPrThreeWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundProgressThreeWidth, 5);
        mSmallRoundWith = mTypedArray.getDimension(R.styleable.RoundProgressBar_smallRoundOneWidth, 6);
        smallRoundTwoStrokeWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_smallRoundTwoStrokeWidth, 6);
        smallRoundTreeStrokeWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_smallRoundTreeStrokeWidth, 6);
        smallRoundTwoColor = mTypedArray.getColor(R.styleable.RoundProgressBar_smallRoundTwoColor, 0XFFFFFF);
        smallRoundThreeColor = mTypedArray.getColor(R.styleable.RoundProgressBar_smallRoundThreeColor, 0XFFFFFF);
        mProgressMax = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 0);

        mTypedArray.recycle();
    }

    /**
     * 滑动的小圆
     */
    private void setCirclePaint() {

        //进度上面的第二个小圆
        smallCirclePaint2 = new Paint();
        smallCirclePaint2.setAntiAlias(true);
        smallCirclePaint2.setDither(true);
        smallCirclePaint2.setStyle(Paint.Style.FILL_AND_STROKE);
        smallCirclePaint2.setStrokeWidth(smallRoundTwoStrokeWidth);
        smallCirclePaint2.setColor(smallRoundTwoColor);

        smallCirclePaint3 = new Paint();
        smallCirclePaint3.setAntiAlias(true);
        smallCirclePaint3.setDither(true);
        smallCirclePaint3.setStyle(Paint.Style.FILL_AND_STROKE);
        smallCirclePaint3.setStrokeWidth(smallRoundTreeStrokeWidth);
        smallCirclePaint3.setColor(smallRoundThreeColor);
        //画进度上面的小圆的实心画笔（主要是将小圆的实心颜色设置成白色）
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(getPaddingLeft(), getPaddingTop());

        mPaint.setColor(mRoundColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundWidth);
        mPaint.setAntiAlias(true);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //第一个圆弧
        mPaint.setColor(mRoundPrOneColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mRoundPrOneWidth);
        mPaint.setAntiAlias(true);
        RectF oval1 = new RectF(0, 0, mRadius * 2, mRadius * 2);
        canvas.drawArc(oval1, -90, 360 * 0.8f, false, mPaint);
        //第二个圆弧
        mPaint.setColor(mRoundPrTwoColor);
        mPaint.setStrokeWidth(mRoundPrTwoWidth);
        mPaint.setAntiAlias(true);
        RectF oval2 = new RectF(0, 0, mRadius * 2, mRadius * 2);
        canvas.drawArc(oval2, -90, 0, false, mPaint);
        //圆弧
        mPaint.setStrokeWidth(mRoundPrThreeWidth);
        mPaint.setColor(mRoundPrThreeColor);
        canvas.drawArc(new RectF(0, 0, mRadius * 2, mRadius * 2), -90, 360 * mCurrentAngle, false, mPaint);

        mPaint.setStrokeWidth(0);
        mPaint.setColor(mTopTextColor);
        mPaint.setTextSize(mTopTextSize);
        mPaint.setTypeface(mTypeface);
        DecimalFormat df = new DecimalFormat("###.0");
        if (mPercent > mProgress)
            mPercent = mProgress;
        float textWidth = mPaint.measureText(count + "");
        float textHeight = (mPaint.descent() + mPaint.ascent()) / 2;
        canvas.drawText(count + "", mRadius - textWidth / 2, mRadius - textHeight, mPaint);

        if (mIsShowSmallCicle) {
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mRoundPrThreeColor);
            float currentDegreeFlag = 360 * mCurrentAngle;
            float smallCircleX = 0, smallCircleY = 0;
            float hudu = (float) Math.abs(Math.PI * currentDegreeFlag / 180);//Math.abs：绝对值 ，Math.PI：表示π ， 弧度 = 度*π / 180
            smallCircleX = (float) Math.abs(Math.sin(hudu) * mRadius + mRadius);
            smallCircleY = (float) Math.abs(mRadius - Math.cos(hudu) * mRadius);
            float bitmapX = (float) (Math.sin(hudu) * mRadius + mRadius - smallCircleProgress.getWidth() / 2);
            float bitmapY = (float) (mRadius - Math.cos(hudu) * mRadius - smallCircleProgress.getWidth() / 2);
            canvas.drawBitmap(smallCircleProgress, bitmapX, bitmapY, null);
//            canvas.drawCircle(smallCircleX, smallCircleY, mSmallRoundWith+smallRoundTwoStrokeWidth+smallRoundTreeStrokeWidth, smallCirclePaint3);
//            canvas.drawCircle(smallCircleX, smallCircleY, mSmallRoundWith+smallRoundTwoStrokeWidth, smallCirclePaint2);
//            canvas.drawCircle(smallCircleX, smallCircleY, mSmallRoundWith, mPaint);

        }
        canvas.restore();

    }

    public void startCountDownTime(final OnProgressFinishListener listener, double prrogress, double progressMax) {
        count = 60;
        mIsShowSmallCicle = false;
        mProgress = prrogress;
        mProgressMax = progressMax;
        mPercent = (mProgress / 60.0);
        float cicle = (float) (mProgress / mProgressMax);
        if (cicle > 1)
            cicle = 1;
        ValueAnimator animator = ValueAnimator.ofFloat(0, cicle);
        //动画时长，让进度条在CountDown时间内正好从0-360走完，这里由于用的是CountDownTimer定时器，倒计时要想减到0则总时长需要多加1000毫秒，所以这里时间也跟着+1000ms
        animator.setDuration(1000 * count);
        animator.setInterpolator(new LinearInterpolator());//匀速
        animator.setRepeatCount(0);//表示不循环，-1表示无限循环
        //值从0-1.0F 的动画，动画时长为countdownTime，ValueAnimator没有跟任何的控件相关联，那也正好说明ValueAnimator只是对值做动画运算，而不是针对控件的，我们需要监听ValueAnimator的动画过程来自己对控件做操作
        //添加监听器,监听动画过程中值的实时变化(animation.getAnimatedValue()得到的值就是0-1.0)
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                /**
                 * 这里我们已经知道ValueAnimator只是对值做动画运算，而不是针对控件的，因为我们设置的区间值为0-1.0f
                 * 所以animation.getAnimatedValue()得到的值也是在[0.0-1.0]区间，而我们在画进度条弧度时，设置的当前角度为360*currentAngle，
                 * 因此，当我们的区间值变为1.0的时候弧度刚好转了360度
                 */
                mCurrentAngle = (float) animation.getAnimatedValue();
                invalidate();//实时刷新view，这样我们的进度条弧度就动起来了
            }
        });
        //开启动画
        animator.start();
        mIsShowSmallCicle = true;
//        还需要另一个监听，监听动画状态的监听器
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //倒计时结束的时候，需要通过自定义接口通知UI去处理其他业务逻辑
                if (listener != null) {
                    listener.progressFinished();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        countdownMethod();
    }

    //倒计时的方法
    private void countdownMethod() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                while (count != 0) {

                    count = count - 1;
                    postInvalidate();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize;
        int heightSize;
        int strokeWidth = Math.max((int) mRoundWidth, (int) mRoundPrThreeWidth);
        if (widthMode != MeasureSpec.EXACTLY) {
            int radiusInt = (int) mRadius;
            widthSize = getPaddingLeft() + radiusInt * 2 + strokeWidth + getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            int radiusInt = (int) mRadius;
            heightSize = getPaddingTop() + radiusInt * 2 + strokeWidth + getPaddingBottom();
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setmRadius(int mRadius) {
        this.mRadius = mRadius;
    }

    public interface OnProgressFinishListener {
        void progressFinished();
    }


//    private Handler handler = new Handler(){
//        public void handleMessage(android.os.Message msg) {
//
//            count = count-1;
//            invalidate();
//            //刷新页面
//            invalidate();
//            //循环动画
//            if (count!=0&&handler!=null) {
//                handler.sendEmptyMessageDelayed(0, 1000);
//            }
//
//        };
//    };

}
