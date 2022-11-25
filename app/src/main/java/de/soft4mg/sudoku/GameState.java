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

import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private boolean finished;
    private int errorCounter;
    private long secondsPlayed;
    private GameModel gameModel;
    private CellModel selectedCell;
    private boolean candidatesUsed;
    private ArrayList<List<CellModel>> undoList = new ArrayList<>();


    @SuppressWarnings("unused") // required for JSON
    public GameState(){}

    public GameState(GameModel gameModel) {
        this.gameModel = gameModel;
        finished = false;
        secondsPlayed = 0;
        errorCounter = 0;
        undoList.add( gameModel.getResetChanged(true, null) );
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted") // needed for JSON
    public boolean isFinished() {
        return finished;
    }
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public int getErrorCounter() {
        return errorCounter;
    }
    public void setErrorCounter(int errorCounter) {
        this.errorCounter = errorCounter;
    }

    public long getSecondsPlayed() {
        return secondsPlayed;
    }
    public void setSecondsPlayed(long secondsPlayed) {
        this.secondsPlayed = secondsPlayed;
    }

    public GameModel getGameModel() {
        return gameModel;
    }
    @SuppressWarnings("unused")
    public void setGameModel(GameModel gameModel) { // needed for JSON
        this.gameModel = gameModel;
    }

    public CellModel getSelectedCell() {
        return selectedCell;
    }
    public void setSelectedCell(CellModel selectedCell) {
        this.selectedCell = selectedCell;
    }

    public ArrayList<List<CellModel>> getUndoList() {
        return undoList;
    }
    @SuppressWarnings("unused")
    public void setUndoList(ArrayList<List<CellModel>> undoList) { // needed for JSON
        this.undoList = undoList;
    }

    public boolean isCandidatesUsed() {
        return candidatesUsed;
    }
    public void setCandidatesUsed(boolean candidatesUsed) {
        this.candidatesUsed = candidatesUsed;
    }

    public boolean isSelected(CellModel cellModel){
        if (selectedCell == null) return false;
        return (selectedCell.getRow() == cellModel.getRow()) && (selectedCell.getColumn() == cellModel.getColumn());
    }
    public boolean isSelectedRowOrColumn(CellModel cellModel){
        if (selectedCell == null) return false;
        return (selectedCell.getRow() == cellModel.getRow()) || (selectedCell.getColumn() == cellModel.getColumn());
    }
    public boolean isSelectedValue(int value){
        if (selectedCell == null) return false;
        if (value == 0) return false;
        return selectedCell.getValue() == value;
    }

    @JSONField(serialize=false)
    int getGamePoints(){
        int points = gameModel.difficulty * (isCandidatesUsed()?1:2);
        points = points / (errorCounter+1);
        return points;
    }

    public void undo(){
        if (!isFinished() && (undoList.size() > 1)){
            List<CellModel> undoCellModels = undoList.remove(undoList.size()-1);
            for (CellModel undoCellModel : undoCellModels){
                CellModel oldCellModel = previous(undoCellModel);
                gameModel.getCellModel(undoCellModel.getRow(), undoCellModel.getColumn()).copyFrom(oldCellModel);
            }
            if (undoCellModels.size() > 0){
                selectedCell = gameModel.getCellModel(undoCellModels.get(0).getRow(), undoCellModels.get(0).getColumn());
            }
        }
    }

    private CellModel previous(CellModel cellModel){
        for (int i=undoList.size()-1; i>=0; i--){
            List<CellModel> changeList = undoList.get(i);
            for (CellModel changedCellModel : changeList){
                if ((changedCellModel.getRow() == cellModel.getRow()) && (changedCellModel.getColumn() == cellModel.getColumn())){
                    return changedCellModel;
                }
            }
        }
        return null;
    }

}
