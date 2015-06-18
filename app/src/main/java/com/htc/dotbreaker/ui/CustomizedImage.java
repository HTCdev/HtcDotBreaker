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
package com.htc.dotbreaker.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import com.htc.dotbreaker.utils.DotBreakerUtils;

public class CustomizedImage extends DotImage {
    private static final String LOG_PREFIX = "[CustomizedImage] ";
    
    private static final int WHAT_UI_PLAY_ANIMATION = 1001;
//    private static final int UI_ANIMATION_INTERVAL_MILLIS = 0;

    public static final int DIM_30_PERCENT = 1;
    private int mCurrentMode = 0;
    
    protected int mFrameCount = 1;
    protected int mDuration = 0;
    private int mAnimIdx = 0;
    private boolean mbAnimRepeat = true;
    private AnimationFinishCallBack mAnimationFinishCallBack = null;
    
    protected boolean mbStartAnimation = true;
    protected ArrayList<int[][]> mImgDotMatrixList = null;
    
    private Handler mUIHandler = new MyUIHandler();
    
    private class MyUIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_UI_PLAY_ANIMATION:
                    mbStartAnimation = true;
                    invalidate();
                    break;
                default:
                    break;
            }
        }
    }
    
    public CustomizedImage(Context context) {
        super(context);
        init();
    }

    public CustomizedImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomizedImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    }
        
    public void setDotMatrixList(ArrayList<int[][]> dotMatrixList, boolean bAnimRepeat) {
        if (dotMatrixList == mImgDotMatrixList) {
            // Log.w(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "setDotMatrixList, same object has been set.");
            return;
        }
        
        mImgDotMatrixList = dotMatrixList;

        if (mImgDotMatrixList != null) {
            mDuration = 0;
            mFrameCount = mImgDotMatrixList.size();
            Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "setDotMatrixList, mFrameCount: " + mFrameCount);
            if (!mImgDotMatrixList.isEmpty()) {
                int[][] tmpFrame = mImgDotMatrixList.get(0);
                if (tmpFrame != null) {
                    mRowSize = tmpFrame.length;
                    mColSize = tmpFrame[0].length;
                }
            } else {
                return;
            }
        } else {
            Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "setDotMatrixList, dotMatrixList is null!!");
        }
        
        mbAnimRepeat = bAnimRepeat;
        initImgDotMatrix();
        invalidate();
    }
    
    public void setDotMatrixList(ArrayList<int[][]> dotMatrixList, boolean bAnimRepeat, int duration_millisec) {
        setDotMatrixList(dotMatrixList, bAnimRepeat);
        mDuration = duration_millisec;
    }

    @Override
    protected void initImgDotMatrix() {
        //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "initImgDotMatrix");
        mAnimIdx = 0;
        if (mImgDotMatrixList != null && !mImgDotMatrixList.isEmpty()) {
            mImgDotMatrix = mImgDotMatrixList.get(mAnimIdx);
        }
    }

    protected void setPaintColor(int color) {
        switch (mCurrentMode) {
            case DIM_30_PERCENT:
                sPaint.setColor(DotBreakerUtils.adjustColorAlpha(color, 0.3f));
                break;
            default:
                sPaint.setColor(color);
                break;
        }
    }
    
    public void updateCurrentMode(int mode) {
        //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "updateCurrentMode, " + mode);
        mCurrentMode = mode;
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "onDraw");
        if (mImgDotMatrixList == null || !mbStartAnimation) {
            return;
        }
                
        if (mFrameCount <= 1 || mDuration < 0) {
            // It's image, so no need to play animation
            //Log.d(DotBreakerUtils.LOG_TAG, LOG_PREFIX + "onDraw, it is a image, not a animation.");
            return;
        }
        
        mAnimIdx++;
        if (mAnimIdx == mImgDotMatrixList.size()) {
            mAnimIdx = 0;
            if (!mbAnimRepeat) {
                animationfinish();
                return;
            }
        }
        mImgDotMatrix = mImgDotMatrixList.get(mAnimIdx);
        
        if (mUIHandler != null) {
            mbStartAnimation = false;
            mUIHandler.removeMessages(WHAT_UI_PLAY_ANIMATION);
            mUIHandler.sendEmptyMessageDelayed(WHAT_UI_PLAY_ANIMATION, mDuration / mFrameCount);
        }
    }
    
    private void animationfinish() {
        if (mAnimationFinishCallBack != null) {
            mAnimationFinishCallBack.onAnimationFinish();
        }
    }
    
    public interface AnimationFinishCallBack {
        public void onAnimationFinish();
    }
    
    public void setAnimationFinishCallBack(AnimationFinishCallBack callBack) {
        mAnimationFinishCallBack = callBack;
    }
    
}
