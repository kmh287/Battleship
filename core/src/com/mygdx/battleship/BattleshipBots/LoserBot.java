package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.MoveResult;


public class LoserBot extends BattleshipBot {

    public LoserBot(String name) {
        super(name);
    }

    @Override
    public String[][] getShipPlacements() {
        return null;
    }

    @Override
    public String getMove() {
        return null;
    }

    @Override
    public void setMyMoveResult(MoveResult result) {

    }

    @Override
    public void setOpponentMoveResult(MoveResult result) {

    }
}
