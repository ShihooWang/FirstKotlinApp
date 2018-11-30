package cc.bodyplus.health.widget.dialog;

import android.content.Context;
import android.widget.TextView;

import cc.bodyplus.health.R;


/**
 * Created by Shihu.Wang on 2017/4/25.
 * Email shihu.wang@bodyplus.cc
 */

public class InitMonitorDialog extends BaseDialog {

    private TextView titleTv;
    private TextView contentTv;

    public InitMonitorDialog(Context context) {
        super(context);
        setContentView(R.layout.layout_dialog_init_monitor);
        initView();
    }

    public void setTitleTxt(String str){
        titleTv.setText(str);
    }
    public void setConfirmTxt(String str){
        contentTv.setText(str);
    }

    private void initView(){
        contentTv = (TextView) findViewById(R.id.input_content);
        titleTv = (TextView) findViewById(R.id.input_title);
    }

    public TextView getContentTv(){
        return contentTv;
    }
}
