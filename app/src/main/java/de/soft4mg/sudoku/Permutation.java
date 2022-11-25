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
import java.util.Arrays;
import java.util.Random;

public class Permutation {

    public static void permuteRowGroups(GameModel gameModel, int permutationNumber){ // permutationNumber should be 1..6
        int[] permutationVector = getPermutationVector(permutationNumber, gameModel.dimension);
        CellModel[] cellModelsClone = new CellModel[gameModel.cellModels.length];
        System.arraycopy(gameModel.cellModels,0,cellModelsClone,0, gameModel.dimension2*gameModel.dimension2);
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {
                int grIdx = (i-1)/gameModel.dimension + 1;
                int newGrIdx = permutationVector[grIdx];
                int newI = (newGrIdx-1)*gameModel.dimension + ((i-1)%gameModel.dimension) + 1;
                gameModel.cellModels[gameModel.cellIndex(newI,j)] = cellModelsClone[gameModel.cellIndex(i,j)].moveTo(newI,j);
            }
        }
    }

    public static void permuteRowGroup(GameModel gameModel, int grIdx, int permutationNumber){ // grIdx should be 1..3/1..4; permutationNumber should be 1..6/1..24
        int[] permutationVector = getPermutationVector(permutationNumber, gameModel.dimension);
        CellModel[] cellModelsClone = new CellModel[gameModel.cellModels.length];
        System.arraycopy(gameModel.cellModels,0,cellModelsClone,0, gameModel.dimension2*gameModel.dimension2);
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {
                int currentGrIdx = (i-1)/gameModel.dimension+1;
                if ( currentGrIdx == grIdx ){ // grIdx is matching
                    int memberIdx = (i-1)%gameModel.dimension+1;
                    int newMemberIdx = permutationVector[memberIdx];
                    int newI = (grIdx-1)*gameModel.dimension+newMemberIdx;
                    gameModel.cellModels[gameModel.cellIndex(newI,j)] = cellModelsClone[gameModel.cellIndex(i,j)].moveTo(newI,j);
                }
            }
        }
    }


    public static void permuteColGroups(GameModel gameModel, int permutationNumber){ // permutationNumber should be 1..6
        int[] permutationVector = getPermutationVector(permutationNumber, gameModel.dimension);
        CellModel[] cellModelsClone = new CellModel[gameModel.cellModels.length];
        System.arraycopy(gameModel.cellModels,0,cellModelsClone,0, gameModel.dimension2*gameModel.dimension2);
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {
                int grIdx = (j-1)/gameModel.dimension + 1;
                int newGrIdx = permutationVector[grIdx];
                int newJ = (newGrIdx-1)*gameModel.dimension + ((j-1)%gameModel.dimension) + 1;
                gameModel.cellModels[gameModel.cellIndex(i,newJ)] = cellModelsClone[gameModel.cellIndex(i,j)].moveTo(i,newJ);
            }
        }
    }

    public static void permuteNumbers(GameModel gameModel, long permutationNumber){ // permutationNumber should be 1..9!/1..16!
        int[] permutationVector = getPermutationVector(permutationNumber, gameModel.dimension2);
        Log.i("MGS", "permuteNumbers permutationVector="+ Arrays.toString(permutationVector));
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {

                CellModel cellModel = gameModel.getCellModel(i,j);
                CellModel cellModelClone = new CellModel(cellModel);
                cellModel.setValue( permutationVector[cellModelClone.getValue()] );
                cellModel.setSolution( permutationVector[cellModelClone.getSolution()] );

                cellModel.setCandidates(0);
                for (int c = 1; c <= gameModel.dimension2; c++) { // iterate candidates
                    if (cellModelClone.isCandidate(c)){
                        cellModel.toggleCandidate(permutationVector[c]);
                    }
                }
            }
        }
    }



    public static void permuteColGroup(GameModel gameModel, int grIdx, int permutationNumber){ // grIdx should be 1..3/1..4; permutationNumber should be 1..6/1..24
        int[] permutationVector = getPermutationVector(permutationNumber, gameModel.dimension);
        CellModel[] cellModelsClone = new CellModel[gameModel.cellModels.length];
        System.arraycopy(gameModel.cellModels,0,cellModelsClone,0, gameModel.dimension2*gameModel.dimension2);
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {
                int currentGrIdx = (j-1)/gameModel.dimension+1;
                if ( currentGrIdx == grIdx ){ // grIdx is matching
                    int memberIdx = (j-1)%gameModel.dimension+1;
                    int newMemberIdx = permutationVector[memberIdx];
                    int newJ = (grIdx-1)*gameModel.dimension+newMemberIdx;
                    gameModel.cellModels[gameModel.cellIndex(i,newJ)] = cellModelsClone[gameModel.cellIndex(i,j)].moveTo(i,newJ);
                }
            }
        }
    }

    public static void randomPermutation(GameModel gameModel){
        Random random = new Random(System.currentTimeMillis());
        int dimFacultaet = (int) faculty( gameModel.dimension );
        long dim2Facultaet = faculty( gameModel.dimension2 );
        permuteRowGroups(gameModel, random.nextInt( dimFacultaet )+1 );
        permuteColGroups(gameModel, random.nextInt( dimFacultaet )+1 );
        for (int i=1; i<=gameModel.dimension; i++){
            permuteRowGroup(gameModel, i, random.nextInt( dimFacultaet )+1 );
            permuteColGroup(gameModel, i, random.nextInt( dimFacultaet )+1 );
        }
        permuteNumbers(gameModel, (long)(random.nextDouble()*dim2Facultaet)+1 );
    }


    public static int[] getPermutationVector(long permutationNumber, int permutationDimension){
        int[] permuationVector = new int[permutationDimension+1];
        permutationNumber--; // 0..5 for dim3 and 0..23 for dim4
        ArrayList<Integer> newIdxs = new ArrayList<>();
        for (int idx=1; idx<=permutationDimension; idx++){
            newIdxs.add(idx);
        }
        for (int idx=1; idx<=permutationDimension; idx++) {
            int num = newIdxs.size();
            int newIdx = newIdxs.remove((int)(permutationNumber % num));
            permutationNumber /= num;
            permuationVector[idx] = newIdx;
        }
        return permuationVector;
    }

    public static long faculty(int f){
        long res = 1;
        for (int i=1; i<=f; i++){
            res *=i;
        }
        return res;
    }



}
