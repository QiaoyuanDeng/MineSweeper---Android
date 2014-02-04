package com.minesweeper.max;

import android.content.Context;
import android.widget.Button;

// tile is a subclass from Button because it needs to be clicked by player
public class Tile extends Button {
	public enum TileState {
		tileCovered, tileRevealedNotMine, tileRevealedMine, tileFlagged, tileCheated
	}

	public TileState tileState;
	public int tileId;
	public int tileNumber;

	// put tile on a context
	public Tile(Context context) {
		super(context);
	}

	public void setTileId(int newId) {
		tileId = newId;
		tileNumber = 0;
		tileState = TileState.tileCovered;
	}

	public void setTileState(TileState state) {
		tileState = state;
	}

	public void setTileNumber(int number) {
		this.tileNumber = number;
	}

	// each mine will add surrounding non-mine one
	public void addTileNumber() {
		tileNumber++;
	}

}
