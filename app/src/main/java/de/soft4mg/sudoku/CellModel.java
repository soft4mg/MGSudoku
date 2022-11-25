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

import androidx.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;

public class CellModel {

    int dimension;
    @JSONField(name="Row", ordinal=1)
    private int row;
    @JSONField(name="Col", ordinal=2)
    private int column;
    @JSONField(name="Value", ordinal=3)
    private int value = 0; // 0 means not yet set
    @JSONField(name="Candidates", ordinal=4)
    private int candidates;
    @JSONField(name="Initial", ordinal=5)
    private boolean initial = false;
    @JSONField(name="Sol", ordinal=6)
    private int solution = 0;
    @JSONField(name="Mark1", ordinal=7)
    private int mark1 = 0;
    @JSONField(name="Mark2", ordinal=8)
    private int mark2 = 0;
    @JSONField(serialize=false)
    private boolean enabled = true;
    @JSONField(name="Changed")
    private boolean changed = true;



    public CellModel(int dimension, int row, int column){
        this.dimension = dimension;
        this.setRow(row);
        this.setColumn(column);
        this.initCandidates();
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public CellModel(CellModel cellModel){
        this(cellModel.dimension, cellModel.row, cellModel.column);
        value = cellModel.value;
        candidates = cellModel.candidates;
        initial = cellModel.initial;
        solution = cellModel.solution;
        mark1 = cellModel.getMark1();
        mark2 = cellModel.getMark2();
    }

    @SuppressWarnings("unused") // required for JSON
    public CellModel(){}

    public void copyFrom(CellModel cellModel){
        if (cellModel != null){
            value = cellModel.value;
            candidates = cellModel.candidates;
            initial = cellModel.initial;
            solution = cellModel.solution;
            mark1 = cellModel.getMark1();
            mark2 = cellModel.getMark2();
            setChanged(false);
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
        if (this.mark1 != mark1){
            this.mark1 = mark1;
            this.setChanged();
        }
    }

    public int getMark2() {
        return mark2;
    }

    public void setMark2(int mark2) {
        if (this.mark2 != mark2){
            this.mark2 = mark2;
            this.setChanged();
        }
    }

    public int getValue(){
        return value;
    }
    public void setValue(int value){
        if (this.value != value){
            this.value = value;
            this.setChanged();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled){
            this.enabled = enabled;
            this.setChanged();
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
    public int toggleCandidateIn(int candidate, Integer bitarray){
        int bit = candidateBit(candidate);
        bitarray ^= bit; // toggle bit
        this.setChanged();
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
    }

    public void initCandidates(){
        candidates = (1 << (dimension*dimension)) -1;
    }

    public boolean hasChanged() {
        return changed;
    }
    public void setChanged(boolean changed) {
        this.changed = changed;
    }
    public void setChanged() {
        setChanged(true);
    }

    @NonNull
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
                ", mark1=" + mark1 +
                ", mark2=" + mark2 +
                ", enabled=" + enabled +
                ", changed=" + changed +
                '}';
    }

    CellModel moveTo(int row, int column){
        this.row = row;
        this.column = column;
        return this;
    }
}
