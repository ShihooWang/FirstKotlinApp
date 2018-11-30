package cc.bodyplus.health.widget.photoview;

/**
 * Created by shihoo.wang on 2018/5/10.
 * Email shihu.wang@bodyplus.cc
 */
interface OnGestureListener {

    void onDrag(float dx, float dy);

    void onFling(float startX, float startY, float velocityX,
                 float velocityY);

    void onScale(float scaleFactor, float focusX, float focusY);

}