package com.mygdx.battleship;

import java.util.ArrayList;

public class State  {
	public ArrayList<BattleshipType> shipsLeft;
	public ArrayList<String> shotsTaken;

    public State() {
        shipsLeft = new ArrayList<>();
        shipsLeft.add(BattleshipType.CRUISER);
        shipsLeft.add(BattleshipType.DESTROYER);
        shipsLeft.add(BattleshipType.SUBMARINE);
        shipsLeft.add(BattleshipType.BATTLESHIP);
        shipsLeft.add(BattleshipType.CARRIER);
        shotsTaken = new ArrayList<>();
    }

	public State(State s) {
		shipsLeft = new ArrayList<>(s.shipsLeft);
		shotsTaken = new ArrayList<>(s.shotsTaken);
	}
	
	public State(ArrayList<BattleshipType> ships, ArrayList<String> shots) {
		shipsLeft = new ArrayList<>(ships);
		shotsTaken = new ArrayList<>(shots);
	}

    @Override
    public int hashCode() {
        return shipsLeft.hashCode() + shotsTaken.hashCode();
    }

	@Override
	public boolean equals(Object other) {
        if (other instanceof State) {
            State s = (State) other;
            // check if all elements are equal regardless of position
            if ((this.shotsTaken.size() == s.shotsTaken.size()) && (this.shipsLeft.size() == s.shipsLeft.size())) {
                boolean sameShips = true;
                for (BattleshipType ship : this.shipsLeft) {
                    sameShips = sameShips && s.shipsLeft.contains(ship);
                }
                for (BattleshipType ship : s.shipsLeft) {
                    sameShips = sameShips && this.shipsLeft.contains(ship);
                }
                if (sameShips) {
                    boolean sameShots = true;
                    for (String shot : this.shotsTaken) {
                        sameShots = sameShots && s.shotsTaken.contains(shot);
                    }
                    for (String shot : s.shotsTaken) {
                        sameShots = sameShots && this.shotsTaken.contains(shot);
                    }
                    return sameShots;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ArrayList<BattleshipType> getShipsLeft() {
        return shipsLeft;
    }
    public ArrayList<String> getShotsTaken() {
        return shotsTaken;
    }
}