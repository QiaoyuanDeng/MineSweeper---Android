package com.minesweeper.max;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.minesweeper.max.Game.GameState;
import com.minesweeper.max.Game.Hardness;
import com.minesweeper.max.Game.PlayState;
import com.minesweeper.max.Tile.TileState;

public class GameViewController extends Activity {
	// private static final String EXTRA_MESSAGE = GameViewController.class
	// .getSimpleName();

	// display setting
	protected double scaleX, scaleY;
	private LinearLayout gameStateLayout;
	private LinearLayout gameLayout;
	private Point gameViewSize;
	private Point gameStateViewSize;
	private Button cheatButton, restartButton, flagButton;
	private TextView minutesLabel, secondsLabel, leftMinesLabel;

	// game states setting
	private Hardness hardness;
	private double tileSide;
	private double blankSide;
	private double MAX_TILE_SIDE;
	private double MIN_TILE_SIDE;
	private Timer timer;
	private int seconds;
	private int minutes;

	// game content setting
	private Game game;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get hardness from main view button being pressed
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		if (message.equals(String.format("Beginner"))) {
			hardness = Hardness.Beginner;
		} else if (message.equals(String.format("Intermediate"))) {
			hardness = Hardness.Intermediate;
		} else if (message.equals(String.format("Expert"))) {
			hardness = Hardness.Expert;
		} else {
			return;
		}

		// set window landscape and full screen
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set background layout as main game layout
		LinearLayout mainGameLayout = new LinearLayout(this);
		mainGameLayout.setBackgroundColor(Color.WHITE);
		this.setContentView(mainGameLayout);

		// set scale number by getting window size
		Point windowSize = new Point();
		this.getWindowManager().getDefaultDisplay().getSize(windowSize);
		scaleX = windowSize.x / 1280;
		scaleY = windowSize.y / 720;

		// set game state view size and game view size
		gameViewSize = new Point((int) (windowSize.y * 1.5), windowSize.y);
		gameStateViewSize = new Point(windowSize.x - gameViewSize.x,
				windowSize.y);

		// set game state view layout
		gameStateLayout = new LinearLayout(this);
		gameStateLayout.setOrientation(LinearLayout.VERTICAL);
		gameStateLayout.setX(0);
		gameStateLayout.setY(0);

		LinearLayout.LayoutParams gameStateLayoutParams = new LayoutParams(
				gameStateViewSize.x, gameStateViewSize.y);
		gameStateLayout.setLayoutParams(gameStateLayoutParams);

		mainGameLayout.addView(gameStateLayout);

		// set game view layout
		gameLayout = new LinearLayout(this);
		gameLayout.setOrientation(LinearLayout.VERTICAL);

		// set tile max and min based on window size for better UI
		MAX_TILE_SIDE = Math.min((double) gameViewSize.x / 8,
				(double) gameViewSize.y / 8);
		MIN_TILE_SIDE = Math.max((double) gameViewSize.x / 15,
				(double) gameViewSize.y / 10);
		
		LinearLayout.LayoutParams gameLayoutParams = new LayoutParams(
				gameViewSize.x, gameViewSize.y);
		gameLayout.setLayoutParams(gameLayoutParams);

		mainGameLayout.addView(gameLayout);

		newGame();
	}

	private void newGame() {
		// clean game state view and game view
		gameStateLayout.removeAllViews();
		gameLayout.removeAllViews();

		// set game state view and game view
		setGameStateView();
		setGameView();
	}

	private void setGameStateView() {
		LayoutParams params;

		// game state layout: return button to back to main window
		if (hardness == Hardness.Beginner) {
			cheatButton = new Button(this);
			cheatButton.setBackgroundResource(R.drawable.cheat);
			params = new LayoutParams((int) (80 * scaleX), (int) (80 * scaleY));
			params.gravity = Gravity.CENTER_HORIZONTAL;
			params.topMargin = (int) (40 * scaleY);
			cheatButton.setLayoutParams(params);
			gameStateLayout.addView(cheatButton);
			listenCheatButtonOnClick();
		}

		// game state layout: time label to show string "Time"
		TextView timeLabel = new TextView(this);
		timeLabel.setText(R.string.time_label);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		if (hardness == Hardness.Beginner)
			params.topMargin = (int) (40 * scaleY);
		else
			params.topMargin = (int) (120 * scaleY);
		timeLabel.setLayoutParams(params);
		gameStateLayout.addView(timeLabel);

		// game state layout: vertical linear layout to display time in minutes
		// and seconds
		LinearLayout timeDisplayLayout = new LinearLayout(this);
		timeDisplayLayout.setOrientation(LinearLayout.HORIZONTAL);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (40 * scaleY);
		timeDisplayLayout.setLayoutParams(params);
		gameStateLayout.addView(timeDisplayLayout);

		// time display layout: Minutes label to show minutes in the format of
		// "00"
		minutesLabel = new TextView(this);
		timeDisplayLayout.addView(minutesLabel);

		// time display layout: colon label to show a colon as ":"
		TextView colonLabel = new TextView(this);
		colonLabel.setText(R.string.colon_label);
		timeDisplayLayout.addView(colonLabel);

		// time display layout: seconds label to show seconds in the format of
		// "00"
		secondsLabel = new TextView(this);
		timeDisplayLayout.addView(secondsLabel);

		// game state layout: restart button to restart a new game
		restartButton = new Button(this);
		restartButton.setBackgroundResource(R.drawable.ready);
		params = new LayoutParams((int) (80 * scaleX), (int) (80 * scaleY));
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (40 * scaleY);
		restartButton.setLayoutParams(params);
		gameStateLayout.addView(restartButton);
		listenRestartButtonOnClick();

		// game state layout: mines label to show string "Time"
		TextView minesLabel = new TextView(this);
		minesLabel.setText(R.string.mines_label);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (40 * scaleY);
		minesLabel.setLayoutParams(params);
		gameStateLayout.addView(minesLabel);

		// game state layout: left mines label to show number of left mines
		leftMinesLabel = new TextView(this);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (40 * scaleY);
		leftMinesLabel.setLayoutParams(params);
		gameStateLayout.addView(leftMinesLabel);

		// game state layout: flag button to flag a tile
		flagButton = new Button(this);
		flagButton.setBackgroundResource(R.drawable.flag);
		params = new LayoutParams((int) (80 * scaleX), (int) (80 * scaleY));
		params.gravity = Gravity.CENTER_HORIZONTAL;
		params.topMargin = (int) (40 * scaleY);
		flagButton.setLayoutParams(params);
		gameStateLayout.addView(flagButton);
		listenFlagButtonOnClick();
	}

	private void setGameView() {
		if (game == null)
			game = new Game();

		// set up default settings
		game.gameState = GameState.ready;
		game.playState = PlayState.reveal;
		game.setGame(hardness, this);
		makeTileSide(game.rows, game.columns);

		timer = new Timer();
		leftMinesLabel.setText(String.format("%d", game.leftMines));
		minutesLabel.setText("00");
		secondsLabel.setText("00");
		seconds = 0;
		minutes = 0;

		// display tiles on game view
		displayTiles();
	}

	// calculate tile side based on window size
	private void makeTileSide(int rows, int columns) {
		tileSide = gameViewSize.x / columns > gameViewSize.y / rows ? gameViewSize.y
				/ rows
				: gameViewSize.x / columns;
		if (tileSide > MAX_TILE_SIDE)
			tileSide = MAX_TILE_SIDE;
		if (tileSide < MIN_TILE_SIDE)
			tileSide = MIN_TILE_SIDE;

		blankSide = (gameViewSize.x - columns * tileSide) / 2;

		if (blankSide < 0)
			blankSide = 0;
		}

	// display tiles in LinearLayout in columns, which layouts linearly in rows
	private void displayTiles() {
		ArrayList<Tile> tiles = game.getTiles();
		int rows = game.rows;
		int columns = game.columns;
		int index = 0;
		LayoutParams tileParams = new LayoutParams((int) tileSide,
				(int) tileSide);

		// create tiles
		for (int i = 0; i < rows; i++) {
			// each row is a linear layout
			LinearLayout rowLayout = new LinearLayout(this);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			rowLayout.setLayoutParams(params);
			gameLayout.addView(rowLayout, i);

			// draw tiles
			for (int j = 0; j < columns; j++) {
				index = i * columns + j;
				Tile aTile = tiles.get(index);
				aTile.setLayoutParams(tileParams);
				aTile.setId(index);
				drawTile(aTile);
				rowLayout.addView(aTile, j);

				// add button listener to each tile
				listenTileOnClick(aTile);
			}
		}
	}

	// reveal surrounding blank tiles from a blank tile
	private void revealSurroundingNonZeroTileUntilZero(Tile aTile) {
		if (aTile.tileNumber != 0)
			return;

		int row = aTile.tileId / game.columns;
		int column = aTile.tileId % game.columns;
		Tile currentTile;

		// does left tile exists
		if (column - 1 >= 0) {
			// is left tile revealed
			currentTile = game.tiles.get(aTile.tileId - 1);
			if (currentTile.tileState != TileState.tileRevealedNotMine
					&& currentTile.tileState != TileState.tileRevealedMine) {
				currentTile.setTileState(TileState.tileRevealedNotMine);
				currentTile.performClick();
			}
		}

		// does right tile exists
		if (column + 1 < game.columns) {
			// is right tile revealed
			currentTile = game.tiles.get(aTile.tileId + 1);
			if (currentTile.tileState != TileState.tileRevealedNotMine
					&& currentTile.tileState != TileState.tileRevealedMine) {
				currentTile.setTileState(TileState.tileRevealedNotMine);
				currentTile.performClick();
			}
		}

		// does top tile exists
		if (row - 1 >= 0) {
			// is top tile revealed
			currentTile = game.tiles.get(aTile.tileId - game.columns);
			if (currentTile.tileState != TileState.tileRevealedNotMine
					&& currentTile.tileState != TileState.tileRevealedMine) {
				currentTile.setTileState(TileState.tileRevealedNotMine);
				currentTile.performClick();
			}
		}

		// does bottom tile exists
		if (row + 1 < game.rows) {
			// is bottom tile revealed
			currentTile = game.tiles.get(aTile.tileId + game.columns);
			if (currentTile.tileState != TileState.tileRevealedNotMine
					&& currentTile.tileState != TileState.tileRevealedMine) {
				currentTile.setTileState(TileState.tileRevealedNotMine);
				currentTile.performClick();
			}
		}
	}

	// draw a tile on the map based on tile state
	private void drawTile(Tile aTile) {
		switch (aTile.tileState) {
		case tileCovered:
			aTile.setBackgroundResource(R.drawable.tile);
			break;
		case tileRevealedNotMine:
			drawNumber(aTile);
			break;
		case tileFlagged:
			aTile.setBackgroundResource(R.drawable.flag);
			break;
		case tileRevealedMine:
			aTile.setBackgroundResource(R.drawable.bombrevealed);
			break;
		case tileCheated:
			aTile.setBackgroundResource(R.drawable.bombcovered);
			break;
		default:
			break;
		}

		// draw tile border by set background image by reducing layout bounds
		// using setLayerInset, then a border image under background image will
		// be shown
		Drawable bgTile = aTile.getBackground();
		if (bgTile != null) {
			Drawable bgBorder = getResources().getDrawable(
					R.drawable.whitebackground);
			Drawable drawableArray[] = new Drawable[] { bgBorder, bgTile };
			LayerDrawable layerDraw = new LayerDrawable(drawableArray);
			layerDraw.setLayerInset(1, 1, 1, 1, 1);
			aTile.setBackground(layerDraw);
		} else
			aTile.setBackgroundResource(R.drawable.whitebackground);
	}

	// draw number inside tile if it is a number tile
	private void drawNumber(Tile aTile) {
		if (aTile.tileNumber == 0) {
			aTile.setText("");
			aTile.setBackground(null);
			revealSurroundingNonZeroTileUntilZero(aTile);
			return;
		}

		aTile.setText(String.format("%d", aTile.tileNumber));
		aTile.setBackground(null);

		// different number displays in different color based on mine sweeper
		// customer
		switch (aTile.tileNumber) {
		case 1:
			aTile.setTextColor(Color.BLUE);
			break;
		case 2:
			aTile.setTextColor(Color.GREEN);
			break;
		case 3:
			aTile.setTextColor(Color.RED);
			break;
		case 4:
			// purple color
			aTile.setTextColor(Color.rgb(128, 0, 128));
			break;
		case 5:
			// maroon color
			aTile.setTextColor(Color.rgb(128, 0, 0));
			break;
		case 6:
			// turquoise color
			aTile.setTextColor(Color.rgb(64, 224, 208));
			break;
		case 7:
			aTile.setTextColor(Color.BLACK);
			break;
		case 8:
			aTile.setTextColor(Color.GRAY);
			break;
		default:
			break;
		}
	}

	// schedule a timer to count time used to play
	private void timerStarts() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						seconds++;
						if (seconds >= 60) {
							minutes++;
							seconds = 0;
							minutesLabel.setText(String.format("%d", minutes));
						}
						secondsLabel.setText(String.format("%d", seconds));
					}
				});
			}

		}, 1000, 1000);
	}

	// when game is finished
	private void gameFinished() {
		timer.cancel();
		if (game.gameState == GameState.lose)
			restartButton.setBackgroundResource(R.drawable.lose);
		else if (game.gameState == GameState.win)
			restartButton.setBackgroundResource(R.drawable.win);

		// show all mines
		for (Tile aTile : game.mines) {
			aTile.setTileState(TileState.tileRevealedMine);
			drawTile(aTile);
		}

		for (Tile aTile : game.tiles)
			aTile.setEnabled(false);

		// timer.invalidate();
		flagButton.setEnabled(false);
	}

	// listener to click a tile
	private void listenTileOnClick(Tile aTile) {
		aTile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				int tileId = view.getId();

				if (game.gameState == GameState.ready
						&& game.playState == PlayState.reveal) {
					game.setGameState(GameState.playing);

					// set mines and number on non-mines
					game.setMines(0, game.rows * game.columns, tileId);
					game.setNonMines();

					// start to record time
					timerStarts();
				}

				// player reveals a tile or flags it
				Tile aTile = game.tiles.get(tileId);
				switch (game.playState) {
				case reveal:
					// if a mine
					if (aTile.tileNumber == -1)
						aTile.setTileState(TileState.tileRevealedMine);
					else
						aTile.setTileState(TileState.tileRevealedNotMine);
					break;
				case flag:
					game.flagATile();
					leftMinesLabel.setText(String.format("%d", game.leftMines));
					if (aTile.tileState != TileState.tileRevealedNotMine)
						aTile.setTileState(TileState.tileFlagged);
					break;
				default:
					break;
				}

				drawTile(aTile);

				if (game.isFinished())
					gameFinished();
			}

		});
	}

	// listener to click restart button to restart a new game
	private void listenRestartButtonOnClick() {
		restartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				timer.cancel();
				newGame();
			}
		});
	}

	// listener to click cheat button in Beginner mode to reveal mines
	private void listenCheatButtonOnClick() {
		cheatButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (game.gameState == GameState.ready)
					return;

				if (cheatButton.isSelected()) {
					if (game.mines.isEmpty())
						return;
					for (Tile aTile : game.mines) {
						aTile.setTileState(TileState.tileCovered);
						drawTile(aTile);
						cheatButton.setBackgroundResource(R.drawable.cheat);
					}

					cheatButton.setSelected(false);
				} else {
					if (game.mines.isEmpty())
						return;
					for (Tile aTile : game.mines) {
						aTile.setTileState(TileState.tileCheated);
						drawTile(aTile);
					}
					cheatButton.setSelected(true);

					Drawable bgCheat = cheatButton.getBackground();
					Drawable bgBorder = getResources().getDrawable(
							R.drawable.whitebackground);
					Drawable drawableArray[] = new Drawable[] { bgBorder,
							bgCheat };
					LayerDrawable layerDraw = new LayerDrawable(drawableArray);
					layerDraw.setLayerInset(1, 1, 1, 1, 1);
					cheatButton.setBackground(layerDraw);
				}
			}

		});
	}

	// listener to click flag button to switch play state from play to flag or
	// exchange
	private void listenFlagButtonOnClick() {
		flagButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (game.playState == PlayState.flag) {
					flagButton.setSelected(false);
					game.playState = PlayState.reveal;
					flagButton.setBackgroundResource(R.drawable.flag);
					return;
				}

				Drawable bgFlag = flagButton.getBackground();
				Drawable bgBorder = getResources().getDrawable(
						R.drawable.whitebackground);
				Drawable drawableArray[] = new Drawable[] { bgBorder, bgFlag };
				LayerDrawable layerDraw = new LayerDrawable(drawableArray);
				layerDraw.setLayerInset(1, 1, 1, 1, 1);
				flagButton.setBackground(layerDraw);
				game.playState = PlayState.flag;
			}

		});
	}

}
