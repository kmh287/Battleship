package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.MoveResult;

public interface BattleshipBot {

    String[][] getShipPlcements();

    //A1....J10
    String getMove();

    void setMyMoveResult(MoveResult result);

    void setOpponentMoveResult(MoveResult result);
}