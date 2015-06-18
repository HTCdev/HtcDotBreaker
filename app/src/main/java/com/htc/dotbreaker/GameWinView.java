/*
 * Copyright (C) 2015 HTC Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.htc.dotbreaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class GameWinView extends View {
	
    Resources mRes = getResources();
    int mInnerFrameMarginTop = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_top);
    int mInnerFrameMarginLeft = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_left);
    int mDotPixel = mRes.getDimensionPixelSize(R.dimen.dot_pixel_width);
    final boolean T = true;
    final boolean F = false;
    final boolean[][] WORD_YOU_WIN_FOR_LOOK = {
        {T,F,T,F,T,T,T,F,T,F,T},
        {T,F,T,F,T,F,T,F,T,F,T},
        {T,T,T,F,T,F,T,F,T,F,T},
        {F,F,T,F,T,F,T,F,T,F,T},
        {T,T,T,F,T,T,T,F,T,T,T},
        {F,F,F,F,F,F,F,F,F,F,F},
        {T,F,F,F,T,F,T,F,T,T,T},
        {T,F,F,F,T,F,T,F,T,F,T},
        {T,F,T,F,T,F,T,F,T,F,T},
        {T,F,T,F,T,F,T,F,T,F,T},
        {T,T,F,T,T,F,T,F,T,F,T}};
    final boolean[][] WORD_YOU_WIN = BrickView.toConvertMatrix(WORD_YOU_WIN_FOR_LOOK);
    final int WORD_YOU_WIN_LOC_X = mInnerFrameMarginLeft + mRes.getDimensionPixelSize(R.dimen.word_you_win_loc_x) * mDotPixel;
    final int WORD_YOU_WIN_LOC_Y = mInnerFrameMarginTop + mRes.getDimensionPixelSize(R.dimen.word_you_win_loc_y) * mDotPixel;

    final boolean[][] WORD_NEXT_FOR_LOOK ={
        {T,T,T,F,T,T,T,F,T,F,T,F,T,T,T},
        {T,F,T,F,T,F,F,F,T,F,T,F,F,T,F},
        {T,F,T,F,T,T,T,F,F,T,F,F,F,T,F},
        {T,F,T,F,T,F,F,F,T,F,T,F,F,T,F},
        {T,F,T,F,T,T,T,F,T,F,T,F,F,T,F}};
    final boolean[][] WORD_NEXT = BrickView.toConvertMatrix(WORD_NEXT_FOR_LOOK);
    final int WORD_NEXT_LOC_X = mInnerFrameMarginLeft + mRes.getDimensionPixelSize(R.dimen.word_next_loc_x) * mDotPixel;
    final int WORD_NEXT_LOC_Y = mInnerFrameMarginTop + mRes.getDimensionPixelSize(R.dimen.word_next_loc_y) * mDotPixel;
    
    Paint mPaintWordYOUWIN = new Paint();
    Paint mPaintWordNEXT = new Paint();

    public GameWinView(Context context) {
        super(context);
        mPaintWordYOUWIN.setColor(mRes.getColor(R.color.word_youwin));
        mPaintWordNEXT.setColor(mRes.getColor(R.color.word_next));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        BrickView.onDrawMatrix(canvas, WORD_YOU_WIN, WORD_YOU_WIN_LOC_X, WORD_YOU_WIN_LOC_Y, mPaintWordYOUWIN);
        if (!BrickView.hint_animation) {
            BrickView.onDrawMatrix(canvas, WORD_NEXT, WORD_NEXT_LOC_X, WORD_NEXT_LOC_Y, mPaintWordNEXT);
        } else if (BrickView.hint_animation) {
            canvas.drawRect(BrickView.SCREEN_WALL_LEFT, BrickView.SCREEN_WALL_TOP, BrickView.SCREEN_WALL_RIGHT, BrickView.SCREEN_WALL_BOTTOM, BrickView.aPaint[BrickView.COLOR_DarkenBackground]);	
            if (BrickView.hint_animation_number < BrickView.hint_animation_number_swipe_down_start) {
                BrickView.onDrawMatrix(canvas, BrickView.MARK_START, BrickView.MARK_START_LOC_X, BrickView.MARK_START_LOC_Y, BrickView.aPaint[BrickView.COLOR_MARK_START]);
                BrickView.onDrawMatrix(canvas, BrickView.toConvertMatrix(BrickView.HINT_ANIMATION[BrickView.hint_animation_number]), BrickView.HINT_ANIMATION_LOC_X, BrickView.HINT_ANIMATION_LOC_Y, BrickView.aPaint[BrickView.COLOR_MARK_START]);
            } else {
                BrickView.onDrawMatrix(canvas, BrickView.MARK_EXIT, BrickView.MARK_EXIT_LOC_X, BrickView.MARK_EXIT_LOC_Y, BrickView.aPaint[BrickView.COLOR_MARK_EXIT]);
                BrickView.onDrawMatrix(canvas, BrickView.toConvertMatrix(BrickView.HINT_ANIMATION[BrickView.hint_animation_number]), BrickView.HINT_ANIMATION_LOC_X, BrickView.HINT_ANIMATION_LOC_Y, BrickView.aPaint[BrickView.COLOR_MARK_EXIT]);
            }
        }
    }
}
