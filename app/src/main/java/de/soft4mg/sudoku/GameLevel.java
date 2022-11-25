/*
    MGSudoku - a free sudoku game
    Copyright (C) 2022  soft4mg

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.soft4mg.sudoku;

public enum GameLevel {

    EASY(0,50, 0, 170),
    MEDIUM(51, 150, 171, 300),
    HARD(151, 400, 301, 600),
    EXTRA(401, 20000,601, 200000);

    final int min3;
    final int max3;
    final int min4;
    final int max4;

    GameLevel(int min3, int max3, int min4, int max4){
        this.min3 = min3;
        this.max3 = max3;
        this.min4 = min4;
        this.max4 = max4;
    }

    public static GameLevel get(int dim, int difficulty){
        if (dim == 3){
            for (GameLevel gameLevel : values()){
                if ((gameLevel.min3 <= difficulty) && (difficulty <= gameLevel.max3)) return gameLevel;
            }
        } else if (dim == 4){
            for (GameLevel gameLevel : values()){
                if ((gameLevel.min4 <= difficulty) && (difficulty <= gameLevel.max4)) return gameLevel;
            }
        }
        return null;
    }

    public static String[] stringValues(){
        GameLevel[] values = values();
        String[] res = new String[values.length];
        for (int i=0; i<values.length; i++){
            res[i] = values[i].toString();
        }
        return res;
    }
}
