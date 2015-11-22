package com.mygdx.battleship;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the map for the *entire board*
 * including both player's fleet placements and target board.
 *
 * This class should *not* be instantiated more than once.
 */
public class BattleshipMap {

    /** File paths for the different tile textures **/
    private static final String WATER = "assets/water.png";
    private static final String SHIP = "assets/ship.png";
    private static final String MISS = "assets/miss.png";
    private static final String HIT = "assets/hit.png";
    private static final String FOG = "assets/fog.png";
    private static final String HORIZONTAL_BLACK_BAR = "assets/horizontalBlackBar.png";
    private static final String VERTICAL_BLACK_BAR = "assets/verticalBlackBar.png";

    /** The actual textures **/
    private static Texture waterTexture;
    private static Texture shipTexture;
    private static Texture missTexture;
    private static Texture hitTexture;
    private static Texture fogTexture;
    private static Texture horizontalBlackBar;
    private static Texture verticalBlackBar;

    private enum TILE_TYPE{
        WATER, //The most common tile, used to represent unoccupied water
        SHIP,  //Used to represent the position of a ship
        MISS,  //Used on the target board to indicate a miss
        HIT,   //Used on the target board and on the fleet board to indicate a hit
        FOG;    //Only used when fog of war is turned on (player 1 is human)

        public Texture getTexture(){
            switch(this){
                case WATER:
                    return waterTexture;
                case SHIP:
                    return shipTexture;
                case MISS:
                    return missTexture;
                case HIT:
                    return hitTexture;
                case FOG:
                    return fogTexture;
                default:
                    return null;
            }
        }
    }

    /* Player 1's 'bottom' board, showing player 1's ship positions */
    private final TILE_TYPE[][] player1Fleet = new TILE_TYPE[10][10];
    /* Player 2's 'bottom' board, showing player 2's ship positions */
    private final TILE_TYPE[][] player2fleet = new TILE_TYPE[10][10];
    /* Player 1's top board, showing where player 1 has fired */
    private final TILE_TYPE[][] player1targets = new TILE_TYPE[10][10];
    /* Player 2's top board, showing where player 2 has fired */
    private final TILE_TYPE[][] player2targets = new TILE_TYPE[10][10];

    private final boolean fog;

    private static boolean assetsLoaded = false;

    /**
     * Constructor for the BattleshipMap.
     * @param fog Whether or not player 2's fleet should be shrouded by the fog of war
     * @param player1Ships *ALREADY VALIDATED* input from player 1 about ship positions
     * @param player2Ships *ALREADY VALIDATED* input from player 2 about ship positions
     */
    public BattleshipMap(boolean fog, AssetManager assetManager,
                         String[][] player1Ships, String[][] player2Ships){
        this.fog = fog;
        if(!assetsLoaded){
            loadAssets(assetManager);
            assetsLoaded = true;
        }
        setupShips(player1Ships, player1Fleet);
        setupTargets(player1targets);
        if (fog){
            setupFog();
        } else {
            setupShips(player2Ships, player2fleet);
            setupTargets(player2targets);
        }
    }

    /**
     * Place fog on player 2's fleet board and target board if player 1 is human
     * and fog of war is turned on.
     */
    private void setupFog(){
        for (int i = 0; i < player2fleet.length; ++i){
            for (int j = 0; j < player2fleet[0].length; ++j){
                player2fleet[i][j] = TILE_TYPE.FOG;
                //Take advantage of identical dimensions!
                player2targets[i][j] = TILE_TYPE.FOG;
            }
        }
    }

    private void setupShips(String[][] ships, TILE_TYPE[][] fleet){
        //First, setup ship spots
        for (String[] ship : ships){
            for (String point : ship){
                // Shift the character down
                int col = BattleshipUtils.getColumnFromCoordinate(point);
                //If the string has 3 characters then it's 10, otherwise it's just the int version of the digit
                int row = BattleshipUtils.getRowFromCoordinate(point);
                fleet[row][col] = TILE_TYPE.SHIP;
            }
        }

        //Fill the rest with water
        for (int i = 0; i < fleet.length; ++i){
            for (int j = 0; j < fleet[0].length; ++j){
                if (fleet[i][j] == null){
                    fleet[i][j] = TILE_TYPE.WATER;
                }
            }
        }
    }

    /**
     * Fill the target boards with water.
     * @param targets the target board to fill
     */
    private void setupTargets(TILE_TYPE[][] targets){
        for (int i = 0; i < targets.length; ++i){
            for (int j = 0; j < targets[0].length; ++j){
                targets[i][j] = TILE_TYPE.WATER;
            }
        }
    }

    /**
     * Set a selected coordinate to . This will update the attacking player's
     * target board, as well as the receiving player's fleet if attack succeeds
     * @param attackingPlayer The attacking player
     * @param coordinate The coordinate of the attack
     */
    public void updateMap(Player attackingPlayer, String coordinate, boolean hit){

        int row = BattleshipUtils.getRowFromCoordinate(coordinate);
        int col = BattleshipUtils.getColumnFromCoordinate(coordinate);

        //Always update target board, only update player 2 fleet if fog is off and hit
        if (attackingPlayer == Player.PLAYER_1){
            player1targets[row][col] = hit? TILE_TYPE.HIT : TILE_TYPE.MISS;
            if(!fog && hit){
                player2fleet[row][col] = TILE_TYPE.HIT;
            }
        }
        //Only update target board if fog is off, update player 1 fleet if hit
        else {
            if (!fog){
                player2targets[row][col] = hit? TILE_TYPE.HIT : TILE_TYPE.MISS;
            }
            if (hit){
                player1Fleet[row][col] = TILE_TYPE.HIT;
            }
        }
    }

    public void draw(GameCanvas canvas){
        // Tile textures are 45px x 45px
        // X coordinate of first tile for player 2
        int board2HorizontalOffset = 10 * 45 + 100;
        // Y coordinate of target board
        int targetBoardOffset = 10 * 45 + 50;

        //Take advantage of equivalent dimensions
        //Remember, coordinates start at the bottom right
        for (int i = 0; i < 10; ++i){
            for (int j = 0; j < 10; ++j){
                //Bottom left
                canvas.draw(player1Fleet[i][j].getTexture(), i * 45, j * 45);
                //Top left
                canvas.draw(player1targets[i][j].getTexture(), i * 45, j * 45 + targetBoardOffset);
                //Bottom right
                canvas.draw(player2fleet[i][j].getTexture(), i * 45 + board2HorizontalOffset, j * 45);
                //Top right
                canvas.draw(player2targets[i][j].getTexture(), i * 45 + board2HorizontalOffset, j * 45 + targetBoardOffset);
            }
        }

        //Draw blackbars
        canvas.draw(horizontalBlackBar, 0, 450);
        canvas.draw(verticalBlackBar, 450, 0);
    }

    private static void loadAssets(AssetManager manager){
        manager.load(WATER, Texture.class);
        manager.load(SHIP, Texture.class);
        manager.load(MISS, Texture.class);
        manager.load(HIT, Texture.class);
        manager.load(FOG, Texture.class);
        manager.load(HORIZONTAL_BLACK_BAR, Texture.class);
        manager.load(VERTICAL_BLACK_BAR, Texture.class);

        manager.finishLoading();

        waterTexture = (Texture) (manager.isLoaded(WATER) ? manager.get(WATER) : null);
        shipTexture = (Texture) (manager.isLoaded(SHIP) ? manager.get(SHIP) : null);
        missTexture = (Texture) (manager.isLoaded(MISS) ? manager.get(MISS) : null);
        hitTexture = (Texture) (manager.isLoaded(HIT) ? manager.get(HIT) : null);
        fogTexture = (Texture) (manager.isLoaded(FOG) ? manager.get(FOG) : null);
        horizontalBlackBar = (Texture) (manager.isLoaded(HORIZONTAL_BLACK_BAR) ? manager.get(HORIZONTAL_BLACK_BAR) : null);
        verticalBlackBar = (Texture) (manager.isLoaded(VERTICAL_BLACK_BAR) ? manager.get(VERTICAL_BLACK_BAR) : null);
    }

}
