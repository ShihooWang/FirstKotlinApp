package cc.bodyplus.health.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.bodyplus.health.utils.SharedPrefHelperUtils;


/**
 * Created by shihu.wang on 2017/9/12.
 * Email shihu.wang@bodyplus.cc
 */

@SuppressLint("AppCompatCustomView")
public class EcgShareImageView extends ImageView {

	private float mWidth, mHeight;
	private Context mContext;

	private float bigHeight,litHeight;

	private int XLineTotal; // 竖直线个数
	private int YLineTotal; // 水平线个数

	private int mBigH = 0;

	private float marginTop ;
	private long stamp;
	private int hr;

	public EcgShareImageView(Context context) {
		super(context);
		mContext = context;
		initPaint();
	}

	public EcgShareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initPaint();
	}

	/**
	 * 水平方向为10秒的数据  则有10*5=50大格 250小格 + 左右的空隙6小格
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		int Width = wm.getDefaultDisplay().getWidth();
		mWidth = Width;
		mHeight = 8*height/10;
		litHeight = mWidth/256;
		bigHeight = 5*litHeight; // 每一大格
		XLineTotal = 250;
		YLineTotal = 6 * 10*5 +2*5;
		marginTop = dipTopx(mContext,80);
		secondsHeight = (int) dipTopx(mContext,20);
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawText(canvas);
		drawRect(canvas);
		drawWave(canvas);
	}
	private int secondsHeight;
	private Paint wavePaint;
	private Paint textPaint;
	private Paint bigRectPaint;
	private Paint litRectPaint;
	private int textMarginTop = 0;

	private void initPaint(){

		wavePaint = new Paint();
		wavePaint.setStyle(Paint.Style.STROKE);
		wavePaint.setAntiAlias(true);//去锯齿
		wavePaint.setColor(Color.parseColor("#4d4d4d"));//颜色

		textPaint = new Paint();
		textPaint.setColor(Color.rgb(46,64,87));
		textPaint.setTextSize(dip2px(mContext,13));
		textPaint.setStrokeWidth(dip2px(mContext,0.5f));
		textPaint.setAntiAlias(true);

		bigRectPaint = new Paint();
		bigRectPaint.setStyle(Paint.Style.FILL);
		bigRectPaint.setColor(Color.parseColor("#f5cedb"));
		bigRectPaint.setAntiAlias(true);

		litRectPaint = new Paint();
		litRectPaint.setStyle(Paint.Style.FILL);
		litRectPaint.setColor(Color.parseColor("#DDf5cedb"));
		litRectPaint.setAntiAlias(true);
		WindowManager wm = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		if (width < 1200) {
			wavePaint.setStrokeWidth(1.5f);
			bigRectPaint.setStrokeWidth(1.5f);
			litRectPaint.setStrokeWidth(1.5f);
		}else {
			wavePaint.setStrokeWidth(2.0f);
			bigRectPaint.setStrokeWidth(2.0f);
			litRectPaint.setStrokeWidth(2.0f);
		}
	}
	private void drawText(Canvas canvas){
		String valueLift1 = "姓 名："+ SharedPrefHelperUtils.getInstance().getNickname();
		String valueLift2 = "年 龄："+ SharedPrefHelperUtils.getInstance().getAge();
		String valueLift3 = "平均心率：";
		if (hr > 0) {
			valueLift3 = valueLift3 + hr;
		}else {
			valueLift3 = "平均心率：- -";
		}
		String valueRight1 = "用 时：60秒";
		String valueRight2 = "时 间：";
		String valueRight3 = "标导 I 25mm/s 10mm/mv";
		if (stamp > 0){
			Date currentTime = new Date(stamp);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			String dateString = formatter.format(currentTime);
			valueRight2 = valueRight2 + dateString;
		}
		canvas.drawText(valueLift1, dip2px(mContext,16), dip2px(mContext,textMarginTop) + dip2px(mContext,23), textPaint);
		canvas.drawText(valueLift2, dip2px(mContext,16), dip2px(mContext,textMarginTop) + dip2px(mContext,46), textPaint);
		canvas.drawText(valueLift3, dip2px(mContext,16), dip2px(mContext,textMarginTop) + dip2px(mContext,69), textPaint);

		canvas.drawText(valueRight1, mWidth/2, dip2px(mContext,textMarginTop) + dip2px(mContext,23), textPaint);
		canvas.drawText(valueRight2, mWidth/2, dip2px(mContext,textMarginTop) + dip2px(mContext,46), textPaint);
		canvas.drawText(valueRight3, mWidth/2, dip2px(mContext,textMarginTop) + dip2px(mContext,69), textPaint);

		String valueBottom = "Copyright © 2015 - 2018 , BodyPlus Technology Co.,Ltd , All Rights Reserved.";
		Paint paint = new Paint();
		paint.setColor(Color.rgb(46,64,87));
		paint.setTextSize(dip2px(mContext,13));
		paint.setStrokeWidth(dip2px(mContext,0.5f));
		paint.setAntiAlias(true);
		paint.setTextSize(dip2px(mContext,10));
		paint.setColor(Color.argb(88,46,64,87));
		canvas.drawText(valueBottom, dip2px(mContext,16), YLineTotal*litHeight + marginTop +dip2px(mContext,12), paint);
	}

	private void drawRect(Canvas canvas){
		//画水平线 从中间往上线画
		float marginLeft = 3*litHeight;
		PathEffect effects = new DashPathEffect(new float[]{1f, litHeight-1f, 1f, litHeight-1f}, 0);
		litRectPaint.setPathEffect(effects);

		float stopX = marginLeft + 50*bigHeight;
		for (int i=0;i<=YLineTotal;i++){
			if ((i)%5 == 0){
				// 画粗线
				canvas.drawLine(marginLeft,marginTop + litHeight*i,stopX,marginTop + litHeight*i,bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(marginLeft,marginTop + litHeight*i,stopX,marginTop + litHeight*i,litRectPaint);
			}
		}

		//画竖直线
		float stopY = marginTop + (6*10+2)*bigHeight;
		for (int i=0;i<=XLineTotal;i++){
			if (i%5 == 0){
				// 画粗线
				canvas.drawLine(marginLeft + litHeight*i,marginTop,marginLeft + litHeight*i,stopY,bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(marginLeft + litHeight*i,marginTop,marginLeft + litHeight*i,stopY,litRectPaint);
			}
		}

		// 画秒
		canvas.drawLine(marginLeft,marginTop + litHeight*YLineTotal,marginLeft + 50*bigHeight,marginTop + litHeight*YLineTotal,wavePaint);

		Paint secondSPaint= new Paint();
		secondSPaint.setAntiAlias(true);//去锯齿
		secondSPaint.setColor(Color.parseColor("#4d4d4d"));//颜色
		secondSPaint.setTextSize(dip2px(mContext,10));


		float secondLineStartY = marginTop+ (6*10+2)*bigHeight-bigHeight;
		float secondLineStopY = marginTop + (6*10+2)*bigHeight;
		float secondsTextY = marginTop+ (6*10+2)*bigHeight-bigHeight - 2*litHeight;
		for (int i=0;i<=10;i++){
			canvas.drawLine(marginLeft + 25*litHeight*i,secondLineStartY,
					marginLeft + 25*litHeight*i,secondLineStopY,wavePaint);

			String valueText = i+"s";
			Rect textBoundsRight = new Rect();
			secondSPaint.getTextBounds(valueText, 0, valueText.length(), textBoundsRight);
			int width = textBoundsRight.width();
			if (i == 0){
				canvas.drawText(valueText, marginLeft, secondsTextY, secondSPaint);
			}else if (i == 10){
				canvas.drawText(valueText, marginLeft + 25*litHeight*i - width, secondsTextY, secondSPaint);
			}else {
				canvas.drawText(valueText, marginLeft + 25*litHeight*i - (width/2), secondsTextY, secondSPaint);
			}
		}
	}

	private boolean isNeedDrawSeconds = false;

	private void drawWave(Canvas canvas){
		if (mTotaldata == null || mTotaldata.length==0){
			return;
		}
		float marginLeft = 3*litHeight;
		int YmaxValue = 2; 	// 2大格 1mv
		int DataLength = XLineTotal * 10 ;
		float[] dataByte = new float[DataLength];
		try {
			int startIndex = 0;
			for (int k=0;k<6;k++) {
				System.arraycopy(mTotaldata,startIndex,dataByte,0,DataLength);
				startIndex += DataLength;

				float baseLine = marginTop + k*10*bigHeight;
				float baseY = 6*bigHeight;
				Path path = new Path();
				float startX = 0.0f, startY = 0.0f;
				float length = litHeight / 10; // 每一个点距离（xy是相等的） 走速固定25mm/s
				float unitY = YmaxValue * bigHeight;
				for (int i = 0; i < DataLength; i++) {
					float data = dataByte[i];
					startY = baseY - (data * unitY);
					if (startY < 0) {
						startY = 1.0f;
					}
					if (startY > 10*bigHeight) {
						startY = 10*bigHeight;
					}
					startY += baseLine;
					startX = marginLeft + length * i;
					if (i == 0) {
						path.moveTo(startX, startY);
					} else {
						path.lineTo(startX, startY);
					}
				}
				canvas.drawPath(path, wavePaint);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	private float dipTopx(Context context, float dpValue) {
		return dpValue * context.getResources().getDisplayMetrics().density;
	}

	private static int dipTTpx(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale);
	}

	private static int dip2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}


	private float[] mTotaldata;
	public void setData(float[] value){
		mTotaldata = value;
		invalidate();
	}

	public void setStamp(long stamp) {
		this.stamp = stamp;
	}

	public void setHr(int hr) {
		this.hr = hr;
	}
}
