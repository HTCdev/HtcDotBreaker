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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.Character.UnicodeBlock;
import java.util.ArrayList;

public class GameLevel extends Activity {
	
    static public final String LOG_TAG = "HtcDotBreaker";
	static public final String LOG_PREFIX = "[GameLevel] ";
	View[] mBGViewLevel = new View[BrickView.MAX_GAME_LEVEL];
	StrokeTextView[] mTextViewLevel = new StrokeTextView[BrickView.MAX_GAME_LEVEL];
	ImageView[] mImageViewLevel = new ImageView[BrickView.MAX_GAME_LEVEL];
	private Resources mRes;

	SharedPreferences mSharedprefs = null;
	SharedPreferences.Editor mSharedprefs_editor = null;
	static final int unlocked = 1;
	static final int passed = 2;
	static final int ongoing = 3;
	static final int locked = 4;
	// unlocked, passed, ongoing, locked
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ActivityList.getInstance().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);	// Hide status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    	    Window window = getWindow();
    	    // Translucent status bar
    	    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    	    // Translucent navigation bar
    	    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    	}
        setContentView(R.layout.gamelevel);
        ActionBar actionBar = getActionBar(); // Get ActionBar
        actionBar.setDisplayShowHomeEnabled(false); // Do not show icon
        actionBar.setTitle(""); // Do not show title
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // No background

        mSharedprefs = getSharedPreferences("PREFS_MyStatus", 0);
        mSharedprefs_editor = mSharedprefs.edit();

        mRes = getResources();
        int upMarginLeft = mRes.getDimensionPixelSize(R.dimen.margin_left_actionbar_up_btn);
        int upMarginRight = mRes.getDimensionPixelSize(R.dimen.margin_right_actionbar_up_btn);
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        ImageView up = (ImageView) findViewById(upId);
        up.setPadding(upMarginLeft, 0, upMarginRight, 0);

        initFont();
        StrokeTextView strokeTextView_Title = (StrokeTextView) findViewById(R.id.strokeTextView_title_select_level);
        String stringTitle = (String) strokeTextView_Title.getText();
        if (GameLevel.isCJText(stringTitle)) {
            strokeTextView_Title.setTypeface(mTextFontChinese);
        } else {
            strokeTextView_Title.setTypeface(mTextFontEnglish);
            strokeTextView_Title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRes.getDimensionPixelSize(R.dimen.dot_breaker_11));
        }
        strokeTextView_Title.setStrokeColor(mRes.getColor(R.color.text_stroke_title));
        strokeTextView_Title.setStrokeWidth(mRes.getDimension(R.dimen.stroke_width_action_bar_title));

        mBGViewLevel[ 0] = (View) findViewById(R.id.view_bg001);
        mBGViewLevel[ 1] = (View) findViewById(R.id.view_bg002);
        mBGViewLevel[ 2] = (View) findViewById(R.id.view_bg003);
        mBGViewLevel[ 3] = (View) findViewById(R.id.view_bg004);
        mBGViewLevel[ 4] = (View) findViewById(R.id.view_bg005);
        mBGViewLevel[ 5] = (View) findViewById(R.id.view_bg006);
        mBGViewLevel[ 6] = (View) findViewById(R.id.view_bg007);
        mBGViewLevel[ 7] = (View) findViewById(R.id.view_bg008);
        mBGViewLevel[ 8] = (View) findViewById(R.id.view_bg009);
        mBGViewLevel[ 9] = (View) findViewById(R.id.view_bg010);
        mBGViewLevel[10] = (View) findViewById(R.id.view_bg011);
        mBGViewLevel[11] = (View) findViewById(R.id.view_bg012);
        mBGViewLevel[12] = (View) findViewById(R.id.view_bg013);
        mBGViewLevel[13] = (View) findViewById(R.id.view_bg014);
        mBGViewLevel[14] = (View) findViewById(R.id.view_bg015);
        mBGViewLevel[15] = (View) findViewById(R.id.view_bg016);
        mBGViewLevel[16] = (View) findViewById(R.id.view_bg017);
        mBGViewLevel[17] = (View) findViewById(R.id.view_bg018);
        mBGViewLevel[18] = (View) findViewById(R.id.view_bg019);
        mBGViewLevel[19] = (View) findViewById(R.id.view_bg020);

        mTextViewLevel[ 0] = (StrokeTextView) findViewById(R.id.textView_level001);
        mTextViewLevel[ 1] = (StrokeTextView) findViewById(R.id.textView_level002);
        mTextViewLevel[ 2] = (StrokeTextView) findViewById(R.id.textView_level003);
        mTextViewLevel[ 3] = (StrokeTextView) findViewById(R.id.textView_level004);
        mTextViewLevel[ 4] = (StrokeTextView) findViewById(R.id.textView_level005);
        mTextViewLevel[ 5] = (StrokeTextView) findViewById(R.id.textView_level006);
        mTextViewLevel[ 6] = (StrokeTextView) findViewById(R.id.textView_level007);
        mTextViewLevel[ 7] = (StrokeTextView) findViewById(R.id.textView_level008);
        mTextViewLevel[ 8] = (StrokeTextView) findViewById(R.id.textView_level009);
        mTextViewLevel[ 9] = (StrokeTextView) findViewById(R.id.textView_level010);
        mTextViewLevel[10] = (StrokeTextView) findViewById(R.id.textView_level011);
        mTextViewLevel[11] = (StrokeTextView) findViewById(R.id.textView_level012);
        mTextViewLevel[12] = (StrokeTextView) findViewById(R.id.textView_level013);
        mTextViewLevel[13] = (StrokeTextView) findViewById(R.id.textView_level014);
        mTextViewLevel[14] = (StrokeTextView) findViewById(R.id.textView_level015);
        mTextViewLevel[15] = (StrokeTextView) findViewById(R.id.textView_level016);
        mTextViewLevel[16] = (StrokeTextView) findViewById(R.id.textView_level017);
        mTextViewLevel[17] = (StrokeTextView) findViewById(R.id.textView_level018);
        mTextViewLevel[18] = (StrokeTextView) findViewById(R.id.textView_level019);
        mTextViewLevel[19] = (StrokeTextView) findViewById(R.id.textView_level020);

        mImageViewLevel[ 0] = (ImageView) findViewById(R.id.imageView_level001);
        mImageViewLevel[ 1] = (ImageView) findViewById(R.id.imageView_level002);
        mImageViewLevel[ 2] = (ImageView) findViewById(R.id.imageView_level003);
        mImageViewLevel[ 3] = (ImageView) findViewById(R.id.imageView_level004);
        mImageViewLevel[ 4] = (ImageView) findViewById(R.id.imageView_level005);
        mImageViewLevel[ 5] = (ImageView) findViewById(R.id.imageView_level006);
        mImageViewLevel[ 6] = (ImageView) findViewById(R.id.imageView_level007);
        mImageViewLevel[ 7] = (ImageView) findViewById(R.id.imageView_level008);
        mImageViewLevel[ 8] = (ImageView) findViewById(R.id.imageView_level009);
        mImageViewLevel[ 9] = (ImageView) findViewById(R.id.imageView_level010);
        mImageViewLevel[10] = (ImageView) findViewById(R.id.imageView_level011);
        mImageViewLevel[11] = (ImageView) findViewById(R.id.imageView_level012);
        mImageViewLevel[12] = (ImageView) findViewById(R.id.imageView_level013);
        mImageViewLevel[13] = (ImageView) findViewById(R.id.imageView_level014);
        mImageViewLevel[14] = (ImageView) findViewById(R.id.imageView_level015);
        mImageViewLevel[15] = (ImageView) findViewById(R.id.imageView_level016);
        mImageViewLevel[16] = (ImageView) findViewById(R.id.imageView_level017);
        mImageViewLevel[17] = (ImageView) findViewById(R.id.imageView_level018);
        mImageViewLevel[18] = (ImageView) findViewById(R.id.imageView_level019);
        mImageViewLevel[19] = (ImageView) findViewById(R.id.imageView_level020);

        for (int level = 0; level < mTextViewLevel.length; ++level) {
            try {
                mTextViewLevel[level].setTypeface(mTextFontEnglish);
            } catch (IndexOutOfBoundsException e) {
                Log.d(LOG_TAG, LOG_PREFIX + "IndexOutOfBoundsException");
            }

            final int mLevel = level;
            mTextViewLevel[level].setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    int gameStatus = mSharedprefs.getInt("GameStatus[" + mLevel + "]", mLevel == 0 ? unlocked : locked);
                    if (gameStatus == unlocked) {
                        mSharedprefs_editor.putInt("PlayedGameLevel", mLevel);
                        mSharedprefs_editor.apply();
                    } else if (gameStatus == passed) {
                        mSharedprefs_editor.putInt("PlayedGameLevel", mLevel);
                        mSharedprefs_editor.apply();
                    } else if (gameStatus == ongoing) {
                        mImageViewLevel[mLevel].setImageResource(R.drawable.dot_breaker_level_resume_press);
                        mSharedprefs_editor.putInt("PlayedGameLevel", mLevel);
                        mSharedprefs_editor.apply();
                    } else if (gameStatus == locked) { }
                    setGameLevelUIStatus();
                    start_Brick_Game(mLevel);
                }
            });;
        }
    }

    public static Typeface mTextFontEnglish;
    public static int mTextSizeEnglish;

    public static Typeface mTextFontChinese;
    public static int mTextSizeChinese;

    public static Typeface mTextFontNumber;
    public static int mTextSizeNumber;

    Typeface LoadTypeFacefromApk(String szFont) {
        Context ctxDealFile = null;
        Typeface result = null;

        try {
            ctxDealFile = createPackageContext("com.htc.dotmatrix", Context.CONTEXT_IGNORE_SECURITY);
        } catch (NameNotFoundException e1) {
            Log.i(LOG_TAG, "Error e1", e1);
            try {
                ctxDealFile = createPackageContext("com.htc.dotmatrix_odm", Context.CONTEXT_IGNORE_SECURITY);
            } catch (NameNotFoundException e2) {
                Log.i(LOG_TAG, "Error e2", e2);
            }
        }

        if (ctxDealFile != null) {
            try {
                result = Typeface.createFromAsset(ctxDealFile.getAssets(), szFont);

                Log.i(LOG_TAG, "LoadTypeFacefromApk:" + szFont + "=" + result);
            } catch (Exception e) {
                Log.i(LOG_TAG, "Error3", e);
            }
        }

        return result;
    }

    void initFont() {
        // English
        mTextFontEnglish = Typeface.createFromAsset(getAssets(), "standard0765-webfont.ttf");
        mTextSizeEnglish = mRes.getDimensionPixelSize(R.dimen.text_size_threeline_english);

        mTextFontChinese = LoadTypeFacefromApk("DFPixelFontGB-A.ttf");
        mTextSizeChinese = mRes.getDimensionPixelSize(R.dimen.text_size_CJK);

        mTextFontNumber = LoadTypeFacefromApk("Lynnpixel03.ttf");
        mTextSizeNumber = mRes.getDimensionPixelSize(R.dimen.text_size_time);
    }

    static private ArrayList<UnicodeBlock> msCJBlockList = null;

    static public boolean isCJText(String text) {
        if (text == null || text.length() == 0) {
            Log.d(LOG_TAG, LOG_PREFIX + "isCJText, text is null!!");
            return false;
        }

        msCJBlockList = getCJBlockList();
        if (msCJBlockList == null) {
            Log.d(LOG_TAG, LOG_PREFIX + "isCJText, msCJKBlockList is null!!");
            return false;
        }

        int i;
        for (i = 0; i < text.length(); i++) {
            int code = Character.codePointAt(text, i);
            if (isInBlockList(msCJBlockList, code) || isInJapaneseUnMappedRange(code)) {
                return true;
            }
            int count = Character.charCount(code);
            if (count > 1) {
                i += count - 1; // find next position ..
            }
        }

        return false;
    }

    static private ArrayList<UnicodeBlock> getCJBlockList() {
        if (msCJBlockList == null || msCJBlockList.size() == 0) {
            Log.d(LOG_TAG, LOG_PREFIX + "block null, generate");
            // generate CJ block
            msCJBlockList = new ArrayList<UnicodeBlock>();
            addBlockChinese(msCJBlockList);
            addBlockJapanese(msCJBlockList);
        }

        return msCJBlockList;
    }

    static private boolean isInBlockList(ArrayList<UnicodeBlock> blockList, int code) {
        if (blockList == null || blockList.size() == 0) {
            return false;
        }
        UnicodeBlock block = UnicodeBlock.of(code);
        for (UnicodeBlock info : blockList) {
            if (block.equals(info)) {
                return true;
            }
        }
        return false;
    }

    static private boolean isInJapaneseUnMappedRange(int code) {
        // only list Japanese un-mapped range (katakana)

        long low = 0xFF65;
        long high = 0xFF9F;
        if (code <= high && code >= low) {
            return true;
        }
        return false;
    }

    static private void addBlockJapanese(ArrayList<UnicodeBlock> codeList) {
        // U+3040..U+309F
        codeList.add(UnicodeBlock.HIRAGANA);
        // U+30A0...U+30FF
        codeList.add(UnicodeBlock.KATAKANA);
        // U+31F0...U+31FF
        codeList.add(UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS);
    }

    static private void addBlockChinese(ArrayList<UnicodeBlock> codeList) {
        // U+2E80..U+2EFF
        codeList.add(UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
        // U+2F00..U+2FDF
        codeList.add(UnicodeBlock.KANGXI_RADICALS);
        // U+3100..U+312F
        codeList.add(UnicodeBlock.BOPOMOFO);
        // U+3400..U+4DBF
        codeList.add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
        // U+4E00..U+9FFF
        codeList.add(UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
        // U+F900..U+FAFF
        codeList.add(UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initGameLevelStatus();
        setGameLevelUIStatus();
        if (mSharedprefs.getBoolean("CongratulationMessage", false))
            showdialog_congratulation();
    }

    private void initGameLevelStatus() {
        if (mSharedprefs.getBoolean("GameStatusInit", false))
            return;
        mSharedprefs_editor.putBoolean("GameStatusInit", true);
        mSharedprefs_editor.putInt("GameStatus[0]", unlocked);
        for (int level = 1; level < BrickView.MAX_GAME_LEVEL; ++level) {
            mSharedprefs_editor.putInt("GameStatus[" + level + "]", locked);
        }
        mSharedprefs_editor.apply();
    }

    private void setGameLevelUIStatus() {
        for (int level = 0; level < mTextViewLevel.length; ++level) {
            int gameStatus;
            if (level == 0)
                gameStatus = mSharedprefs.getInt("GameStatus[" + level + "]", unlocked);
            else
                gameStatus = mSharedprefs.getInt("GameStatus[" + level + "]", locked);
            if (gameStatus == unlocked) {
                mBGViewLevel[level].setBackgroundResource(R.drawable.dot_breaker_level_normal);
                mTextViewLevel[level].setTextColor(mRes.getColor(R.color.dot_breaker_02));
                mTextViewLevel[level].setStrokeColor(Color.BLACK);
                mImageViewLevel[level].setVisibility(View.INVISIBLE);
            } else if (gameStatus == passed) {
                mBGViewLevel[level].setBackgroundResource(R.drawable.dot_breaker_level_normal);
                mTextViewLevel[level].setTextColor(mRes.getColor(R.color.dot_breaker_02));
                mTextViewLevel[level].setStrokeColor(Color.BLACK);
                mImageViewLevel[level].setVisibility(View.INVISIBLE);
            } else if (gameStatus == ongoing) {
                mBGViewLevel[level].setBackgroundResource(R.drawable.dot_breaker_level_normal);
                mTextViewLevel[level].setTextColor(mRes.getColor(R.color.dot_breaker_02));
                mTextViewLevel[level].setStrokeColor(Color.BLACK);
                mImageViewLevel[level].setImageResource(R.drawable.dot_breaker_level_resume);
                mImageViewLevel[level].setVisibility(View.VISIBLE);
            } else if (gameStatus == locked) {
                mBGViewLevel[level].setBackgroundResource(R.drawable.dot_breaker_level_lock);
                mTextViewLevel[level].setTextColor(Color.TRANSPARENT);
                mTextViewLevel[level].setStrokeColor(Color.TRANSPARENT);
                mImageViewLevel[level].setVisibility(View.INVISIBLE);
            }
        }
        int mPlayedGameLevel = mSharedprefs.getInt("PlayedGameLevel", -1);
        if (mPlayedGameLevel != -1) {
            if(mPlayedGameLevel>=BrickView.MAX_GAME_LEVEL) {
                mPlayedGameLevel = BrickView.MAX_GAME_LEVEL-1;
            }
            mBGViewLevel[mPlayedGameLevel].setBackgroundResource(R.drawable.dot_breaker_level_select);
            mTextViewLevel[mPlayedGameLevel].setTextColor(mRes.getColor(R.color.dot_breaker_03));
            mImageViewLevel[mPlayedGameLevel].setImageResource(R.drawable.dot_breaker_level_resume_press);
        }
    }

    private void start_Brick_Game(int level) {
        if (mSharedprefs.getInt("GameStatus[" + level + "]", locked) == locked) {
            return; // locked game level.
        }
        BrickView.brick_game_level = level;
        showdialog_battery();
    }

    private void showdialog_battery() {
        if (mSharedprefs.getBoolean("checkbox_DontShowDialogBattery", false)) {
            startActivity(new Intent(GameLevel.this, GameActivity.class));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mRes.getString(R.string.dialog_battery_title));
            LayoutInflater layoutInflater = LayoutInflater.from(GameLevel.this);
            View view = layoutInflater.inflate(R.layout.alertdialog_battery, null);
            builder.setView(view);
            TextView mesg = (TextView) view.findViewById(R.id.textView_dialog_message);
            mesg.setText(mRes.getString(R.string.dialog_battery_message));
            final CheckBox dontShow_dialog = (CheckBox) view.findViewById(R.id.checkBox_dialog_dontShow);
            dontShow_dialog.setText(mRes.getString(R.string.dialog_battery_checkbox_message));
            dontShow_dialog.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSharedprefs_editor.putBoolean("checkbox_DontShowDialogBattery", isChecked);
                    mSharedprefs_editor.apply();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(GameLevel.this, GameActivity.class));
                }
            });
            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        }
    }

    private void showdialog_congratulation() {
        if (mSharedprefs.getBoolean("checkbox_DontShowDialogCongratulation", false)) {
            return;
        } else if (mSharedprefs.getBoolean("CongratulationMessage", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(mRes.getString(R.string.dialog_congratulation_title));
            LayoutInflater layoutInflater = LayoutInflater.from(GameLevel.this);
            View view = layoutInflater.inflate(R.layout.alertdialog_battery, null);
            builder.setView(view);
            TextView mesg = (TextView) view.findViewById(R.id.textView_dialog_message);
            mesg.setText(mRes.getString(R.string.dialog_congratulation_message));
            final CheckBox dontShow_dialog = (CheckBox) view.findViewById(R.id.checkBox_dialog_dontShow);
            dontShow_dialog.setText(mRes.getString(R.string.dialog_congratulation_checkbox_message));
            dontShow_dialog.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mSharedprefs_editor.putBoolean("checkbox_DontShowDialogCongratulation", isChecked);
                    mSharedprefs_editor.apply();
                }
            });
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mSharedprefs_editor.putBoolean("CongratulationMessage", false);
                    mSharedprefs_editor.commit();
                }
            });
            AlertDialog alertdialog = builder.create();
            alertdialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gamelevel, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_game_stats:
                startActivity(new Intent(GameLevel.this, GameStats.class));
                break;
            // ===== test for P2 =====
            case R.id.menu_settings:
                startActivity(new Intent(GameLevel.this, Settings.class));
                break;
            // =======================
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
