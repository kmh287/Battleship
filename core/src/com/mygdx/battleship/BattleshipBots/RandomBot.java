package com.mygdx.battleship.BattleshipBots;


import java.util.ArrayList;

import com.mygdx.battleship.MoveResult;


public class RandomBot implements BattleshipBot  {

	private final int[] shipSizes = {2,3,3,4,5};
	private String[][] shipsLocations;
	private String move;
	private HashTable<String,String> botShots; //bot's shots
	private HashTable<String,String> opponentShots; //oponents shots
	
	private ArrayList<String> placeValid = new ArrayList<String>;
	
	/**
	 * Constructor
	 */
	BattleshipBot() {
		shipsLocations = new ArrayList<String>[5];
		botShots = new HashTable<String,String>;
		opponentShots = new HashTable<String,String>;
		shipsPlacements();
		nextMove();
	}
	
	/**
	 * Decide ship placement.
	 * Sets ship placement variable.
	 */
	public void shipPlacements() {
		//start from the largest ship.
		boolean curShip = 4; 
		
		while (curShip >= 0) {
			String[] placement = new String[shipSizes[curShip]];
			//decide random starting position and orientation
			Random rand1 = new Random();
			Random rand2 = new Random();
			char startL = (char) (rand1.nextInt(10) + 'a');
			int  startR = rand1.nextInt(10);
			orientation = rand2.nextInt(4);

			//place ships
			String start = String.valueOf(Character.toUpperCase(startL)) + startR
			placeValid.add(newPlace);
			placement[0] = start;
			if (orientation == 0) { // north
				for (int x = 1; x < shipSizes[curShip]; x++) {
					char newL = (char) (startL - x);
					int newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
					placeValid.add(newPlace);
					placement[x] = newPlace;
				}
			} else if (orientation == 1) { // east
				for (int x = 1; x < shipSizes[curShip]; x++) {
					int newR = startR + x;
					int newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
					placeValid.add(newPlace);
					placement[x] = newPlace;
				}
			} else if (orientation == 2) { // south
				for (int x = 1; x < shipSizes[curShip]; x++) {
					char newL = (char) (startL + x);
					int newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
					placeValid.add(newPlace);
					placement[x] = newPlace;
				}
			} else { // west
				for (int x = 1; x < shipSizes[curShip]; x++) {
					int newR = startR - x;
					int newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
					placeValid.add(newPlace);
					placement[x] = newPlace;
				}
			}
			
			//check invariants
			if (shipPlacementCheck(placement)) {
				for (int i = 0; i < 5; i++) {
					placeValid.add(placement[i]);
				}
				shipsLocations[curShip] = placement;
				curShip--;
			}
		}
	}
	
	/**
	 * Invariant checker for ship placements.
	 * Checks whether ships have the right length and whether or not they
	 * intersect with other ships.
	 */
	public boolean shipPlacementCheck(String[] placement) {
		boolean valid = true;
		for (int i = 0; i < 5; i++) {
			valid = valid && (!(placeValid.contains(placement[i])));
			String left = placement[i].substring(0,1);
			int right = Integer.parseInt(placement[i].substring(1,placement[i].length()));
			valid = valid && (left <= "J") && (left >= "A");
			valid = valid && (right <= 10) && (right >= 1);
		}
		return valid;
	}
	
	/**
	 * returns array of ship placements as array of grid points.
	 * [destroyer2, cruiser3, submarine3, battleship4, carrier5]
	 */
	public ArrayList<String>[] getShipPlcements() {
		shipPlacements();
		return shipsLocations;
	}
	
	/**
	 * Decides next move.
	 */
	public void nextMove() {
		Random rand1 = new Random();
		Random rand2 = new Random();
		
		//decide random letter A -> J
		String str = String.valueOf(Character.toUpperCase((char) (rand1.nextInt(10) + 'a')));
		//set move
		move = str + ("" + (rand2.nextInt(10) + 1));
	}
	
    /**
     * returns the next move
     */
    public String getMove() {
    	nextMove();
    	return move;
    }

    
    public void setMyMoveResult(MoveResult result);

    public void setOpponentMoveResult(MoveResult result);
}