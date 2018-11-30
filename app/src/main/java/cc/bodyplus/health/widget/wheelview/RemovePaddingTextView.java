package cc.bodyplus.health.widget.wheelview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by kate.chen on 2017/11/16.
 * chunyan.chen@bodyplus.cc
 */

public class RemovePaddingTextView extends AppCompatTextView {
    public RemovePaddingTextView(Context context) {
        super(context);
    }
    public RemovePaddingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemovePaddingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint.FontMetricsInt fontMetricsInt;
    @Override
    protected void onDraw(Canvas canvas) {
       //设置是否remove间距，true为remove
            if (fontMetricsInt == null){
                fontMetricsInt = new Paint.FontMetricsInt();
                getPaint().getFontMetricsInt(fontMetricsInt);
            }
            canvas.translate(0, fontMetricsInt.top - fontMetricsInt.ascent);
        super.onDraw(canvas);
    }
}
