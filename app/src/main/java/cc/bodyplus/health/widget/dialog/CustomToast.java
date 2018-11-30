package cc.bodyplus.health.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cc.bodyplus.health.R;

/**
 * Created by rui.gao on 2018-05-24.
 */

public class CustomToast {
    public static Toast makeText(@NonNull Context context,@NonNull CharSequence text, int duration) {
        Toast result = new Toast(context);
        LayoutInflater inflate = (LayoutInflater)
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.view_toast_notification, null);
        TextView tv = (TextView)v.findViewById(R.id.show_text);
        tv.setText(text);
        result.setView(v);
        return result;
    }
}
