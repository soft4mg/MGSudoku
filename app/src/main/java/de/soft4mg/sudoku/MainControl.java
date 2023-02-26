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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.TimerTask;

public class MainControl {

    MainActivity mainActivity;
    MainView mainView;
    Context context;
    PrefUtil prefUtil;

    HashMap<String, ArrayList<String>> gameMap = new HashMap<>();
    boolean gameMapLoaded = false;
    GameState gameState;

    Handler timer = new Handler();
    TimerTask ttSecond = new TimerTask() {
        @Override
        public void run() {
            Log.v(MainControl.class.getName(), "second "+(gameState.getSecondsPlayed() + 1)+ " finished="+gameState.isFinished());
            if (!gameState.isFinished()){
                timer.postDelayed(ttSecond, 1000);
                gameState.setSecondsPlayed(gameState.getSecondsPlayed() + 1);
                if (mainView.controlView != null){
                    mainView.controlView.setGameDuration(gameState.getSecondsPlayed());
                }
            }
        }
    };


    public MainControl(MainActivity mainActivity){
        this.mainActivity = mainActivity;

        this.mainView = mainActivity.findViewById(R.id.main_sudoku_layout);
        context = mainView.getContext();
        prefUtil = new PrefUtil(context);

        mainView.init(mainActivity, mainViewListener);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            Rect r1 = mainActivity.getWindowManager().getCurrentWindowMetrics().getBounds();
            prefUtil.putInt(R.string.prefDefaultMainWidth, r1.width());
            prefUtil.putInt(R.string.prefDefaultMainHeight, r1.height());
        } else {
            int w = mainActivity.getResources().getDisplayMetrics().widthPixels;
            prefUtil.putInt(R.string.prefDefaultMainWidth, w);
            int h = mainActivity.getResources().getDisplayMetrics().widthPixels;
            prefUtil.putInt(R.string.prefDefaultMainHeight, h);
        }
        gameState = null;
        initGameMap();

    }

    public void onResume(){
        String sGameState = prefUtil.getString(R.string.stateGameControl, null);
        if (sGameState!=null) {
            gameState = JSON.parseObject(sGameState, GameState.class);
        }
        if (gameState != null){
            onNewGameState();
        } else {
            gameState = startNewGame();
            onNewGameState();
        }
    }

    public void onPause(){
        String sGameState =  JSON.toJSONString(gameState, true);
        prefUtil.putString(R.string.stateGameControl, sGameState);
        timer.removeCallbacks(ttSecond);
    }


    public GameState startNewGame(){
        try {
            int dimension = prefUtil.getInt( R.string.prefModelDimension, 3);
            GameLevel gameLevel = GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) );
            ArrayList<String> gameOptions = gameMap.get(gameLevel.toString()+dimension);
            String gameOption = "dim3/GameModel_0108_21_98982fb4-d357-42fd-bcf8-6993d9525cfe.json"; // defalut for very first game after install
            if ((gameOptions != null) && (gameOptions.size() > 0)){
                Random random = new Random(System.currentTimeMillis());
                gameOption = gameOptions.get((int)(random.nextDouble()*gameOptions.size()));
            }
            try (InputStream is  = context.getAssets().open(gameOption)){
                int available = is.available();
                byte[] data = new byte[available];
                if (is.read(data) == available){
                    GameModel gameModel = JSON.parseObject(new String(data), GameModel.class);
                    if (dimension == 4){
                        gameModel.difficulty *= 3;
                    }
                    gameModel.logValues();
                    Permutation.randomPermutation(gameModel);
                    gameModel.logValues();
                    return new GameState(gameModel);
                }
            }
        } catch (IOException e) {
            Log.e(MainControl.class.getName(),e.getMessage(),e);
        }
        return null;
    }

    void onNewGameState(){
        gameState.setCandidatesUsed( prefUtil.getBoolean(R.string.prefShowCandidates, true) );
        mainView.initNewGame(gameState, numbersListener, controlViewListener);

        timer.removeCallbacks(ttSecond);
        timer.postDelayed(ttSecond, 1000);
    }

    MainViewListener mainViewListener = new MainViewListener() {
        @Override
        public void layoutRequested() {
            if (gameState != null){
                // callback is from onLayout -> don't do the real work in this Thread
                new Thread(() -> mainActivity.runOnUiThread(() -> mainView.initNewGame(gameState, numbersListener, controlViewListener))).start();
            }
        }
    };

    ControlViewListener controlViewListener = new ControlViewListener() {
        @Override
        public void newGameRequested(int dimension, GameLevel gameLevel) {
            gameState = startNewGame();
            onNewGameState();
        }

        @Override
        public void undoRequested() {
            gameState.undo();
            mainView.gameView.invalidate();
            mainView.numbersView.invalidate();
        }

        @Override
        public void repaintRequested() {
            mainView.invalidateGameAndNumbers();
        }

        @Override
        public void initCandidatesRequested() {
            gameState.getGameModel().initCandidates();
            recordUndoStep();
            mainView.gameView.invalidate();
        }

        @Override
        public void clearMarkerRequested() {
            gameState.getGameModel().clearMark1();
            gameState.getGameModel().clearMark2();
            mainView.gameView.invalidate();
        }

        @Override
        public void showCandidatesRequested(boolean show) {
            gameState.setCandidatesUsed(gameState.isCandidatesUsed() | show);
            mainView.controlView.setPoints(gameState.getGamePoints());
        }

        @Override
        public void showHelpRequested() {
            showHelp();
        }
    };

    NumbersListener numbersListener = new NumbersListener() {
        @Override
        public void numberPressed(int number, NumberAction numberAction) {
            if (!gameState.isFinished() && (gameState.getSelectedCell() != null)){
                if (gameState.getSelectedCell().isInitial()) return; // don't change initial set cells

                CellModel selectedCellModel = gameState.getSelectedCell();

                if ( numberAction == NumberAction.SET_CANDIDATE){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        selectedCellModel.toggleCandidate(number);
                    }
                } if ( numberAction == NumberAction.MARK_CANDIDATE_1){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        if (selectedCellModel.isCandidate(number)){ // ... and number pressed is a candiate
                            selectedCellModel.setMark1( selectedCellModel.toggleCandidateIn(number, selectedCellModel.getMark1()) );
                        }
                    }
                } if ( numberAction == NumberAction.MARK_CANDIDATE_2){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        if (selectedCellModel.isCandidate(number)){ // ... and number pressed is a candiate
                            selectedCellModel.setMark2( selectedCellModel.toggleCandidateIn(number, selectedCellModel.getMark2()) );
                        }
                    }
                } else if ( numberAction == NumberAction.SET_NUMBER){
                    Log.i(MainControl.class.getName(), "number="+number+" cell="+selectedCellModel);
                    if (selectedCellModel.getValue() ==  number){ // if cell is set to given value, then unset it
                        selectedCellModel.setValue(0);
                        gameState.setSelectedCell(selectedCellModel);
                    } else {
                        GameModel gameModel = gameState.getGameModel();
                        if (selectedCellModel.getValue() !=  selectedCellModel.getSolution()){
                            if (  gameModel.getNumValue(number) < gameModel.dimension2 ){
                                gameModel.setValue(selectedCellModel, number);
                                gameState.setSelectedCell(selectedCellModel);
                                if (selectedCellModel.getValue() != selectedCellModel.getSolution()){
                                    gameState.setErrorCounter( gameState.getErrorCounter()+1 );
                                    mainView.controlView.setGameErrors(gameState.getErrorCounter());
                                    mainView.controlView.setPoints(gameState.getGamePoints());
                                } else {
                                    if (gameModel.isSolved( true)){
                                        gameState.setFinished(true);
                                        GameLevel gameLevel = GameLevel.get(gameModel.dimension, gameModel.difficulty);
                                        GameResult gameResult = new GameResult(System.currentTimeMillis(), gameState.getGamePoints(), gameLevel, gameState.getSecondsPlayed(), gameModel.difficulty);
                                        String sGameResult =  JSON.toJSONString(gameResult, true);
                                        prefUtil.preferences.edit().putString("result_"+gameResult.timestamp, sGameResult).apply();
                                        showResultDialog(gameResult);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            recordUndoStep();
            mainView.gameView.invalidate();
            mainView.numbersView.invalidate();
        }

        @Override
        public void numberPressedLong(int number, NumberAction numberAction) {
            NumberAction newNumberAction =  (numberAction==NumberAction.SET_NUMBER)?NumberAction.SET_CANDIDATE:NumberAction.SET_NUMBER;
            mainView.numbersView.setNumberAction( newNumberAction );
            numberPressed(number, newNumberAction);
        }

        @Override
        public void buttonPressed(NumberAction numberAction) {

        }

        @Override
        public void buttonPressedLong(NumberAction numberAction) {
            if ((numberAction == NumberAction.MARK_CANDIDATE_1) || (numberAction == NumberAction.MARK_CANDIDATE_2)) {
                showAreYouReallySureDialog(numberAction);
            }
        }
    };


    void recordUndoStep(){
        ArrayList<CellModel> undoCells = gameState.getGameModel().getResetChanged(false, gameState.getSelectedCell());
        if (undoCells.size() > 0){
            gameState.getUndoList().add(undoCells);
        }
    }

    public void showResultDialog(GameResult gameResult){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Game Winner");
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Gratulations - you win the game.\n"+
                        "\nLevel: "+gameResult.gameLevel +
                        "\nDifficulty: "+gameResult.difficulty+
                        "\nPoints: "+gameResult.points +
                        String.format(Locale.ENGLISH,"\nTime: %d:%02d ", gameState.getSecondsPlayed() /60, gameState.getSecondsPlayed() %60))
                .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showAreYouReallySureDialog(NumberAction numberAction){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Action with marked numbers");
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("for: "+mainView.numbersView.getTextOfNumberAction(numberAction))
                .setPositiveButton("Apply", (dialog, id) -> {
                    dialog.dismiss();
                    new Thread(() -> evaluateMark(numberAction)).start();
                })
                .setNegativeButton("Clear", (dialog, id) -> {
                    dialog.dismiss();
                    if (numberAction == NumberAction.MARK_CANDIDATE_1) gameState.getGameModel().clearMark1();
                    if (numberAction == NumberAction.MARK_CANDIDATE_2) gameState.getGameModel().clearMark2();
                    mainView.gameView.invalidate();
                })
                .setNeutralButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void initGameMap(){
        new Thread(() -> {
            Log.i(MainControl.class.getName(), "initGameMap started");
            for (int dim=3; dim <= 4; dim++){
                try {
                    for (GameLevel gameLevel : GameLevel.values()){
                        gameMap.put(gameLevel.toString()+dim,new ArrayList<>());
                    }
                    for (String name : context.getAssets().list("dim"+dim)){
                        GameLevel gameLevel = GameLevel.get(dim, Integer.parseInt( name.split("_")[1] ));
                        assert (gameLevel != null);
                        ArrayList<String> dimLevelGameList = gameMap.get(gameLevel.toString()+dim);
                        if (dimLevelGameList != null) dimLevelGameList.add("dim"+dim+"/"+name);
                    }
                } catch (IOException e) {
                    Log.e(MainControl.class.getName(), e.getMessage(), e);
                }
            }
            gameMapLoaded = true;
            synchronized (this){
                notifyAll();
            }
            Log.i(MainControl.class.getName(), "initGameMap finished");
        }).start();
    }


    public void evaluateMark(NumberAction numberAction){
        GameModel gameModel = gameState.getGameModel();
        if (applyMark(numberAction)) {
            if (gameModel.isSolved( true)){
                gameState.setFinished(true);

                GameResult gameResult = new GameResult(System.currentTimeMillis(), gameState.getGamePoints(), GameLevel.get(gameModel.dimension, gameModel.difficulty), gameState.getSecondsPlayed(), gameModel.difficulty);
                String sGameResult =  JSON.toJSONString(gameResult, true);
                prefUtil.preferences.edit().putString("result_"+gameResult.timestamp, sGameResult).apply();
                if (context instanceof Activity) {
                    Activity activity = (Activity) context;
                    activity.runOnUiThread(() -> showResultDialog(gameResult));
                }
            }
        }else {
            gameState.setErrorCounter( gameState.getErrorCounter()+1 );
            mainView.controlView.setGameErrors(gameState.getErrorCounter());
            mainView.controlView.setPoints(gameState.getGamePoints());
        }
    }

    private boolean applyMark(NumberAction numberAction){
        GameModel gameModel = gameState.getGameModel();
        for (int i=1; i<=gameModel.dimension2; i++){
            for (int j=1; j<=gameModel.dimension2; j++){
                CellModel cellModel = gameModel.getCellModel(i,j);
                if (cellModel.getValue() == 0){ // value not yet set
                    int mark = 0;
                    if (numberAction == NumberAction.MARK_CANDIDATE_1){
                        mark = cellModel.getMark1();
                    }
                    if (numberAction == NumberAction.MARK_CANDIDATE_2){
                        mark = cellModel.getMark2();
                    }
                    for (int v=1; v<=gameModel.dimension2; v++){
                        if (cellModel.isCandidateIn(v,mark)){
                            gameState.setSelectedCell(cellModel);
                            mainView.gameView.invalidate();
                            WaitUtil.doWait(this, 300, MainControl.class.getName());
                            gameModel.setValue(cellModel, v);
                            gameState.setSelectedCell(cellModel);
                            mainView.gameView.invalidate();
                            WaitUtil.doWait(this, 200, MainControl.class.getName());
                            if (cellModel.getSolution() != v){
                                return false;
                            }
                            break;
                        }
                    }
                }
            }
        }
        return true;
    }


    protected void showHelp(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://mg4gh.github.io/MGSudoku/index.html"));
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.startActivity(browserIntent);
        }

    }


}
