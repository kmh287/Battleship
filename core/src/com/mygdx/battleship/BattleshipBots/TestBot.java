package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.BattleshipUtils;
import com.mygdx.battleship.MoveResult;


public class TestBot extends BattleshipBot  {

    private final List<String> moves = new ArrayList<>(100);

    /**
     * Constructor
     */
    public TestBot(String name) {
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