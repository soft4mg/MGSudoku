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

import java.util.Observable;

public class CellModel extends Observable {

    int dimension;
    @JSONField(name="Row", ordinal=1)
    int row;
    @JSONField(name="Col", ordinal=2)
    int column;
    @JSONField(name="Value", ordinal=3)
    int value = 0; // 0 means not yet set
    @JSONField(name="Candidates", ordinal=4)
    int candidates;
    @JSONField(name="Initial", ordinal=5)
    boolean initial = false;
    @JSONField(name="Sol", ordinal=6)
    int solution = 0;
    @JSONField(name="Mark1", ordinal=7)
    int mark1 = 0;
    @JSONField(name="Mark2", ordinal=8)
    int mark2 = 0;
    @JSONField(serialize=false)
    boolean enabled = true;



    public CellModel(int dimension, int row, int column){
        this.dimension = dimension;
        this.row = row;
        this.column = column;
        this.initCandidates();
    }

    public CellModel(CellModel cellModel){
        this(cellModel.dimension, cellModel.row, cellModel.column);
        value = cellModel.value;
        candidates = cellModel.candidates;
        initial = cellModel.initial;
        solution = cellModel.solution;
    }
    public CellModel(){}

    public void copyFrom(CellModel cellModel){
        if (cellModel != null){
            value = cellModel.value;
            candidates = cellModel.candidates;
            initial = cellModel.initial;
            solution = cellModel.solution;
            unsetChanged();
        }
    }

    public int getCandidates() {
        return candidates;
    }

    public void setCandidates(int candidates) {
        this.candidates = candidates;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getMark1() {
        return mark1;
    }

    public void setMark1(int mark1) {
        this.mark1 = mark1;
    }

    public int getMark2() {
        return mark2;
    }

    public void setMark2(int mark2) {
        this.mark2 = mark2;
    }

    public int getValue(){
        return value;
    }
    public void setValue(int value){
        if (this.value != value){
            this.value = value;
            this.setChanged();
//            this.notifyObservers();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled){
            this.enabled = enabled;
            this.setChanged();
//            this.notifyObservers();
        }
    }

    public boolean isInitial() {
        return initial;
    }
    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public int getDimension(){
        return dimension;
    }
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    String getText(){
        return getText(value);
    }
    static String getText(int i){
        return Integer.toString(i, 17).substring(0,1).toUpperCase();
    }



    private int candidateBit(int candidate){
        return 1<<(candidate-1);
    }

    boolean isCandidateIn(int value, Integer bitarray){
        return (candidateBit(value) & bitarray) != 0;
    }
    public int unsetCandidateIn(int value, Integer bitarray){
        if (( bitarray & candidateBit(value) ) != 0){ // bit is yet set
            toggleCandidateIn(value, bitarray);
        }
        return bitarray;
    }
    public int toggleCandidateIn(int candidate, Integer bitarray){
        int bit = candidateBit(candidate);
        bitarray ^= bit; // toggle bit
        this.setChanged();
//        this.notifyObservers();
        return bitarray;
    }


    boolean isCandidate(int i){
        return ((1 << (i-1)) & candidates) != 0;
    }
    public void unsetCandidate(int candidate){
        if (( this.candidates & candidateBit(candidate) ) != 0){ // bit is yet set
            toggleCandidate(candidate);
        }
    }
    public void toggleCandidate(int candidate){
        int bit = candidateBit(candidate);
        this.candidates ^= bit; // toggle bit
        this.setChanged();
//        this.notifyObservers();
    }

    @JSONField(serialize=false)
    public int getNumberOfCandidates(){
        return getNumberOfCandidates(candidates);
    }
    public static int getNumberOfCandidates(int candidates){
        int num = 0;
        while (candidates != 0){
            if ((candidates & 0x1) == 1) num++;
            candidates = candidates / 2;
        }
        return num;
    }

    public int getCandidate(int n){
        int num = 0;
        for (int i=1; i<=dimension*dimension; i++){
            if (isCandidate(i)) {
                num++;
                if (num == n) return i;
            }
        }
        return 0;
    }
    public boolean unsetCandidateGroup(int candidatesGroup){
        if (value == 0){
            int newCandidates = candidates & ~candidatesGroup;
            if ((newCandidates != 0) && (newCandidates != candidates)){
                candidates &= ~candidatesGroup;
                setChanged();
//                notifyObservers();
                return true;
            }
        }
        return false;
    }

    public void initCandidates(){
        candidates = (1 << (dimension*dimension)) -1;
    }

    void unsetChanged(){
        clearChanged();
    }

    @Override
    public String toString() {
        return "CellModel{" +
                "dimension=" + dimension +
                ", row=" + row +
                ", column=" + column +
                ", value=" + value +
                ", candidates=" + candidates +
                ", initial=" + initial +
                ", solution=" + solution +
                '}';
    }

    CellModel moveTo(int row, int column){
        this.row = row;
        this.column = column;
        return this;
    }
}
