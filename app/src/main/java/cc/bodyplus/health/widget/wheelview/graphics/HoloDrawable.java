/*
 * Copyright (C) 2016 venshine.cn@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.bodyplus.health.widget.wheelview.graphics;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import cc.bodyplus.health.widget.wheelview.WheelView;
import cc.bodyplus.health.widget.wheelview.common.WheelConstants;


/**
 * holo滚轮样式
 *
 * @author venshine
 */
public class HoloDrawable extends WheelDrawable {

    private Paint mHoloBgPaint, mHoloPaint;

    private int mWheelSize, mItemH;
    private Paint textPaint;


    public HoloDrawable(int width, int height, WheelView.WheelViewStyle style,
                        int wheelSize, int itemH) {
        super(width, height, style);

        mWheelSize = wheelSize;
        mItemH = itemH;
        init();
    }

    private void init() {
        mHoloBgPaint = new Paint();
        mHoloBgPaint.setColor(mStyle.backgroundColor != -1 ? mStyle.backgroundColor : WheelConstants
                .WHEEL_SKIN_HOLO_BG);

        mHoloPaint = new Paint();
        mHoloPaint.setStrokeWidth(mStyle.holoBorderWidth != -1 ? mStyle.holoBorderWidth : 3);
        mHoloPaint.setColor(mStyle.holoBorderColor != -1 ? mStyle.holoBorderColor : WheelConstants
                .WHEEL_SKIN_HOLO_BORDER_COLOR);
        textPaint = new Paint();
        textPaint.setColor(mStyle.textColor != -1 ? mStyle.textColor : WheelConstants
                .WHEEL_TEXT_COLOR);//设置颜色
        textPaint.setTextSize(mStyle.labelTextSize != -1 ? mStyle.labelTextSize : WheelConstants
                .WHEEL_TEXT_SIZE);
        textPaint.setAntiAlias(true);//设置抗锯齿
//        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * 需要优化
     */
    @Override
    public void draw(Canvas canvas) {
        // draw background
        canvas.drawRect(0, 0, mWidth, mHeight, mHoloBgPaint);

        // draw select border
        if (mItemH != 0) {

                canvas.drawLine(mStyle.padding, mItemH * (mWheelSize / 2), mWidth - mStyle.padding, mItemH
                        * (mWheelSize / 2), mHoloPaint);
                canvas.drawLine(mStyle.padding, mItemH * (mWheelSize / 2 + 1), mWidth - mStyle.padding, mItemH
                        * (mWheelSize / 2 + 1), mHoloPaint);
        }



        String word = "岁";

        if (mStyle.labelLocation== mStyle.LABEL_LEFT){
            //字母的高和宽
            int wordHeight = getTextHeight(word);
            float left = textPaint.measureText("岁岁") + mStyle.padding;// 给 list数据留出五个字符的空间 +37dp padding
            //计算每个字母在视图上的坐标位置
            float wordY = mItemH * (mWheelSize / 2) + wordHeight / 2 + mItemH / 2;
            canvas.drawText(word, left, wordY, textPaint);
        } else if(mStyle.labelLocation== mStyle.LABEL_CENTER){
            //字母的高和宽
            int wordHeight = getTextHeight(word);
            float left = textPaint.measureText("岁岁") + mWidth/2;//
            float wordY = mItemH * (mWheelSize / 2) + wordHeight / 2 + mItemH / 2;
            canvas.drawText(word, left, wordY, textPaint);

        }

    }

    private int getTextHeight(String text) {
        //获取文本的高度
        Rect bounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }
}
