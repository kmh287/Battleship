package com.mygdx.battleship;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class GridState {
	private final HashMap<String,Float> grid = new HashMap<>();
	private final ArrayList<String> coordinates = new ArrayList<>();

    //initialize with parity of 2 (every other square)
	public GridState() {
		//create parity arrays
		for (int i = 0; i < 10; i++) {
			for (int j = (i%2); j < 10; j=j+2) {
				char l = (char) (i + 'a');
				String c = String.valueOf(Character.toUpperCase(l)) + (j+1);
				coordinates.add(c);
			}
		}

        //map all values to 0
        for (int i = 0; i < 10; i++) {
            for (int j = 1; j <= 10; j++) {
                char l = (char) (i + 'a');
                String c = String.valueOf(Character.toUpperCase(l))  + (j);
                grid.put(c,0.0f);
            }
        }
	}
	
	//returns a string list sorted from coordinate with highest value to lowest value
	public String getBestShot(ArrayList<String> usedMoves) {
        //return optimal choice
		String move = coordinates.get(0);
		float big = grid.get(move);
		for (int i = 1; i < coordinates.size(); i++) {
			float y = grid.get(coordinates.get(i));
			if ((y >= big)&&(!usedMoves.contains(coordinates.get(i)))) {
				move = coordinates.get(i);
				big = y;
			}
		}
		return move;
	}
	
	//returns highest value in hashmap
	public float highestValue() {
		return Collections.max(grid.values());
	}
	
	public void update(String coor, boolean res, float max) {
		float prevValue = grid.get(coor);
		float q = learning(prevValue, res, max);
		grid.remove(coor); //remove old value
		grid.put(coor,q); //add new value
	}

	//Q-learning equation
	private float learning(float oldValue, boolean res, float maxV) {
		//reward values
		final float hit = 2;
		final float miss = -1.4f;
		
		float reward;
		if (res) {
			reward = hit;
		} else {
			reward = miss;
		}

		//equation
		float alpha = 0.6f; //learns but not impossible to unlearn
		float lambda = 1;
		float newValue = oldValue + alpha * (reward + (lambda*maxV) - oldValue);
		return newValue;
	}

    public HashMap<String,Float> getGrid() {
        return grid;
    }

    public ArrayList<String> getCoordinates() {
        return coordinates;
    }
}