package com.htc.dotbreaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class DrawViewFromFile extends View {

    Bitmap mBitmap;

    public DrawViewFromFile(Context context, Bitmap bitmap) {
        super(context);
        mBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

}
