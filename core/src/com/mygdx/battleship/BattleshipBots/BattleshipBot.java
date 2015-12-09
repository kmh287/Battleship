package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.OrientationType;

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
    public final String[][] getShipPlacements(){
        return shipsLocations;
    }

    public abstract String getMove();

	public abstract void setMyMoveResult(MoveResult result);

    public abstract void setOpponentMoveResult(MoveResult result);

    /**
     * Decide ship placement.
     * Sets ship placement variable.
     */
    protected void shipPlacements() {
        int curShip = 0;
        HashSet<String> usedCoordinates = new HashSet<>();

        while (curShip < 5) {
            boolean reset = false;
            String[] placement = new String[shipSizes[curShip]];
            //decide random starting position and orientation
            Random rand1 = new Random();
            Random rand2 = new Random();
            char startL = (char) (rand1.nextInt(10) + 'a');
            int  startR = rand1.nextInt(10)+1;
            OrientationType orientation = OrientationType.values()[rand2.nextInt(4)];

            //place ships
            String start = String.valueOf(Character.toUpperCase(startL)) + startR;
            if (usedCoordinates.contains(start)){
                continue; //try again
            } else {
                placement[0] = start;
            }
            switch (orientation) {
                case WEST:
                    for (int x = 1; x < shipSizes[curShip]; x++) {
                        char newL = (char) (startL - x);
                        String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                        if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)) {
                            reset = true;
                            break;
                        } else {
                            placement[x] = newPlace;
                        }
                    }
                    break;

                case NORTH:
                    for (int x = 1; x < shipSizes[curShip]; x++) {
                        int newR = startR + x;
                        String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                        if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)) {
                            reset = true;
                            break;
                        } else {
                            placement[x] = newPlace;
                        }
                    }
                    break;

                case EAST:
                    for (int x = 1; x < shipSizes[curShip]; x++) {
                        char newL = (char) (startL + x);
                        String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                        if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)) {
                            reset = true;
                            break;
                        } else {
                            placement[x] = newPlace;
                        }
                    }
                    break;

                case SOUTH:
                    for (int x = 1; x < shipSizes[curShip]; x++) {
                        int newR = startR - x;
                        String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                        if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)) {
                            reset = true;
                            break;
                        } else {
                            placement[x] = newPlace;
                        }
                    }
                    break;
            }

            //check invariants
            reset = reset || !BattleshipUtils.validateShip(placement);
            if (!reset) {
                shipsLocations[curShip] = placement;
                curShip++;
                usedCoordinates.addAll(Arrays.asList(placement));
            }
        }
    }

}