package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.MoveResult;


public class LoserBot extends BattleshipBot {

    public LoserBot(String name) {
        super(name);
        shipPlacements();
    }

    @Override
    public String getMove() {
        return "A1";
    }

    @Override
    public void setMyMoveResult(MoveResult result) {

    }

    @Override
    public void setOpponentMoveResult(MoveResult result) {

    }
}
