package com.mygdx.battleship.BattleshipBots;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mygdx.battleship.*;

import java.util.*;
import java.io.*;

public class QBot extends BattleshipBot  {

    private final File state_file;
    private final File grid_file;

    public static final String STANDARD_PATH = "bots/Qbot/";
    private final ArrayList<String> usedMoves = new ArrayList<>();
    private  HashMap<State,GridState> allStates;
    private final ArrayList<BattleshipType> shipsLeft = new ArrayList<>();
    private State curState;
    private State lastState;
    private ObjectMapper mapper = new ObjectMapper();

    //hunt variables
    private final ArrayList<OrientationType> usedDirections = new ArrayList<>();
    private final LinkedList<String> lastHits = new LinkedList<>();
    private int huntHits = 0;
    private boolean huntOver = true;
    private boolean hit = false;
    private boolean foundShip = false;
    private OrientationType direction = OrientationType.NORTH;

    /**
     * Constructor
     */
    public QBot(String name) {
        super(name);
        state_file = new File(STANDARD_PATH + name + "_state" + ".txt");
        grid_file = new File(STANDARD_PATH + name + "_grid" + ".txt");
        if (!state_file.exists()) {
            try {
                state_file.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        if (!grid_file.exists()) {
            try {
                grid_file.createNewFile();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        //place ships
        shipPlacements();

        //initialize shipsLeft to all ships for new round
        shipsLeft.add(BattleshipType.CRUISER);
        shipsLeft.add(BattleshipType.DESTROYER);
        shipsLeft.add(BattleshipType.SUBMARINE);
        shipsLeft.add(BattleshipType.BATTLESHIP);
        shipsLeft.add(BattleshipType.CARRIER);

        //set up grid states
        try {
            ArrayList<State> keys = mapper.readValue(state_file, new TypeReference<ArrayList<State>>(){});
            ArrayList<GridState> maps = mapper.readValue(grid_file, new TypeReference<ArrayList<GridState>>(){});
            allStates = new HashMap<>();
            System.out.println(keys.size());
            for (int i = 0; i < keys.size(); i++) {
                allStates.put(keys.get(i), maps.get(i));
            }
            curState = new State(shipsLeft, usedMoves);
            lastState = new State(shipsLeft, usedMoves);
        } catch (IOException e) {
            System.out.println("No files found. Writing new files.");
            allStates = new HashMap<>();
            curState = new State(shipsLeft, usedMoves);
            lastState = new State(shipsLeft, usedMoves);
            GridState fresh = new GridState();
            allStates.put(curState, fresh);
        }
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
        boolean chk1 = (!usedMoves.contains(String.valueOf(Character.toUpperCase((char) (left-1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left-1))) + right));
        boolean chk2 = (!usedMoves.contains(String.valueOf(Character.toUpperCase((char) (left+1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left+1))) + right));
        boolean chk3 = (!usedMoves.contains(String.valueOf(Character.toUpperCase((left))) + (right+1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right+1)));
        boolean chk4 = (!usedMoves.contains(String.valueOf(Character.toUpperCase((left))) + (right-1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right-1)));
        return (chk1 || chk2 || chk3 || chk4);
    }

    private void checkStates(State curState) {
        if (allStates.get(curState) == null) {
            GridState fresh = new GridState();
            allStates.put(curState, fresh);
        }
        else {
//            System.out.println("hello");
        }
    }

    private void updateState(MoveResult result) {
        lastState = curState;
        ResultType res = result.getResult();
        float max;
        switch (res) {
            case MISS:
                curState = new State(shipsLeft, usedMoves);
                checkStates(curState);
                max = allStates.get(curState).highestValue();
                allStates.get(lastState).update(move, false, max);
                break;

            case HIT:
                curState = new State(shipsLeft, usedMoves);
                checkStates(curState);
                max = allStates.get(curState).highestValue();
                allStates.get(lastState).update(move, false, max);
                break;

            case SINK:
                shipsLeft.remove(result.getSunkShip());
                curState = new State(shipsLeft, usedMoves);
                checkStates(curState);
                max = allStates.get(curState).highestValue();
                allStates.get(lastState).update(move, false, max);
                break;

            case WIN:
                shipsLeft.remove(result.getSunkShip());
                curState = new State(shipsLeft, usedMoves);
                checkStates(curState);
                max = allStates.get(curState).highestValue();
                allStates.get(lastState).update(move, false, max);
                //serialize data
                try {
                    mapper.writeValue(state_file, allStates.keySet());
                    mapper.writeValue(grid_file, allStates.values());
//                    mapper.writeValue(file, allStates);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                break;
        }
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
            checkStates(curState);
            move = allStates.get(curState).getBestShot(usedMoves);
            usedMoves.add(move);
        } else {
            boolean badCoordinate = false;
            while (true) {
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
                if ((!usedMoves.contains(newMove)) && BattleshipUtils.validateCoordinate(newMove)) {
                    usedMoves.add(newMove);
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

            case WIN:
                break;
        }
        updateState(result);
    }

    public void setOpponentMoveResult(MoveResult result) {}
}