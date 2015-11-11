package com.mygdx.battleship;


public enum Player {
    PLAYER_1, PLAYER_2;

    public Player adversary(){
        switch(this){
            case PLAYER_1:
                return PLAYER_2;
            case PLAYER_2:
                return PLAYER_1;
            default:
                return null;
        }
    }

    public int getPlayerNum(){
        return this.ordinal()+1;
    }

}
