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

import com.htc.dotbreaker.utils.DotBreakerUtils;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class BrickView extends View {

    Resources mRes = getResources();
    int inner_frame_margin_left = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_left);
    int inner_frame_margin_top = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_top);
    int inner_frame_margin_right = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_right);
    int inner_frame_margin_bottom = mRes.getDimensionPixelSize(R.dimen.inner_frame_margin_bottom);
    int inner_frame_width = mRes.getDimensionPixelSize(R.dimen.inner_frame_width);
    int inner_frame_height = mRes.getDimensionPixelSize(R.dimen.inner_frame_height);
    int dot_pixel_width = mRes.getDimensionPixelSize(R.dimen.dot_pixel_width);
    int dot_pixel_height = mRes.getDimensionPixelSize(R.dimen.dot_pixel_height);

    Timer mTimerBall, mTimerBoard;
    TimerTask mTimerTaskBall, mTimerTaskBoard;

    // ====================+++++ Define some setting for other Activity to use +++++ ====================
    static boolean T = true;
    static boolean F = false;

    static boolean[][] MARK_PAUSE_FOR_LOOK = {
        {T,T,F,T,T},
        {T,T,F,T,T},
        {T,T,F,T,T},
        {T,T,F,T,T},
        {T,T,F,T,T}};
    static boolean[][] MARK_PAUSE = toConvertMatrix(MARK_PAUSE_FOR_LOOK);
    static int MARK_PAUSE_LOC_X;
    static int MARK_PAUSE_LOC_Y;

    static boolean[][] MARK_START_FOR_LOOK = {
        {T,F,F,F},
        {T,T,F,F},
        {T,T,T,F},
        {T,T,T,T},
        {T,T,T,F},
        {T,T,F,F},
        {T,F,F,F}};
    static boolean[][] MARK_START = toConvertMatrix(MARK_START_FOR_LOOK);
    static int MARK_START_LOC_X;
    static int MARK_START_LOC_Y;

    static boolean[][] MARK_EXIT_FOR_LOOK = {
        {T,F,F,F,T},
        {F,T,F,T,F},
        {F,F,T,F,F},
        {F,T,F,T,F},
        {T,F,F,F,T}};
    static boolean[][] MARK_EXIT = toConvertMatrix(MARK_EXIT_FOR_LOOK);
    static int MARK_EXIT_LOC_X;
    static int MARK_EXIT_LOC_Y;
	
    static boolean hint_animation = false;
    static final int hint_animation_number_swipe_down_start = 5;
    static int hint_animation_number = 0;
    static int HINT_ANIMATION_LOC_X;
    static int HINT_ANIMATION_LOC_Y;
    static boolean[][][] HINT_ANIMATION = {
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,F,F,T}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {T,F,F,F,T}
        },
        {
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {T,F,F,F,T},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,F,F,T},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {T,F,F,F,T},
            {F,T,F,T,F},
            {F,F,T,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {T,F,F,F,T},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {F,F,T,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {T,F,F,F,T},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {F,F,T,F,F}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {T,F,F,F,T},
            {F,T,F,T,F},
            {F,F,T,F,F}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        }};
    static private boolean set_static_variable;
    // ====================----- Define some setting for other Activity to use ----- ====================

    // ====================+++++ Define the array of numeric (3*5 dots) +++++==================== 
    boolean[][][] aNumeric = {
        {	// 0
            {T,T,T},
            {T,F,T},
            {T,F,T},
            {T,F,T},
            {T,T,T}
        },
        {	// 1
            {F,T,F},
            {F,T,F},
            {F,T,F},
            {F,T,F},
            {F,T,F}
        },
        {	// 2
            {T,T,T},
            {F,F,T},
            {T,T,T},
            {T,F,F},
            {T,T,T}
        },
        {	// 3
            {T,T,T},
            {F,F,T},
            {T,T,T},
            {F,F,T},
            {T,T,T}
        },
        {	// 4
            {T,F,T},
            {T,F,T},
            {T,T,T},
            {F,F,T},
            {F,F,T}
        },
        {	// 5
            {T,T,T},
            {T,F,F},
            {T,T,T},
            {F,F,T},
            {T,T,T}
        },
        {	// 6
            {T,T,T},
            {T,F,F},
            {T,T,T},
            {T,F,T},
            {T,T,T}
        },
        {	// 7
            {T,T,T},
            {T,F,T},
            {F,F,T},
            {F,F,T},
            {F,F,T}
        },
        {	// 8
            {T,T,T},
            {T,F,T},
            {T,T,T},
            {T,F,T},
            {T,T,T}
        },
        {	// 9
            {T,T,T},
            {T,F,T},
            {T,T,T},
            {F,F,T},
            {T,T,T}
        }};
    boolean[][][] NUMERIC_MORE_K = {
        {
            {F,F,F},
            {F,T,F},
            {T,T,T},
            {F,T,F},
            {F,F,F}
        },
        {
            {T,F,F},
            {T,F,T},
            {T,T,F},
            {T,F,T},
            {T,F,T}
        }};
    int NUMERIC_MORE_K_LOC_X = inner_frame_margin_left + mRes.getDimensionPixelSize(R.dimen.score_k_loc_x) * dot_pixel_width;
    int NUMERIC_MORE_K_LOC_Y = inner_frame_margin_top + mRes.getDimensionPixelSize(R.dimen.score_k_loc_y) * dot_pixel_height;
    // ====================----- Define the array of numeric (3*5 dots) -----====================


    // ====================+++++ Item's color setting +++++====================
    static Paint[] aPaint = new Paint[29];
    static final int BLACK = 0, BLUE = 1, CYAN = 2, DKGRAY = 3, GRAY = 4, GREEN = 5;
    static final int LTGRAY = 6, MAGENTA = 7, RED = 8, TRANSPARENT = 9, WHITE = 10, YELLOW = 11;
        // ===============+++++ used for bonus item, rainbow colors +++++===============
    static final int COLOR_RAINBOW_RED = 12;
    static final int COLOR_RAINBOW_ORANGE = COLOR_RAINBOW_RED + 1;
    static final int COLOR_RAINBOW_YELLOW = COLOR_RAINBOW_RED + 2;
    static final int COLOR_RAINBOW_GREEN = COLOR_RAINBOW_RED + 3;
    static final int COLOR_RAINBOW_BLUE = COLOR_RAINBOW_RED + 4;
    static final int COLOR_RAINBOW_INDIGO = COLOR_RAINBOW_RED + 5;
    static final int COLOR_RAINBOW_VIOLET = COLOR_RAINBOW_RED + 6;
    final int COLOR_RAINBOW_VIOLET_Color = 0xFFEE82EE;
        // ===============----- used for bonus item, rainbow colors -----===============
    static final int COLOR_DarkenBackground = 19;
        // ===============+++++ used for GameOverView/GameWinView +++++===============
    final int COLOR_WORD_GAMEOVER = 20;
    final int COLOR_WORD_RESTART = 21;
    final int COLOR_WORD_YOUWIN = 22;
    final int COLOR_WORD_NEXT = 23;
    final int COLOR_MARK_PAUSE = 24;
    static final int COLOR_MARK_START = 25;
    static final int COLOR_MARK_EXIT = 26;
        // ===============----- used for GameOverView/GameWinView -----===============
    final int COLOR_SCORE_LIFE_LINE = 27;
    int COLOR_SCORE = 28;
    final int COLOR_SCORE_DEFAULT = 0;
    final int COLOR_SCORE_MORE10000 = 1;
    final int COLOR_SCORE_MORE20000 = 2;
    final int COLOR_SCORE_MORE30000 = 3;
    final int COLOR_SCORE_MORE40000 = 4;
    final int COLOR_SCORE_MORE50000 = 5;
    int mColorScoreCurrent = 0;
    final int COLOR_MASK = 0x00FFFFFF; // For brick's setting loaded from file, filtering BLACK dots
    final int BACKGROUND = BLACK; // Background color = BLACK
    final int BOARD = LTGRAY; // Board color = LTGRAY
    final int BALL = WHITE; // Ball color = WHITE
    final int LIFE = RED; // Life color = RED
    final int HEART = RED; // HEART color = RED
    final int HINT_START = YELLOW; // HINT_START = YELLOW
    // ====================----- Item's color setting -----====================


    // ====================+++++ Default setting +++++====================
    // Screen walls
    static final int SCREEN_WALL_LEFT = 0;
    static final int SCREEN_WALL_TOP = 0;
    static int SCREEN_WALL_RIGHT; // Initial at constructor
    static int SCREEN_WALL_BOTTOM; // Initial at constructor
	
    // Dot View's walls
    int WALL_LEFT; // Left wall, Initial at constructor
    int WALL_TOP; // Top wall, Initial at constructor
    int WALL_RIGHT; // Right wall, Initial at constructor
    int WALL_BOTTOM; // Bottom wall, Initial at constructor

    final int BLOCK_NUMBER_ROW = mRes.getDimensionPixelSize(R.dimen.block_number_row);
    final int BLOCK_NUMBER_COLUMN = mRes.getDimensionPixelSize(R.dimen.block_number_column);
    static int BLOCK_WIDTH; // Initial at constructor
    static int BLOCK_HEIGHT; // Initial at constructor
    // ====================----- Default setting -----====================


    // ====================+++++ The settings for score +++++====================
    Numeric score; // Initial at constructor
    private int SCORE_BITS = 4;
    // ====================----- The settings for score -----====================


    // ====================+++++ The settings for life +++++====================
    private final int SCORE_ADD_LIFE_BONUS = 50;
    private final int MAX_LIFE = 20;
    private int tmp_score_add_life_check = 0;
    Numeric life;
    static int keep_life;
    private int LIFE_BITS = 2;
    final boolean[][] LIFE_HEART_FOR_LOOK = {
        {F,T,F,T,F},
        {T,T,T,T,T},
        {T,T,T,T,T},
        {F,T,T,T,F},
        {F,F,T,F,F}};
    final boolean[][] LIFE_HEART = toConvertMatrix(LIFE_HEART_FOR_LOOK);
    static final int KEEP_LIFE_INIT = 0;
    final int KEEP_LIFE_DEFAULT = 5;
    // ====================----- The settings for life -----====================

	
    // ====================+++++ The settings for bricks +++++====================
    private final int BRICKS_AREA_SIZE_HORIZONTAL = mRes.getDimensionPixelSize(R.dimen.bricks_area_size_horizontal);
    private final int BRICKS_AREA_SIZE_VERTICAL = mRes.getDimensionPixelSize(R.dimen.bricks_area_size_vertical);
    private final int BRICKS_AREA_LEFT;
    private final int BRICKS_AREA_TOP;
    private final int BRICKS_AREA_RIGHT;
    private final int BRICKS_AREA_BOTTOM;
    int bricks_shiftoffset_horizontal;
    int bricks_shiftoffset_vertical;
    int bricks_Current_Rectangle_Boundary_Left;
    int bricks_Current_Rectangle_Boundary_Right;
    int bricks_Current_Rectangle_Boundary_Top;
    int bricks_Current_Rectangle_Boundary_Bottom;
    BrickItem[][] aBricks = new BrickItem[BRICKS_AREA_SIZE_HORIZONTAL][BRICKS_AREA_SIZE_VERTICAL];
    // ====================----- The settings for bricks -----====================


    // ====================+++++ The settings for board +++++====================
    private Board mBoard;
    static int board_speed = 40; // Default board speed
    static float board_moveAngle = 0.4f;
    // ====================----- The settings for board -----====================
	
	
    // ====================+++++ The settings for ball +++++====================
    Ball[] aBall = new Ball[4];
    static int ball_speed = 70; // Default ball speed
    final int BALL_MOVE_UNIT_HORIZONTAL_POSITIVE;
    final int BALL_MOVE_UNIT_HORIZONTAL_NEGATIVE;
    final int BALL_MOVE_UNIT_VERTICAL_POSITIVE;
    final int BALL_MOVE_UNIT_VERTICAL_NEGATIVE;
    final int MAX_NUMBER_OF_BALLS = 4;
    int mNumber_of_balls_alive = 1; // The default number of ball is 1, a_ball[0].
    boolean mBall_alive; // Ball alive or dead
    int ball_travelled = 0;
    static final int KEEP_SCORE_INIT = -1;
    static protected int keep_score;
    // =================== =----- The settings for ball -----====================


    // ====================+++++ The settings for bonus items +++++====================
    protected BrickItem[] aBonus = new BrickItem[7];
    protected CountDownTimer[] aTimerBonus = new CountDownTimer[7];
    private CountDownTimer bonus_item_timer_change_color;
    private int MAX_NUMBER_OF_BONUS = 5; // Limit the existing bonus items
    private int number_of_bonus = 0; // Record the existing bonus items
    // private final int ZERO = 0;
    // private final int ONE = 1;
    // private final int TWO = 2;
    // private final int THREE= 3;
    private final int mBonusTypeBarGreen = 4;
    private final int mBonusTypeBarRed = 5;
    private final int mBonusTypeHeart = 6;

    private final int SPEED_BONUS = 100;

    private boolean mBonus_22YELLOW = false;
    private boolean mBonus_22RED = false;
    private boolean mBonus_22BLUE_change_color = false;
    private int mBonus_22BLUE = 1;


    boolean[][] FAILING_DOWN_HEART_FOR_LOOK = {
        {F,T,F,T,F},
        {T,T,T,T,T},
        {F,T,T,T,F},
        {F,F,T,F,F}};
    boolean[][] FAILING_DOWN_HEART = toConvertMatrix(FAILING_DOWN_HEART_FOR_LOOK);
    // ====================----- The settings for bonus items -----====================


    // ====================+++++ The settings for game +++++====================
    static boolean mode_setting = false; // mode_setting: false => normal mode, true => advance mode, default is false.
    static int brick_game_level; // Game level
    final int current_game_level;
    static boolean game_level_complete;
    static boolean game_over;
    boolean game_pause;
    static final int MAX_GAME_LEVEL = 20;
    // ====================----- The settings for game -----====================


    // ====================+++++ Others +++++====================
    Bitmap mBitmap4827;

    boolean start_animation = false;
    static int start_animation_number = 0;
    private int START_ANIMATION_LOC_X;
    private int START_ANIMATION_LOC_Y;
    static boolean[][][] START_ANIMATION = {
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,F,F,T}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {T,F,F,F,T}
        },
        {
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,T,F,T},
            {F,T,F,T,F},
            {T,F,F,F,T},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {F,F,T,F,F},
            {F,T,F,T,F},
            {T,F,F,F,T},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        },
        {
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F},
            {F,F,F,F,F}
        }};
    public static final String LOG_PREFIX = "[BrickView] ";
    // ====================----- Others -----====================
    
    
    // ====================+++++ test for P2 +++++====================
    // vibrate
    private Vibrator mVibrator;
    
    // sound
    SoundPool mSoundPool;
    int mSoundBallCatch;
    int mSoundBallHit;
    int mSoundBallMiss;
    int mSoundCatchBonusItemHeart;
    int mSoundCatchBonusItemBarGreen;
    int mSoundCatchBonusItemBarRed;
    // ====================+++++ test for P2 +++++====================


    public BrickView(Context context) {
        super(context);
        // TODO Auto-generated method stub
        
        // ====================+++++ test for P2 +++++====================
        mVibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        
        mSoundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        mSoundBallCatch = mSoundPool.load(context, R.raw.ballcatch, 1);
        mSoundBallHit = mSoundPool.load(context, R.raw.ballhit, 1);
        mSoundBallMiss = mSoundPool.load(context, R.raw.ballmiss, 1);
        mSoundCatchBonusItemHeart = mSoundPool.load(context, R.raw.catch_heart, 1);
        mSoundCatchBonusItemBarGreen = mSoundPool.load(context, R.raw.catch_greenbar, 1);
        mSoundCatchBonusItemBarRed = mSoundPool.load(context, R.raw.catch_redbar, 1);
        // ====================+++++ test for P2 +++++====================

        current_game_level = brick_game_level;
        init_margins();
        if (!set_static_variable) {
            init_dot_screen_size();
            init_mark_loc();
            init_Paint_color();
            set_static_variable = true;
        }
        // Reset score color.
        aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_default));

        // ====================+++++ Initial game level pattern +++++====================
        String game_level_string ="dot_breaker_level_" + String.format(Locale.US, "%03d", brick_game_level + 1) + ".png";
        mBitmap4827 = DotBreakerUtils.getBitmapFromAsset(context.getAssets(), game_level_string);

        ball_speed = GameActivity.preferencesGet("SpeedOfBall", 70);
        board_speed = GameActivity.preferencesGet("SpeedOfBoard", 40);
        board_moveAngle = GameActivity.preferencesGet("MoveAngleOfBoard", 0.4f);
        // ====================----- Initial game level pattern -----====================

        boolean isResumeGame = is_Resume_Game();
        if (isResumeGame) {
            game_pause = true;
        }

        // ====================+++++ score setting +++++====================
        int score_loc_x = mRes.getDimensionPixelSize(R.dimen.score_loc_x);
        int score_loc_y = mRes.getDimensionPixelSize(R.dimen.score_loc_y);
        if (isResumeGame) {
            score = new Numeric(GameActivity.preferencesGet("Current_Score", 0), WALL_LEFT + score_loc_x * BLOCK_WIDTH, WALL_TOP + score_loc_y * BLOCK_HEIGHT);
        } else {
            score = new Numeric(keep_score == KEEP_SCORE_INIT ? 0 : keep_score, WALL_LEFT + score_loc_x * BLOCK_WIDTH, WALL_TOP + score_loc_y * BLOCK_HEIGHT);
        }
        keep_score = score.mNumeric;
        // ====================----- score setting -----====================


        // ====================+++++ life setting +++++====================
        int life_loc_x = mRes.getDimensionPixelSize(R.dimen.life_loc_x);
        int life_loc_y = mRes.getDimensionPixelSize(R.dimen.life_loc_y);
        if (isResumeGame) {
            life = new Numeric(GameActivity.preferencesGet("Current_Life", 5), WALL_LEFT + life_loc_x * BLOCK_WIDTH, WALL_TOP + life_loc_y * BLOCK_HEIGHT);
        } else {
            life = new Numeric(keep_life <= KEEP_LIFE_INIT ? KEEP_LIFE_DEFAULT : keep_life, WALL_LEFT + life_loc_x * BLOCK_WIDTH, WALL_TOP + life_loc_y * BLOCK_HEIGHT);
        }
        keep_life = life.mNumeric;
        // ====================----- life setting -----====================


        // ====================+++++ initial bricks +++++======================
        BRICKS_AREA_LEFT = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.bricks_area_left) * BLOCK_WIDTH;
        BRICKS_AREA_TOP = WALL_TOP + mRes.getDimensionPixelSize(R.dimen.bricks_area_top) * BLOCK_HEIGHT;
        BRICKS_AREA_RIGHT = WALL_RIGHT - mRes.getDimensionPixelSize(R.dimen.bricks_area_right) * BLOCK_WIDTH;
        BRICKS_AREA_BOTTOM = WALL_BOTTOM - mRes.getDimensionPixelSize(R.dimen.bricks_area_bottom) * BLOCK_HEIGHT;
        bricks_initial(brick_game_level); // initial bricks
        // bricks_Current_Rectangle_Boundary_Initial(); // used in P2
        // ====================----- initial bricks -----======================


        // ====================+++++ initial board +++++====================
        int board_init_loc_left = mRes.getDimensionPixelSize(R.dimen.board_init_loc_left);
        int board_init_loc_top = mRes.getDimensionPixelSize(R.dimen.board_init_loc_top);
        int board_init_loc_right = mRes.getDimensionPixelSize(R.dimen.board_init_loc_right);
        int board_init_loc_bottom = mRes.getDimensionPixelSize(R.dimen.board_init_loc_bottom);
        int board_width = mRes.getDimensionPixelSize(R.dimen.board_width);
        if (isResumeGame) {
            mBoard = new Board(
                    true,
                    aPaint[BOARD],
                    GameActivity.preferencesGet("Current_Board_Left", WALL_LEFT + board_init_loc_left * BLOCK_WIDTH),
                    GameActivity.preferencesGet("Current_Board_Top", WALL_TOP + board_init_loc_top * BLOCK_HEIGHT),
                    GameActivity.preferencesGet("Current_Board_Right", WALL_LEFT + board_init_loc_right * BLOCK_WIDTH),
                    GameActivity.preferencesGet("Current_Board_Bottom", WALL_TOP + board_init_loc_bottom * BLOCK_HEIGHT),
                    GameActivity.preferencesGet("Current_Board_Width", board_width * BLOCK_WIDTH),
                    GameActivity.preferencesGet("Current_Board_MoveUnitHorizontal", BLOCK_WIDTH));
        } else {
            mBoard = new Board(
                    true,
                    aPaint[BOARD],
                    WALL_LEFT + board_init_loc_left * BLOCK_WIDTH,
                    WALL_TOP + board_init_loc_top * BLOCK_HEIGHT,
                    WALL_LEFT + board_init_loc_right * BLOCK_WIDTH,
                    WALL_TOP + board_init_loc_bottom * BLOCK_HEIGHT,
                    board_width * BLOCK_WIDTH,
                    BLOCK_WIDTH);
        }
        // ====================----- initial board -----====================


        // ====================+++++ initial ball +++++====================
        BALL_MOVE_UNIT_HORIZONTAL_POSITIVE = 1 * BLOCK_WIDTH; // ball move right
        BALL_MOVE_UNIT_HORIZONTAL_NEGATIVE = -1 * BLOCK_WIDTH; // ball move left
        BALL_MOVE_UNIT_VERTICAL_POSITIVE = 1 * BLOCK_HEIGHT; // ball move down
        BALL_MOVE_UNIT_VERTICAL_NEGATIVE = -1 * BLOCK_HEIGHT; // ball move up
        int ball_width = mRes.getDimensionPixelSize(R.dimen.ball_width);
        int ball_height = mRes.getDimensionPixelSize(R.dimen.ball_height);
        if (isResumeGame) {
            for (int ballNum = 0; ballNum < MAX_NUMBER_OF_BALLS; ++ballNum) {
                if (GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Exist", false)) {
                    aBall[ballNum] = new Ball(	
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Exist", false), aPaint[BALL], 
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Left", mBoard.mLeft + mBoard.mWidth / 2 + ball_width / 2 * BLOCK_WIDTH),
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Top", mBoard.mTop - ball_height * BLOCK_HEIGHT),
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Right", mBoard.mLeft + mBoard.mWidth / 2 + ball_width / 2 * BLOCK_WIDTH + ball_width * BLOCK_WIDTH),
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_Bottom", mBoard.mTop),
                            GameActivity.preferencesGet("Current_Ball_Width", ball_width * BLOCK_WIDTH),
                            GameActivity.preferencesGet("Current_Ball_Height", ball_height * BLOCK_HEIGHT),
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_MoveUnitHorizontal", BALL_MOVE_UNIT_HORIZONTAL_POSITIVE),
                            GameActivity.preferencesGet("Current_Ball[" + ballNum + "]_MoveUnitVertical", BALL_MOVE_UNIT_VERTICAL_NEGATIVE));
                }
            }
            mBall_alive = GameActivity.preferencesGet("Current_Ball_Alive", false);
        } else {
            aBall[0] = new Ball(	
                    true, 
                    aPaint[BALL], 
                    mBoard.mLeft + mBoard.mWidth / 2 + ball_width / 2 * BLOCK_WIDTH, 
                    mBoard.mTop - ball_height * BLOCK_HEIGHT, 
                    mBoard.mLeft + mBoard.mWidth / 2 + ball_width / 2 * BLOCK_WIDTH + ball_width * BLOCK_WIDTH, 
                    mBoard.mTop,
                    ball_width * BLOCK_WIDTH,
                    ball_height * BLOCK_HEIGHT,
                    BALL_MOVE_UNIT_HORIZONTAL_POSITIVE,
                    BALL_MOVE_UNIT_VERTICAL_NEGATIVE);
            mBall_alive = false;
        }
        // ====================----- initial ball -----====================


        // ====================+++++ initial others +++++====================
        START_ANIMATION_LOC_X = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.start_animation_loc_x) * BLOCK_WIDTH;
        START_ANIMATION_LOC_Y = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.start_animation_loc_y) * BLOCK_HEIGHT;
        // ====================----- initial others -----====================
		gameStart();
	}

    private void init_margins() {
        WALL_LEFT = inner_frame_margin_left;
        WALL_TOP = inner_frame_margin_top;
        WALL_RIGHT = inner_frame_margin_left + inner_frame_width;
        WALL_BOTTOM = inner_frame_margin_top + inner_frame_height;
    }

    private void init_dot_screen_size() {
        BLOCK_WIDTH = dot_pixel_width;
        BLOCK_HEIGHT = dot_pixel_height;
        SCREEN_WALL_RIGHT = inner_frame_margin_left + inner_frame_width + inner_frame_margin_right;
        SCREEN_WALL_BOTTOM = inner_frame_margin_top + inner_frame_height + inner_frame_margin_bottom;
    }

    private void init_mark_loc() {
        MARK_PAUSE_LOC_X = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.mark_pause_loc_x) * BLOCK_WIDTH;
        MARK_PAUSE_LOC_Y = WALL_TOP + mRes.getDimensionPixelSize(R.dimen.mark_pause_loc_y) * BLOCK_HEIGHT;
        MARK_START_LOC_X = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.mark_start_loc_x) * BLOCK_WIDTH;
        MARK_START_LOC_Y = WALL_TOP + mRes.getDimensionPixelSize(R.dimen.mark_start_loc_y) * BLOCK_HEIGHT;
        MARK_EXIT_LOC_X = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.mark_exit_loc_x) * BLOCK_WIDTH;
        MARK_EXIT_LOC_Y = WALL_TOP + mRes.getDimensionPixelSize(R.dimen.mark_exit_loc_y) * BLOCK_HEIGHT;
        HINT_ANIMATION_LOC_X = WALL_LEFT + mRes.getDimensionPixelSize(R.dimen.hint_animation_loc_x) * BLOCK_WIDTH;
        HINT_ANIMATION_LOC_Y = WALL_TOP + mRes.getDimensionPixelSize(R.dimen.hint_animation_loc_y) * BLOCK_HEIGHT;
    }

    private void init_Paint_color() {
        for (int i = 0; i < aPaint.length; i++) {
            aPaint[i] = new Paint();
        }
        aPaint[BLACK].setColor(Color.BLACK);
        aPaint[BLUE].setColor(Color.BLUE);
        aPaint[CYAN].setColor(Color.CYAN);
        aPaint[DKGRAY].setColor(Color.DKGRAY);
        aPaint[GRAY].setColor(Color.GRAY);
        aPaint[GREEN].setColor(Color.GREEN);
        aPaint[LTGRAY].setColor(Color.LTGRAY);
        aPaint[MAGENTA].setColor(Color.MAGENTA);
        aPaint[RED].setColor(Color.RED);
        aPaint[TRANSPARENT].setColor(Color.TRANSPARENT);
        aPaint[WHITE].setColor(Color.WHITE);
        aPaint[YELLOW].setColor(Color.YELLOW);
        // ==================== for bonus change color ===========================
        aPaint[COLOR_RAINBOW_RED] = aPaint[RED];
        aPaint[COLOR_RAINBOW_ORANGE].setColor(mRes.getColor(R.color.rainbow_orange));
        aPaint[COLOR_RAINBOW_YELLOW] = aPaint[YELLOW];
        aPaint[COLOR_RAINBOW_GREEN] = aPaint[GREEN];
        aPaint[COLOR_RAINBOW_BLUE] = aPaint[BLUE];
        aPaint[COLOR_RAINBOW_INDIGO].setColor(mRes.getColor(R.color.rainbow_indigo));
        aPaint[COLOR_RAINBOW_VIOLET].setColor(mRes.getColor(R.color.rainbow_violet));
        // ==================== for bonus change color ===========================
        aPaint[COLOR_DarkenBackground].setColor(mRes.getColor(R.color.darken_background)); // Darken the background, for game pause/win/lose.
        aPaint[COLOR_WORD_GAMEOVER].setColor(mRes.getColor(R.color.word_gameover));
        aPaint[COLOR_WORD_RESTART].setColor(mRes.getColor(R.color.word_restart));
        aPaint[COLOR_WORD_YOUWIN].setColor(mRes.getColor(R.color.word_youwin));
        aPaint[COLOR_WORD_NEXT].setColor(mRes.getColor(R.color.word_next));
        aPaint[COLOR_MARK_PAUSE].setColor(mRes.getColor(R.color.mark_pause));
        aPaint[COLOR_MARK_START].setColor(mRes.getColor(R.color.mark_start));
        aPaint[COLOR_MARK_EXIT].setColor(mRes.getColor(R.color.mark_exit));
        aPaint[COLOR_SCORE_LIFE_LINE].setColor(mRes.getColor(R.color.score_life_line));
        aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_default));
    }

    private boolean is_Resume_Game() {
        if (GameActivity.preferencesGet("Current_GameLevel", -1) == brick_game_level
                && GameActivity.preferencesGet("Current_GameState", false)) {
            return true;
        }
        return false;
    }

    private void gameStart() {
        startMove();
        playTime_timer.start();
    }

    private void startMove() {
        boolean isResumeGame = is_Resume_Game();
        mTimerBoard = new Timer();
        if (!isResumeGame) {
            mTimerTaskBoard = board_TimerTask_renew();
            mTimerBoard.schedule(mTimerTaskBoard, 0, BrickView.board_speed);
        }

        mTimerBall = new Timer();
        if (!isResumeGame) {
            mTimerTaskBall = ball_TimerTask_renew();
            mTimerBall.schedule(mTimerTaskBall, 0, BrickView.ball_speed);
        }
        /*
        // move of bricks
        if (move_setting) {
            a_bar_timer = new CountDownTimer(3000, 400) {

                public void onTick(long millisUntilFinished) {
                    if (BrickView.game_over) {
                        return;
                    }
                    a_Brick.bricks_move();
                    a_Brick.invalidate();
                }

                public void onFinish() {
                    this.start(); // re-countdown
                }
            }.start();
        }
         */
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // canvas.drawRect(SCREEN_WALL_LEFT, SCREEN_WALL_TOP, SCREEN_WALL_RIGHT, SCREEN_WALL_BOTTOM, sPaint[BACKGROUND]); // Draw background (BLACK)
        onDrawScore(canvas, score.mLoc_x, score.mLoc_y, score.mNumeric); // Draw score
        // onDrawMatrix(canvas, LIFE_HEART, LIFE_HEART_LOC_X, LIFE_HEART_LOC_Y, HEART); // Draw LIFE_HEART (RED)
        onDrawLife(canvas, life.mLoc_x, life.mLoc_y, life.mNumeric);
        canvas.drawRect(WALL_LEFT, BRICKS_AREA_TOP - BLOCK_HEIGHT, WALL_RIGHT, BRICKS_AREA_TOP, aPaint[COLOR_SCORE_LIFE_LINE]);
        canvas.drawRect(mBoard.mLeft, mBoard.mTop, mBoard.mRight, mBoard.mBottom, mBoard.mPaint); // Draw board (LTGRAY)
        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) { // Draw ball(s) (WHITE)
            if (aBall[i] != null && aBall[i].mExist != false)
                canvas.drawRect(aBall[i].mLeft, aBall[i].mTop, aBall[i].mRight, aBall[i].mBottom, aBall[i].mPaint);
        }

        if (number_of_bonus > 0 && number_of_bonus <= MAX_NUMBER_OF_BONUS) { // Draw bonus item(s)
            for (int i = 0; i < aBonus.length - 1; ++i) {
                if (aBonus[i] == null)
                    continue;
                canvas.drawRect(aBonus[i].mLeft, aBonus[i].mTop, aBonus[i].mRight, aBonus[i].mBottom, aBonus[i].mPaint);
            }
            if (aBonus[6] != null && aBonus[6].mPaint == aPaint[RED]) {
                for (int row = 0; row < FAILING_DOWN_HEART.length; ++row) {
                    for (int column = 0; column < FAILING_DOWN_HEART[0].length; ++column) {
                        if (FAILING_DOWN_HEART[row][column] == true) {
                            canvas.drawRect(aBonus[6].mLeft + row * BLOCK_WIDTH,
                                            aBonus[6].mTop + column * BLOCK_HEIGHT,
                                            aBonus[6].mLeft + (row + 1) * BLOCK_WIDTH,
                                            aBonus[6].mTop + (column + 1) * BLOCK_HEIGHT,
                                            aPaint[RED]);
                        }
                    }
                }
            }
        }

        for (int row = 0; row < BRICKS_AREA_SIZE_HORIZONTAL; ++row) {
			for (int column = 0 ; column < BRICKS_AREA_SIZE_VERTICAL ; ++column) {
                if (aBricks[row][column].mExist == true) { // Draw bricks (normal color)
                    canvas.drawRect(aBricks[row][column].mLeft, 
                                    aBricks[row][column].mTop,
                                    aBricks[row][column].mRight,
                                    aBricks[row][column].mBottom,
                                    aBricks[row][column].mPaint);
                }
            }
        }

        if (start_animation) {
            onDrawMatrix(canvas, toConvertMatrix(START_ANIMATION[start_animation_number]), START_ANIMATION_LOC_X, START_ANIMATION_LOC_Y, aPaint[HINT_START]); // Draw start animation
        } else if (game_pause) {
            canvas.drawRect(SCREEN_WALL_LEFT, SCREEN_WALL_TOP, SCREEN_WALL_RIGHT, SCREEN_WALL_BOTTOM, aPaint[COLOR_DarkenBackground]);	
            if (hint_animation) {
                if (hint_animation_number < 5) {
                    onDrawMatrix(canvas, MARK_START, MARK_START_LOC_X, MARK_START_LOC_Y, aPaint[COLOR_MARK_START]);
                    onDrawMatrix(canvas, toConvertMatrix(HINT_ANIMATION[hint_animation_number]), HINT_ANIMATION_LOC_X, HINT_ANIMATION_LOC_Y, aPaint[COLOR_MARK_START]); // Draw hint animation (YELLOW/RED)
                } else {
                    onDrawMatrix(canvas, MARK_EXIT, MARK_EXIT_LOC_X, MARK_EXIT_LOC_Y, aPaint[COLOR_MARK_EXIT]);
                    onDrawMatrix(canvas, toConvertMatrix(HINT_ANIMATION[hint_animation_number]), HINT_ANIMATION_LOC_X, HINT_ANIMATION_LOC_Y, aPaint[COLOR_MARK_EXIT]); // Draw hint animation (YELLOW/RED)
                }
            } else {
                onDrawMatrix(canvas, MARK_PAUSE, MARK_PAUSE_LOC_X, MARK_PAUSE_LOC_Y, aPaint[COLOR_MARK_PAUSE]); // Draw the mark of PAUSE (YELLOW)
            }
        }
    }

    protected void onDrawLife(Canvas canvas, int life_loc_x, int life_loc_y, int life) {
        if (life < 0) {
            Log.d(GameLevel.LOG_TAG, LOG_PREFIX + "onDrawLife	life < 0.	life=" + life);
            life = 0;
        }
        int showNumber = 0;
        for (int i = 0; i < LIFE_BITS; ++i) {
            showNumber = life % 10;
            onDrawMatrix(canvas, toConvertMatrix(aNumeric[showNumber]), life_loc_x, life_loc_y, aPaint[LIFE]);
            life = life / 10;
            life_loc_x = life_loc_x - 4 * BLOCK_WIDTH;
        }
    }

    protected void onDrawScore(Canvas canvas, int score_loc_x, int score_loc_y, int score) {
        if (score < 0) {
            Log.d(GameLevel.LOG_TAG, LOG_PREFIX + "onDrawScore	score < 0.	score=" + score);
            score = 0;
        }
        int showNumber = 0;
        int updateScoreColor = score / 10000;
        if (mColorScoreCurrent != updateScoreColor) {
            switch (updateScoreColor) {
                case 0:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_default));
                    break;
                case 1:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_more10000));
                    break;
                case 2:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_more20000));
                    break;
                case 3:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_more30000));
                    break;
                case 4:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_more40000));
                    break;
                case 5:
                default:
                    aPaint[COLOR_SCORE].setColor(mRes.getColor(R.color.score_more50000));
                    break;
            }
            mColorScoreCurrent = updateScoreColor;
        }
        for (int i = 0; i < SCORE_BITS; ++i) {
            showNumber = score % 10;
            onDrawMatrix(canvas, toConvertMatrix(aNumeric[showNumber]), score_loc_x, score_loc_y, aPaint[COLOR_SCORE]);
            score = score / 10;
            score_loc_x = score_loc_x - 4 * BLOCK_WIDTH;
        }
    }

    static protected void onDrawMatrix(Canvas canvas, boolean[][] matrix, int loc_x, int loc_y, Paint paint) {
        for (int row = 0; row < matrix.length; ++row) {
            for (int column = 0; column < matrix[row].length; ++column) {
                if (matrix[row][column])
                    canvas.drawRect(loc_x + row * BLOCK_WIDTH,
                                    loc_y + column * BLOCK_HEIGHT,
                                    loc_x + (row + 1) * BLOCK_WIDTH,
                                    loc_y + (column + 1) * BLOCK_HEIGHT, 
                                    paint);
            }
        }
    }

    static protected boolean[][] toConvertMatrix(boolean[][] matrix) {
        boolean[][] temp = new boolean[matrix[0].length][matrix.length];
        for (int row = 0; row < matrix.length; ++row) {
            for (int column = 0; column < matrix[row].length; ++column) {
                temp[column][row] = matrix[row][column];
            }
        }
        return temp;
    }

    private void ball_initial() {
        aBall[0].mWidth = mRes.getDimensionPixelSize(R.dimen.ball_width) * BLOCK_WIDTH;
        aBall[0].mHeight = mRes.getDimensionPixelSize(R.dimen.ball_height) * BLOCK_WIDTH;
        mBonus_22YELLOW = false;
        mBall_alive = false;
        for (int i = 1; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null && aBall[i].mExist != false)
                aBall[i].mExist = false;
        }
        aBall[0].mExist = true;
        ball_move_up(0);
        mNumber_of_balls_alive = 1;
        ball_reset_location();
    }

    private void ball_reset_location() {
        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null && aBall[i].mExist != false) {
                aBall[i].mLeft = mBoard.mLeft + ((mBoard.mWidth / BLOCK_WIDTH) / 2) * BLOCK_WIDTH - ((aBall[i].mWidth / BLOCK_WIDTH) / 2) * BLOCK_WIDTH;
                aBall[i].mRight = aBall[i].mLeft + aBall[i].mWidth;
                aBall[i].mTop = mBoard.mTop - aBall[i].mHeight;
                aBall[i].mBottom = mBoard.mTop;
            }
        }
    }

    private void board_initial() {
        mBoard.mWidth = mRes.getDimensionPixelSize(R.dimen.board_width) * BLOCK_WIDTH;
    }

    private void bricks_initial(int level) {
        /*
        // move of bricks, P2
        if (BrickActivity.move_setting)
            bricks_Current_Rectangle_Boundary_Initial();
         */
        if (is_Resume_Game()) {
            int tmp_Loc_Left = GameActivity.preferencesGet("Current_Bricks_Location_Left", BRICKS_AREA_LEFT);
            int tmp_Loc_Top = GameActivity.preferencesGet("Current_Bricks_Location_Top", BRICKS_AREA_TOP);
            int tmp_Width = GameActivity.preferencesGet("Current_Bricks_Width", BLOCK_WIDTH);
            int tmp_Height = GameActivity.preferencesGet("Current_Bricks_Height", BLOCK_HEIGHT);
            for (int row = 0; row < BRICKS_AREA_SIZE_HORIZONTAL; ++row) {
                for (int column = 0; column < BRICKS_AREA_SIZE_VERTICAL; ++column) {
                    Paint paint = new Paint();
                    paint.setColor(GameActivity.preferencesGet("Current_Bricks[" + row + "][" + column + "]_PaintColor", Color.BLACK));
                    aBricks[row][column] = new BrickItem(	
                            GameActivity.preferencesGet("Current_Bricks[" + row + "][" + column + "]_Exist", true),
                            paint,
                            tmp_Loc_Left+row*tmp_Width,
                            tmp_Loc_Top+column*tmp_Height,
                            tmp_Loc_Left+(row+1)*tmp_Width,
                            tmp_Loc_Top+(column+1)*tmp_Height);
                }
            }
        } else {
            int color = Color.BLACK;
            for (int row = 0; row < BRICKS_AREA_SIZE_HORIZONTAL; ++row) {
                for (int column = 0; column < BRICKS_AREA_SIZE_VERTICAL; ++column) {
                    if (mBitmap4827 != null)
                        color = mBitmap4827.getPixel(row, column + 7);
                    Paint paint = new Paint();
                    paint.setColor(color);
                    aBricks[row][column] = new BrickItem(	
                            true,
                            paint,
                            BRICKS_AREA_LEFT+row*BLOCK_WIDTH,
                            BRICKS_AREA_TOP+column*BLOCK_HEIGHT,
                            BRICKS_AREA_LEFT+(row+1)*BLOCK_WIDTH,
                            BRICKS_AREA_TOP+(column+1)*BLOCK_HEIGHT);
    
                    if ((color & COLOR_MASK) == 0)
                        aBricks[row][column].mExist = false;
                }
            }
        }
    }
    
    /*
    // move of bricks, P2
    private void bricks_Current_Rectangle_Boundary_Initial() {
        Log.d("Alvis", "bricks_Current_Rectangle_Boundary_Initial()	begin.");
        bricks_shiftoffset_horizontal = 0;
        bricks_shiftoffset_vertical = 0;
        bricks_Current_Rectangle_Boundary_Left = 0;
        bricks_Current_Rectangle_Boundary_Top = 0;
        bricks_Current_Rectangle_Boundary_Right = a_bricks.length - 1;
        bricks_Current_Rectangle_Boundary_Bottom = a_bricks[0].length - 1;
        Log.d("Alvis", "bricks_Current_Rectangle_Boundary_Initial()	end.");
    }

    protected void bricks_move() {
        Random randon = new Random();
        int move_horizontal = randon.nextInt(3) - 1; // -1:left 0:no move 1:right
        int move_vertical = randon.nextInt(3) - 1; // -1:top 0:no move 1:bottom

        if (check_bricks_move_with_ball(move_horizontal, move_vertical))
            return;

        if (move_horizontal == -1) {
            move_horizontal = find_Bricks_Current_Rectangle_Boundary_Left(bricks_Current_Rectangle_Boundary_Left, move_horizontal);
            if (bricks_Current_Rectangle_Boundary_Left + bricks_shiftoffset_horizontal > 0) {
                --bricks_shiftoffset_horizontal;
            }
        } else if (move_horizontal == 1) {
            move_horizontal = find_Bricks_Current_Rectangle_Boundary_Right(bricks_Current_Rectangle_Boundary_Right, move_horizontal);
            if (bricks_Current_Rectangle_Boundary_Right + bricks_shiftoffset_horizontal < a_bricks.length - 1) {
                ++bricks_shiftoffset_horizontal;
            }
        }

        if (move_vertical == -1) {
            move_vertical = find_Bricks_Current_Rectangle_Boundary_Top(bricks_Current_Rectangle_Boundary_Top, move_vertical);
            if (bricks_Current_Rectangle_Boundary_Top + bricks_shiftoffset_vertical > 0) {
                --bricks_shiftoffset_vertical;
            }
        } else if (move_vertical == 1) {
            move_vertical = find_Bricks_Current_Rectangle_Boundary_Bottom(bricks_Current_Rectangle_Boundary_Bottom, move_vertical);
            if (bricks_Current_Rectangle_Boundary_Bottom + bricks_shiftoffset_vertical < a_bricks[0].length - 1) {
                ++bricks_shiftoffset_vertical;
            }
        }

        bricks_repositioning(move_horizontal, move_vertical);
        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null && aBall[i].mExist != false)
                check_touch_ball_and_bricks(i);
        }
    }

    private boolean check_bricks_move_with_ball(int horizontal, int vertical) {
        for (int row = 0; row < a_bricks.length; ++row) {
            for (int column = 0; column < a_bricks[row].length; ++column) {
                for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
                    if (aBall[i] != null && aBall[i].mExist != false) {
                        if (a_bricks[row][column].mLeft + horizontal * BLOCK_WIDTH == aBall[i].mLeft
                                && a_bricks[row][column].mTop + vertical * BLOCK_HEIGHT == aBall[i].mTop
                                && a_bricks[row][column].mExist == true) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void bricks_repositioning(int horizontal, int vertical) {
        for (int row = 0; row < a_bricks.length; ++row) {
            for (int column = 0; column < a_bricks[row].length; ++column) {
                a_bricks[row][column].mLeft += horizontal * BLOCK_WIDTH;
                a_bricks[row][column].mRight += horizontal * BLOCK_WIDTH;
                a_bricks[row][column].mTop += vertical * BLOCK_HEIGHT;
                a_bricks[row][column].mBottom += vertical * BLOCK_HEIGHT;
            }
        }
    }

    private int find_Bricks_Current_Rectangle_Boundary_Left(int left, int horizontal) {
        for (int row = left; row <= a_bricks.length - 1; ++row) {
            for (int column = 0; column < a_bricks[row].length; ++column) {
                if (a_bricks[row][column].mExist == true) {
                    bricks_Current_Rectangle_Boundary_Left = row;
                    if (a_bricks[row][column].mLeft <= BRICKS_AREA_LEFT)
                        return 0;
                    return horizontal;
                }
            }
        }
        return 0;
    }

    private int find_Bricks_Current_Rectangle_Boundary_Right(int right, int horizontal) {
        for (int row = right; row >= 0; --row) {
            for (int column = 0; column < a_bricks[row].length; ++column) {
                if (a_bricks[row][column].mExist == true) {
                    bricks_Current_Rectangle_Boundary_Right = row;
                    if (a_bricks[row][column].mRight >= BRICKS_AREA_RIGHT)
                        return 0;
                    return horizontal;
                }
            }
        }
        return 0;
    }

    private int find_Bricks_Current_Rectangle_Boundary_Top(int top, int vertical) {
        for (int column = top; column <= a_bricks[0].length - 1; ++column) {
            for (int row = 0; row < a_bricks.length; ++row) {
                if (a_bricks[row][column].mExist == true) {
                    bricks_Current_Rectangle_Boundary_Top = column;
                    if (a_bricks[row][column].mTop <= BRICKS_AREA_TOP)
                        return 0;
                    return vertical;
                }
            }
        }
        return 0;
    }

    private int find_Bricks_Current_Rectangle_Boundary_Bottom(int bottom, int vertical) {
        for (int column = bottom; column >= 0; --column) {
            for (int row = 0; row < a_bricks.length; ++row) {
                if (a_bricks[row][column].mExist == true) {
                    bricks_Current_Rectangle_Boundary_Bottom = column;
                    if (a_bricks[row][column].mBottom >= BRICKS_AREA_BOTTOM)
                        return 0;
                    return vertical;
                }
            }
        }
        return 0;
    }
    */

    private void ball_move_up(int index_of_ball) {
        aBall[index_of_ball].mMoveUnitVertical = BALL_MOVE_UNIT_VERTICAL_NEGATIVE;
    }

    private void ball_move_down(int index_of_ball) {
        aBall[index_of_ball].mMoveUnitVertical = BALL_MOVE_UNIT_VERTICAL_POSITIVE;
    }

    private void ball_move_left(int index_of_ball) {
        aBall[index_of_ball].mMoveUnitHorizontal = BALL_MOVE_UNIT_HORIZONTAL_NEGATIVE;
    }

    private void ball_move_right(int index_of_ball) {
        aBall[index_of_ball].mMoveUnitHorizontal = BALL_MOVE_UNIT_HORIZONTAL_POSITIVE;
    }

    private void check_touch_ball_and_board(int index_of_ball) {
        if (aBall[index_of_ball].mBottom == mBoard.mTop) { // ball's height is high one block than board's height
            if (aBall[index_of_ball].mRight >= mBoard.mLeft && aBall[index_of_ball].mLeft <= mBoard.mRight) {
                ball_move_up(index_of_ball);
                
                // ====================+++++ test for P2 +++++====================
                if (mBall_alive == true) {
                    if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
                        mSoundPool.play(this.mSoundBallCatch, 1f, 1f, 0, 0, 1);
                    }
                    if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
                        mVibrator.vibrate(100);
                    }
                }
                // ====================+++++ test for P2 +++++====================
            }
            if (aBall[index_of_ball].mRight == mBoard.mLeft || aBall[index_of_ball].mRight == mBoard.mLeft + BLOCK_WIDTH)
                ball_move_left(index_of_ball);
            if (aBall[index_of_ball].mLeft == mBoard.mRight || aBall[index_of_ball].mLeft == mBoard.mRight - BLOCK_WIDTH)
                ball_move_right(index_of_ball);
        } else if (aBall[index_of_ball].mBottom == mBoard.mBottom || aBall[index_of_ball].mBottom == mBoard.mBottom - BLOCK_HEIGHT) { // ball's height is equal board's height
            if (aBall[index_of_ball].mRight == mBoard.mLeft)
                ball_move_left(index_of_ball);
            else if (aBall[index_of_ball].mLeft == mBoard.mRight)
                ball_move_right(index_of_ball);
            if (aBall[index_of_ball].mLeft >= mBoard.mLeft && aBall[index_of_ball].mLeft <= mBoard.mLeft + 2 * BLOCK_WIDTH) {
                mBoard.mLeft = aBall[index_of_ball].mRight;
                ball_move_left(index_of_ball);
            } else if (aBall[index_of_ball].mLeft >= mBoard.mLeft + 3 * BLOCK_WIDTH && aBall[index_of_ball].mLeft <= mBoard.mLeft + 5 * BLOCK_WIDTH) {
                mBoard.mLeft = aBall[index_of_ball].mLeft - mBoard.mWidth;
                ball_move_right(index_of_ball);
            }
        }
        check_touch_ball_and_wall(index_of_ball); // check touch between ball and wall
    }

    private void check_touch_ball_and_wall(int index_of_ball) {
        if (aBall[index_of_ball].mLeft <= BRICKS_AREA_LEFT && aBall[index_of_ball].mMoveUnitHorizontal < 0) // left wall
            ball_move_right(index_of_ball);
        else if (aBall[index_of_ball].mRight >= BRICKS_AREA_RIGHT && aBall[index_of_ball].mMoveUnitHorizontal > 0) // right wall
            ball_move_left(index_of_ball);
        if (aBall[index_of_ball].mTop <= BRICKS_AREA_TOP && aBall[index_of_ball].mMoveUnitVertical < 0) // top wall
            ball_move_down(index_of_ball);
        else if (aBall[index_of_ball].mTop >= WALL_BOTTOM) { // bottom wall
            --mNumber_of_balls_alive;
            aBall[index_of_ball].mExist = false;

            if (mNumber_of_balls_alive == 0) {
                board_initial();
                ball_initial();
                keep_life = --life.mNumeric;
                if (life.mNumeric <= 0)
                    game_over = true;
            }
            // ====================+++++ test for P2 +++++====================
            if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
                mSoundPool.play(this.mSoundBallMiss, 1, 1, 0, 0, 1);
            }
            // ====================+++++ test for P2 +++++====================
        }
    }

    private void check_touch_ball_and_bricks(int index_of_ball) {
        for (int row = 0; row < aBricks.length; ++row) {
            for (int column = 0; column < aBricks[row].length; ++column) {
                if (aBricks[row][column].mExist == false)
                    continue;
                if (aBall[index_of_ball].mRight == aBricks[row][column].mLeft
                        && aBall[index_of_ball].mTop < aBricks[row][column].mBottom
                        && aBall[index_of_ball].mBottom > aBricks[row][column].mTop) { // ball touch brick from left side
                    ball_touch_brick(index_of_ball, row, column);
                } else if (aBall[index_of_ball].mLeft == aBricks[row][column].mRight
                        && aBall[index_of_ball].mTop < aBricks[row][column].mBottom
                        && aBall[index_of_ball].mBottom > aBricks[row][column].mTop) { // ball touch brick from right side
                    ball_touch_brick(index_of_ball, row, column);
                } else if (aBall[index_of_ball].mBottom == aBricks[row][column].mTop
                        && aBall[index_of_ball].mLeft < aBricks[row][column].mRight
                        && aBall[index_of_ball].mRight > aBricks[row][column].mLeft) { // ball touch brick from top side
                    ball_touch_brick(index_of_ball, row, column);
                } else if (aBall[index_of_ball].mTop == aBricks[row][column].mBottom
                        && aBall[index_of_ball].mLeft < aBricks[row][column].mRight
                        && aBall[index_of_ball].mRight > aBricks[row][column].mLeft) { // ball touch brick from bottom side
                    ball_touch_brick(index_of_ball, row, column);
                } else if (aBall[index_of_ball].mBottom == aBricks[row][column].mTop
                        && aBall[index_of_ball].mRight == aBricks[row][column].mLeft
                        && aBall[index_of_ball].mMoveUnitHorizontal == BALL_MOVE_UNIT_HORIZONTAL_POSITIVE
                        && aBall[index_of_ball].mMoveUnitVertical == BALL_MOVE_UNIT_VERTICAL_POSITIVE
                        && ((row - 1 < 0) || (row - 1 >= 0 && aBricks[row - 1][column].mExist == false))
                        && ((column - 1 < 0) || (column - 1 >= 0 && aBricks[row][column - 1].mExist == false))) { // ball touch brick from the left-top corner
                    ball_touch_brick(index_of_ball, row, column);
                    if (aBall[index_of_ball].mLeft <= WALL_LEFT) // left wall
                        ball_move_right(index_of_ball);
                    if (aBall[index_of_ball].mTop <= WALL_TOP) // top wall
                        ball_move_down(index_of_ball);
                } else if (aBall[index_of_ball].mBottom == aBricks[row][column].mTop
                        && aBall[index_of_ball].mLeft == aBricks[row][column].mRight
                        && aBall[index_of_ball].mMoveUnitHorizontal == BALL_MOVE_UNIT_HORIZONTAL_NEGATIVE
                        && aBall[index_of_ball].mMoveUnitVertical == BALL_MOVE_UNIT_VERTICAL_POSITIVE
                        && ((row + 1 == aBricks.length) || (row + 1 < aBricks.length && aBricks[row + 1][column].mExist == false))
                        && ((column - 1 < 0) || (column - 1 >= 0 && aBricks[row][column - 1].mExist == false))) { // ball touch brick from the right-top corner
                    ball_touch_brick(index_of_ball, row, column);
                    if (aBall[index_of_ball].mRight >= WALL_RIGHT) // right wall
                        ball_move_left(index_of_ball);
                    if (aBall[index_of_ball].mTop <= WALL_TOP) // top wall
                        ball_move_down(index_of_ball);
                } else if (aBall[index_of_ball].mTop == aBricks[row][column].mBottom
                        && aBall[index_of_ball].mRight == aBricks[row][column].mLeft
                        && aBall[index_of_ball].mMoveUnitHorizontal == BALL_MOVE_UNIT_HORIZONTAL_POSITIVE
                        && aBall[index_of_ball].mMoveUnitVertical == BALL_MOVE_UNIT_VERTICAL_NEGATIVE
                        && ((row - 1 < 0) || (row - 1 >= 0 && aBricks[row - 1][column].mExist == false))
                        && ((column + 1 == aBricks[row].length) || (column + 1 < aBricks[row].length && aBricks[row][column + 1].mExist == false))) { // ball touch brick from the left-bottom corner
                    ball_touch_brick(index_of_ball, row, column);
                    if (aBall[index_of_ball].mLeft <= WALL_LEFT) // left wall
                        ball_move_right(index_of_ball);
                    if (aBall[index_of_ball].mBottom >= WALL_BOTTOM) // bottom wall
                        ball_move_up(index_of_ball);
                } else if (aBall[index_of_ball].mTop == aBricks[row][column].mBottom
                        && aBall[index_of_ball].mLeft == aBricks[row][column].mRight
                        && aBall[index_of_ball].mMoveUnitHorizontal == BALL_MOVE_UNIT_HORIZONTAL_NEGATIVE
                        && aBall[index_of_ball].mMoveUnitVertical == BALL_MOVE_UNIT_VERTICAL_NEGATIVE
                        && ((row + 1 == aBricks.length) || (row + 1 < aBricks.length && aBricks[row + 1][column].mExist == false))
                        && ((column + 1 == aBricks[row].length) || (column + 1 < aBricks[row].length && aBricks[row][column + 1].mExist == false))) { // ball touch brick from the right-bottom corner
                    ball_touch_brick(index_of_ball, row, column);
                    if (aBall[index_of_ball].mRight >= WALL_RIGHT) // right wall
                        ball_move_left(index_of_ball);
                    if (aBall[index_of_ball].mBottom >= WALL_BOTTOM) // bottom wall
                        ball_move_up(index_of_ball);
                }
            }
        }
    }

    protected void board_move(float gravity_sensor_x) {
        if (gravity_sensor_x > board_moveAngle)
            mBoard.mLeft -= 1 * mBoard.mMoveUnitHerizontal;
        else if (gravity_sensor_x < -board_moveAngle)
            mBoard.mLeft += 1 * mBoard.mMoveUnitHerizontal;

        if (mBoard.mLeft <= BRICKS_AREA_LEFT) // Check board touch left wall
            mBoard.mLeft = BRICKS_AREA_LEFT;
        else if (mBoard.mLeft >= BRICKS_AREA_RIGHT - mBoard.mWidth) // Check board touch right wall
            mBoard.mLeft = BRICKS_AREA_RIGHT - mBoard.mWidth;

        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null && aBall[i].mExist != false) {
                //check_touch_ball_and_board(i); // check touch between ball and board
            }
        }

        if (!mBall_alive) {
            ball_reset_location();
        }
        mBoard.mRight = mBoard.mLeft + mBoard.mWidth;
    }

    private void ball_move(int index_of_ball) {
        aBall[index_of_ball].mLeft += aBall[index_of_ball].mMoveUnitHorizontal;
        aBall[index_of_ball].mRight += aBall[index_of_ball].mMoveUnitHorizontal;
        aBall[index_of_ball].mTop += aBall[index_of_ball].mMoveUnitVertical;
        aBall[index_of_ball].mBottom += aBall[index_of_ball].mMoveUnitVertical;
        ++ball_travelled;
    }

    protected void ball_move_and_touchcheck() {
        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null && aBall[i].mExist != false) {
                ball_move(i);
                check_touch_ball_and_board(i); // check touch between ball and board
                check_touch_ball_and_bricks(i); // check touch between ball and bricks
            }
        }
        if (check_level_complete()) {
            bricks_nextlevel();
            cancelTimer();
            game_level_complete = true;
        }
    }

    private void cancelTimer() {
        mTimerTaskBall.cancel();
        mTimerTaskBoard.cancel();
        for (int type = 0; type < aBonus.length; ++type) {
            Log.d(GameLevel.LOG_TAG, LOG_PREFIX + "cancelTimer()	type=" + type);
            if (aTimerBonus[type] != null) {
                Log.d(GameLevel.LOG_TAG, LOG_PREFIX + "cancelTimer()	type=" + type + "  a_bonus_timer[type] != null");
                aTimerBonus[type].cancel();
            }
        }
        if (bonus_item_timer_change_color != null)
            bonus_item_timer_change_color.cancel();
    }

    private void ball_touch_brick(int index_of_ball, int row, int column) {
        // ====================+++++ test for P2 +++++====================
        if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
            mSoundPool.play(this.mSoundBallHit, 1, 1, 0, 0, 1);
        }
        if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
            mVibrator.vibrate(100);
        }
        // ====================+++++ test for P2 +++++====================
        
    	// ====================+++++ random create bonus +++++====================
        // To create bonus item, P2
        Random randon = new Random();
        int type = randon.nextInt(10); // random number: 0~9, 0~6 will create bonus
        // int type = randon.nextInt(1)+1;
        // if (number_of_bonus < MAX_NUMBER_OF_BONUS && type < a_bonus.length && a_bonus[type] == null && type != SIX) { // maximum number of bonus is 5
        if ((type == mBonusTypeBarGreen || type == mBonusTypeBarRed) && aBonus[type] == null) { // enable type 4&5
            create_bonus(type, row, column);
        }
    	// ====================+++++ random create bonus +++++====================

        if (mBonus_22YELLOW) {
            ball_touch_brick_and_clear_brick(row - 3, column);
            ball_touch_brick_and_clear_brick(row + 3, column);
            ball_touch_brick_and_clear_brick(row, column - 3);
            ball_touch_brick_and_clear_brick(row, column + 3);
        }
        ball_touch_brick_and_clear_brick(row, column);

        if (!mBonus_22RED) { // check the status of 2*2 RED bonus item
            if (aBall[index_of_ball].mLeft < aBricks[row][column].mLeft && aBall[index_of_ball].mRight < aBricks[row][column].mRight)
                ball_move_left(index_of_ball);
            else if (aBall[index_of_ball].mLeft > aBricks[row][column].mLeft && aBall[index_of_ball].mRight > aBricks[row][column].mRight)
                ball_move_right(index_of_ball);
            if (aBall[index_of_ball].mTop < aBricks[row][column].mTop && aBall[index_of_ball].mBottom < aBricks[row][column].mBottom)
                ball_move_up(index_of_ball);
            else if (aBall[index_of_ball].mTop > aBricks[row][column].mTop && aBall[index_of_ball].mBottom > aBricks[row][column].mBottom)
                ball_move_down(index_of_ball);
        }
    }

    int mBricksHit;

    private void ball_touch_brick_and_clear_brick(int row, int column) {
        if (mode_setting == true) { // mode_setting: false => normal mode, true => advance mode
            aBricks[row][column].mExist = false;
            keep_score = ++score.mNumeric;
            ++mBricksHit;
        } else {
            for (int i = row - 1; i <= row + 1; ++i) {
                for (int j = column - 1; j <= column + 1; ++j) {
                    if (i < 0 || i >= aBricks.length || j < 0 || j >= aBricks[0].length || aBricks[i][j].mExist == false)
                        continue;
                    aBricks[i][j].mExist = false;
                    keep_score = ++score.mNumeric;
                    check_create_life_bonus(score.mNumeric, row, column);
                    ++mBricksHit;
                }
            }
        }
    }

    /*
     * Bonus item, P2
     */
    private BrickItem toNewBonusTypeA(Paint paint, int row, int column) {
        return new BrickItem(true,
                                paint,
                                aBricks[row][column].mLeft,
                                aBricks[row][column].mTop+BLOCK_HEIGHT,
                                aBricks[row][column].mLeft+2*BLOCK_WIDTH,
                                aBricks[row][column].mTop+3*BLOCK_HEIGHT);
    }

    private BrickItem toNewBonusTypeB(Paint paint, int row, int column) {
        return new BrickItem(true,
                                paint,
                                aBricks[row][column].mLeft,
                                aBricks[row][column].mTop+BLOCK_HEIGHT,
                                aBricks[row][column].mLeft+3*BLOCK_WIDTH,
                                aBricks[row][column].mTop+2*BLOCK_HEIGHT);
    }

    private BrickItem toNewBonusTypeHeart(Paint paint, int row, int column) {
        return new BrickItem(true,
                                paint,
                                aBricks[row][column].mLeft,
                                aBricks[row][column].mTop+BLOCK_HEIGHT,
                                aBricks[row][column].mLeft+5*BLOCK_WIDTH,
                                aBricks[row][column].mTop+5*BLOCK_HEIGHT);
    }

    protected void toSetCountDownTimer (final int type, int column) {
        aTimerBonus[type] = new CountDownTimer((BLOCK_NUMBER_COLUMN - column + 4) * SPEED_BONUS, SPEED_BONUS) {
                    public void onTick(long millisUntilFinished) {
                        check_touch_bonusitems_and_board(type);
                        failing_down(aBonus[type]);
                    }

                    public void onFinish() {
                        aBonus[type] = null;
                        --number_of_bonus;
                        aTimerBonus[type] = null;
                    }
                }.start();


        if (type == 1) {
            mBonus_22BLUE_change_color = true;
            bonus_item_timer_change_color = new CountDownTimer((BLOCK_NUMBER_COLUMN - column + 1) * SPEED_BONUS, SPEED_BONUS) {
                public void onTick(long millisUntilFinished) {
                    if (aBonus[1] == null) {
                        bonus_item_timer_change_color.cancel();
                        mBonus_22BLUE_change_color = false;
                    }
                    bonus_22BLUE_change_color(aBonus[1]);
                    BrickView.this.invalidate();
                }

                public void onFinish() {
                    if (aBonus[1] != null && aBonus[1].mBottom < WALL_BOTTOM) {
                        this.start();
                    }
                }
            }.start();
        }
    }

    private void create_bonus(final int bonusType, int row, int column) {
        ++number_of_bonus;
        int bonusItemWidth_TypeA = 2;
        int bonusItemWidth_TypeB = 3;
        int bonusItemWidth_TypeHeart = 5;
        int bonusItemRightMargin = 0;
        if (bonusType >= 0 && bonusType <= 3) {
            bonusItemRightMargin = BRICKS_AREA_SIZE_HORIZONTAL - bonusItemWidth_TypeA;
            if (row > bonusItemRightMargin)
                row = bonusItemRightMargin;
            if (bonusType == 0)
                aBonus[bonusType] = toNewBonusTypeA(aPaint[YELLOW], row, column);
            else if (bonusType == 1)
                aBonus[bonusType] = toNewBonusTypeA(aPaint[BLUE], row, column);
            else if (bonusType == 2)
                aBonus[bonusType] = toNewBonusTypeA(aPaint[RED], row, column);
            else if (bonusType == 3)
                aBonus[bonusType] = toNewBonusTypeA(aPaint[CYAN], row, column);
        } else if (bonusType >= 4 && bonusType <= 5) {
            bonusItemRightMargin = BRICKS_AREA_SIZE_HORIZONTAL - bonusItemWidth_TypeB;
            if (row > bonusItemRightMargin)
                row = bonusItemRightMargin;
            if (bonusType == 4)
                aBonus[bonusType] = toNewBonusTypeB(aPaint[GREEN], row, column);
            else if (bonusType == 5)
                aBonus[bonusType] = toNewBonusTypeB(aPaint[RED], row, column);
        } else if (bonusType == 6) {
            bonusItemRightMargin = BRICKS_AREA_SIZE_HORIZONTAL - bonusItemWidth_TypeHeart;
            if (row > bonusItemRightMargin)
                row = bonusItemRightMargin;
            aBonus[bonusType] = toNewBonusTypeHeart(aPaint[RED], row, column);
        }
        toSetCountDownTimer(bonusType, aBonus[bonusType].mTop / BLOCK_HEIGHT);
    }

    private void failing_down(BrickItem bonus) {
        bonus.mTop += BLOCK_HEIGHT;
        bonus.mBottom += BLOCK_HEIGHT;
    }

    private void check_touch_bonusitems_and_board(int type) {
        if (aBonus[type].mBottom == mBoard.mTop && aBonus[type].mPaint != aPaint[TRANSPARENT]) {
            if (aBonus[type].mLeft < mBoard.mRight && aBonus[type].mRight > mBoard.mLeft) {
                touch_bonusitems_and_board(type);
            }
        } else if (aBonus[type].mTop < mBoard.mBottom && aBonus[type].mBottom > mBoard.mTop && aBonus[type].mPaint != aPaint[TRANSPARENT]) {
            if (aBonus[type].mLeft < mBoard.mRight && aBonus[type].mRight > mBoard.mLeft) {
                touch_bonusitems_and_board(type);
            }
        }
    }

    private void touch_bonusitems_and_board(int type) {
        aBonus[type].mPaint = aPaint[TRANSPARENT];
        if (type == 0) { // bonus item 2*2 yellow
            if (!mBonus_22YELLOW) {
                mBonus_22YELLOW = true;
                for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
                    if (aBall[i] != null && aBall[i].mExist != false) {
                        aBall[i].mWidth += BLOCK_WIDTH;
                        aBall[i].mHeight += BLOCK_HEIGHT;
                        aBall[i].mRight += BLOCK_WIDTH;
                        aBall[i].mBottom += BLOCK_HEIGHT;
                    }
                }
            }
        } else if (type == 1) { // bonus item 2*2 blue (rainbow)
            mBonus_22BLUE_change_color = false;
            mNumber_of_balls_alive = 4;
            int aliveBallIndex = 0;
            for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
                if (aBall[i] != null && aBall[i].mExist != false) {
                    aliveBallIndex = i;
                }
            }

            for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
                if (aBall[i] == null || aBall[i].mExist == false) {
                    aBall[i] = new Ball(true,
                                        aBall[aliveBallIndex].mPaint,
                                        aBall[aliveBallIndex].mLeft,
                                        aBall[aliveBallIndex].mTop,
                                        aBall[aliveBallIndex].mRight,
                                        aBall[aliveBallIndex].mBottom,
                                        aBall[aliveBallIndex].mWidth,
                                        aBall[aliveBallIndex].mHeight,
                                        aBall[aliveBallIndex].mMoveUnitHorizontal,
                                        aBall[aliveBallIndex].mMoveUnitVertical);
                    if (i == 1)
                        aBall[i].mMoveUnitHorizontal = -aBall[i].mMoveUnitHorizontal;
                    else if (i == 2)
                        aBall[i].mMoveUnitVertical = -aBall[i].mMoveUnitVertical;
                    else if (i == 3) {
                        aBall[i].mMoveUnitHorizontal = -aBall[i].mMoveUnitHorizontal;
                        aBall[i].mMoveUnitVertical = -aBall[i].mMoveUnitVertical;
                    }
                }
            }
        } else if (type == 2) { // bonus item 2*2 red
            mBonus_22RED = true;
            new CountDownTimer(10000, 10000) {
                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    mBonus_22RED = false;
                }
            }.start();
        } else if (type == 3) { // bonus item 2*2 cyan
            mTimerTaskBall.cancel();
            mTimerBall.schedule(mTimerTaskBall = ball_TimerTask_renew(), 0, (int) (ball_speed / 1.5));
            new CountDownTimer(10000, 10000) {
                public void onTick(long millisUntilFinished) {}

                public void onFinish() {
                    mTimerTaskBall.cancel();
                    if (!game_pause) {
                        mTimerBall.schedule(mTimerTaskBall = ball_TimerTask_renew(), 0, ball_speed);
                    }
                }
            }.start();
        } else if (type == 4) { // bonus item 1*3 green
            if (mBoard.mWidth < 10 * BLOCK_WIDTH)
                mBoard.mWidth += 2 * BLOCK_WIDTH;
            // ====================+++++ test for P2 +++++====================
            if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
                mSoundPool.play(this.mSoundCatchBonusItemBarGreen, 1, 1, 0, 0, 1);
            }
            if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
                mVibrator.vibrate(new long[] { 0, 100, 100, 100 }, -1);
            }
            // ====================+++++ test for P2 +++++====================
        } else if (type == 5) { // bonus item 1*3 red
            if (mBoard.mWidth > 4 * BLOCK_WIDTH)
                mBoard.mWidth -= 2 * BLOCK_WIDTH;
            // ====================+++++ test for P2 +++++====================
            if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
                mSoundPool.play(this.mSoundCatchBonusItemBarRed, 1, 1, 0, 0, 1);
            }
            if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
                mVibrator.vibrate(new long[] { 0, 100, 100, 100 }, -1);
            }
            // ====================+++++ test for P2 +++++====================
        } else if (type == 6) { // bonus item 5*4 heart
            add_life();
            // ====================+++++ test for P2 +++++====================
            if (GameActivity.preferencesGet(Settings.sSOUND, true)) {
                mSoundPool.play(this.mSoundCatchBonusItemHeart, 1, 1, 0, 0, 1);
            }
            if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
                mVibrator.vibrate(new long[] { 0, 100, 100, 100 }, -1);
            }
            // ====================+++++ test for P2 +++++====================
    	}
    }

    private void bonus_22BLUE_change_color(BrickItem bonus) {
        if (mBonus_22BLUE_change_color) {
            mBonus_22BLUE = ++mBonus_22BLUE % 7;
            bonus.mPaint = aPaint[mBonus_22BLUE + COLOR_RAINBOW_RED];
        }
    }

    private void add_life() {
        if (life.mNumeric < MAX_LIFE)
            keep_life = ++life.mNumeric;
    }

    private void check_create_life_bonus(int score, final int row, final int column) {
        int temp = tmp_score_add_life_check;
        tmp_score_add_life_check = score % SCORE_ADD_LIFE_BONUS;
        if (tmp_score_add_life_check < temp)
            if (number_of_bonus < MAX_NUMBER_OF_BONUS && aBonus[mBonusTypeHeart] == null) { // maximum number of bonus is 5, bonus[Six] is life_bonus
                create_bonus(mBonusTypeHeart, row, column);
            }
    }

//    private void check_add_life(int score) {
//        int temp = tmp_score_add_life_check;
//        tmp_score_add_life_check = score % SCORE_ADD_LIFE;
//        if (tmp_score_add_life_check < temp)
//            add_life();
//    }

    private boolean check_level_complete() {
        for (int row = 0; row < aBricks.length; ++row) {
            for (int column = 0; column < aBricks[row].length; ++column) {
                if (aBricks[row][column].mExist == true)
                    return false;
            }
        }
        BrickView.this.invalidate();
        return true;
    }

    private void bricks_nextlevel() {
        int ori_level = current_game_level;
        int new_level = current_game_level >= MAX_GAME_LEVEL ? MAX_GAME_LEVEL : ori_level + 1;
        brick_game_level = new_level;

        if (ori_level == new_level) // Final Win
            GameActivity.preferencesEditor("CongratulationMessage", true);
        GameActivity.preferencesEditor("GameStatus[" + ori_level + "]", GameLevel.passed);
        GameActivity.preferencesEditor("GameStatus[" + new_level + "]", GameLevel.unlocked);
        GameActivity.preferencesEditor("PlayedGameLevel", new_level);
        GameActivity.preferencesEditorApply();
    }

    protected void toStartGame() {
        start_animation_timer.cancel();
        start_animation = false;
        mBall_alive = true;
    }

    protected void toShowStartAnimation() {
        start_animation_timer.cancel();
        start_animation = true;
        start_animation_number = BrickView.START_ANIMATION.length - 1;
        start_animation_timer.start();
    }

    protected void toGamePause() {
        game_pause = true;
        if (mTimerTaskBall != null)
            mTimerTaskBall.cancel();
        if (mTimerTaskBoard != null)
            mTimerTaskBoard.cancel();
        for (int type = 0; type < aBonus.length; ++type) {
            if (aBonus[type] != null)
                aTimerBonus[type].cancel();
        }
        // ====================+++++ test for P2 +++++====================
        if (GameActivity.preferencesGet(Settings.sVIBRATE, true)) {
            mVibrator.vibrate(1000); 
        }
        // ====================+++++ test for P2 +++++====================
        mSoundPool.release();
        invalidate();
    }

    protected void toGameResume() {
        game_pause = false;
        hint_animation_timer.cancel();
        BrickView.hint_animation = false;
        mTimerTaskBall = ball_TimerTask_renew();
        mTimerBall.schedule(mTimerTaskBall, 0, BrickView.ball_speed);
        mTimerTaskBoard = board_TimerTask_renew();
        mTimerBoard.schedule(mTimerTaskBoard, 0, BrickView.board_speed);

        for (int type = 0; type < aBonus.length; ++type) {
            if (aBonus[type] != null)
                toSetCountDownTimer(type, aBonus[type].mTop / BLOCK_HEIGHT);
        }
    }

    protected void toExitBrick() {
        start_animation_timer.cancel();
        hint_animation_timer.cancel();
        playTime_timer.cancel();
        BrickView.hint_animation = false;
        toGamePause();
        GameActivity.preferencesEditor("BricksHit", GameActivity.preferencesGet("BricksHit", 0) + mBricksHit);
        GameActivity.preferencesEditor("BallTravelled", GameActivity.preferencesGet("BallTravelled", 0) + ball_travelled);
        GameActivity.preferencesEditor("PlayTime", GameActivity.preferencesGet("PlayTime", 0) + tmp_playTime);
        saveCurrentGameState();

        ActivityList.getInstance().exit(); // Exit APP, delete all Activity.
    }

    public void onBackPressed() {
        start_animation_timer.cancel();
        hint_animation_timer.cancel();
        playTime_timer.cancel();
        BrickView.hint_animation = false;
        toGamePause();
        GameActivity.preferencesEditor("BricksHit", GameActivity.preferencesGet("BricksHit", 0) + mBricksHit);
        GameActivity.preferencesEditor("BallTravelled", GameActivity.preferencesGet("BallTravelled", 0) + ball_travelled);
        GameActivity.preferencesEditor("PlayTime", GameActivity.preferencesGet("PlayTime", 0) + tmp_playTime);
        saveCurrentGameState();
        BrickView.keep_score = BrickView.KEEP_SCORE_INIT;
        BrickView.keep_life = BrickView.KEEP_LIFE_INIT;
    }


    CountDownTimer start_animation_timer = new CountDownTimer(2010, 200) {
        public void onTick(long millisUntilFinished) {
            ++start_animation_number;
            start_animation_number %= 5;
        }

        public void onFinish() {
            start_animation = false;
        }
    };
    CountDownTimer hint_animation_timer = new CountDownTimer(2100, 200) {
        public void onTick(long millisUntilFinished) {
            ++BrickView.hint_animation_number;
            BrickView.hint_animation_number %= 10;
            invalidate();
        }

        public void onFinish() {
            BrickView.hint_animation = false;
            invalidate();
        }
    };

    int tmp_playTime = 0;
    CountDownTimer playTime_timer = new CountDownTimer(1000, 1000) {
        public void onTick(long millisUntilFinished) {}

        public void onFinish() {
            ++tmp_playTime;
            this.start();
        }
    };
	

    TimerTask board_TimerTask_renew() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = 1;
                board_TimerTask_Handler.sendMessage(msg);
            }
        };
        return timerTask;
    }
	
    Handler board_TimerTask_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (BrickView.game_over) {
                        return;
                    }
                    board_move(GameActivity.gravity_sensor[GameActivity.x]);
                    BrickView.this.invalidate();
                    break;
                default:
            }
        }
    };

    TimerTask ball_TimerTask_renew() {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message msg = new Message();
                msg.what = 1;
                ball_TimerTask_Handler.sendMessage(msg);
            }
        };
        return timerTask;
    }

    Handler ball_TimerTask_Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (!mBall_alive) {
                        return;
                    }
                    ball_move_and_touchcheck();
                    BrickView.this.invalidate();
                    break;
                default:
            }
        }
    };

    protected void saveCurrentGameState() {
        GameActivity.preferencesEditor("Current_GameLevel", brick_game_level);
        GameActivity.preferencesEditor("Current_GameState", true);
		
        GameActivity.preferencesEditor("Current_Ball_Alive", mBall_alive);
        GameActivity.preferencesEditor("Current_Score", score.mNumeric);
        GameActivity.preferencesEditor("Current_Life", life.mNumeric);
        GameActivity.preferencesEditor("Current_Bricks_Location_Left", aBricks[0][0].mLeft);
        GameActivity.preferencesEditor("Current_Bricks_Location_Top", aBricks[0][0].mTop);
        GameActivity.preferencesEditor("Current_Bricks_Width", BLOCK_WIDTH);
        GameActivity.preferencesEditor("Current_Bricks_Height", BLOCK_HEIGHT);
        for (int row = 0; row < aBricks.length; ++row) {
            for (int column = 0; column < aBricks[0].length; ++column) {
                GameActivity.preferencesEditor("Current_Bricks[" + row + "][" + column + "]_Exist", aBricks[row][column].mExist);
                GameActivity.preferencesEditor("Current_Bricks[" + row + "][" + column + "]_PaintColor", aBricks[row][column].mPaint.getColor());
            }
        }
        GameActivity.preferencesEditor("Current_Ball_Width", aBall[0].mWidth);
        GameActivity.preferencesEditor("Current_Ball_Height", aBall[0].mHeight);
        for (int i = 0; i < MAX_NUMBER_OF_BALLS; ++i) {
            if (aBall[i] != null) {
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Exist", aBall[i].mExist);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Left", aBall[i].mLeft);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Top", aBall[i].mTop);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Right", aBall[i].mRight);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Bottom", aBall[i].mBottom);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_MoveUnitHorizontal", aBall[i].mMoveUnitHorizontal);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_MoveUnitVertical", aBall[i].mMoveUnitVertical);
            } else {
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Exist", false);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Left", aBall[0].mLeft);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Top", aBall[0].mTop);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Right", aBall[0].mRight);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_Bottom", aBall[0].mBottom);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_MoveUnitHorizontal", aBall[0].mMoveUnitHorizontal);
                GameActivity.preferencesEditor("Current_Ball[" + i + "]_MoveUnitVertical", aBall[0].mMoveUnitVertical);
            }
        }
        GameActivity.preferencesEditor("Current_Board_Exist", mBoard.mExist);
        GameActivity.preferencesEditor("Current_Board_PaintColor", mBoard.mPaint.getColor());
        GameActivity.preferencesEditor("Current_Board_Left", mBoard.mLeft);
        GameActivity.preferencesEditor("Current_Board_Top", mBoard.mTop);
        GameActivity.preferencesEditor("Current_Board_Right", mBoard.mRight);
        GameActivity.preferencesEditor("Current_Board_Bottom", mBoard.mBottom);
        GameActivity.preferencesEditor("Current_Board_Width", mBoard.mWidth);
        GameActivity.preferencesEditor("Current_Board_MoveUnitHorizontal", mBoard.mMoveUnitHerizontal);

        if (GameActivity.preferencesGet("GameStatus[" + brick_game_level + "]", GameLevel.unlocked) != GameLevel.ongoing) {
            for (int level = 1; level <= BrickView.MAX_GAME_LEVEL; ++level) {
                if (GameActivity.preferencesGet("GameStatus[" + (level + 1) + "]", GameLevel.unlocked) != GameLevel.locked)
                    GameActivity.preferencesEditor("GameStatus[" + level + "]", GameLevel.passed);
                else {
                    GameActivity.preferencesEditor("GameStatus[" + level + "]", GameLevel.unlocked);
                    break;
                }
            }
        }
        GameActivity.preferencesEditor("GameStatus[" + brick_game_level + "]", GameLevel.ongoing);
        GameActivity.preferencesEditorApply();
	}
}

