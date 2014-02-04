package com.minesweeper.max;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;

import com.minesweeper.max.Tile.TileState;

public class Game {

	public enum Hardness {
		Beginner, Intermediate, Expert
	}

	public enum GameState {
		ready, playing, win, lose
	}

	public enum PlayState {
		reveal, flag
	}

	public Hardness hardness;    // game hardness
	public GameState gameState;  // current game state
	public PlayState playState;  // current play state
	public int rows, columns;    // total rows and columns
	public int minesNumber;      // total mines number
	public int leftMines;        // current left mines number, some tiles may be flagged
	public ArrayList<Tile> tiles, mines;

	public Game() {
		if (tiles == null)
			tiles = new ArrayList<Tile>();
		if (mines == null)
			mines = new ArrayList<Tile>();
	}

	// set game based on hardness to a context
	public void setGame(Hardness newHardness, Context context) {
		this.hardness = newHardness;
		switch (hardness) {
		case Beginner:
			this.rows = 8;
			this.columns = 8;
			this.minesNumber = 10;
			break;
		case Intermediate:
			this.rows = 8;
			this.columns = 12;
			this.minesNumber = 15;
			break;
		case Expert:
			this.rows = 10;
			this.columns = 15;
			this.minesNumber = 30;
			break;
		default:
			break;
		}
		this.leftMines = this.minesNumber;
		this.tiles.clear();
		this.mines.clear();

		this.gameState = GameState.ready;
		this.playState = PlayState.reveal;

		// create tiles
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.columns; j++) {
				Tile aTile = new Tile(context);
				aTile.setTileId(i * this.columns + j);
				this.tiles.add(aTile);
			}
		}
	}

	// set mines from "start" number to "end" number exclude "except" number
	public void setMines(int start, int end, int except) {
		if (this.minesNumber > this.rows * this.columns) {
			System.out.printf("Error: Mines are more than tiles.");
			return;
		}

		// clear mines before use
		this.mines.clear();

		Random rand = new Random();
		boolean isRepeated = false;
		int createdMines = 0;

		while (createdMines < this.minesNumber) {
			// get random number 
			int randNumber = rand.nextInt(this.rows * this.columns);

			if (randNumber == except)
				isRepeated = true;
			else
				for (Tile mine : mines) {
					if (randNumber == mine.tileId)
						isRepeated = true;
				}

			if (!isRepeated) {
				// set mine number as 1 if a tile is mine
				this.tiles.get(randNumber).setTileNumber(-1);

				// add this tile to mine array list
				this.mines.add(this.tiles.get(randNumber));
				createdMines++;
			}

			isRepeated = false;
		}
	}

	// each mine add 1 to surrounding not mine tiles
	public void setNonMines() {
		int row, column, aMineId;

		for (Tile aTile : this.mines) {
			aMineId = aTile.tileId;
			row = aMineId / this.columns;
			column = aMineId % this.columns;

			for (int i = -1; i < 2; i++) {
				if (row + i >= 0 && row + i < rows)
					for (int j = -1; j < 2; j++) {
						if (column + j >= 0 && column + j < columns) {
							if (this.tiles.get((row + i) * this.columns
									+ column + j).tileNumber != -1)
								this.tiles.get(
										(row + i) * this.columns + column + j)
										.addTileNumber();
						}
					}
			}
		}
	}

	// change game state to finish game
	public void setGameState(GameState state) {
		gameState = state;
	}

	// flag a tile means one less left mines
	public void flagATile() {
		leftMines--;
		if (leftMines < 0)
			leftMines = 0;
	}

	// judge is game finished
	public boolean isFinished() {
		// player failed?
		for (Tile aTile : this.tiles) {
			if (aTile.tileState == TileState.tileRevealedMine) {
				this.gameState = GameState.lose;
				return true;
			}
		}

		// player wins?
		int coverTiles = 0;
		for (Tile aTile : this.tiles) {
			if (aTile.tileState == TileState.tileCovered
					|| aTile.tileState == TileState.tileFlagged
					|| aTile.tileState == TileState.tileCheated) {
				coverTiles++;
			}
		}

		if (coverTiles == minesNumber) {
			this.gameState = GameState.win;
			return true;
		}

		return false;
	}

	public ArrayList<Tile> getTiles() {
		return tiles;
	}

	public ArrayList<Tile> getMines() {
		return mines;
	}
}
