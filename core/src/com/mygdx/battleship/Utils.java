package com.mygdx.battleship;

import java.util.HashSet;

public class Utils {

    /**
     * Returns the __array index position__ for the given coordinate
     * so A10 returns 0, not 1; E1 returns 4, not 5.
     * @param coordinate A validated coordinate
     * @return [0,9]
     */
    public static int getRowFromCoordinate(String coordinate){
        return coordinate.charAt(0) - 'A';
    }

    /**
     * Returns the __array index position__ for the given coordinate
     * so A10 returns 9, not 10; A1 returns 0, not 1.
     * @param coordinate A validated coordinate
     * @return [0,9]
     */
    public static int getColumnFromCoordinate(String coordinate){
        return Integer.parseInt(coordinate.substring(1)) - 1;
    }

    public static boolean validateCoordinate(String coordinate){
        try {
            //A1...J10
            if (coordinate.length() != 2 && coordinate.length() != 3) return false;

            //Check first letter
            char colChar = coordinate.charAt(0);
            if ('A' > colChar || 'J' < colChar) return false;

            //Check row number between 1 and 10
            String rowNumberString = coordinate.substring(1);
            int rowNumber = Integer.parseInt(rowNumberString);
            return (1 <= rowNumber && rowNumber <= 10);

        } catch (Exception e){
            return false;
        }
    }

}
