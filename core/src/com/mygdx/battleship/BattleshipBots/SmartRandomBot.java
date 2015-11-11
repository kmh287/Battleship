package com.mygdx.battleship.BattleshipBots;

import java.util.ArrayList;
import java.util.Random;

import com.mygdx.battleship.BattleshipType;
import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.ResultType;


public class SmartRandomBot extends BattleshipBot  {

    private boolean hit = false;
    private boolean sink = false;
    private boolean foundShip = true;
    private int direction = 0;
    private ArrayList usedDirections = new ArrayList<>();

	/**
	 * Constructor
	 */
	public SmartRandomBot(String name) {
		super(name);
	}
	
	/**
	 * Randomly decides next move.
     * If hit in last turn, randomly choose a direction and continue on direction
     * If another hit, continue on direction.
     * If it has found a ship, will intelligently shoot around the ship until it dies.
	 */
	private void nextMove() {
        if (sink) {
            move = getRandomMove();
        } else if (foundShip) {
            //if miss, select an untried direction, otherwise keep direction.
            if (!hit) {
                usedDirections.add(direction);
                Random rand = new Random();
                int newDirection = rand.nextInt();
                while (usedDirections.contains(newDirection)) {
                    newDirection = rand.nextInt();
                }
                direction = newDirection;
            }
            char left = move.charAt(0);
            int right = Integer.parseInt(move.substring(1,move.length()));
            if (direction == 0) { // north
                left = (char) (left - 1);
            } else if (direction == 1) { // east
                right = right + 1;
            } else if (direction == 2) { // south
                left = (char) (left + 1);
            } else { // west
                right = right - 1;
            }
            move = String.valueOf(Character.toUpperCase(left)) + right;
        } else {
            move = getRandomMove();
        }
	}

    /**
     * Calculates and returns a random move.
     */
    private String getRandomMove() {
        Random rand1 = new Random();
        Random rand2 = new Random();

        //decide random letter A -> J
        String str = String.valueOf(Character.toUpperCase((char) (rand1.nextInt(10) + 'a')));
        //set move
        return str + ("" + (rand2.nextInt(10) + 1));
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