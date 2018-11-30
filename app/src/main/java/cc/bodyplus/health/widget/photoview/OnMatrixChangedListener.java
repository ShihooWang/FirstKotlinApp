package cc.bodyplus.health.widget.photoview;

import android.graphics.RectF;

/**
 * Created by shihoo.wang on 2018/5/10.
 * Email shihu.wang@bodyplus.cc
 */

public interface OnMatrixChangedListener {

    /**
     * Callback for when the Matrix displaying the Drawable has changed. This could be because
     * the View's bounds have changed, or the user has zoomed.
     *
     * @param rect - Rectangle displaying the Drawable's new bounds.
     */
    void onMatrixChanged(RectF rect);
}