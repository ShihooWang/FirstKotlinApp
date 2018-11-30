package cc.bodyplus.health.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import cc.bodyplus.health.R;


public class ProgressDialog extends Dialog {
    private Context mContext;
    private TextView tvMessage;
    private ImageView loadImage;
//    private boolean cancelable = false;
    private boolean cancelable = true;
    private RotateAnimation animation;

    public ProgressDialog(Context context) {
        super(context, R.style.globalDialog);
        mContext = context;
        initView();
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }

    public void initView() {
        setCanceledOnTouchOutside(false);
        setCancelable(cancelable);
        getWindow().setBackgroundDrawable(new BitmapDrawable((Resources) null));
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_dialog_progress, null);
        setContentView(view);
        tvMessage = (TextView) view.findViewById(R.id.tv_message);
        loadImage = (ImageView) view.findViewById(R.id.loading_image);
       
    }

    @Override
    public void onBackPressed() {
        if(cancelable){
            super.onBackPressed();
        }
    }

    public ProgressDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

    public ProgressDialog setMessage(int id) {
        setMessage(mContext.getResources().getString(id));
        return this;
    }

    @Override
    public void show() {
        super.show();
        animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(1000);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin);
        animation.setRepeatCount(Animation.INFINITE);
        loadImage.startAnimation(animation);
    }

    @Override
    public void dismiss() {
        if (!isValidContext()) {
            return;
        }
        super.dismiss();
    }

    private boolean isValidContext() {
        Activity a = (Activity) mContext;
        if (a.isFinishing()) {
            return false;
        } else {
            return true;
        }

    }
}
