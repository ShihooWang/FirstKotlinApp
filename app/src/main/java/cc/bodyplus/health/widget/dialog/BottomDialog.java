package cc.bodyplus.health.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import cc.bodyplus.health.R;
import cc.bodyplus.health.utils.UIutils;
import cc.bodyplus.health.widget.wheelview.WheelView;
import cc.bodyplus.health.widget.wheelview.adapter.DialogWheelAdapter;

/**
 *
 */

public class BottomDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String mDialogTitle;
    ArrayList<String> mList = new ArrayList<>();
    private boolean mShowLabel; //是否显示标签
    private WheelView myWheelView;
    private TextView tvDialogTitle;
    private TextView tv_cancel;
    private TextView tv_save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bottom_dialog);

        initView();
    }


    private void initView() {
        myWheelView = (WheelView)findViewById(R.id.view_wheel);
        tvDialogTitle = (TextView)findViewById(R.id.tv_dialog_title);
        tv_cancel = (TextView)findViewById(R.id.tv_cancel);
        tv_save = (TextView)findViewById(R.id.tv_save);
        tv_cancel.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        tvDialogTitle.setText(mDialogTitle);
        myWheelView.setWheelAdapter(new DialogWheelAdapter(mContext));
        myWheelView.setWheelSize(3);
        myWheelView.setSkin(WheelView.Skin.Holo);
        myWheelView.setLabelState(WheelView.LabelState.CENTER);
        myWheelView.setWheelData(mList);

        myWheelView.setSelection(2);
        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        style.holoBorderWidth = UIutils.dip2px(1);
        style.holoBorderColor = mContext.getResources().getColor(R.color.color_divider);
        style.textColor = mContext.getResources().getColor(R.color.color_text_1);

        if (mShowLabel) {
            style.labelLocation = style.LABEL_CENTER;
            style.labelTextSize = UIutils.dip2px(22);

        }

        style.textSize = 22;

        style.backgroundColor = mContext.getResources().getColor(R.color.dialog_backgroud);

        myWheelView.setStyle(style);

    }

    public BottomDialog(Context context, ArrayList<String> list, boolean showLabel, String dialogTitle,int themeResId) {
        this(context, themeResId);
        this.mContext = context;
        this.mList = list;
        this.mShowLabel = showLabel;
        this.mDialogTitle = dialogTitle;

    }

    public BottomDialog(Context context, int themeResId) {
        super(context, R.style.BottomDialogStyle);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = display.getWidth();
        if(themeResId == 1){
            attributes.height = UIutils.dip2px(285);
        }else{
            attributes.height = UIutils.dip2px(285);
        }

        attributes.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        window.setAttributes(attributes);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                dismiss();
                break;
            case R.id.tv_save:
                String item = (String) myWheelView.getSelectionItem();
                mListener.onSave(item);
                dismiss();
                break;
        }
    }

    private OnDialogSaveClickListener mListener;

    public interface OnDialogSaveClickListener {
        public void onSave(String item);
    }

    public void setOnDialogSaveClickListener(OnDialogSaveClickListener listener) {
        this.mListener = listener;
    }

}
