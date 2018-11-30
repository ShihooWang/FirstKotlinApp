package cc.bodyplus.health.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;

import cc.bodyplus.health.R;


/**
 * Created by ameng on 15/9/8.
 */
public class BaseDialog extends Dialog {
    private Handler mHandler;

    public BaseDialog(Context context) {
        super(context, R.style.customDialog);
        mHandler = new Handler();

    }

    public BaseDialog(Context context, int theme) {
        super(context, theme);
        getWindow().setBackgroundDrawable(new BitmapDrawable((Resources) null));
        mHandler = new Handler();
    }

    public void post(final Runnable r) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    r.run();
                }
            });
        }
    }

    public void postDelayed(final Runnable r, long delayMillis) {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    r.run();
                }
            }, delayMillis);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mHandler = null;
    }

}
