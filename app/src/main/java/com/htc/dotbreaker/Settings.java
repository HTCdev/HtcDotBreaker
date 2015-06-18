package com.htc.dotbreaker;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

public class Settings extends Activity {

    SharedPreferences mSharedprefs = null;
    SharedPreferences.Editor mSharedprefs_editor = null;
    CheckBox mCheckBoxSound, mCheckBoxMusic, mCheckBoxVibrate;
    private Resources mRes;
    
    static final String sSOUND = "checkbox_Sound";
    static final String sMUSIC = "checkbox_Music";
    static final String sVIBRATE = "checkbox_Vibrate";

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        ActionBar actionBar = getActionBar(); // Get ActionBar
        actionBar.setDisplayShowHomeEnabled(false); // Do not show icon
        actionBar.setTitle(""); // Do not show title
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.transparent)); // No background
        setContentView(R.layout.settings);

        mRes = getResources();
        int upMarginLeft = mRes.getDimensionPixelSize(R.dimen.margin_left_actionbar_up_btn);
        int upMarginRight = mRes.getDimensionPixelSize(R.dimen.margin_right_actionbar_up_btn);
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        ImageView up = (ImageView) findViewById(upId);
        up.setPadding(upMarginLeft, 0, upMarginRight, 0);

        StrokeTextView strokeTextView_title = (StrokeTextView) findViewById(R.id.strokeTextView_title_settings);
        if (GameLevel.isCJText((String) strokeTextView_title.getText())) {
            strokeTextView_title.setTypeface(GameLevel.mTextFontChinese);
        } else {
            strokeTextView_title.setTypeface(GameLevel.mTextFontEnglish);
            strokeTextView_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRes.getDimensionPixelSize(R.dimen.dot_breaker_11));
        }
        strokeTextView_title.setStrokeColor(mRes.getColor(R.color.text_stroke_title));
        strokeTextView_title.setStrokeWidth(mRes.getDimension(R.dimen.stroke_width_action_bar_title));

        mSharedprefs = getSharedPreferences("PREFS_MyStatus", 0);
        mSharedprefs_editor = mSharedprefs.edit();

        mCheckBoxSound = (CheckBox) findViewById(R.id.checkBox_sound);
        mCheckBoxMusic = (CheckBox) findViewById(R.id.checkBox_music);
        mCheckBoxVibrate = (CheckBox) findViewById(R.id.checkBox_vibrate);

        mCheckBoxSound.setChecked(mSharedprefs.getBoolean(sSOUND, true));
        mCheckBoxMusic.setChecked(mSharedprefs.getBoolean(sMUSIC, true));
        mCheckBoxVibrate.setChecked(mSharedprefs.getBoolean(sVIBRATE, true));
        
        toSetOnCheckedChangeListener(mCheckBoxSound, sSOUND);
        toSetOnCheckedChangeListener(mCheckBoxMusic, sMUSIC);
        toSetOnCheckedChangeListener(mCheckBoxVibrate, sVIBRATE);
	}
	
	private void toSetOnCheckedChangeListener (CheckBox checkbox, final String setting) {
	    checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSharedprefs_editor.putBoolean(setting, isChecked);
            }
        });
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
	
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSharedprefs_editor.commit();
	}
}
