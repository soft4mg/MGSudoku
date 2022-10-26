package de.soft4mg.sudoku;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import android.content.Intent;
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

import de.soft4mg.sudoku.R;

public class MainControl {

    MainView mainView;
    Context context;
    PrefUtil prefUtil;

    HashMap<String, ArrayList<String>> gameMap = new HashMap<>();
    GameState gameState;
    ArrayList<GameResult> gameResults;


    Handler timer = new Handler();
    TimerTask ttSecend = new TimerTask() {
        @Override
        public void run() {
            if (!gameState.isFinished()){
                timer.postDelayed(ttSecend, 1000);
                ++gameState.secondsPlayed;
                if (mainView.controlView != null){
                    mainView.controlView.setGameDuration(gameState.secondsPlayed);
                }
            }
        }
    };


    public MainControl(MainView mainView){
        this.mainView = mainView;
        context = mainView.getContext();
        prefUtil = new PrefUtil(context);
        mainView.initControlView(context, controlViewListener);
        gameState = null;
        initGameMap();

    }

    public void onCreate(){

    }

    public void onResume(){
        String sGameState = prefUtil.getString(R.string.stateGameControl, null);
        this.gameState = (sGameState==null)?startNewGame():JSON.parseObject(sGameState, GameState.class);
        onNewGameState();
    }

    public void onPause(){
        String sGameState =  JSON.toJSONString(gameState, true);
        prefUtil.putString(R.string.stateGameControl, sGameState);
        timer.removeCallbacks(ttSecend);
    }

    public void onDestroy(){

    }



    public GameState startNewGame(){
        try {
            int dimension = prefUtil.getInt( R.string.prefModelDimension, 3);
            GameLevel gameLevel = GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) );
            ArrayList<String> gameOptions = gameMap.get(gameLevel.toString()+dimension);
            Random random = new Random(System.currentTimeMillis());
            String gameOption = gameOptions.get((int)(random.nextDouble()*gameOptions.size()));
            InputStream is  = context.getAssets().open(gameOption);
            byte[] data = new byte[is.available()];
            is.read(data);
            String sdata = new String(data);
            GameModel gameModel = JSON.parseObject(sdata, GameModel.class);
            if (dimension == 4){
                gameModel.difficulty *= 3;
            }
            gameModel.logValues();
            Permutation.randomPermutation(gameModel);
            gameModel.logValues();

            return new GameState(gameModel);
        } catch (IOException e) {
            Log.e("MGS",e.getMessage(),e);
        }
        return null;
    }

    void onNewGameState(){
        gameState.setCandidatesUsed( prefUtil.getBoolean(R.string.prefShowCandidates, true) );
        mainView.initNewGame(gameState, numbersListener);

        timer.removeCallbacks(ttSecend);
        timer.postDelayed(ttSecend, 1000);
    }

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
            gameState.gameModel.initCandidates();
            recordUndoStep();
            mainView.gameView.invalidate();
        }

        @Override
        public void clearMarkerRequested() {
            gameState.gameModel.clearMarker();
            mainView.gameView.invalidate();
        }

        @Override
        public void showCandidatesRequested(boolean show) {
            gameState.setCandidatesUsed(gameState.isCandidatesUsed() | show);
            mainView.controlView.setPoints(gameState.getGamePoints(null));
        }

        @Override
        public void showHelpRequested() {
            showHelp();
        }
    };

    NumbersListener numbersListener = new NumbersListener() {
        @Override
        public void numberPressed(int number, NumberAction numberAction) {
            if (!gameState.isFinished() && (gameState.selectedCell != null)){
                if (gameState.selectedCell.isInitial()) return; // don't change initial set cells

                CellModel selectedCellModel = gameState.selectedCell;

                if ( numberAction == NumberAction.SET_CANDIDATE){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        selectedCellModel.toggleCandidate(number);
                    }
                } if ( numberAction == NumberAction.MARK_CANDIDATE_1){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        if (selectedCellModel.isCandidate(number)){ // ... and number pressed is a candiate
                            selectedCellModel.mark1 = selectedCellModel.toggleCandidateIn(number, selectedCellModel.mark1);
                        }
                    }
                } if ( numberAction == NumberAction.MARK_CANDIDATE_2){ // toggleCellCandidate value
                    if (selectedCellModel.getValue() == 0){ // act only, if cell is not yet set
                        if (selectedCellModel.isCandidate(number)){ // ... and number pressed is a candiate
                            selectedCellModel.mark2 = selectedCellModel.toggleCandidateIn(number, selectedCellModel.mark2);
                        }
                    }
                } else if ( numberAction == NumberAction.SET_NUMBER){

                    if (selectedCellModel.getValue() ==  number){ // if cell is set to given value, then unset it
                        selectedCellModel.setValue(0);
                    } else {
                        GameModel gameModel = gameState.getGameModel();
                        if (selectedCellModel.getValue() !=  selectedCellModel.getSolution()){
                            if (  gameModel.getNumValue(number) < gameModel.dimension2 ){
                                gameModel.setValue(selectedCellModel, number);
                                if (selectedCellModel.value != selectedCellModel.solution){
                                    gameState.setErrorCounter( gameState.getErrorCounter()+1 );
                                    mainView.controlView.setGameErrors(gameState.getErrorCounter());
                                    mainView.controlView.setPoints(gameState.getGamePoints(null));
                                } else {
                                    if (gameModel.isSolved( true)){
                                        gameState.setFinished(true);

                                        GameResult gameResult = new GameResult(System.currentTimeMillis(), gameState.getGamePoints(null), GameLevel.get(gameState.gameModel.dimension, gameState.gameModel.difficulty), gameState.getSecondsPlayed(), gameState.gameModel.difficulty);
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
        ArrayList<CellModel> undoCells = gameState.getGameModel().getResetChanged(false, gameState.selectedCell);
        if (undoCells.size() > 0){
            gameState.undoList.add(undoCells);
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
                        String.format(Locale.ENGLISH,"\nTime: %d:%02d ",gameState.secondsPlayed/60,gameState.secondsPlayed%60))
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    public void showAreYouReallySureDialog(NumberAction numberAction){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setTitle("Apply marked numbers");
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("Apply marked Numbers for.\n"+mainView.numbersView.getTextOfNumberAction(numberAction))
                .setPositiveButton("OK", (dialog, id) -> {
                    dialog.dismiss();
                    new Thread(){
                        @Override
                        public void run() {
                            evaluateMark(numberAction);
                        }
                    }.start();
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                   dialog.dismiss();
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void initGameMap(){
        try {
            for (int dim=3; dim <= 4; dim++){
                for (GameLevel gameLevel : GameLevel.values()){
                    gameMap.put(gameLevel.toString()+dim,new ArrayList<>());
                }
                for (String name : context.getAssets().list("dim"+dim)){
                    GameLevel gameLevel = GameLevel.get(dim, Integer.parseInt( name.split("_")[1] ));
                    gameMap.get(gameLevel.toString()+dim).add("dim"+dim+"/"+name);
                }
            }
        } catch (IOException e) {
            Log.e("MGS", e.getMessage(), e);
        }

    }


    public void evaluateMark(NumberAction numberAction){
        if (applyMark(numberAction)) {
            if (gameState.gameModel.isSolved( true)){
                gameState.setFinished(true);

                GameResult gameResult = new GameResult(System.currentTimeMillis(), gameState.getGamePoints(null), GameLevel.get(gameState.gameModel.dimension, gameState.gameModel.difficulty), gameState.getSecondsPlayed(), gameState.gameModel.difficulty);
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
            mainView.controlView.setPoints(gameState.getGamePoints(null));
        }
    }

    private boolean applyMark(NumberAction numberAction){
        GameModel gameModel = gameState.gameModel;
        for (int i=1; i<=gameModel.dimension2; i++){
            for (int j=1; j<=gameModel.dimension2; j++){
                CellModel cellModel = gameModel.getCellModel(i,j);
                if (cellModel.getValue() == 0){ // value not yet set
                    int mark = 0;
                    if (numberAction == NumberAction.MARK_CANDIDATE_1){
                        mark = cellModel.mark1;
                    }
                    if (numberAction == NumberAction.MARK_CANDIDATE_2){
                        mark = cellModel.mark2;
                    }
                    for (int v=1; v<=gameState.gameModel.dimension2; v++){
                        if (cellModel.isCandidateIn(v,mark)){
                            gameState.setSelectedCell(cellModel);
                            mainView.gameView.invalidate();
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {}
                            gameModel.setValue(cellModel, v);
                            gameState.setSelectedCell(cellModel);
                            mainView.gameView.invalidate();
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {}
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
