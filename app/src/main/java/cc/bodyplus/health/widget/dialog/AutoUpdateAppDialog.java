package cc.bodyplus.health.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cc.bodyplus.health.R;


/**
 * Created by shihoo.wang on 2018/5/18.
 * Email shihu.wang@bodyplus.cc
 *
 */
public class AutoUpdateAppDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private TextView contentTxt, dialog_title;
    private TextView confirmBtn;
    private OnUpdateDialogClickListener mListener;

    public AutoUpdateAppDialog(Context context) {
        super(context, R.style.update_app_dialog);
        setContentView(R.layout.layout_dialog_update_app);
        mContext = context;
        initView();
    }

    private void initView() {
        contentTxt = (TextView) findViewById(R.id.input_content);
        confirmBtn = (TextView) findViewById(R.id.input_confirm);
        confirmBtn.setOnClickListener(this);
        dialog_title = (TextView) findViewById(R.id.input_title);

    }

    @Override
    public void onClick(View v) {
        if (v == confirmBtn) {
            if (mListener != null)
                mListener.onConfirmBtnClick();
        }
    }

    public void setTitleTxt(String text) {
        dialog_title.setText(text);
    }


    public void setDialogContent(String text) {
        contentTxt.setText(text);
    }

    public void setDialogContent(int resId) {
        contentTxt.setText(mContext.getResources().getString(resId));
    }


    public void setDialogClickListener(OnUpdateDialogClickListener listener) {
        mListener = listener;
    }


    public interface OnUpdateDialogClickListener {

        void onConfirmBtnClick();
    }

}
