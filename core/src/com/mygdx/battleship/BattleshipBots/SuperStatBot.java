package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.*;
import com.mygdx.battleship.JSON.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SuperStatBot extends BattleshipBot {

    public static final String STANDARD_PATH = "bots/superstatbot/";
    private PriorityQueue<String> moves =
            new PriorityQueue<>(100, new MoveComparator());
    private final JSONObject json;
    private final String path;

    private final LinkedList<String> lastHits = new LinkedList<>();
    private int huntHits = 0;
    private boolean huntOver = true;
    private boolean hit = false;
    private boolean foundShip = false;
    private OrientationType direction = OrientationType.NORTH;
    private final ArrayList<OrientationType> usedDirections = new ArrayList<>();

    public SuperStatBot(String name) {
        super(name);
        path = new File(STANDARD_PATH + name).getAbsolutePath();
        shipPlacements();
        json = getStats();

        for (int i = 0; i < 10; ++i){
            for (int j = 1; j <= 10; ++j){
                char row = (char) (i + 'A');
                moves.add("" + row + j);
            }
        }
    }

    private JSONObject getStats(){
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String s = new String(encoded, Charset.defaultCharset());
            return new JSONObject(s);
        } catch (IOException e){
            return new JSONObject();
        }
    }


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
        boolean chk1 = (moves.contains(String.valueOf(Character.toUpperCase((char) (left-1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left-1))) + right));
        boolean chk2 = (moves.contains(String.valueOf(Character.toUpperCase((char) (left+1))) + right)
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((char) (left+1))) + right));
        boolean chk3 = (moves.contains(String.valueOf(Character.toUpperCase((left))) + (right+1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right+1)));
        boolean chk4 = (moves.contains(String.valueOf(Character.toUpperCase((left))) + (right-1))
                && BattleshipUtils.validateCoordinate(String.valueOf(Character.toUpperCase((left))) + (right-1)));
        return (chk1 || chk2 || chk3 || chk4);
    }

    private void nextMove() {
        if ((!foundShip)&&(huntOver)) {
            lastHits.clear();
            usedDirections.clear();
            move = moves.poll();
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


    @Override
    public String getMove() {
        nextMove();
        return move;
    }

    @Override
    public void setMyMoveResult(MoveResult result) {
        ResultType res = result.getResult();
        String coordinate = result.getCoordinate();
        int currVal = json.optInt(coordinate,0);
        switch (res) {
            case MISS:
                hit = false;
                break;
            case HIT:
                //update stats
                json.put(coordinate, currVal + 1);
                hit = true;
                foundShip = true;
                lastHits.addLast(move);
                huntHits++;
                break;
            case SINK:
                //update stats
                json.put(coordinate, currVal + 1);
                hit = true;
                foundShip = false;
                usedDirections.clear();
                lastHits.addLast(move);
                huntHits++;
                huntCheck(result);
                break;
            case WIN:
                saveStats();
        }
    }

    @Override
    public void setOpponentMoveResult(MoveResult result) {
        if (result.getResult() == ResultType.WIN){
            saveStats();
        }
    }

    private void saveStats(){
        int numGamesPlayed = json.optInt("numGames", 0)+1;
        json.put("numGames", numGamesPlayed);
        try {
            File jsonFile = new File(STANDARD_PATH + name);
            jsonFile.getParentFile().mkdirs();
            FileWriter fw = new FileWriter(path);
            fw.write(json.toString());
            fw.flush();
            fw.close();
        } catch (IOException e){
            System.out.println("Unable to save stats");
        }
    }

    private class MoveComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int val1 = json.optInt(o1, 0);
            int val2 = json.optInt(o2, 0);
            //Want best moves at the front of the queue
            //So report they have 'lesser' value
            return val2 - val1;
        }

        @Override
        public boolean equals(Object obj) {
            return false;
        }
    }
}
