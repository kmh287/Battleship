package com.mygdx.battleship.BattleshipBots;

import com.mygdx.battleship.MoveResult;
import com.mygdx.battleship.ResultType;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Statbot extends BattleshipBot {

    public static final String STANDARD_PATH = "bots/statbot/";
    private PriorityQueue<String> moves =
            new PriorityQueue<>(100, new MoveComparator());
    private final JSONObject json;
    private final String path;

    public Statbot(String name) {
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

    @Override
    public String getMove() {
        String move = moves.poll();
        return move != null ? move : "A0";
    }

    @Override
    public void setMyMoveResult(MoveResult result) {
        if (result.getResult() != ResultType.MISS){
            //Update stats
            String coordinate = result.getCoordinate();
            int currVal = json.optInt(coordinate,0);
            json.put(coordinate, currVal + 1);
        }
        if (result.getResult() == ResultType.WIN) {
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
