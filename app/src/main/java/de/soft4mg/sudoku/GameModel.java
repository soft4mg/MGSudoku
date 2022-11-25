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

public class GameModel {

    int dimension;
    int dimension2;
    public CellModel[] cellModels;
    int difficulty = 0;

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
        return (column-1)*dimension2 + row-1;
    }

    public CellModel getCellModel(int row, int column){
        return cellModels[cellIndex(row, column)];
    }

    public void setValue(CellModel cellModel, int value){
        cellModel.setValue(value);
        unsetCandidatesForCell(cellModel);
    }


    public boolean isSolved(boolean correct){
        StringBuilder solution = new StringBuilder("Solution:\n");
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = cellModels[cellIndex(i,j)];
                if (cellModel.getValue() == 0) return false;
                if (correct && (cellModel.getValue() != cellModel.getSolution())) return false; // if correct is requested, then check against solution value
                solution.append(" ").append(cellModels[cellIndex(i, j)].getText());
            }
            solution.append("\n");
        }
        Log.i("MGS", solution.toString());
        return true;
    }

    public void logValues(){
        StringBuilder solution = new StringBuilder("Current Values:\n");
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                solution.append(" ").append(cellModels[cellIndex(i, j)].getText());
            }
            solution.append("\n");
        }
        Log.i("MGS", solution.toString());
    }

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

    CellModel[] getRowGroup(int row, CellModel[] cellGroup){
        for (int i=1; i<= dimension2; i++){
            cellGroup[i] = getCellModel(row, i);
        }
        return cellGroup;
    }
    CellModel[] getColumnGroup(int column, CellModel[] cellGroup){
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

    public void initCandidates(boolean autoSetCandidates) {
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                getCellModel(i,j).initCandidates();
            }
        }
        if (autoSetCandidates){
            unsetCandidatesForValues();
        }
    }
    public void initCandidates(){
        initCandidates(true);
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
        unsetCandidateForCellGroup( getRowGroup(i, cellGroup), cellModel.getValue());
        unsetCandidateForCellGroup( getColumnGroup(j, cellGroup), cellModel.getValue());
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
        }
        for (int i = 1; i <= dimension2; i++){
            for (int j = 1; j <= dimension2; j++) {
                CellModel cellModel = getCellModel(i,j);
                if (all || cellModel.hasChanged()){
                    list.add(new CellModel(cellModel));
                    cellModel.setChanged(false);
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
