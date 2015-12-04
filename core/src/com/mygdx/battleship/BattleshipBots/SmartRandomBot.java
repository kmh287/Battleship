package com.mygdx.battleship.BattleshipBots;

import java.util.*;

import com.mygdx.battleship.*;

public class SmartRandomBot extends BattleshipBot  {

    private final ArrayList<OrientationType> usedDirections = new ArrayList<>();
    private final LinkedList<String> moves = new LinkedList<>();
    private final LinkedList<String> lastHits = new LinkedList<>();

    //hunt validation
    private int huntHits = 0;
    private boolean huntOver = true;

    private boolean hit = false;
    private boolean foundShip = false;
    private OrientationType direction = OrientationType.NORTH;


	/**
	 * Constructor
	 */
	public SmartRandomBot(String name) {
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
     * Check if still need to hunt
     */
    private void huntCheck(MoveResult result) {
        BattleshipType sunk = result.getSunkShip();
        switch (sunk) {
            case DESTROYER:
                if (huntHits == 2) {
                    huntHits = 0;
                    huntOver = true;
                } else {
                    huntHits  = huntHits - 2;
                    huntOver = false;
                }
                break;
            case CRUISER:
                if (huntHits == 3) {
                    huntHits = 0;
                    huntOver = true;
                } else {
                    huntHits  = huntHits - 3;
                    huntOver = false;
                }
                break;
            case SUBMARINE:
                if (huntHits == 3) {
                    huntHits = 0;
                    huntOver = true;
                } else {
                    huntHits  = huntHits - 3;
                    huntOver = false;
                }
                break;
            case BATTLESHIP:
                if (huntHits == 4) {
                    huntHits = 0;
                    huntOver = true;
                } else {
                    huntHits  = huntHits - 4;
                    huntOver = false;
                }
                break;
            case CARRIER:
                if (huntHits == 5) {
                    huntHits = 0;
                    huntOver = true;
                } else {
                    huntHits  = huntHits - 5;
                    huntOver = false;
                }
                break;
        }
    }

    private boolean directionCheck(String coord) {
        char left = coord.charAt(0);
        int right = Integer.parseInt(coord.substring(1, coord.length()));
        boolean chk1 = moves.contains(String.valueOf(Character.toUpperCase((char) (left-1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left-1))) + right);
        boolean chk2 = moves.contains(String.valueOf(Character.toUpperCase((char) (left+1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left+1))) + right);
        boolean chk3 = moves.contains(String.valueOf(Character.toUpperCase((left))) + (right+1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right+1));
        boolean chk4 = moves.contains(String.valueOf(Character.toUpperCase((left))) + (right-1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right-1));
        return (chk1 || chk2 || chk3 || chk4);
    }

    /**
     * Randomly decides next move.
     * If hit in last turn, randomly choose a direction and continue on direction
     * If another hit, continue on direction.
     * If it has found a ship, will intelligently shoot around the ship until it dies.
     */
    private void nextMove() {
        if ((!foundShip)&&(huntOver)) {
            lastHits.clear();
            usedDirections.clear();
            move = getRandomMove();
        } else {
            boolean badCoordinate = false;
            while (true) {
                //if miss, select an untried direction, otherwise keep direction
                if ((!hit)||badCoordinate) {
                    usedDirections.add(direction);
                    //select new direction
                    OrientationType newDirection = OrientationType.values()[(direction.ordinal()+2)%4];
                    Random rand = new Random();
                    //if all directions have been tried,
                    if (usedDirections.size() == 4) {
                        usedDirections.clear();
                        if (!directionCheck(lastHits.getLast())) {
                            lastHits.removeLast();
                        }
                    }
                    while (usedDirections.contains(newDirection)) {
                        newDirection = OrientationType.values()[rand.nextInt(4)];
                    }
                    direction = newDirection;
                }
                String lastHit = lastHits.getLast();
                char left = lastHit.charAt(0);
                int right = Integer.parseInt(lastHit.substring(1, lastHit.length()));
                switch (direction) {
                    case WEST:
                        left = (char) (left - 1);
                        break;
                    case NORTH:
                        right = right + 1;
                        break;
                    case EAST:
                        left = (char) (left + 1);
                        break;
                    case SOUTH:
                        right = right - 1;
                        break;
                }
                String newMove = String.valueOf(Character.toUpperCase(left)) + right;
                //Validate Move
                if (moves.contains(newMove) && BattleshipUtils.validateCoordinate(newMove)) {
                    moves.remove(newMove); //remove from moveset
                    move = newMove;
                    break;
                } else {
                    //repeat
                    badCoordinate = true;
                }
            }
        }
	}

    /**
     * Calculates and returns unplayed random move.
     */
    private String getRandomMove() {
        if(moves.size() > 0) {
            String move = moves.get(0);
            moves.remove(0);
            return move;
        } else{
            return "A1";
        }
    }

    /**
     * returns the next move
     */
    public String getMove() {
        nextMove();
        return move;
    }

    /**
     * Sets the result of bot's move the last turn.
     */
    public void setMyMoveResult(MoveResult result) {
        ResultType res = result.getResult();
        switch (res) {
            case MISS:
                hit = false;
                break;
            case HIT:
                hit = true;
                foundShip = true;
                lastHits.addLast(move);
                huntHits++;
                break;
            case SINK:
                hit = true;
                foundShip = false;
                usedDirections.clear();
                lastHits.addLast(move);
                huntHits++;
                huntCheck(result);
                break;
        }
    }

    public void setOpponentMoveResult(MoveResult result) {}
}