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

import java.io.IOException;
import java.util.List;

import com.htc.dotbreaker.ui.CustomizedImage;
import com.htc.dotbreaker.utils.DotBreakerUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class GameActivity extends Activity implements SensorEventListener {
    final String LOG_TAG = "HtcDotBreaker";
    final String LOG_PREFIX = "[GameActivity] ";

    private static final String ANIMATION_LEVEL = "level_animation";
    private static final String ANIMATION_WIN = "win_animation";
    private static final String ANIMATION_LOSE = "lose_animation";
    private static final String ANIMATION_FINAL_WIN = "open_cover_animation";
    private static final String DOTMATRIX_PACKAGENAME = "com.htc.dotmatrix";
    private static final String DOTMATRIX_PACKAGENAME_ODM = "com.htc.dotmatrix_odm";

    private Resources mRes = null;
    float SLIDE_MIN_DISTANCE;

    RelativeLayout mLayout;
    View mView;

    View mTipsView;
    CustomizedImage mAnimationLevel = null;
    CustomizedImage mAnimationWin = null;
    CustomizedImage mAnimationLose = null;
    CustomizedImage mAnimationFinalWin = null;
    GameStartsLevelView mGameStartsLevelView;
    BrickView mBrickView;
    GameWinView mGameWinView;
    GameOverView mGameOverView;

    CountDownTimer mToGamePageTimer;

    static private SharedPreferences mSharedprefs = null;
    static private SharedPreferences.Editor mSharedprefs_editor = null;

    static float gravity_sensor[] = new float[3];
    static final int x = 0, y = 1, z = 2;
    static private SensorManager sSensorManager;
    static private Sensor sSensor;
    
    MediaPlayer mMediaPlayer;

    private static final String GAME_ID = "activityName";
    final static String ACTION_COVEROPEN = "com.htc.cover.closed";
    // true: cover open, false: cover close
    public static final String KEY_STATE = "state";
    // N pole for flip case, S pole for DotMatrix
    public static final String KEY_POLE_N = "north";

    BroadcastReceiver mCoverStateReceiver = new BroadcastReceiver() {
        boolean mFirstNear = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String szAction = intent.getAction();
            // false: Full HW cover, true: special HW, there is no UI
            boolean HWFullType = !(intent.getBooleanExtra(KEY_POLE_N, false));
            // false: cover is open true: cover is close
            boolean close = intent.getBooleanExtra(KEY_STATE, false);

            Log.i(LOG_TAG, "onReceive:" + szAction + " HWFULLTYPE:" + HWFullType + " close=" + close);

            if (HWFullType && close) {
                setMaxBright();
            } else {
                restoreBright();
            }

            if (ACTION_COVEROPEN == szAction) {
                if (close) {
                    mFirstNear = true;
                    // start game in full screen mode
                    getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility()
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                    checkViewStatus();
                } else {
                    if (mFirstNear) {
                        stop_timer();
                        onBackPressed();
                        GameActivity.this.finish();
                        String toast_pause = mRes.getString(R.string.toast_pause);
                        Toast.makeText(GameActivity.this, toast_pause, Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus && mTipsView == null) {
            getWindow().getDecorView().setSystemUiVisibility(
                    getWindow().getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    void setMaxBright() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL;
        getWindow().setAttributes(layoutParams);
    }

    void restoreBright() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mBrickView != null) {
            mBrickView.onBackPressed();
        } else if (mGameWinView != null) {
            hint_animation_stop();
            BrickView.hint_animation = false;
        } else if (mGameOverView != null) {
            hint_animation_stop();
            BrickView.hint_animation = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityList.getInstance().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.brickbreakergame);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // keep screen on.

        WindowManager.LayoutParams window = getWindow().getAttributes(); // get window attributes
        window.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL; // set bright to Max
        getWindow().setAttributes(window); // set window attributes

        mRes = getResources();
        SLIDE_MIN_DISTANCE = mRes.getDimension(R.dimen.slide_min_distance);

        sSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sSensor = sSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sSensorManager.registerListener(this, sSensor, SensorManager.SENSOR_DELAY_NORMAL);

        mLayout = (RelativeLayout) findViewById(R.id.brickgame_view);
        mTipsView = getLayoutInflater().inflate(R.layout.tips, null);
        LayoutParams param = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mLayout.addView(mTipsView, param);

        // start the tip animation
        ImageView iv = (ImageView) findViewById(R.id.animation);
        AnimationDrawable tipsAnimation = (AnimationDrawable) iv.getDrawable();
        tipsAnimation.start();

        mSharedprefs = getSharedPreferences("PREFS_MyStatus", 0);
        mSharedprefs_editor = mSharedprefs.edit();

        mDetector = new GestureDetector(getApplicationContext(), mGestureListener);

        mDotViewCommunity.bindDotViewService();
        
        IntentFilter intentFilter = new IntentFilter(ACTION_COVEROPEN);
        if (mCoverStateReceiver != null) {
            registerReceiver(mCoverStateReceiver, intentFilter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();



        Window win = getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;

        mDotViewCommunity.resume();
    }

    public boolean isTopActivity(String szAPKName) {
        String szTopActivity = null;
        boolean bRes = false;
        ActivityManager actManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningTaskInfo> runningTasks = actManager.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName topActivity = runningTasks.get(0).topActivity;
            if (topActivity != null)
                szTopActivity = topActivity.getPackageName();
            else
                Log.e(LOG_TAG, "TopActivity is null");
        } else {
            Log.e(LOG_TAG, "TopActivity is 0 task");
        }

        if ((szAPKName != null) && (szTopActivity != null)) {
            bRes = (0 == szAPKName.compareTo(szTopActivity));
        }

        Log.d(LOG_TAG, "isTopActivity send: " + szAPKName + ", top: " + szTopActivity + ", match: " + bRes);
        return bRes;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause, top: " + isTopActivity(getPackageName()));
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            if (mMediaPlayer != null) {
                mMediaPlayer.release();
            }
        }
        mDotViewCommunity.pause();
    }

    @Override
    protected void onDestroy() {
        
        unregisterReceiver(mCoverStateReceiver);
        
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            if (mMediaPlayer != null)
                mMediaPlayer.release();
        }
        mDotViewCommunity.doUnbindDotViewService();
        super.onDestroy();
    }

    static protected boolean preferencesGet(String key, boolean value) {
        return mSharedprefs.getBoolean(key, value);
    }

    static protected int preferencesGet(String key, int value) {
        return mSharedprefs.getInt(key, value);
    }

    static protected float preferencesGet(String key, float value) {
        return mSharedprefs.getFloat(key, value);
    }

    static protected void preferencesEditor(String key, boolean value) {
        mSharedprefs_editor.putBoolean(key, value);
    }

    static protected void preferencesEditor(String key, int value) {
        mSharedprefs_editor.putInt(key, value);
    }

    static protected void preferencesEditorApply() {
        mSharedprefs_editor.apply();
    }

    static protected void preferencesEditorCommit() {
        mSharedprefs_editor.commit();
    }

    private void stop_timer() {
        mToGamePageTimer.cancel();
        check_game_complete_timer.cancel();
        hint_animation_timer.cancel();
    }

    private void check_game_complete() {
        check_game_complete_timer.cancel();
        check_game_complete_timer.start();
    }

    CountDownTimer check_game_complete_timer = new CountDownTimer(1000, BrickView.ball_speed) {
        public void onTick(long millisUntilFinished) {
            if (BrickView.game_level_complete) {
                toGameWinPage();
            } else if (BrickView.game_over) {
                toGameOverPage();
            }
        }

        public void onFinish() {
            check_game_complete();
        }
    };

    private void checkViewStatus() {
        if (mTipsView != null) {
            toGameStartPage();
        } else if (mGameStartsLevelView != null) {
            toGameStartPage();
        } else if (mBrickView != null) {
            toGamePage();
        } else if (mGameWinView != null) {
            toGameWinPage();
        } else if (mGameOverView != null) {
            toGameOverPage();
        } else if (mAnimationFinalWin != null) {
            toGameWin_FinalWin();
        }
    }

    private void setGameLevelPreferencesStatus() {
        for (int level = 0; level < BrickView.MAX_GAME_LEVEL; ++level) {
            int gameStatus = mSharedprefs.getInt("GameStatus[" + level + "]", GameLevel.locked);
            if (gameStatus != GameLevel.locked) {
                GameActivity.preferencesEditor("GameStatus[" + level + "]", GameLevel.passed);
            } else {
                break;
            }
        }
        GameActivity.preferencesEditor("GameStatus[" + BrickView.brick_game_level + "]", GameLevel.ongoing);
        GameActivity.preferencesEditorApply();
    }

    private void toGameStartPage() {
        mLayout.removeAllViews();
        mTipsView = null;
        mGameStartsLevelView = new GameStartsLevelView(GameActivity.this);
        mBrickView = null;
        mAnimationWin = null;
        mGameWinView = null;
        mAnimationLose = null;
        mGameOverView = null;

        mAnimationLevel = new CustomizedImage(this);
        if (mAnimationLevel != null && mRes != null) {
            mAnimationLevel.setLayoutParams(getRelativeLayoutParams());
            try {
                mAnimationLevel.setDotMatrixList(DotBreakerUtils.readPNGtoAnimation(this, true, ANIMATION_LEVEL, 66, 48, 27), false, 1500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mGameStartsLevelView = new GameStartsLevelView(GameActivity.this);
        mLayout.addView(mAnimationLevel);
        mLayout.addView(mGameStartsLevelView);

        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                if (mGameStartsLevelView != null) {
                    mGameStartsLevelView.updateLevelNum(false);
                }
            }
        }.start();

        mToGamePageTimer = new CountDownTimer(3000, 3000) {
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                toGamePage();
            }
        }.start();

        setGameLevelPreferencesStatus();
    }

    private void toGamePage() {
        mLayout.removeAllViews();
        mAnimationLevel = null;
        mTipsView = null;
        mGameStartsLevelView = null;
        mAnimationWin = null;
        mGameWinView = null;
        mAnimationLose = null;
        mGameOverView = null;
        
        if (mBrickView == null)
        	mBrickView = new BrickView(GameActivity.this);
        mLayout.addView(mBrickView);
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            if (BrickView.brick_game_level <= 5)
                mMediaPlayer = MediaPlayer.create(this, R.raw.gameplay_loop_1_5);
            else if (BrickView.brick_game_level <= 10)
                mMediaPlayer = MediaPlayer.create(this, R.raw.gameplay_loop_6_10);
            else if (BrickView.brick_game_level <= 16)
                mMediaPlayer = MediaPlayer.create(this, R.raw.gameplay_loop_11_16);
            else if (BrickView.brick_game_level <= 19)
                mMediaPlayer = MediaPlayer.create(this, R.raw.gameplay_loop_17_19);
            else
                mMediaPlayer = MediaPlayer.create(this, R.raw.gameplay_loop_20);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
        check_game_complete();
    }

    private void toGameWin_FinalWin() {
        mLayout.removeAllViews();
        mTipsView = null;
        mGameStartsLevelView = null;
        mBrickView = null;
        mAnimationWin = null;
        mGameWinView = null;
        mAnimationLose = null;
        mGameOverView = null;
        mAnimationFinalWin = new CustomizedImage(this);
        if (mAnimationFinalWin != null && mRes != null) {
            mAnimationFinalWin.setLayoutParams(getRelativeLayoutParams());
            try {
                mAnimationFinalWin.setDotMatrixList(DotBreakerUtils.readPNGtoAnimation(this, true, ANIMATION_FINAL_WIN, 31, 48, 27), true, 2000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mLayout.addView(mAnimationFinalWin);
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(this, R.raw.gamewin_loop);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    private void toGameWinPage() {
        if (mBrickView != null) {
            GameActivity.preferencesEditor("Current_GameState", false);
            GameActivity.preferencesEditor("BricksHit", GameActivity.preferencesGet("BricksHit", 0) + mBrickView.mBricksHit);
            GameActivity.preferencesEditor("PlayTime", GameActivity.preferencesGet("PlayTime", 0) + mBrickView.tmp_playTime);
            GameActivity.preferencesEditor("LevelPass", GameActivity.preferencesGet("LevelPass", 0) + 1);
            GameActivity.preferencesEditor("BallTravelled", GameActivity.preferencesGet("BallTravelled", 0) + mBrickView.ball_travelled);
            GameActivity.preferencesEditorApply();
        }

        mLayout.removeAllViews();
        mTipsView = null;
        mGameStartsLevelView = null;
        check_game_complete_timer.cancel();
        mBrickView = null;
        mAnimationLose = null;
        mGameOverView = null;

        mAnimationWin = new CustomizedImage(this);
        if (mAnimationWin != null && mRes != null) {
            mAnimationWin.setLayoutParams(getRelativeLayoutParams());
            try {
                mAnimationWin.setDotMatrixList(DotBreakerUtils.readPNGtoAnimation(this, true, ANIMATION_WIN, 30, 48, 27), true, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mGameWinView = new GameWinView(GameActivity.this);
        mLayout.addView(mAnimationWin);
        mLayout.addView(mGameWinView);
        BrickView.game_level_complete = false;
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(this, R.raw.gamewin_loop);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
	}
    
	private void toGameOverPage() {
        if (mBrickView != null) {
            GameActivity.preferencesEditor("Current_GameState", false);
            if (mBrickView.score != null)
                preferencesEditor("BestScore", Math.max(mBrickView.score.mNumeric, preferencesGet("BestScore", 0)));
            GameActivity.preferencesEditor("BricksHit", GameActivity.preferencesGet("BricksHit", 0) + mBrickView.mBricksHit);
            GameActivity.preferencesEditor("PlayTime", GameActivity.preferencesGet("PlayTime", 0) + mBrickView.tmp_playTime);
            GameActivity.preferencesEditor("Deaths", GameActivity.preferencesGet("Deaths", 0) + 1);
            GameActivity.preferencesEditor("BallTravelled", GameActivity.preferencesGet("BallTravelled", 0) + mBrickView.ball_travelled);
            GameActivity.preferencesEditorApply();
        }

        BrickView.keep_score = BrickView.KEEP_SCORE_INIT;
        mLayout.removeAllViews();
        mTipsView = null;
        mGameStartsLevelView = null;
        check_game_complete_timer.cancel();
        mBrickView = null;
        mGameWinView = null;
        mAnimationWin = null;

        mAnimationLose = new CustomizedImage(this);
        if (mAnimationLose != null && mRes != null) {
            mAnimationLose.setLayoutParams(getRelativeLayoutParams());
            try {
                mAnimationLose.setDotMatrixList(DotBreakerUtils.readPNGtoAnimation(this, true, ANIMATION_LOSE, 30, 48, 27), true, 5000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mGameOverView = new GameOverView(GameActivity.this);
        mLayout.addView(mAnimationLose);
        mLayout.addView(mGameOverView);
        BrickView.game_over = false;
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            mMediaPlayer.release();
            mMediaPlayer = MediaPlayer.create(this, R.raw.gameover_loop);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }

    private void nextLevel() {
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            mMediaPlayer.release();
        }
        toGameStartPage();
    }

    private void restartLevel() {
        if (mSharedprefs.getBoolean(Settings.sMUSIC, true)) {
            mMediaPlayer.release();
        }
        toGameStartPage();
    }

    private GestureDetector mDetector;

    private SimpleOnGestureListener mGestureListener = new SimpleOnGestureListener() {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            float X = e.getX();
            float Y = e.getY();
            Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onSingleTapUp, e.x = " + X + ", e.y = " + Y);

            if (mTipsView != null) {
                Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onSingleTapUp  in View: drawView");
            } else if (mGameStartsLevelView != null) {
                Log.d(GameLevel.LOG_TAG, LOG_PREFIX
                        + "mGestureListener.onSingleTapUp  in View: gameStartsLevelView");
            } else if (mBrickView != null) {
                Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onSingleTapUp  in View: brickView");
                if (!mBrickView.mBall_alive) {
                    mBrickView.toStartGame();
                } else if (mBrickView.game_pause && X > BrickView.MARK_PAUSE_LOC_X
                        && X < BrickView.MARK_PAUSE_LOC_X + 5 * BrickView.BLOCK_WIDTH
                        && Y > BrickView.MARK_PAUSE_LOC_Y
                        && Y < BrickView.MARK_PAUSE_LOC_Y + 5 * BrickView.BLOCK_WIDTH
                        && BrickView.hint_animation == false) {
                    hint_animation_show(mBrickView);
                }
            } else if (mGameWinView != null) {
                Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onSingleTapUp  in View: gameWinView");
                if (X > mGameWinView.WORD_NEXT_LOC_X
                        && X < mGameWinView.WORD_NEXT_LOC_X + 15 * BrickView.BLOCK_WIDTH
                        && Y > mGameWinView.WORD_NEXT_LOC_Y
                        && Y < mGameWinView.WORD_NEXT_LOC_Y + 5 * BrickView.BLOCK_WIDTH
                        && BrickView.hint_animation == false) {
                    if (preferencesGet("CongratulationMessage", false)) { // Final Win
                        // Show open case animation
                        toGameWin_FinalWin();
                    } else {
                        hint_animation_show(mGameWinView);
                    }
                }
            } else if (mGameOverView != null) {
                Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onSingleTapUp  in View: gameOverView");
                if (X > mGameOverView.WORD_RESTART_LOC_X
                        && X < mGameOverView.WORD_RESTART_LOC_X + 27 * BrickView.BLOCK_WIDTH
                        && Y > mGameOverView.WORD_RESTART_LOC_Y
                        && Y < mGameOverView.WORD_RESTART_LOC_Y + 5 * BrickView.BLOCK_WIDTH
                        && BrickView.hint_animation == false) {
                    hint_animation_show(mGameOverView);
                }
            }
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float Y1 = e1.getY();
            float Y2 = e2.getY();
            if (Y2 >= Y1 && (Y2 - Y1) > SLIDE_MIN_DISTANCE) { // swipe_down
                if (mTipsView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_down  in View: drawView");
                } else if (mGameStartsLevelView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_down  in View: gameStartsLevelView");
                } else if (mBrickView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_down  in View: brickView");
                    if (mBrickView.game_pause) {
                        mBrickView.toExitBrick();
                    } else
                        mBrickView.toGamePause();
                } else if (mGameWinView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_down  in View: gameWinView");
                    if (preferencesGet("CongratulationMessage", false)) { // Final Win
                        preferencesEditor("CongratulationMessage", false);
                        preferencesEditorApply();
                    }
                    hint_animation_stop();
                    BrickView.hint_animation = false;
                    ActivityList.getInstance().exit(); // Exit APP, delete all Activity.
                } else if (mGameOverView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_down  in View: gameOverView");
                    hint_animation_stop();
                    BrickView.hint_animation = false;
                    ActivityList.getInstance().exit(); // Exit APP, delete all Activity.
                }
            } else if (Y2 < Y1 && (Y1 - Y2) > SLIDE_MIN_DISTANCE) { // swipe_up
                if (mTipsView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_up  in View: drawView");
                } else if (mGameStartsLevelView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_up  in View: gameStartsLevelView");
                } else if (mBrickView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_up  in View: brickView");
                    if (mBrickView.game_pause) {
                        mBrickView.toGameResume();
                    } else {
                        mBrickView.toStartGame();
                    }
                } else if (mGameWinView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_up  in View: gameWinView");
                    if (preferencesGet("CongratulationMessage", false)) { // Final Win
                        // Show open case animation
                        toGameWin_FinalWin();
                    } else {
                        hint_animation_stop();
                        BrickView.hint_animation = false;
                        nextLevel();
                    }
                } else if (mGameOverView != null) {
                    Log.d(LOG_TAG, LOG_PREFIX + "mGestureListener.onFling.swipe_up  in View: gameOverView");
                    hint_animation_stop();
                    BrickView.hint_animation = false;
                    mGameOverView.invalidate();
                    restartLevel();
                }
            }
			return true;
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void hint_animation_show(View view) {
        BrickView.hint_animation = true;
        BrickView.hint_animation_number = BrickView.HINT_ANIMATION.length - 1;
        hint_animation_stop();
        mView = view;
        hint_animation_timer.start();
    }

    private void hint_animation_stop() {
        mView = null;
        hint_animation_timer.cancel();
    }

    CountDownTimer hint_animation_timer = new CountDownTimer(2100, 200) {
        public void onTick(long millisUntilFinished) {
            ++BrickView.hint_animation_number;
            BrickView.hint_animation_number %= 10;
            mView.invalidate();
        }

        public void onFinish() {
            BrickView.hint_animation = false;
            mView.invalidate();
        }
    };

	
    
    final static String DOTVIEWACTION_STRING = "com.htc.intent.action.dotviewgameservice";
    static final String PERMISSION_DOTVIEW_GAME = "com.htc.permission.dotviewgame";

    static final int MSG_DOTVIEW_CLIENT_REGISTER = 1;
    static final int MSG_DOTVIEW_CLIENT_UNREGISTER = 2;
    static final int MSG_DOTVIEW_CLIENT_RESUME = 3;
    static final int MSG_DOTVIEW_CLIENT_PAUSE = 4;

    static final int MSG_DOTVIEW_SERVCER_REGISTER_SUCCESS = 101;
    static final int MSG_DOTVIEW_SERVER_REGISTER_FAIL = 102;
    static final int MSG_DOTVIEW_SERVER_REMOVECURRENTGAME = 105;
	static final int MSG_DOTVIEW_SERVER_RESUME_SUCCESSS = 106;
    static final int MSG_DOTVIEW_SERVER_RESUME_FAIL = 107;
    static final int MSG_DOTVIEW_SERVER_PAUSE_SUCCESSS = 108;
    static final int MSG_DOTVIEW_SERVER_PAUSE_FAIL = 109;
    static final int MSG_DOTVIEW_SERVER_DOTVIEW_LEAVE_GAME_MODE = 110;
    static final int MSG_DOTVIEW_SERVER_DOTVIEWVERSION = 111;

    DotViewCommunity mDotViewCommunity = new DotViewCommunity();
    int mDotViewVersion;
    
    public class DotViewCommunity {
        static final int MSG_SET_VALUE = 3;

        static final int MSG_INNER_RETRY_RESUME = 1;

        Messenger mService = null;
        boolean mIsBound = false;

        Messenger mMessenger = new Messenger(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i(LOG_TAG, "handleMessage: " + msg.what);
                String szText;
                Bundle data;                

                switch (msg.what) {
                    case MSG_DOTVIEW_SERVER_DOTVIEWVERSION:
                        mDotViewVersion = msg.arg1;
                        Log.i(LOG_TAG, "DotView ver:" + mDotViewVersion);
                        break;
                        
                    case MSG_DOTVIEW_SERVER_RESUME_SUCCESSS:
                        Log.i(LOG_TAG, " resume succeed");
                        break;
                    case MSG_DOTVIEW_SERVER_RESUME_FAIL:                        
                    case MSG_DOTVIEW_SERVER_PAUSE_FAIL:
                    case MSG_DOTVIEW_SERVER_REGISTER_FAIL:
                        // Do retry?
                        szText = "DotView error:";
                        data = msg.getData();
                        if (data != null)
                            szText += data.getString("reason");
                      
                        Log.i(LOG_TAG, "DotView Game Fail:" +szText);

                        break;                        
                    default:
                        super.handleMessage(msg);
                }
            }
        });

        Handler mInnerHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i(LOG_TAG, "mInnerMessenger, msg.what: " + msg.what);
                switch (msg.what) {
                    case MSG_INNER_RETRY_RESUME:
                        if (mService != null) {
                            resume();
                        } else {
                            int retry = msg.arg1;
                            int delay = msg.arg2;
                            if (retry < 5) {
                                retry++;
                                delay += 100;

                                if (mInnerHandler.hasMessages(MSG_INNER_RETRY_RESUME)) {
                                    mInnerHandler.removeMessages(MSG_INNER_RETRY_RESUME);
                                }
                                mInnerHandler.sendMessageDelayed(Message.obtain(null, MSG_INNER_RETRY_RESUME, retry, delay), (long) delay);
                            }
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };

        DotViewSericeConnection mServiceConnect = new DotViewSericeConnection();

        class DotViewSericeConnection implements ServiceConnection {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(LOG_TAG, "onServiceConnected: " + name);
                mService = new Messenger(service);

                try {
                    Message msg = Message.obtain(null, MSG_DOTVIEW_CLIENT_REGISTER);
                    msg.replyTo = mMessenger;

                    Bundle data = new Bundle();
                    // data.putString(GAME_ID, getPackageName());
                    data.putString(GAME_ID, GameActivity.class.getName());

					Window win = getWindow();
                    WindowManager.LayoutParams params = win.getAttributes();
                    msg.arg1 = params.flags;

                    msg.setData(data);
                    mService.send(msg);

                    // msg = Message.obtain(null,MSG_DOTVIEWSERVER_SET_VALUE,
                    // this.hashCode(), 0);
                    // mService.send(msg);
                } catch (RemoteException e) {
                    Log.i(LOG_TAG, "send server fail:" + e, e);
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(LOG_TAG, "onServiceDisconnected:" + name);
                mService = null;
            }
        }

        public boolean resume() {
            boolean bRes = false;
            Log.i(LOG_TAG, "resume");

            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MSG_DOTVIEW_CLIENT_RESUME);
                    Bundle data = new Bundle();
                    // data.putString(GAME_ID, getPackageName());
                    data.putString(GAME_ID, GameActivity.class.getName());
                    msg.setData(data);
                    mService.send(msg);

                    bRes = true;
                } catch (RemoteException e) {
                    Log.i(LOG_TAG, "send server fail:" + e, e);
                }
            } else {
                if (mInnerHandler.hasMessages(MSG_INNER_RETRY_RESUME)) {
                    mInnerHandler.removeMessages(MSG_INNER_RETRY_RESUME);
                }
                int retry = 0;
                int delay = 100;
                mInnerHandler.sendMessageDelayed(Message.obtain(null, MSG_INNER_RETRY_RESUME, retry, delay), (long) delay);
            }
            return bRes;
        }

        public boolean pause() {
            boolean bRes = false;
            Log.i(LOG_TAG, "pause");

            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MSG_DOTVIEW_CLIENT_PAUSE);
                    Bundle data = new Bundle();
                    // data.putString(GAME_ID, getPackageName());
                    data.putString(GAME_ID, GameActivity.class.getName());
                    msg.setData(data);

                    mService.send(msg);
                    bRes = true;

                    if (mInnerHandler.hasMessages(MSG_INNER_RETRY_RESUME)) {
                        mInnerHandler.removeMessages(MSG_INNER_RETRY_RESUME);
                    }
                } catch (RemoteException e) {
                    Log.i(LOG_TAG, "send server fail:" + e, e);
                }
            }
            return bRes;
        }

        public boolean bindDotViewService() {
            boolean bRes = false;
            Log.i(LOG_TAG, "bindDotService: " + !mIsBound);

            if (!mIsBound) {
                mIsBound = true;
                Intent intent = new Intent(DOTVIEWACTION_STRING);
                if (DotBreakerUtils.isPackageExisted(DOTMATRIX_PACKAGENAME, GameActivity.this)) {
                    intent.setPackage(DOTMATRIX_PACKAGENAME);
                }
                else if(DotBreakerUtils.isPackageExisted(DOTMATRIX_PACKAGENAME_ODM, GameActivity.this)) {
                    intent.setPackage(DOTMATRIX_PACKAGENAME_ODM);
                }
                else {
                    Log.v(LOG_TAG, "DotView don't exist");
                    return bRes;
                }
                bRes = bindService(intent, mServiceConnect, BIND_AUTO_CREATE);
            }
            return bRes;
        }

        public void doUnbindDotViewService() {
            Log.i(LOG_TAG, "doUnbindDotViewService:" + mIsBound);
            if (mIsBound) {
                if (mService != null) {
                    try {
                        Message msg = Message.obtain(null, MSG_DOTVIEW_CLIENT_UNREGISTER);
                        Bundle data = new Bundle();
                        // data.putString(GAME_ID, getPackageName());
                        data.putString(GAME_ID, GameActivity.class.getName());
                        msg.setData(data);
                        // msg.replyTo = mMessenger;
                        mService.send(msg);
                    } catch (RemoteException e) {
                        Log.i(LOG_TAG, "send server2 fail:" + e, e);
                        // There is nothing special we need to do if the service
                        // has crashed.
                    }
                }

                // Detach our existing connection.
                unbindService(mServiceConnect);
                mIsBound = false;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        gravity_sensor[x] = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    private RelativeLayout.LayoutParams getRelativeLayoutParams() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                DotBreakerUtils.getDotViewInnerFrameWidth(mRes),
                DotBreakerUtils.getDotViewInnerFrameHeight(mRes));
        params.leftMargin = DotBreakerUtils.getDotViewInnerFrameMarginLeft(mRes);
        params.topMargin = DotBreakerUtils.getDotViewInnerFrameMarginTop(mRes);
        return params;
    }
}
