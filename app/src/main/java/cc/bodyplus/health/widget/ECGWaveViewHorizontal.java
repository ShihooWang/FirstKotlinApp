package cc.bodyplus.health.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by shihu.wang on 2017/9/12.
 * Email shihu.wang@bodyplus.cc
 */

public class ECGWaveViewHorizontal extends View {

	private float mWidth, mHeight;
	private Context mContext;

	private float bigHeight,litHeight;

	private int DataLength; // 一共有多少个点，目前硬件每秒钟有250个点的数据
	private float[] dataByte;
	private int XLineTotal; // 竖直线个数
	private int YLineTotal; // 水平线个数


	private int YmaxValue = 2; 	// 2大格 1mv
	private int mBigH = 0;

	public ECGWaveViewHorizontal(Context context) {
		super(context);
		mContext = context;
		initPaint();
	}

	public ECGWaveViewHorizontal(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initPaint();
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			mWidth = MeasureSpec.getSize(widthMeasureSpec);
			mHeight = MeasureSpec.getSize(heightMeasureSpec);
			litHeight = dipTopx(mContext,6); // 每一小格6dp
			bigHeight = 5*litHeight; // 每一大格
			XLineTotal = (int)(mWidth/litHeight);
			YLineTotal = (int)(mHeight/litHeight);
			DataLength = XLineTotal * 10 ; // 走速25mm/s
			dataByte = new float[DataLength];
			baseY = (YLineTotal/2)*litHeight + mBigH*bigHeight;
			secondsHeight = (int) dipTopx(mContext,20);
	}

	@Override
	public void onDraw(Canvas canvas) {
		drawRect(canvas);
		drawWave(canvas);
		drawSeconds(canvas);
		drawText(canvas);
	}
	private int secondsHeight;
	private Paint wavePaint;
	private Paint textPaint;
	private Paint bigRectPaint;
	private Paint litRectPaint;
	private int textMarginTop = 12;
	private String valueLift = "";
	private void initPaint(){

		wavePaint = new Paint();
		wavePaint.setStyle(Paint.Style.STROKE);
		wavePaint.setAntiAlias(true);//去锯齿
		wavePaint.setColor(Color.parseColor("#8c8c8c"));//颜色
		wavePaint.setStrokeWidth(2);

		textPaint = new Paint();
		textPaint.setColor(Color.rgb(165,165,165));
		textPaint.setTextSize(dip2px(mContext,15));
		textPaint.setStrokeWidth(dip2px(mContext,0.5f));
		textPaint.setAntiAlias(true);

		bigRectPaint = new Paint();
		bigRectPaint.setStyle(Paint.Style.STROKE);
		bigRectPaint.setColor(Color.parseColor("#e4e5e6"));
		bigRectPaint.setStrokeWidth(dip2px(mContext,0.5f));

		litRectPaint = new Paint();
		litRectPaint.setStyle(Paint.Style.STROKE);
		litRectPaint.setColor(Color.parseColor("#70e4e5e6"));
		litRectPaint.setStrokeWidth(dip2px(mContext,0.5f));
	}
	private void drawText(Canvas canvas){
		String valueRight = "25mm/s 10mm/mv";
		float sizeRight = textPaint.measureText(valueRight);
		Rect textBoundsRight = new Rect();
		textPaint.getTextBounds(valueRight, 0, valueRight.length(), textBoundsRight);
		int ysizeRight = textBoundsRight.height();
		canvas.drawText(valueLift, dip2px(mContext,16), dip2px(mContext,textMarginTop) + ysizeRight/2, textPaint);
		canvas.drawText(valueRight, mWidth - dip2px(mContext,16) - sizeRight, dip2px(mContext,textMarginTop) + ysizeRight/2, textPaint);
	}

	private void drawRect(Canvas canvas){
		//画水平线 从中间往上线画
		int avg = YLineTotal/2;
		canvas.drawLine(0,litHeight*avg,mWidth,litHeight*avg,bigRectPaint);
		canvas.drawLine(0,YLineTotal*litHeight-1,mWidth,YLineTotal*litHeight-1,bigRectPaint);
		for (int i=2;i<=avg+1;i++){
			if ((i-1)%5 == 0){
				// 画粗线
				canvas.drawLine(0,litHeight*(avg+1-i),mWidth,litHeight*(avg+1-i),bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(0, litHeight * (avg + 1 - i), mWidth, litHeight * (avg + 1 - i), litRectPaint);

			}
		}
		for (int i=avg+1;i<=YLineTotal;i++){
			if ((i-avg)%5 == 0){
				// 画粗线
				canvas.drawLine(0,litHeight*i,mWidth,litHeight*i,bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(0,litHeight*i,mWidth,litHeight*i,litRectPaint);
			}
		}

		//画竖直线
		int avgX = XLineTotal/2;
		canvas.drawLine(litHeight*(avgX),0,litHeight*(avgX),mHeight,bigRectPaint);
		for (int i=2;i<=avgX+1;i++){
			if ((i-1)%5 == 0){
				// 画粗线
				canvas.drawLine(litHeight*(avgX+1-i),0,litHeight*(avgX+1-i),mHeight,bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(litHeight*(avgX+1-i),0,litHeight*(avgX+1-i),mHeight,litRectPaint);
			}
		}

		for (int i=avgX+1;i<=XLineTotal;i++){
			if ((i-avgX)%5 == 0){
				// 画粗线
				canvas.drawLine(litHeight*i,0,litHeight*i,mHeight,bigRectPaint);
			}else {
				//画细线
				canvas.drawLine(litHeight*i,0,litHeight*i,mHeight*i,litRectPaint);
			}
		}
	}

	private boolean isNeedDrawSeconds = false;
	private float baseY;

	private void drawWave(Canvas canvas){
		try {
			Path path = new Path();
			float startX = 0.0f, startY = 0.0f;
			float length = litHeight/10; // 每一个点距离（xy是相等的） 走速固定25mm/s
			float unitY = YmaxValue*bigHeight;
			for (int i=0;i<DataLength;i++){
				float data = dataByte[i];
				startY = baseY - data*unitY;
				if (startY < 0){
					startY = 1.0f;
				}
				if (startY > mHeight ){
					startY = mHeight;
				}
				startX = length*i;
				if (i == 0){
					path.moveTo(startX, startY);
				}else {
					path.lineTo(startX, startY);
				}
			}
			canvas.drawPath(path, wavePaint);
		}catch (Exception e){
			e.printStackTrace();
		}

	}


	private void drawSeconds(Canvas canvas){
		if (isNeedDrawSeconds){
			//画水平线 从中间往上线画
			int avg = XLineTotal/2;
			canvas.drawLine(litHeight*avg,baseY+bigHeight*2,litHeight*avg,baseY+bigHeight*2+secondsHeight,wavePaint);

			for (int i=0;i<avg;i++){
				if (i>0 && i%(5*5) == 0){
					canvas.drawLine(litHeight*(avg-i),baseY+bigHeight*2,litHeight*(avg-i),baseY+bigHeight*2+secondsHeight,wavePaint);
				}
			}
			for (int i=avg;i<XLineTotal;i++){
				if (i>0 && (i-avg)%(5*5) == 0){
					canvas.drawLine(litHeight*i,baseY+bigHeight*2,litHeight*i,baseY+bigHeight*2+secondsHeight,wavePaint);
				}
			}
		}
	}

	/**
	 * 设置当前心率值，该方法需要
	 * @param value
	 */
	public void setCurrentHeartData(float[] value){
		if (dataByte == null){
			return;
		}
		try {
			System.arraycopy(dataByte,value.length,dataByte,0,DataLength-value.length);
			System.arraycopy(value,0,dataByte,DataLength-value.length,value.length);
			invalidate();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void reSet(){
		dataByte = new float[DataLength];
		invalidate();
	}

	public void setChangeData(float[] value){
		dataByte = value;
        invalidate();
	}

	public void setLeftText(String content){
		if (content != null){
			valueLift = content;
		}
	}

	public int getDataLength(){
		return DataLength;
	}


	private float dipTopx(Context context, float dpValue) {
		return dpValue * context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 将心电图向上或向下移动一大格
	 * @param bigH >0向下  0< 向上
	 */
	public void moveBaseLineDown(int bigH){
		mBigH = bigH;
	}

	public void needDrawSeconds(boolean isNeed){
		isNeedDrawSeconds = isNeed;
	}

	public void setTextMargin(int margin){
		textMarginTop = margin;
	}

	public static int dip2px(Context context, float dp) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
