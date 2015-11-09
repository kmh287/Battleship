package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;


public class TestBot implements BattleshipBot  {

    private final int[] shipSizes = {2,3,3,4,5};
    private String[][] shipsLocations;
    private String move;
    private HashMap<String,String> botShots; //bot's shots
    private HashMap<String,String> opponentShots; //oponents shots
    private final List<String> moves = new ArrayList<>(100);

    /**
     * Constructor
     */
    public TestBot() {
        shipsLocations = new String[5][];
        botShots = new HashMap<>();
        opponentShots = new HashMap<>();
        shipPlacements();
        setupMoves();
    }

    private void setupMoves() {
        for (int i = 0; i < 10; ++i){
            for (int j = 1; j <= 10; ++j){
                char row = (char) (i + 'A');
                int col = j;
                moves.add("" + row + col);
            }
        }
        Collections.shuffle(moves);
    }

    /**
     * Decide ship placement.
     * Sets ship placement variable.
     */
    public void shipPlacements() {
        //start from the largest ship.
        int curShip = 0;

        HashSet<String> usedCoordinates = new HashSet<String>();

        while (curShip < 5) {

            if(usedCoordinates.size() > 2+3+3+4+5){
                int y = 2;
            }

            int orientation;
            boolean reset = false;
            String[] placement = new String[shipSizes[curShip]];
            //decide random starting position and orientation
            Random rand1 = new Random();
            Random rand2 = new Random();
            char startL = (char) (rand1.nextInt(10) + 'a');
            int  startR = rand1.nextInt(10);
            orientation = rand2.nextInt(4);

            //place ships
            String start = String.valueOf(Character.toUpperCase(startL)) + startR;
            if (usedCoordinates.contains(start)){
                continue; //try again
            } else {
                placement[0] = start;
            }

            if (orientation == 0) { // north
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    char newL = (char) (startL - x);
                    String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                    if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)){
                        reset = true;
                        break;
                    } else {
                        placement[x] = newPlace;
                    }
                }
            } else if (orientation == 1) { // east
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    int newR = startR + x;
                    String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                    if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)){
                        reset = true;
                        break;
                    } else {
                        placement[x] = newPlace;
                    }
                }
            } else if (orientation == 2) { // south
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    char newL = (char) (startL + x);
                    String newPlace = String.valueOf(Character.toUpperCase(newL)) + startR;
                    if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)){
                        reset = true;
                        break;
                    } else {
                        placement[x] = newPlace;
                    }
                }
            } else { // west
                for (int x = 1; x < shipSizes[curShip]; x++) {
                    int newR = startR - x;
                    String newPlace = String.valueOf(Character.toUpperCase(startL)) + newR;
                    if (usedCoordinates.contains(newPlace) || !BattleshipUtils.validateCoordinate(newPlace)){
                        reset = true;
                        break;
                    } else {
                        placement[x] = newPlace;
                    }
                }
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
    public String[][] getShipPlcements() {
        return shipsLocations;
    }

    /**
     * returns the next move
     */
    public String getMove() {
        String move = moves.get(0);
        moves.remove(0);
        return move;
    }


    public void setMyMoveResult(MoveResult result){}

    public void setOpponentMoveResult(MoveResult result){}
}