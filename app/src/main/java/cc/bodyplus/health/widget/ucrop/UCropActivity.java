package cc.bodyplus.health.widget.ucrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cc.bodyplus.health.R;
import cc.bodyplus.health.widget.ucrop.util.BitmapLoadUtils;
import cc.bodyplus.health.widget.ucrop.view.CropImageView;
import cc.bodyplus.health.widget.ucrop.view.GestureCropImageView;
import cc.bodyplus.health.widget.ucrop.view.OverlayView;
import cc.bodyplus.health.widget.ucrop.view.TransformImageView;
import cc.bodyplus.health.widget.ucrop.view.UCropView;
import cc.bodyplus.health.widget.ucrop.view.widget.AspectRatioTextView;
import cc.bodyplus.health.widget.ucrop.view.widget.HorizontalProgressWheelView;


/**
 * Created by Oleksii Shliama (https://github.com/shliama).
 */
public class UCropActivity extends Activity implements View.OnClickListener {

    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.JPEG;

    private static final String TAG = "UCropActivity";

    private static final int SCALE_WIDGET_SENSITIVITY_COEFFICIENT = 15000;
    private static final int ROTATE_WIDGET_SENSITIVITY_COEFFICIENT = 42;

    private GestureCropImageView mGestureCropImageView;
    private OverlayView mOverlayView;
    private ViewGroup mWrapperStateAspectRatio, mWrapperStateRotate, mWrapperStateScale;
    private ViewGroup mLayoutAspectRatio, mLayoutRotate, mLayoutScale;
    private List<ViewGroup> mCropAspectRatioViews = new ArrayList<>();
    private TextView mTextViewRotateAngle, mTextViewScalePercent;
    private TextView mCancelTxt, mCompleteTxt;

    private Uri mOutputUri;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;
    private boolean mGesturesAlwaysEnabled = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ucrop_activity_photobox);

//        ScreenUtils.setAllowFullScreen(findViewById(R.id.title_view), this, true);

        setupViews();
        setImageData();
        setInitialState();
    }


    @Override
    public void onClick(View v) {
        if (v == mCancelTxt) {
            onBackPressed();
        } else if (v == mCompleteTxt) {
            cropAndSaveImage();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGestureCropImageView != null) {
            mGestureCropImageView.cancelAllAnimations();
        }
    }

    /**
     * This method extracts all value from the incoming intent and setups views properly.
     */
    private void setImageData() {
        final Intent intent = getIntent();

        Uri inputUri = intent.getParcelableExtra(UCrop.EXTRA_INPUT_URI);
        mOutputUri = intent.getParcelableExtra(UCrop.EXTRA_OUTPUT_URI);
        processOptions(intent);

        if (inputUri != null && mOutputUri != null) {
            try {
                mGestureCropImageView.setImageUri(inputUri);
            } catch (Exception e) {
                setResultException(e);
                finish();
            }
        } else {
            setResultException(new NullPointerException(getString(R.string.ucrop_error_input_data_is_absent)));
            finish();
        }

        if (intent.getBooleanExtra(UCrop.EXTRA_ASPECT_RATIO_SET, false)) {
            mWrapperStateAspectRatio.setVisibility(View.GONE);

            int aspectRatioX = intent.getIntExtra(UCrop.EXTRA_ASPECT_RATIO_X, 0);
            int aspectRatioY = intent.getIntExtra(UCrop.EXTRA_ASPECT_RATIO_Y, 0);

            if (aspectRatioX > 0 && aspectRatioY > 0) {
                mGestureCropImageView.setTargetAspectRatio(aspectRatioX / (float) aspectRatioY);
            } else {
                mGestureCropImageView.setTargetAspectRatio(CropImageView.SOURCE_IMAGE_ASPECT_RATIO);
            }
        }

        if (intent.getBooleanExtra(UCrop.EXTRA_MAX_SIZE_SET, false)) {
            int maxSizeX = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_X, 0);
            int maxSizeY = intent.getIntExtra(UCrop.EXTRA_MAX_SIZE_Y, 0);

            if (maxSizeX > 0 && maxSizeY > 0) {
                mGestureCropImageView.setMaxResultImageSizeX(maxSizeX);
                mGestureCropImageView.setMaxResultImageSizeY(maxSizeY);
            } else {
                Log.w(TAG, "EXTRA_MAX_SIZE_X and EXTRA_MAX_SIZE_Y must be greater than 0");
            }
        }
    }

    /**
     * This method extracts {@link UCrop.Options #optionsBundle} from incoming intent
     * and setups Activity, {@link OverlayView} and {@link CropImageView} properly.
     */
    @SuppressWarnings("deprecation")
    private void processOptions(Intent intent) {
        Bundle optionsBundle = intent.getBundleExtra(UCrop.EXTRA_OPTIONS);
        if (optionsBundle != null) {
            // Bitmap compression options
            String compressionFormatName = optionsBundle.getString(UCrop.Options.EXTRA_COMPRESSION_FORMAT_NAME);
            Bitmap.CompressFormat compressFormat = null;
            if (!TextUtils.isEmpty(compressionFormatName)) {
                compressFormat = Bitmap.CompressFormat.valueOf(compressionFormatName);
            }
            mCompressFormat = (compressFormat == null) ? DEFAULT_COMPRESS_FORMAT : compressFormat;

            mCompressQuality = optionsBundle.getInt(UCrop.Options.EXTRA_COMPRESSION_QUALITY, UCropActivity.DEFAULT_COMPRESS_QUALITY);

            // Gestures options
            mGesturesAlwaysEnabled = optionsBundle.getBoolean(UCrop.Options.EXTRA_GESTURES_ALWAYS_ENABLED, false);

            // Crop image view options
            mGestureCropImageView.setMaxBitmapSize(optionsBundle.getInt(UCrop.Options.EXTRA_MAX_BITMAP_SIZE, CropImageView.DEFAULT_MAX_BITMAP_SIZE));
            mGestureCropImageView.setMaxScaleMultiplier(optionsBundle.getFloat(UCrop.Options.EXTRA_MAX_SCALE_MULTIPLIER, CropImageView.DEFAULT_MAX_SCALE_MULTIPLIER));
            mGestureCropImageView.setImageToWrapCropBoundsAnimDuration(optionsBundle.getInt(UCrop.Options.EXTRA_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION, CropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION));


            // Overlay view options
            mOverlayView.setDimmedColor(optionsBundle.getInt(UCrop.Options.EXTRA_DIMMED_LAYER_COLOR, getResources().getColor(R.color.ucrop_color_default_dimmed)));
            mOverlayView.setOvalDimmedLayer(optionsBundle.getBoolean(UCrop.Options.EXTRA_OVAL_DIMMED_LAYER, OverlayView.DEFAULT_OVAL_DIMMED_LAYER));

            mOverlayView.setShowCropFrame(optionsBundle.getBoolean(UCrop.Options.EXTRA_SHOW_CROP_FRAME, OverlayView.DEFAULT_SHOW_CROP_FRAME));
            mOverlayView.setCropFrameColor(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_FRAME_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_frame)));
            mOverlayView.setCropFrameStrokeWidth(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_FRAME_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_frame_stoke_width)));

            mOverlayView.setShowCropGrid(optionsBundle.getBoolean(UCrop.Options.EXTRA_SHOW_CROP_GRID, OverlayView.DEFAULT_SHOW_CROP_GRID));
            mOverlayView.setCropGridRowCount(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_GRID_ROW_COUNT, OverlayView.DEFAULT_CROP_GRID_ROW_COUNT));
            mOverlayView.setCropGridColumnCount(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_GRID_COLUMN_COUNT, OverlayView.DEFAULT_CROP_GRID_COLUMN_COUNT));
            mOverlayView.setCropGridColor(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_GRID_COLOR, getResources().getColor(R.color.ucrop_color_default_crop_grid)));
            mOverlayView.setCropGridStrokeWidth(optionsBundle.getInt(UCrop.Options.EXTRA_CROP_GRID_STROKE_WIDTH, getResources().getDimensionPixelSize(R.dimen.ucrop_default_crop_grid_stoke_width)));
        }
    }

    private void setupViews() {
//        setStatusBarColor(getResources().getColor(R.color.ucrop_color_statusbar));
        mCancelTxt = (TextView) findViewById(R.id.txt_cancel);
        mCancelTxt.setOnClickListener(this);
        mCompleteTxt = (TextView) findViewById(R.id.txt_complete);
        mCompleteTxt.setOnClickListener(this);

        UCropView uCropView = (UCropView) findViewById(R.id.ucrop);
        mGestureCropImageView = uCropView.getCropImageView();
        mOverlayView = uCropView.getOverlayView();

        mGestureCropImageView.setTransformImageListener(new TransformImageView.TransformImageListener() {
            @Override
            public void onRotate(float currentAngle) {
                setAngleText(currentAngle);
            }

            @Override
            public void onScale(float currentScale) {
                setScaleText(currentScale);
            }
        });

        mWrapperStateAspectRatio = (ViewGroup) findViewById(R.id.state_aspect_ratio);
        mWrapperStateAspectRatio.setOnClickListener(mStateClickListener);
        mWrapperStateRotate = (ViewGroup) findViewById(R.id.state_rotate);
        mWrapperStateRotate.setOnClickListener(mStateClickListener);
        mWrapperStateScale = (ViewGroup) findViewById(R.id.state_scale);
        mWrapperStateScale.setOnClickListener(mStateClickListener);

        mLayoutAspectRatio = (ViewGroup) findViewById(R.id.layout_aspect_ratio);
        mLayoutRotate = (ViewGroup) findViewById(R.id.layout_rotate_wheel);
        mLayoutScale = (ViewGroup) findViewById(R.id.layout_scale_wheel);

        setupAspectRatioWidget();
        setupRotateWidget();
        setupScaleWidget();
    }


    private void setupAspectRatioWidget() {
        mCropAspectRatioViews.add((ViewGroup) findViewById(R.id.crop_aspect_ratio_1_1));
        mCropAspectRatioViews.add((ViewGroup) findViewById(R.id.crop_aspect_ratio_3_4));
        mCropAspectRatioViews.add((ViewGroup) findViewById(R.id.crop_aspect_ratio_original));
        mCropAspectRatioViews.add((ViewGroup) findViewById(R.id.crop_aspect_ratio_3_2));
        mCropAspectRatioViews.add((ViewGroup) findViewById(R.id.crop_aspect_ratio_16_9));

        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
            cropAspectRatioView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGestureCropImageView.setTargetAspectRatio(
                            ((AspectRatioTextView) ((ViewGroup) v).getChildAt(0)).getAspectRatio(v.isSelected()));
                    mGestureCropImageView.setImageToWrapCropBounds();
                    if (!v.isSelected()) {
                        for (ViewGroup cropAspectRatioView : mCropAspectRatioViews) {
                            cropAspectRatioView.setSelected(cropAspectRatioView == v);
                        }
                    }
                }
            });
        }
        mCropAspectRatioViews.get(2).setSelected(true);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mCropAspectRatioViews!= null && mCropAspectRatioViews.get(4)!= null) {
                    mCropAspectRatioViews.get(4).performClick();
                }
            }
        }, 500);

    }
    public void postDelayed(final Runnable r, long delayMillis) {
        if (mHandler != null) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!isFinishing()) {
                        r.run();
                    }
                }
            }, delayMillis);
        }
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private void setupRotateWidget() {
        mTextViewRotateAngle = ((TextView) findViewById(R.id.text_view_rotate));
        ((HorizontalProgressWheelView) findViewById(R.id.rotate_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        mGestureCropImageView.postRotate(delta / ROTATE_WIDGET_SENSITIVITY_COEFFICIENT);
                    }

                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });


        findViewById(R.id.wrapper_reset_rotate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetRotation();
            }
        });
        findViewById(R.id.wrapper_rotate_by_angle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotateByAngle(90);
            }
        });
    }

    private void setupScaleWidget() {
        mTextViewScalePercent = ((TextView) findViewById(R.id.text_view_scale));
        ((HorizontalProgressWheelView) findViewById(R.id.scale_scroll_wheel))
                .setScrollingListener(new HorizontalProgressWheelView.ScrollingListener() {
                    @Override
                    public void onScroll(float delta, float totalDistance) {
                        if (delta > 0) {
                            mGestureCropImageView.zoomInImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        } else {
                            mGestureCropImageView.zoomOutImage(mGestureCropImageView.getCurrentScale()
                                    + delta * ((mGestureCropImageView.getMaxScale() - mGestureCropImageView.getMinScale()) / SCALE_WIDGET_SENSITIVITY_COEFFICIENT));
                        }
                    }

                    @Override
                    public void onScrollEnd() {
                        mGestureCropImageView.setImageToWrapCropBounds();
                    }

                    @Override
                    public void onScrollStart() {
                        mGestureCropImageView.cancelAllAnimations();
                    }
                });
    }

    private void setAngleText(float angle) {
        if (mTextViewRotateAngle != null) {
            mTextViewRotateAngle.setText(String.format("%.1f°", angle));
        }
    }

    private void setScaleText(float scale) {
        if (mTextViewScalePercent != null) {
            mTextViewScalePercent.setText(String.format("%d%%", (int) (scale * 100)));
        }
    }

    private void resetRotation() {
        mGestureCropImageView.postRotate(-mGestureCropImageView.getCurrentAngle());
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private void rotateByAngle(int angle) {
        mGestureCropImageView.postRotate(angle);
        mGestureCropImageView.setImageToWrapCropBounds();
    }

    private final View.OnClickListener mStateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!v.isSelected()) {
                setWidgetState(v.getId());
            }
        }
    };

    private void setInitialState() {
        setWidgetState(R.id.state_aspect_ratio);
    }
    private void setWidgetState(int stateViewId) {
        mWrapperStateAspectRatio.setSelected(stateViewId == R.id.state_aspect_ratio);
        mWrapperStateRotate.setSelected(stateViewId == R.id.state_rotate);
        mWrapperStateScale.setSelected(stateViewId == R.id.state_scale);

        mLayoutAspectRatio.setVisibility(stateViewId == R.id.state_aspect_ratio ? View.VISIBLE : View.GONE);
        mLayoutRotate.setVisibility(stateViewId == R.id.state_rotate ? View.VISIBLE : View.GONE);
        mLayoutScale.setVisibility(stateViewId == R.id.state_scale ? View.VISIBLE : View.GONE);

        mGestureCropImageView.setRotateEnabled(mGesturesAlwaysEnabled || stateViewId != R.id.state_scale);
        mGestureCropImageView.setScaleEnabled(mGesturesAlwaysEnabled || stateViewId != R.id.state_rotate);
    }

    private void cropAndSaveImage() {
        OutputStream outputStream = null;
        try {
            final Bitmap croppedBitmap = mGestureCropImageView.cropImage();
            if (croppedBitmap != null) {
                outputStream = getContentResolver().openOutputStream(mOutputUri);
                croppedBitmap.compress(mCompressFormat, mCompressQuality, outputStream);
                croppedBitmap.recycle();

                setResultUri(mOutputUri);
                finish();
            } else {
                setResultException(new NullPointerException("CropImageView.cropImage() returned null."));
            }
        } catch (Exception e) {
            setResultException(e);
            finish();
        } finally {
            BitmapLoadUtils.close(outputStream);
        }
    }

    private void setResultUri(Uri uri) {
        setResult(RESULT_OK, new Intent().putExtra(UCrop.EXTRA_OUTPUT_URI, uri));
    }

    private void setResultException(Throwable throwable) {
        setResult(UCrop.RESULT_ERROR, new Intent().putExtra(UCrop.EXTRA_ERROR, throwable));
    }

}
