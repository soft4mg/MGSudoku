package de.soft4mg.sudoku;

public class Extra {

    public GameModel create(GameModel gameModel){
        gameModel.getCellModel(1,1).setValue(4);
        gameModel.getCellModel(1,2).setValue(0);
        gameModel.getCellModel(1,3).setValue(0);
        gameModel.getCellModel(1,4).setValue(0);
        gameModel.getCellModel(1,5).setValue(7);
        gameModel.getCellModel(1,6).setValue(0);
        gameModel.getCellModel(1,7).setValue(0);
        gameModel.getCellModel(1,8).setValue(0);
        gameModel.getCellModel(1,9).setValue(0);

        gameModel.getCellModel(2,1).setValue(3);
        gameModel.getCellModel(2,2).setValue(0);
        gameModel.getCellModel(2,3).setValue(0);
        gameModel.getCellModel(2,4).setValue(1);
        gameModel.getCellModel(2,5).setValue(0);
        gameModel.getCellModel(2,6).setValue(0);
        gameModel.getCellModel(2,7).setValue(0);
        gameModel.getCellModel(2,8).setValue(0);
        gameModel.getCellModel(2,9).setValue(9);

        gameModel.getCellModel(3,1).setValue(0);
        gameModel.getCellModel(3,2).setValue(0);
        gameModel.getCellModel(3,3).setValue(0);
        gameModel.getCellModel(3,4).setValue(0);
        gameModel.getCellModel(3,5).setValue(3);
        gameModel.getCellModel(3,6).setValue(0);
        gameModel.getCellModel(3,7).setValue(0);
        gameModel.getCellModel(3,8).setValue(6);
        gameModel.getCellModel(3,9).setValue(0);

        gameModel.getCellModel(4,1).setValue(0);
        gameModel.getCellModel(4,2).setValue(2);
        gameModel.getCellModel(4,3).setValue(0);
        gameModel.getCellModel(4,4).setValue(0);
        gameModel.getCellModel(4,5).setValue(0);
        gameModel.getCellModel(4,6).setValue(0);
        gameModel.getCellModel(4,7).setValue(0);
        gameModel.getCellModel(4,8).setValue(0);
        gameModel.getCellModel(4,9).setValue(0);

        gameModel.getCellModel(5,1).setValue(0);
        gameModel.getCellModel(5,2).setValue(0);
        gameModel.getCellModel(5,3).setValue(0);
        gameModel.getCellModel(5,4).setValue(0);
        gameModel.getCellModel(5,5).setValue(0);
        gameModel.getCellModel(5,6).setValue(8);
        gameModel.getCellModel(5,7).setValue(0);
        gameModel.getCellModel(5,8).setValue(2);
        gameModel.getCellModel(5,9).setValue(0);

        gameModel.getCellModel(6,1).setValue(0);
        gameModel.getCellModel(6,2).setValue(0);
        gameModel.getCellModel(6,3).setValue(0);
        gameModel.getCellModel(6,4).setValue(0);
        gameModel.getCellModel(6,5).setValue(0);
        gameModel.getCellModel(6,6).setValue(3);
        gameModel.getCellModel(6,7).setValue(5);
        gameModel.getCellModel(6,8).setValue(1);
        gameModel.getCellModel(6,9).setValue(0);

        gameModel.getCellModel(7,1).setValue(0);
        gameModel.getCellModel(7,2).setValue(0);
        gameModel.getCellModel(7,3).setValue(0);
        gameModel.getCellModel(7,4).setValue(3);
        gameModel.getCellModel(7,5).setValue(8);
        gameModel.getCellModel(7,6).setValue(5);
        gameModel.getCellModel(7,7).setValue(0);
        gameModel.getCellModel(7,8).setValue(4);
        gameModel.getCellModel(7,9).setValue(0);

        gameModel.getCellModel(8,1).setValue(0);
        gameModel.getCellModel(8,2).setValue(5);
        gameModel.getCellModel(8,3).setValue(0);
        gameModel.getCellModel(8,4).setValue(0);
        gameModel.getCellModel(8,5).setValue(0);
        gameModel.getCellModel(8,6).setValue(0);
        gameModel.getCellModel(8,7).setValue(0);
        gameModel.getCellModel(8,8).setValue(8);
        gameModel.getCellModel(8,9).setValue(1);

        gameModel.getCellModel(9,1).setValue(0);
        gameModel.getCellModel(9,2).setValue(0);
        gameModel.getCellModel(9,3).setValue(0);
        gameModel.getCellModel(9,4).setValue(0);
        gameModel.getCellModel(9,5).setValue(0);
        gameModel.getCellModel(9,6).setValue(0);
        gameModel.getCellModel(9,7).setValue(0);
        gameModel.getCellModel(9,8).setValue(0);
        gameModel.getCellModel(9,9).setValue(0);

        gameModel.initCandidates(true);
        return gameModel;
    }
}
