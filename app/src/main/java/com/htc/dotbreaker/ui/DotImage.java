
package com.htc.dotbreaker.ui;

import com.htc.dotbreaker.R;
import com.htc.dotbreaker.utils.DotBreakerUtils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

public abstract class DotImage extends RelativeLayout {
    private static final String LOG_PREFIX = "[DotImage] ";

    private static boolean sIsInit = false;
    protected static int sDotPixelHeight;
    protected static int sDotPixelWidth;
    protected static int sInnerFrameWidth;
    protected static int sInnerFrameHeight;
    protected static Paint sPaint = new Paint();
    
    protected static int sBackgroundColor;
    protected int mRowSize;
    protected int mColSize;
    protected int[][] mImgDotMatrix = null;
    
    public DotImage(Context context) {
        super(context);
        init();
    }

    public DotImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DotImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected abstract void initImgDotMatrix();

    private void init() {
        String className = (getClass() != null ? getClass().getName() : "");
        Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "init " + className);

        Resources res = getResources();
        if (res == null) {
            Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "init, res is null!!");
            return;
        }

        if (!sIsInit) {
            sIsInit = true;
            sDotPixelWidth = res.getDimensionPixelSize(R.dimen.dot_pixel_width);
            sDotPixelHeight = res.getDimensionPixelSize(R.dimen.dot_pixel_height);
            sInnerFrameWidth = res.getDimensionPixelSize(R.dimen.inner_frame_width);
            sInnerFrameHeight = res.getDimensionPixelSize(R.dimen.inner_frame_height);
            if (sPaint != null) {
                sPaint.setAntiAlias(true);
            }
            sBackgroundColor = Color.BLACK;
        }
        setBackgroundColor(Color.TRANSPARENT);
    }
    
    protected void createImgDotMatrix(int imgPixelWidth, int imgPixelHeight) {
        //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "imgPixelWidth = " + imgPixelWidth + ", imgPixelHeight = " + imgPixelHeight);

        if ((imgPixelHeight % sDotPixelHeight != 0) || (imgPixelWidth % sDotPixelWidth != 0)) {
            Log.w(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "none divisible!!");
            return;
        }
        
        mColSize = imgPixelWidth / sDotPixelWidth;
        mRowSize = imgPixelHeight / sDotPixelHeight;
        
        //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "column size = " + mColSize + ", row size = " + mRowSize);
        
        mImgDotMatrix = new int[mRowSize][mColSize];
        
        setLayoutParams(new RelativeLayout.LayoutParams(imgPixelWidth, imgPixelHeight));
    }
    
    protected void setPaintColor(int color) {
        sPaint.setColor(color);         
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "onDraw");
        if (this.getVisibility() != VISIBLE) {
            return;
        }
        if (mImgDotMatrix == null || sPaint == null) {
            //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "onDraw, mImgDotMatrix or mPaint is null!!");
            return;
        }
        int left = 0;
        int top = 0;
        int right = sDotPixelWidth;
        int bottom = sDotPixelHeight;

        for (int row = 0; row < mRowSize; ++row) {
            for (int col = 0; col < mColSize; ++col) {
                setPaintColor(mImgDotMatrix[row][col]);
                canvas.drawRect(left, top, right, bottom, sPaint);
                left += sDotPixelWidth;
                right += sDotPixelWidth;
            }
            left = 0;
            right = sDotPixelWidth;
            top += sDotPixelHeight;
            bottom += sDotPixelHeight;
        }
    }

    public void resetImgDotMatrixValue() {
        // Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "resetImgDotMatrixValue");

        if (mImgDotMatrix != null) {
            for (int row = 0; row < mRowSize; ++row) {
                for (int col = 0; col < mColSize; ++col) {
                    mImgDotMatrix[row][col] = sBackgroundColor;
                }
            }
        }
    }
}
