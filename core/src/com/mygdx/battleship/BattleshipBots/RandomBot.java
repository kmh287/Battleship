package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.OrientationType;

public class RandomBot extends BattleshipBot  {

    private final LinkedList<String> moves = new LinkedList<>();

    /**
     * Constructor
     */
    public RandomBot(String name) {
        super(name);
        setupMoves();
    }

    private void setupMoves() {
        for (int i = 0; i < 10; ++i){
            for (int j = 1; j <= 10; ++j){
                char row = (char) (i + 'A');
                moves.add("" + row + j);
            }
        }
        Collections.shuffle(moves);
    }

    /**
     * Decide ship placement.
     * Sets ship placement variable.
     */
    private void shipPlacements() {
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

    /**
     * returns array of ship placements as array of grid points.
     * [destroyer2, cruiser3, submarine3, battleship4, carrier5]
     */
    public String[][] getShipPlacements() {
        shipPlacements();
        return shipsLocations;
    }

    /**
     * returns the next move
     */
    public String getMove() {
        if(moves.size() > 0) {
            String move = moves.get(0);
            moves.remove(0);
            return move;
        } else{
            return "A1";
        }
    }

    public void setMyMoveResult(MoveResult result){}

    public void setOpponentMoveResult(MoveResult result){}
}