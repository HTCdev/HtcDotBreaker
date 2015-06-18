package com.htc.dotbreaker;

import android.graphics.Paint;

public class Ball extends BrickItem {
    int mWidth;
    int mHeight;
    int mMoveUnitHorizontal;
    int mMoveUnitVertical;

    Ball(boolean exist, Paint paint, int left, int top, int right, int bottom, int width, int height, int moveUnitHeight, int moveUnitVertical) {
        super(exist, paint, left, top, right, bottom);
        mWidth = width;
        mHeight = height;
        mMoveUnitHorizontal = moveUnitHeight;
        mMoveUnitVertical = moveUnitVertical;
    }
}
