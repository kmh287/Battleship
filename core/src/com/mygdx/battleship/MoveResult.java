package com.mygdx.battleship;


public class MoveResult {

    private final String coordinate;
    private final BattleshipType sunkShip;
    private final ResultType result;

    public MoveResult(String coordinate, ResultType result,
                      BattleshipType sunkShip){
        this.coordinate = coordinate;
        this.result = result;
        this.sunkShip = sunkShip;
    }

}
