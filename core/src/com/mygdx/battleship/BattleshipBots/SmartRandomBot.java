package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.ResultType;
import com.mygdx.battleship.OrientationType;

public class SmartRandomBot extends BattleshipBot  {

    private final ArrayList<OrientationType> usedDirections = new ArrayList<>();
    private final LinkedList<String> moves = new LinkedList<>();
    private final LinkedList<String> lastHits = new LinkedList<>();

    private boolean hit = false;
    private boolean sink = false;
    private boolean foundShip = false;
    private OrientationType direction = OrientationType.NORTH;

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
	 * Randomly decides next move.
     * If hit in last turn, randomly choose a direction and continue on direction
     * If another hit, continue on direction.
     * If it has found a ship, will intelligently shoot around the ship until it dies.
	 */
	private void nextMove() {
        if ((sink)||(!foundShip)) {
            usedDirections.clear();
            move = getRandomMove();
        } else {
            boolean badCoordinate = false;
            while (true) {
                //if miss, select an untried direction, otherwise keep direction
                if ((!hit)||badCoordinate) {
                    usedDirections.add(direction);
                    OrientationType newDirection = OrientationType.values()[(direction.ordinal()+2)%4];
                    Random rand = new Random();
                    if (usedDirections.size() == 4) {
                        usedDirections.clear();
                        lastHits.removeLast();
                    }
                    while (usedDirections.contains(newDirection)) {
                        newDirection = OrientationType.values()[rand.nextInt(4)];
                    }
                    direction = newDirection;
                }
                String lastHit = lastHits.getLast();
                char left = lastHit.charAt(0);
                int right = Integer.parseInt(lastHit.substring(1, lastHit.length()));
                switch (direction) {
                    case WEST:
                        left = (char) (left - 1);
                        break;

                    case NORTH:
                        right = right + 1;
                        break;

                    case EAST:
                        left = (char) (left + 1);
                        break;

                    case SOUTH:
                        right = right - 1;
                        break;
                }
                String newMove = String.valueOf(Character.toUpperCase(left)) + right;
                //Validate Move
                if (moves.contains(newMove) && BattleshipUtils.validateCoordinate(newMove)) {
                    moves.remove(newMove); //remove from moveset
                    move = newMove;
                    break;
                } else {
                    //repeat
                    badCoordinate = true;
                }
            }
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
                break;

            case HIT:
                hit = true;
                sink = false;
                foundShip = true;
                lastHits.add(move);
                break;

            case SINK:
                hit = true;
                sink = true;
                foundShip = false;
                usedDirections.clear();
                break;
        }
    }

    public void setOpponentMoveResult(MoveResult result) {}
}