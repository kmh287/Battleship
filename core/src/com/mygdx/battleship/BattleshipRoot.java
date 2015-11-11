package com.mygdx.battleship;


import com.badlogic.gdx.Game;

public class BattleshipRoot extends Game {
    @Override
    public void create () {
        GameCanvas canvas = new GameCanvas();
        BattleshipGame battleshipGame = new BattleshipGame(canvas);
        setScreen(battleshipGame);
    }
}
