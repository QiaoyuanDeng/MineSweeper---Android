package com.minesweeper.max;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	static final String EXTRA_MESSAGE = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);
	}

	public void pressBeginnerButton(View view) {
		Intent intent = new Intent(this, GameViewController.class);
		String message = "Beginner";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void pressIntermediateButton(View view) {
		Intent intent = new Intent(this, GameViewController.class);
		String message = "Intermediate";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void pressExpertButton(View view) {
		Intent intent = new Intent(this, GameViewController.class);
		String message = "Expert";
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

}
