package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.ResultType;


public class SmartRandomBot extends BattleshipBot  {

    private boolean hit = false;
    private boolean sink = false;
    private boolean foundShip = true;
    private int direction = 0;
    private ArrayList<Integer> usedDirections = new ArrayList<>();
    private final List<String> moves = new ArrayList<>(100);

	/**
	 * Constructor
	 */
	public SmartRandomBot(String name) {
		super(name);
        shipPlacements();
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
        //start from the largest ship.
        int curShip = 0;

        HashSet<String> usedCoordinates = new HashSet<>();

        while (curShip < 5) {

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
	 * Randomly decides next move.
     * If hit in last turn, randomly choose a direction and continue on direction
     * If another hit, continue on direction.
     * If it has found a ship, will intelligently shoot around the ship until it dies.
	 */
	private void nextMove() {
        if ((sink)||(!foundShip)) {
            move = getRandomMove();
        } else {
            //if miss, select an untried direction, otherwise keep direction.
            if (!hit) {
                usedDirections.add(direction);
                int newDirection = getOpposite(direction); //prioritize opposite direction
                Random rand = new Random();
                while (usedDirections.contains(newDirection)) {
                    newDirection = rand.nextInt();
                }
                direction = newDirection;
            }
            char left = move.charAt(0);
            int right = Integer.parseInt(move.substring(1, move.length()));
            if (direction == 0) { // north
                left = (char) (left - 1);
            } else if (direction == 1) { // east
                right = right + 1;
            } else if (direction == 2) { // south
                left = (char) (left + 1);
            } else { // west
                right = right - 1;
            }
            String newMove = String.valueOf(Character.toUpperCase(left)) + right;
            moves.remove(newMove); //remove from moveset
            move = newMove;
        }
	}

    private int getOpposite(int x) {
        int y = x + 2;
        if (y < 3) {
            return y;
        } else {
            return (y%4);
        }
    }

    /**
     * Calculates and returns unplayed random move.
     */
    private String getRandomMove() {
        if(moves.size() > 0) {
            String move = moves.get(0);
            moves.remove(0);
            return move;
        } else{
            return "A1";
        }
    }

    public String[][] getShipPlacements() {
        shipPlacements();
        return shipsLocations;
    }

    /**
     * returns the next move
     */
    public String getMove() {
        nextMove();
        return move;
    }

    /**
     * Sets the result of bot's move the last turn.
     */
    public void setMyMoveResult(MoveResult result) {
        ResultType res = result.getResult();
        switch (res) {
            case MISS:
                hit = false;
                sink = false;

            case HIT:
                hit = true;
                sink = false;
                foundShip = true;

            case SINK:
                hit = true;
                sink = true;
                foundShip = false;
                usedDirections.clear();
        }
    }

    public void setOpponentMoveResult(MoveResult result) {}
}