package cc.bodyplus.health.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cc.bodyplus.health.R;


/**
 * Created by Shihu.Wang on 2017/4/25.
 * Email shihu.wang@bodyplus.cc
 */

public class CancleDialog extends BaseDialog implements View.OnClickListener {

    private TextView titleTv;
    private TextView cancelTv;
    private TextView confirmTv;
    private OnClickListener mListener;

    public CancleDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_dialog_cancle);
        initView();
    }

    public void setTitleTxt(String str){
        titleTv.setText(str);
    }
    public void setConfirmTxt(String str){
        confirmTv.setText(str);
    }
    public void setCancelTxt(String str){
        cancelTv.setText(str);
    }

    private void initView(){
        cancelTv = (TextView) findViewById(R.id.dialog_tv_cancel);
        confirmTv = (TextView) findViewById(R.id.dialog_tv_confirm);
        titleTv = (TextView) findViewById(R.id.input_title);
        cancelTv.setOnClickListener(this);
        confirmTv.setOnClickListener(this);
    }



    public void setDialogClickListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (v == cancelTv){
            if (mListener != null)
                mListener.onCancelBtnClick();
        }else if (v == confirmTv){
            if (mListener != null)
                mListener.onConfirmBtnClick();
        }
    }

    public interface OnClickListener {
        void onCancelBtnClick();

        void onConfirmBtnClick();
    }
}
