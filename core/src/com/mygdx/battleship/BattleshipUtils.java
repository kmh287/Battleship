package com.mygdx.battleship;

import java.util.HashSet;

public class BattleshipUtils {

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

    public static boolean validateFleet(String[][] fleet){
        if (fleet == null) return false;
        if (fleet.length != 5) return false;
        for (String[] ship : fleet){
            if (ship == null) return false;
        }
        if (fleet[0].length != 2) return false;
        if (fleet[1].length != 3) return false;
        if (fleet[2].length != 3) return false;
        if (fleet[3].length != 4) return false;
        if (fleet[4].length != 5) return false;
        // This is in a separate loop so that we can avoid
        // trying to validate a ship that is already bad due to incorrect length
        for (String[] ship : fleet){
            if (!validateShip(ship)) return false;
        }

        //Check no points overlap
        HashSet<String> coordinates = new HashSet<String>();
        for (String[] ship : fleet){
            for (String coordinate : ship){
                if (coordinates.contains(coordinate)) return false;
                coordinates.add(coordinate);
            }
        }
        return true;
    }

    public static boolean validateShip(String[] ship){
        if (ship == null) return false;
        int[] xCoords = new int[ship.length];
        int[] yCoords = new int[ship.length];
        for (int i = 0; i < ship.length; ++i){
            String coordinate = ship[i];
            if (!validateCoordinate(coordinate)) return false;
            xCoords[i] = getColumnFromCoordinate(coordinate);
            yCoords[i] = getRowFromCoordinate(coordinate);
        }

        boolean properVerticalShip = allElementsSame(xCoords) && allElementsSequential(yCoords);
        boolean properHorizontalShip = allElementsSame(yCoords) && allElementsSequential(xCoords);
        return properHorizontalShip ^ properVerticalShip;
    }

    /**
     * Return true if all elements are the same
     */
    public static boolean allElementsSame(int[] arr){
        if (arr == null || arr.length == 0) return true;
        int first = arr[0];
        for (int ele : arr) {
            if (ele != first) return false;
        }
        return true;
    }

    /**
     * Returns true if all elements are sequential
     *
     * [1,2,3,4,5] is good, [1,1,1] is not
     *
     * @param arr an int array
     */
    public static boolean allElementsSequential(int[] arr){
        if (arr == null || arr.length == 0) return true;
        int curr = arr[0];
        for (int i = 1; i < arr.length; ++i){
            if (arr[i] != ++curr) return false;
        }
        return true;
    }

    public static void sleep(long miliseconds){
        if (miliseconds > 0) {
            try {
                Thread.sleep(miliseconds);
            } catch (InterruptedException e) {

            }
        }
    }

}
