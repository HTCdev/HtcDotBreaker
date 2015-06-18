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
