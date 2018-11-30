package cc.bodyplus.health.widget.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import cc.bodyplus.health.R;


/**
 * Created by Bodyplus_1 on 2016/4/29.
 */
public class VersionUpdateDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private TextView contentTxt, dialog_title;
    private TextView cancelBtn;
    private TextView confirmBtn;
    private View btnView;
    private OnUpdateDialogClickListener mListener;

    public VersionUpdateDialog(Context context) {
        super(context, R.style.update_app_dialog);
        setContentView(R.layout.view_dialog_app_update);
        mContext = context;
        initView();
    }

    private void initView() {
        contentTxt = (TextView) findViewById(R.id.dialog_content_txt);
        cancelBtn = (TextView) findViewById(R.id.dialog_left_btn);
        cancelBtn.setOnClickListener(this);
        confirmBtn = (TextView) findViewById(R.id.dialog_right_btn);
        confirmBtn.setOnClickListener(this);
        btnView = findViewById(R.id.btn_view);
        dialog_title = (TextView) findViewById(R.id.dialog_title);

    }

    @Override
    public void onClick(View v) {
        if (v == cancelBtn) {
            if (mListener != null)
                mListener.onCancelBtnClick();
        } else if (v == confirmBtn) {
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

    public void downloadingView() {
        btnView.setVisibility(View.GONE);
    }

    public void downloadedView() {
        btnView.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.GONE);
        cancelBtn.setText("确认");
    }

    public void setDialogClickListener(OnUpdateDialogClickListener listener) {
        mListener = listener;
    }


    public interface OnUpdateDialogClickListener {
        void onCancelBtnClick();

        void onConfirmBtnClick();
    }

}
