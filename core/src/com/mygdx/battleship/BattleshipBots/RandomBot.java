package com.mygdx.battleship.BattleshipBots;

import java.util.Random;

import com.mygdx.battleship.MoveResult;


public class RandomBot extends BattleshipBot  {

	/**
	 * Constructor
	 */
	public RandomBot(String name) {
		super(name);
	}
	
	/**
	 * Randomly decides next move.
	 */
	private void nextMove() {
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

    public void setMyMoveResult(MoveResult result) {}

    public void setOpponentMoveResult(MoveResult result) {}
}