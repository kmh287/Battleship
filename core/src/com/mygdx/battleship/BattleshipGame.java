package com.mygdx.battleship;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.battleship.BattleshipBots.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BattleshipGame implements Screen {

	private static int RETRY_THRESHOLD = 3;
	private static int SLEEP_TIME_BETWEEN_TURNS = 0; //miliseconds

    //How many times should the game play
    //TODO take as user input
    private int numGamesPlayed = 0;
    private int numGamesToPlay = 1000;

    int[] wins = new int[]{0,0};

	private final GameCanvas canvas;
	private BattleshipMap map;
	private BattleshipBot player1Bot;
	private BattleshipBot player2Bot;

	//Need two maps per player to keep track of meta data
	//One maps coordinates to ships so we can check if a coordinate is a hit
	//One maps ships to sets of coordinates, so we can tally damage and report on sinks
	private final Map<String, BattleshipType> player1CoordinateMap = new HashMap<>();
	private final Map<BattleshipType, Set<String>> player1ShipMap = new HashMap<>();
	private final Map<String, BattleshipType> player2CoordinateMap = new HashMap<>();
	private final Map<BattleshipType, Set<String>> player2ShipMap = new HashMap<>();

    private String[][] fleet1;
    private String[][] fleet2;

    private boolean gameOver = false;

	public BattleshipGame (GameCanvas canvas){
		this.canvas = canvas;
        setup();
	}

    private void reset(){
        player1CoordinateMap.clear();
        player2CoordinateMap.clear();
        player1ShipMap.clear();
        player2ShipMap.clear();
        gameOver = false;
        setup();
    }

    private void setup(){
        this.player1Bot = getPlayer1();
        this.player2Bot = getPlayer2();
        validateFleets();
        setupMetadata();
        this.map = new BattleshipMap(false, new AssetManager(), fleet1, fleet2);
    }

	private void validateFleets(){
		String[][] player1Fleet = null;
		String[][] player2Fleet = null;

		int tries = 0;
		// Validate player 1
		while(true) {
			player1Fleet = player1Bot.getShipPlacements();
			if (BattleshipUtils.validateFleet(player1Fleet)){
                fleet1 = player1Fleet;
				break;
			} else if (++tries >= RETRY_THRESHOLD){
				System.out.println("Player 1 loses due to bad placement");
				System.exit(-1);
			}
		}

		tries = 0;

		// Validate player 2
		while(true) {
			player2Fleet = player2Bot.getShipPlacements();
			if (BattleshipUtils.validateFleet(player2Fleet)){
                fleet2 = player2Fleet;
				break;
			} else if (++tries >= RETRY_THRESHOLD){
				System.out.println("Player 2 loses due to bad placement");
				System.exit(-1);
			}
		}
	}

	// Setup the data used to play the game such as
	private void setupMetadata(){
		String[][] player1Fleet = player1Bot.getShipPlacements();
		String[][] player2Fleet = player2Bot.getShipPlacements();

		//Put empty sets into ship -> set(coordinates) map
		for (BattleshipType type : BattleshipType.values()){
			player1ShipMap.put(type, new HashSet<>());
			player2ShipMap.put(type, new HashSet<>());
		}

		for (int i = 0; i < player1Fleet.length; ++i){
			Set<String> player1currentShipSet = player1ShipMap.get(BattleshipType.values()[i]);
			Set<String> player2currentShipSet = player2ShipMap.get(BattleshipType.values()[i]);
			for (int j = 0; j < player1Fleet[i].length; ++j){
				String player1CurrentCoordinate = player1Fleet[i][j];
				player1CoordinateMap.put(player1CurrentCoordinate, BattleshipType.values()[i]);
				player1currentShipSet.add(player1CurrentCoordinate);

				String player2CurrentCoordinate = player2Fleet[i][j];
				player2CoordinateMap.put(player2CurrentCoordinate, BattleshipType.values()[i]);
				player2currentShipSet.add(player2CurrentCoordinate);
			}
		}
	}

	private BattleshipBot getPlayer1(){
		//TODO replace, obviously
		return new Statbot("sb3");
	}

	private BattleshipBot getPlayer2(){
		//TODO replace, obviously
		return new RandomBot("b");
	}

	@Override
	public void render (float delta) {
        update(delta);
        draw(delta);
	}

	private void update(float delta) {
        if (!gameOver) {
            performMoveForPlayer(Player.PLAYER_1);
            performMoveForPlayer(Player.PLAYER_2);
            BattleshipUtils.sleep(SLEEP_TIME_BETWEEN_TURNS);
        } else {
            if (++numGamesPlayed < numGamesToPlay) {
                reset();
            } else {
                waitForUserInput();
                System.out.println("Player 1 Wins: " + wins[0] + "\n" +
                                   "Player 2 Wins: " + wins[1]);
                System.exit(0);
            }
        }
	}

	private void performMoveForPlayer(Player player){

		Player adversary = player.adversary();

		int tries = 0;
		String playerMove = null;
		while(true){
			playerMove = getBotForPlayer(player).getMove();
			if (BattleshipUtils.validateCoordinate(playerMove)){
				break;
			} else if (++tries >= RETRY_THRESHOLD){
				System.out.println("Player " + player.getPlayerNum() +
										   " loses due to bad move");
				System.exit(-1);
			}
		}

		Map<String, BattleshipType> enemyCoordinateMap = getCoordinateMapForPlayer(adversary);
		Map<BattleshipType, Set<String>> enemyShipMap = getShipMapForPlayer(adversary);

		//Fill in for now. This gets changed if themove is actually a hit.
		MoveResult result = new MoveResult(playerMove, ResultType.MISS, null);

		// If the coordinate belongs to an enemy ship
		if(enemyCoordinateMap.containsKey(playerMove)){
			BattleshipType type = enemyCoordinateMap.get(playerMove);
			// Get the coordinates that belong to that ship
			Set<String> shipCoordinates = enemyShipMap.get(type);
			// If those coordinates haven't been hit yet, record a HIT or SINK
			if (shipCoordinates.contains(playerMove)){
				shipCoordinates.remove(playerMove);
				if (shipCoordinates.size() == 0){
					result = new MoveResult(playerMove, ResultType.SINK, type);
				} else {
					result = new MoveResult(playerMove, ResultType.HIT, null);
				}
				map.updateMap(player, playerMove, true);
			}
		} else {
			map.updateMap(player, playerMove, false);
		}

		// Check victry conditions on sink
		if (result.getResult() == ResultType.SINK){
			if (checkIfPlayerHasWon(player)){
				// if the player has won, update the result
				result = new MoveResult(result.getCoordinate(), ResultType.WIN, result.getSunkShip());
			}
		}

		getBotForPlayer(player).setMyMoveResult(result);
		getBotForPlayer(adversary).setOpponentMoveResult(result);
		if (result.getResult() == ResultType.WIN){
			System.out.println("Player " + player.getPlayerNum() + " wins");
            wins[player.getPlayerNum()-1]++;
            gameOver = true;
		}
	}

	//Check to see if this player has won
	private boolean checkIfPlayerHasWon(Player player){
		Player adversary = player.adversary();
		Map<BattleshipType,Set<String>> shipMap = getShipMapForPlayer(adversary);
		for (Set<String> shipCoordinates : shipMap.values()){
			if (shipCoordinates.size() > 0) return false;
		}
		return true;

	}

	private void draw(float delta){
		canvas.begin();
		map.draw(canvas);
		canvas.end();
	}

	private Map<String,BattleshipType> getCoordinateMapForPlayer(Player player){
		return (player == Player.PLAYER_1) ? player1CoordinateMap : player2CoordinateMap;
	}

	private Map<BattleshipType, Set<String>> getShipMapForPlayer(Player player){
		return (player == Player.PLAYER_1) ? player1ShipMap : player2ShipMap;
	}

	private BattleshipBot getBotForPlayer(Player player){
		return (player == Player.PLAYER_1) ? player1Bot : player2Bot;
	}

    private void waitForUserInput(){
        try{
            System.in.read();
        } catch (Exception e){
            System.exit(-1);
        }
    }

/** Unused overrides **/

	@Override
	public void show () {

	}

	@Override
	public void resize (int width, int height) {

	}

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}

	@Override
	public void hide () {

	}

	@Override
	public void dispose () {

	}
}
