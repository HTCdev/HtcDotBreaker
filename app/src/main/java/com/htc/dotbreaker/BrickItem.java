package com.htc.dotbreaker;

import android.graphics.Paint;

public class BrickItem {
    boolean mExist;
    Paint mPaint;
    int mLeft;
    int mTop;
    int mRight;
    int mBottom;

    BrickItem(boolean exist, Paint paint, int left, int top, int right, int bottom) {
        mExist = exist;
        mPaint = paint;
        mLeft = left;
        mTop = top;
        mRight = right;
        mBottom = bottom;
    }
}
