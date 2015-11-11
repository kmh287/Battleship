package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;

import java.util.*;

abstract class BattleshipBot {

    protected String name; //name of bot
    protected final int[] shipSizes = {2,3,3,4,5};
    protected String[][] shipsLocations;
    protected String move;
    protected HashMap<String,String> botShots; //bot's shots
    protected HashMap<String,String> opponentShots; //opponents shots

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
	abstract public String[][] getShipPlacements();

	abstract public String getMove();

	abstract void setMyMoveResult(MoveResult result);

	abstract public void setOpponentMoveResult(MoveResult result);

}