package com.htc.dotbreaker;

import android.graphics.Paint;

public class Board extends BrickItem {
    int mWidth;
    int mMoveUnitHerizontal;

    Board(boolean exist, Paint paint, int left, int top, int right, int bottom, int width, int moveUnitHerizontal) {
        super(exist, paint, left, top, right, bottom);
        mWidth = width;
        mMoveUnitHerizontal = moveUnitHerizontal;
    }
}
