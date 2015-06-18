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
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class GameStartsLevelView extends View {

    private boolean mbDrawNum = true;
    boolean T = true;
    boolean F = false;

    final boolean[][][] WORD_NUMERIC_6_10 = {
        {
            {T,T,T,T,T,T},	// 0
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {F,F,T,T,F,F},	// 1
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F},
            {F,F,T,T,F,F}
        },
        {
            {T,T,T,T,T,T},	// 2
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {T,T,F,F,F,F},
            {T,T,F,F,F,F},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {T,T,T,T,T,T},	// 3
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {T,T,F,F,T,T},	// 4
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T}
        },
        {
            {T,T,T,T,T,T},	// 5
            {T,T,T,T,T,T},
            {T,T,F,F,F,F},
            {T,T,F,F,F,F},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {T,T,T,T,T,T},	// 6
            {T,T,T,T,T,T},
            {T,T,F,F,F,F},
            {T,T,F,F,F,F},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {T,T,T,T,T,T},	// 7
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T}
        },
        {
            {T,T,T,T,T,T},	// 8
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        },
        {
            {T,T,T,T,T,T},	// 9
            {T,T,T,T,T,T},
            {T,T,F,F,T,T},
            {T,T,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T},
            {F,F,F,F,T,T},
            {F,F,F,F,T,T},
            {T,T,T,T,T,T},
            {T,T,T,T,T,T}
        }
    };

    Resources res = getResources();
    // Dot View's walls
    private final int WALL_LEFT = res.getDimensionPixelSize(R.dimen.inner_frame_margin_left);
    private final int WALL_TOP = res.getDimensionPixelSize(R.dimen.inner_frame_margin_top);
    private final int BLOCK_WIDTH = res.getDimensionPixelSize(R.dimen.dot_pixel_width);
    private final int BLOCK_HEIGHT = res.getDimensionPixelSize(R.dimen.dot_pixel_height);

    Paint mPaintNumeric = new Paint();

    final int WORD_NUMERIC_6_10_LOC_X = WALL_LEFT+10*BLOCK_WIDTH;
    final int WORD_NUMERIC_6_10_LOC_Y = WALL_TOP+22*BLOCK_HEIGHT;

	public GameStartsLevelView(Context context) {
		super(context);
		mPaintNumeric.setColor(Color.GREEN);
	}

    public void updateLevelNum(boolean isDrawNum) {
        mbDrawNum = isDrawNum;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mbDrawNum) {
            return;
        }

        int showBits = 0;
        int checkShowBits = BrickView.brick_game_level + 1;
        while (checkShowBits != 0) {
            ++showBits;
            checkShowBits /= 10;
        }
        
        int showLevelNumeric = BrickView.brick_game_level + 1;
        int showLevelNumericLocX = WORD_NUMERIC_6_10_LOC_X;
        showLevelNumericLocX += 4 * BLOCK_WIDTH * (showBits - 1);

        while (showBits > 0) {
            onDrawWord(canvas, BrickView.toConvertMatrix(WORD_NUMERIC_6_10[showLevelNumeric % 10]), showLevelNumericLocX, WORD_NUMERIC_6_10_LOC_Y, mPaintNumeric);
            --showBits;
            showLevelNumericLocX -= 8 * BLOCK_WIDTH;
            showLevelNumeric /= 10;
        }
    }

    protected void onDrawWord(Canvas canvas, boolean[][] matrix, int loc_x, int loc_y, Paint paint) {
        for (int row = 0; row < matrix.length; ++row) {
            for (int column = 0; column < matrix[row].length; ++column) {
                if (matrix[row][column])
                    canvas.drawRect(loc_x + row * BLOCK_WIDTH, 
                                    loc_y + column * BLOCK_HEIGHT, 
                                    loc_x + (row + 1) * BLOCK_WIDTH, 
                                    loc_y + (column + 1) * BLOCK_HEIGHT, paint);
            }
        }
    }
}
