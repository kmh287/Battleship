package com.mygdx.battleship.BattleshipBots;


import com.mygdx.battleship.MoveResult;

public interface BattleshipBot {
    
	private String[][] shipsLocations;
	private String move;
	private HashTable<String,String> botShots; //bot's shots
	private HashTable<String,String> opponentShots; //oponents shots
	
	
	BattleshipBot() {
		botShots = new HashTable<String,String>;
		opponentShots = new HashTable<String,String>;
		shipsPlacements();
		nextMove();
	}
	
	/**
	 * Decide ship placement.
	 * Sets ship placement variable.
	 */
	public void shipPlacements();
	
	/**
	 * Invariant checker for ship placements.
	 * Checks whether ships have the right length and whether or not they
	 * intersect with other ships.
	 */
	public boolean shipPlacementCheck();
	
	/**
	 * returns array of ship placements as array of grid points.
	 * [destroyer2, cruiser3, submarine3, battleship4, carrier5]
	 */
	public String[][] getShipPlcements();
	
	/**
	 * Decides next move.
	 */
	public void nextMove();
	
    /**
     * returns the next move
     */
    public String getMove();

    
    public void setMyMoveResult(MoveResult result);

    public void setOpponentMoveResult(MoveResult result);
}
