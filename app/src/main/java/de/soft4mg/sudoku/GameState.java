package de.soft4mg.sudoku;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    boolean finished;
    int errorCounter;
    long secondsPlayed;
    GameModel gameModel;
    CellModel selectedCell;
    boolean candidatesUsed;
    ArrayList<List<CellModel>> undoList = new ArrayList<>();


    public GameState(){}

    public GameState(GameModel gameModel) {
        this.gameModel = gameModel;
        finished = false;
        secondsPlayed = 0;
        errorCounter = 0;
        undoList.add( gameModel.getResetChanged(true, null) );
    }

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
    public void setGameModel(GameModel gameModel) {
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
    public void setUndoList(ArrayList<List<CellModel>> undoList) {
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
        if ((selectedCell.row == cellModel.row) && (selectedCell.column == cellModel.column)) return true;
        return false;
    }
    public boolean isSelectedRowOrColumn(CellModel cellModel){
        if (selectedCell == null) return false;
        if ((selectedCell.row == cellModel.row) || (selectedCell.column == cellModel.column)) return true;
        return false;
    }
    public boolean isSelectedValue(int value){
        if (selectedCell == null) return false;
        if (value == 0) return false;
        if ((selectedCell.value == value)) return true;
        return false;
    }

    int getGamePoints(Integer notUsed){
        int points = gameModel.difficulty * (isCandidatesUsed()?1:2);
        points = points / (errorCounter+1);
        return points;
    }

    public void undo(){
        if (!isFinished() && (undoList.size() > 1)){
            List<CellModel> undoCellModels = undoList.remove(undoList.size()-1);
            for (CellModel undoCellModel : undoCellModels){
                CellModel oldCellModel = previous(undoCellModel);
                gameModel.getCellModel(undoCellModel.row, undoCellModel.column).copyFrom(oldCellModel);
            }
            if (undoCellModels.size() > 0){
//                selectedCell = undoCellModels.get(0);
                selectedCell = gameModel.getCellModel(undoCellModels.get(0).row, undoCellModels.get(0).column);
            }
        }
    }

    private CellModel previous(CellModel cellModel){
        for (int i=undoList.size()-1; i>=0; i--){
            List<CellModel> changeList = undoList.get(i);
            for (CellModel changedCellModel : changeList){
                if ((changedCellModel.row == cellModel.row) && (changedCellModel.column == cellModel.column)){
                    return changedCellModel;
                }
            }
        }
        return null;
    }

}
