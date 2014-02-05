package com.minesweeper.max;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button beginnerButton;
	private Button intermediateButton;
	private Button expertButton;

	static final String EXTRA_MESSAGE = MainActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// setContentView(R.layout.activity_main);

		LinearLayout mainActivityLayout = new LinearLayout(this);
		mainActivityLayout.setOrientation(LinearLayout.VERTICAL);
		mainActivityLayout.setBackgroundResource(R.drawable.background);
		this.setContentView(mainActivityLayout);

		Point windowSize = new Point();
		this.getWindowManager().getDefaultDisplay().getSize(windowSize);

		LayoutParams params;

		TextView tittleView = new TextView(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (windowSize.y / 7);
		tittleView.setLayoutParams(params);
		tittleView.setText("M i n e S w e e p e r");
		tittleView.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ufonts.com_bradley_hand_itc_tt_bold.ttf"));
		tittleView.setTextSize(40);
		tittleView.setTextColor(Color.RED);
		mainActivityLayout.addView(tittleView);

		beginnerButton = new Button(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (windowSize.y / 15);
		beginnerButton.setLayoutParams(params);
		beginnerButton.setText("Beginner");
		beginnerButton.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ufonts.com_bradley_hand_itc_tt_bold.ttf"));
		beginnerButton.setTextSize(30);
		beginnerButton.setTextColor(Color.BLUE);
		beginnerButton.setBackgroundColor(Color.TRANSPARENT);
		mainActivityLayout.addView(beginnerButton);
		listenBeginnerButtonOnClick();

		intermediateButton = new Button(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (windowSize.y / 15);
		intermediateButton.setLayoutParams(params);
		intermediateButton.setText("Intermediate");
		intermediateButton.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ufonts.com_bradley_hand_itc_tt_bold.ttf"));
		intermediateButton.setTextSize(30);
		intermediateButton.setTextColor(Color.BLUE);
		intermediateButton.setBackgroundColor(Color.TRANSPARENT);
		mainActivityLayout.addView(intermediateButton);
		listenIntermediateButtonOnClick();

		expertButton = new Button(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (windowSize.y / 15);
		expertButton.setLayoutParams(params);
		expertButton.setText("Expert");
		expertButton.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/ufonts.com_bradley_hand_itc_tt_bold.ttf"));
		expertButton.setTextSize(30);
		expertButton.setTextColor(Color.BLUE);
		expertButton.setBackgroundColor(Color.TRANSPARENT);
		mainActivityLayout.addView(expertButton);
		listenExpertButtonOnClick();

	}

	private void listenBeginnerButtonOnClick() {
		beginnerButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						GameViewController.class);
				String message = "Beginner";
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
			}
		});
	}

	private void listenExpertButtonOnClick() {
		expertButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						GameViewController.class);
				String message = "Expert";
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
			}

		});

	}

	private void listenIntermediateButtonOnClick() {
		intermediateButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						GameViewController.class);
				String message = "Expert";
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
			}

		});
	}

}
