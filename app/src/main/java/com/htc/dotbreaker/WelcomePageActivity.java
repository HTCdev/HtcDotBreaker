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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class WelcomePageActivity extends Activity {
	CountDownTimer mCountDownTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActivityList.getInstance().addActivity(this);
		getWindow().getDecorView().setSystemUiVisibility(getWindow().getDecorView().getSystemUiVisibility() | 	
															View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | 
															View.SYSTEM_UI_FLAG_FULLSCREEN |
															View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		setContentView(R.layout.empty_layout);
		RelativeLayout empty_layout = (RelativeLayout)findViewById(R.id.empty_view);
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dot_breaker_splash);
		DrawViewFromFile drawView = new DrawViewFromFile(this, bitmap);
		empty_layout.addView(drawView);
		
		mCountDownTimer = new CountDownTimer(2000, 2000) {
			public void onTick(long millisUntilFinished) { }
			public void onFinish() { 
				WelcomePageActivity.this.finish();
				startActivity(new Intent(WelcomePageActivity.this, GameLevel.class));
			}
		}.start();
	}
	
	@Override
	public void onBackPressed () {
    	super.onBackPressed();
    	mCountDownTimer.cancel();
	}
}


