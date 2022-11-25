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

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;

public class GameModel extends Observable {

    int dimension;
    int dimension2;
    public CellModel[] cellModels;
    int difficulty = 0;

    public GameModel(int dimension){
        this.dimension = dimension;
        this.dimension2 = dimension * dimension;
        this.difficulty = 0;
        cellModels = new CellModel[dimension2*dimension2];

        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                cellModels[cellIndex(i,j)] = new CellModel(dimension, i, j);
            }
        }
    }

    public GameModel(GameModel gameModel){
        this.dimension = gameModel.dimension;
        this.dimension2 = dimension * dimension;
        this.difficulty = gameModel.difficulty;
        cellModels = new CellModel[dimension2*dimension2];
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                cellModels[cellIndex(i,j)] = new CellModel(gameModel.getCellModel(i,j));
            }
        }
    }

//    public void copyFrom(GameModel gameModel){
//        this.dimension = gameModel.dimension;
//        this.dimension2 = dimension * dimension;
//        this.difficulty = gameModel.difficulty;
//        for (int i = 1; i <= dimension2; i++){
//            for (int j = 1; j <= dimension2; j++) {
//                cellModels[cellIndex(i,j)].copyFrom(gameModel.getCellModel(i,j));
//            }
//        }
//    }

    public GameModel(){}

    public int getDimension(){
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    public int getDimension2() {
        return dimension2;
    }
    public void setDimension2(int dimension2) {
        this.dimension2 = dimension2;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * @param row 1..dimension2
     * @param column 1..dimension2
     * @return index of corresponding CellModel in cellModels
     */
    public int cellIndex(int row, int column){
        int idx =  (column-1)*dimension2 + row-1;
        return idx;
    }

    public CellModel getCellModel(int row, int column){
        CellModel cellModel = cellModels[cellIndex(row, column)];
        return cellModel;
    }

    public void setValue(CellModel cellModel, int value){
        cellModel.setValue(value);
        unsetCandidatesForCell(cellModel);
//        Log.d("MGS","GMsV: "+cellModel);
    }


    public boolean isSolved(boolean correct){
        String solution = "Solution:\n";
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = cellModels[cellIndex(i,j)];
                if (cellModel.getValue() == 0) return false;
                if (correct && (cellModel.getValue() != cellModel.getSolution())) return false; // if correct is requested, then check against solution value
                solution += " "+cellModels[cellIndex(i,j)].getText();
            }
            solution += "\n";
        }
        Log.i("MGS", solution);
        return true;
    }

    public boolean logValues(){
        String solution = "Current Values:\n";
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = cellModels[cellIndex(i,j)];
                solution += " "+cellModels[cellIndex(i,j)].getText();
            }
            solution += "\n";
        }
        Log.i("MGS", solution);
        return true;
    }

//    @JSONField(serialize=false)
//    public boolean isAborted(){
//        for (int i = 1; i <= dimension2; i++){
//            for (int j = 1; j <= dimension2; j++) {
//                CellModel cellModel = cellModels[cellIndex(i,j)];
//                if ((cellModel.getValue() == 0) && (cellModel.candidates == 0)) {
//                    Log.w("MGS","Model aborted due to cell "+cellModel);
//                    return true;
//                }
//            }
//        }
//        return false;
//    }

//    int getNumCellSet(){
//        int num = 0;
//        for (int i = 1; i <= dimension2; i++){
//            for (int j = 1; j <= dimension2; j++) {
//                CellModel cellModel = cellModels[cellIndex(i,j)];
//                if (cellModel.getValue() != 0) num++;
//            }
//        }
//        return num;
//    }
//    CellModel getCellSet(int number){ // assume number 1..getNumCellSet
//        int num = 0;
//        for (int i = 1; i <= dimension2; i++){
//            for (int j = 1; j <= dimension2; j++) {
//                CellModel cellModel = cellModels[cellIndex(i,j)];
//                if (cellModel.getValue() != 0) {
//                    num++;
//                    if (num == number) return cellModel;
//                }
//            }
//        }
//        return null;
//    }

    int getNumValue(int value){
        int num = 0;
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = cellModels[cellIndex(i,j)];
                if (cellModel.getValue() == value) num++;
            }
        }
        return num;
    }

    CellModel[] getRowGroup(int row, int column, CellModel[] cellGroup){
        for (int i=1; i<= dimension2; i++){
            cellGroup[i] = getCellModel(row, i);
        }
        return cellGroup;
    }
    CellModel[] getColumnGroup(int row, int column, CellModel[] cellGroup){
        for (int i=1; i<= dimension2; i++){
            cellGroup[i] = getCellModel(i, column);
        }
        return cellGroup;
    }
    CellModel[] getBoxGroup(int row, int column, CellModel[] cellGroup){
        row = row - ((row-1) % dimension) ;
        column = column - ((column-1) % dimension);
        int cnt = 1;
        for (int i=row; i< row+dimension; i++){
            for (int j=column; j< column+dimension; j++){
                cellGroup[cnt++] = getCellModel(i, j);
            }
        }
        return cellGroup;
    }

    public GameModel initCandidates(boolean autoSetCandidates) {
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                getCellModel(i,j).initCandidates();
            }
        }
        if (autoSetCandidates){
            unsetCandidatesForValues();
        }
        return this;
    }
    public GameModel initCandidates(){
        initCandidates(true);
        return this;
    }

    public void unsetCandidatesForValues(){
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = getCellModel(i,j);
                if (cellModel.getValue() > 0){
                    unsetCandidatesForCell(cellModel);
                }
            }
        }
    }
    public void unsetCandidatesForCell(CellModel cellModel){
        CellModel[] cellGroup = new CellModel[dimension2+1];
        int i = cellModel.getRow();
        int j = cellModel.getColumn();
        unsetCandidateForCellGroup( getRowGroup(i, j, cellGroup), cellModel.getValue());
        unsetCandidateForCellGroup( getColumnGroup(i, j, cellGroup), cellModel.getValue());
        unsetCandidateForCellGroup( getBoxGroup(i, j, cellGroup), cellModel.getValue());
    }

    private void unsetCandidateForCellGroup(CellModel[] cellGroup, int value){
        for (int k=1; k <= dimension2; k++){
            cellGroup[k].unsetCandidate(value);
        }
    }

    public ArrayList<CellModel> getResetChanged(boolean all, CellModel selected){
        ArrayList<CellModel> list = new ArrayList<>();
        if ((selected != null ) && selected.hasChanged()){
            list.add(new CellModel(selected));
            selected.setChanged(false);
//            selected.unsetChanged();
        }
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = getCellModel(i,j);
                if (all || cellModel.hasChanged()){
                    list.add(new CellModel(cellModel));
                    cellModel.setChanged(false);
//                    cellModel.unsetChanged();
                }
            }
        }
        return list;
    }


    public void clearMarker(){
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                getCellModel(i,j).setMark1(0);
                getCellModel(i,j).setMark2(0);
            }
        }
    }

}
