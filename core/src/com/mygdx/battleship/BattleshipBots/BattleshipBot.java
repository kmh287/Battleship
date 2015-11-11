package com.mygdx.battleship.BattleshipBots;


import com.mygdx.battleship.MoveResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class BattleshipBot {

    //name of bot
    public String name;

	public final int[] shipSizes = {2,3,3,4,5};
	public String[][] shipsLocations;
	public String move;
	public HashMap<String,String> botShots; // bot's shots
	public HashMap<String,String> opponentShots; // opponents shots

    //ArrayList of occupied spaces by ships
	public ArrayList<String> placeValid;

	/**
	 * Constructor
	 */
	public BattleshipBot(String name) {
        this.name = name;
		shipsLocations = new String[5][0];
		botShots = new HashMap<>();
		opponentShots = new HashMap<>();
        placeValid = new ArrayList<>();
		nextMove();
	}

	/**
	 * Decide ship placement.
	 * Sets ship placement variable.
	 */
    /**
     * Randomly decides ship placement.
     * Sets ship placement variable.
     */
    private void shipPlacements() {
        // start from the largest ship.
        int curShip = 4;

        while (curShip >= 0) {
            String[] placement = new String[shipSizes[curShip]];
            // decide random starting position and orientation
            Random rand1 = new Random();
            Random rand2 = new Random();
            char startL = (char) (rand1.nextInt(10) + 'a');
            int  startR = rand1.nextInt(10);
            int orientation = rand2.nextInt(4);

            // place ships
            String start = String.valueOf(Character.toUpperCase(startL)) + startR;
            placeValid.add(start);
            placement[0] = start;
            if (orientation == 0) { // north
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    char newL = (char) (startL - x);
                    String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                    placeValid.add(newPlace);
                    placement[x] = newPlace;
                }
            } else if (orientation == 1) { // east
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    int newR = startR + x;
                    String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                    placeValid.add(newPlace);
                    placement[x] = newPlace;
                }
            } else if (orientation == 2) { // south
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    char newL = (char) (startL + x);
                    String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                    placeValid.add(newPlace);
                    placement[x] = newPlace;
                }
            } else { // west
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    int newR = startR - x;
                    String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                    placeValid.add(newPlace);
                    placement[x] = newPlace;
                }
            }

            // check invariants
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
            char left = placement[i].charAt(0);
            int right = Integer.parseInt(placement[i].substring(1,placement[i].length()));
            valid = valid && (left <= 'J') && (left >= 'A');
            valid = valid && (right <= 10) && (right >= 1);
        }
        return valid;
	}

	/**
	 * returns array of ship placements as array of grid points.
	 * [destroyer2, cruiser3, submarine3, battleship4, carrier5]
	 */
	public String[][] getShipPlacements() {
		shipPlacements();
		return shipsLocations;
	}

	/**
	 * Decides next move
     * Sets move variable.
	 */
	private void nextMove() {}

	/**
	 * returns the next move
	 */
	public String getMove() {
		nextMove();
		return move;
	}

	public void setMyMoveResult(MoveResult result) {}

	public void setOpponentMoveResult(MoveResult result) {}
}