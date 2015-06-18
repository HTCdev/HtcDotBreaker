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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class GameStats extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityList.getInstance().addActivity(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Translucent navigation bar
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.game_stats);
        ActionBar actionBar = getActionBar(); // Get ActionBar
        actionBar.setDisplayShowHomeEnabled(false); // Do not show icon
        actionBar.setTitle(""); // Do not show title
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // No background

        Resources res = getResources();
        int upMarginLeft = res.getDimensionPixelSize(R.dimen.margin_left_actionbar_up_btn);
        int upMarginRight = res.getDimensionPixelSize(R.dimen.margin_right_actionbar_up_btn);
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        ImageView up = (ImageView) findViewById(upId);
        up.setPadding(upMarginLeft, 0, upMarginRight, 0);

        StrokeTextView strokeTextView_Title = (StrokeTextView) findViewById(R.id.strokeTextView_title_game_status);
        String stringTitle = (String) strokeTextView_Title.getText();
        if (GameLevel.isCJText(stringTitle)) {
            strokeTextView_Title.setTypeface(GameLevel.mTextFontChinese);
        } else {
            strokeTextView_Title.setTypeface(GameLevel.mTextFontEnglish);
            strokeTextView_Title.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_11));
        }
        strokeTextView_Title.setStrokeColor(res.getColor(R.color.text_stroke_title));
        strokeTextView_Title.setStrokeWidth(res.getDimension(R.dimen.stroke_width_action_bar_title));

        SharedPreferences sharedprefs = getSharedPreferences("PREFS_MyStatus", 0);

        TextView textView_BestScore = (TextView) findViewById(R.id.textView_BestScore);
        TextView textView_BestScore_Numeric = (TextView) findViewById(R.id.textView_BestScore_Numeric);
        TextView textView_BricksHit = (TextView) findViewById(R.id.textView_BricksHit);
        TextView textView_BricksHit_Numeric = (TextView) findViewById(R.id.textView_BricksHit_Numeric);
        TextView textView_PlayTime = (TextView) findViewById(R.id.textView_PlayTime);
        TextView textView_MINS = (TextView) findViewById(R.id.textView_MINS);
        TextView textView_HOURS = (TextView) findViewById(R.id.textView_HOURS);
        TextView textView_PlayTime_Numeric_M = (TextView) findViewById(R.id.textView_PlayTime_Numeric_M);
        TextView textView_PlayTime_Numeric_H = (TextView) findViewById(R.id.textView_PlayTime_Numeric_H);
        TextView textView_Death = (TextView) findViewById(R.id.textView_Death);
        TextView textView_Death_Numeric = (TextView) findViewById(R.id.textView_Death_Numeric);
        TextView textView_LevelPass = (TextView) findViewById(R.id.textView_LevelPass);
        TextView textView_LevelPass_Numeric = (TextView) findViewById(R.id.textView_LevelPass_Numeric);
        TextView textView_BallTravelled = (TextView) findViewById(R.id.textView_BallTravelled);
        TextView textView_MILES = (TextView) findViewById(R.id.textView_MILES);
        TextView textView_BallTravelled_Numeric = (TextView) findViewById(R.id.textView_BallTravelled_Numeric);
        
        int numericHighScore = sharedprefs.getInt("BestScore", 0);
        int numericBricksBroken = sharedprefs.getInt("BricksHit", 0);
        int numericPlayTime = sharedprefs.getInt("PlayTime", 0);
        int numericLosses = sharedprefs.getInt("Deaths", 0);
        int numericLevel = sharedprefs.getInt("LevelPass", 0);
        int numericDistanceTravelled = sharedprefs.getInt("BallTravelled", 0);

        textView_BestScore_Numeric.setText(Integer.toString(numericHighScore));
        textView_BricksHit_Numeric.setText(Integer.toString(numericBricksBroken));
        int tmp_hours = numericPlayTime / 3600;
        if (tmp_hours == 0) {
            textView_PlayTime_Numeric_H.setText("");
            textView_HOURS.setText("");
        } else {
            textView_PlayTime_Numeric_H.setText(Integer.toString(numericPlayTime / 3600));
        }
        textView_PlayTime_Numeric_M.setText(Integer.toString((numericPlayTime % 3600) / 60));
        textView_Death_Numeric.setText(Integer.toString(numericLosses));
        for (int level = 1; level <= BrickView.MAX_GAME_LEVEL; ++level) {
            int gameStatus = sharedprefs.getInt("GameStatus[" + level + "]", GameLevel.locked);
            if (gameStatus == GameLevel.locked) {
                numericLevel = level;
                break;
            }
        }
        textView_LevelPass_Numeric.setText(Integer.toString(numericLevel));
        textView_BallTravelled_Numeric.setText(Integer.toString(numericDistanceTravelled));

        // To set textView font
        if (GameLevel.isCJText((String) textView_BestScore.getText())) {
            textView_BestScore.setTypeface(GameLevel.mTextFontChinese);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) textView_BestScore.getLayoutParams();
            lp.setMargins(0, res.getDimensionPixelSize(R.dimen.game_stats_v_margin_item_firstrow_CJ), 0, 0);
        } else {
            textView_BestScore.setTypeface(GameLevel.mTextFontEnglish);
            textView_BestScore.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_12));
        }
        textView_BestScore_Numeric.setTypeface(GameLevel.mTextFontEnglish);

        if (GameLevel.isCJText((String) textView_BricksHit.getText())) {
            textView_BricksHit.setTypeface(GameLevel.mTextFontChinese);
        } else {
            textView_BricksHit.setTypeface(GameLevel.mTextFontEnglish);
            textView_BricksHit.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_13));
        }
        textView_BricksHit_Numeric.setTypeface(GameLevel.mTextFontEnglish);

        if (GameLevel.isCJText((String) textView_PlayTime.getText())) {
            textView_PlayTime.setTypeface(GameLevel.mTextFontChinese);
        } else {
            textView_PlayTime.setTypeface(GameLevel.mTextFontEnglish);
            textView_PlayTime.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_14));
        }
        textView_MINS.setTypeface(GameLevel.mTextFontEnglish);
        textView_HOURS.setTypeface(GameLevel.mTextFontEnglish);
        textView_PlayTime_Numeric_M.setTypeface(GameLevel.mTextFontEnglish);
        textView_PlayTime_Numeric_H.setTypeface(GameLevel.mTextFontEnglish);

        if (GameLevel.isCJText((String) textView_Death.getText())) {
            textView_Death.setTypeface(GameLevel.mTextFontChinese);
        } else {
            textView_Death.setTypeface(GameLevel.mTextFontEnglish);
            textView_Death.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_13));
        }
        textView_Death_Numeric.setTypeface(GameLevel.mTextFontEnglish);

        if (GameLevel.isCJText((String) textView_LevelPass.getText())) {
            textView_LevelPass.setTypeface(GameLevel.mTextFontChinese);
        } else {
            textView_LevelPass.setTypeface(GameLevel.mTextFontEnglish);
            textView_LevelPass.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_14));
        }
        textView_LevelPass_Numeric.setTypeface(GameLevel.mTextFontEnglish);

        if (GameLevel.isCJText((String) textView_BallTravelled.getText())) {
            textView_BallTravelled.setTypeface(GameLevel.mTextFontChinese);
        } else {
            textView_BallTravelled.setTypeface(GameLevel.mTextFontEnglish);
            textView_BallTravelled.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimensionPixelSize(R.dimen.dot_breaker_13));
        }
        textView_MILES.setTypeface(GameLevel.mTextFontEnglish);
        textView_BallTravelled_Numeric.setTypeface(GameLevel.mTextFontEnglish);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
