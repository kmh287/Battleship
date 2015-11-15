package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.MoveResult;

import java.util.*;

public abstract class BattleshipBot {

    protected final String name; //name of bot
    protected final int[] shipSizes = {2,3,3,4,5};
    protected final String[][] shipsLocations;
    protected final HashMap<String,String> botShots; //bot's shots
    protected final HashMap<String,String> opponentShots; //opponents shots
    protected String move;

	public BattleshipBot(String name) {
        this.name = name;
		shipsLocations = new String[5][0];
		botShots = new HashMap<>();
		opponentShots = new HashMap<>();
	}

	/**
	 * returns array of ship placements as array of grid points.
	 * [destroyer2, cruiser3, submarine3, battleship4, carrier5]
	 */
    public abstract String[][] getShipPlacements();

    public abstract String getMove();

	public abstract void setMyMoveResult(MoveResult result);

    public abstract void setOpponentMoveResult(MoveResult result);

}