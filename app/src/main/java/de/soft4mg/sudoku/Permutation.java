package de.soft4mg.sudoku;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Permutation {

    public static void permutateRowGroups(GameModel gameModel, int permutationNumber){ // permutationNumber should be 1..6
        int permutationVector[] = getPermutationVector(permutationNumber, gameModel.dimension);
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

    public static void permutateRowGroup(GameModel gameModel, int grIdx, int permutationNumber){ // grIdx should be 1..3/1..4; permutationNumber should be 1..6/1..24
        int permutationVector[] = getPermutationVector(permutationNumber, gameModel.dimension);
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


    public static void permutateColGroups(GameModel gameModel, int permutationNumber){ // permutationNumber should be 1..6
        int permutationVector[] = getPermutationVector(permutationNumber, gameModel.dimension);
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

    public static void permutateNumbers(GameModel gameModel, long permutationNumber){ // permutationNumber should be 1..9!/1..16!
        int permutationVector[] = getPermutationVector(permutationNumber, gameModel.dimension2);
        Log.i("MGS", "XXX permutateNumbers permutationVector="+ Arrays.toString(permutationVector));
        for (int i = 1; i <= gameModel.dimension2; i++){
            for (int j = 1; j <= gameModel.dimension2; j++) {

                CellModel cellModel = gameModel.getCellModel(i,j);
                CellModel cellModelClone = new CellModel(cellModel);
                cellModel.setValue( permutationVector[cellModelClone.getValue()] );
                cellModel.setSolution( permutationVector[cellModelClone.getSolution()] );

                cellModel.candidates = 0;
                for (int c = 1; c <= gameModel.dimension2; c++) { // iterate candidates
                    if (cellModelClone.isCandidate(c)){
                        cellModel.toggleCandidate(permutationVector[c]);
                    }
                }
            }
        }
    }



    public static void permutateColGroup(GameModel gameModel, int grIdx, int permutationNumber){ // grIdx should be 1..3/1..4; permutationNumber should be 1..6/1..24
        int permutationVector[] = getPermutationVector(permutationNumber, gameModel.dimension);
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
        int dimFacultaet = (int)facultaet( gameModel.dimension );
        long dim2Facultaet = facultaet( gameModel.dimension2 );
        permutateRowGroups(gameModel, random.nextInt( dimFacultaet )+1 );
        permutateColGroups(gameModel, random.nextInt( dimFacultaet )+1 );
        for (int i=1; i<=gameModel.dimension; i++){
            permutateRowGroup(gameModel, i, random.nextInt( dimFacultaet )+1 );
            permutateColGroup(gameModel, i, random.nextInt( dimFacultaet )+1 );
        }
        permutateNumbers(gameModel, (long)(random.nextDouble()*dim2Facultaet)+1 );
    }


    public static int[] getPermutationVector(long permutationNumber, int permutationDimension){
        int permuationVector[] = new int[permutationDimension+1];
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

    public static long facultaet(int f){
        long res = 1;
        for (int i=1; i<=f; i++){
            res *=i;
        }
        return res;
    }



}
