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

public class GameResult {

    public long timestamp;
    public int points;
    public GameLevel gameLevel;
    public long secondsPlayed;
    public int difficulty;

    public GameResult(){}

    public GameResult(long timestamp, int points, GameLevel gameLevel, long secondsPlayed, int difficulty) {
        this.timestamp = timestamp;
        this.points = points;
        this.gameLevel = gameLevel;
        this.secondsPlayed = secondsPlayed;
        this.difficulty = difficulty;
    }
}
