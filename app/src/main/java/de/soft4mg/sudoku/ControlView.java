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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class ControlView extends RelativeLayout {


    HashMap<TextView, float[]> viewDetailsMap = new HashMap<>();

    Context context;

    PrefUtil prefUtil;
    ControlViewListener controlViewListener;

    TextView tvPoints;
    TextView tvError;
    TextView tvTime;
    Button btShowCandidates;


    public void setGameDuration(long duration){
        tvTime.setText(String.format(Locale.ENGLISH,"%d:%02d", duration/60, duration%60));
    }
    public void setGameErrors(int errors){
        tvError.setText(String.format(Locale.ENGLISH,"%d",errors));
    }
    public void setPoints(int difficulty){
        tvPoints.setText(String.format(Locale.ENGLISH,"%d",difficulty));
    }

    private String getDimText(){
        return "Dimension:\n"+((prefUtil.getInt(R.string.prefModelDimension,3)==3)?"9x9":"16x16");
    }
    private String getLevelText(){
        return "Level:\n"+GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) );
    }
    private String getShowCandidateText(){
        return "Candidates:\n"+(prefUtil.getBoolean(R.string.prefShowCandidates, true)?"Show":"Hide" );
    }

    public ControlView(Context context, ControlViewListener controlViewListener) {
        super(context);
        Log.i(ControlView.class.getName(),"ControlView.constructor start");

        this.context = context;
        this.controlViewListener = controlViewListener;
        prefUtil = new PrefUtil(context);

        setBackgroundColor(getResources().getColor(R.color.sd_bg, context.getTheme()) );

        viewDetailsMap.put( LayoutUtil.createTextView(this, "Points:"), new float[]{3,84,20,25,8});
        tvPoints = LayoutUtil.createTextView(this,  "0");
        viewDetailsMap.put(tvPoints, new float[]{18,84,20,25,8});
        viewDetailsMap.put( LayoutUtil.createTextView(this, "Time:"), new float[]{35,84,15,25,8});
        tvTime = LayoutUtil.createTextView(this,  "0:00");
        viewDetailsMap.put(tvTime, new float[]{47,84,20,25,8});
        viewDetailsMap.put( LayoutUtil.createTextView(this, "Errors:"), new float[]{68,84,15,25,8});
        tvError = LayoutUtil.createTextView(this, "0");
        viewDetailsMap.put(tvError, new float[]{82,84,20,25,8});

        Button btDim = LayoutUtil.createButton(this,getDimText());
        viewDetailsMap.put(btDim, new float[]{1,2,32,23,8});
        btDim.setOnClickListener(view -> {
            if (prefUtil.getInt(R.string.prefModelDimension,3) == 3){
                prefUtil.putInt(R.string.prefModelDimension, 4);
            } else {
                prefUtil.putInt(R.string.prefModelDimension, 3);
            }
            btDim.setText(getDimText());
        });
        Button btLevel = LayoutUtil.createButton(this, getLevelText());
        viewDetailsMap.put(btLevel, new float[]{34,2,32,23,8});
        btLevel.setOnClickListener(view -> showLevelDialog(btLevel));

        Button btHelp = LayoutUtil.createButton(this, "Help");
        viewDetailsMap.put(btHelp, new float[]{67,2,32,23,8});
        btHelp.setOnClickListener(v -> controlViewListener.showHelpRequested());

        Button btNewGame = LayoutUtil.createButton(this, "New Game");
        viewDetailsMap.put(btNewGame, new float[]{1,27,32,23,8});
        btNewGame.setOnClickListener(v -> startNewGame(prefUtil.getBoolean(R.string.prefShowCandidates)) );

        btShowCandidates = LayoutUtil.createButton(this, getShowCandidateText());
        viewDetailsMap.put(btShowCandidates, new float[]{34,27,32,23,8});
        btShowCandidates.setOnClickListener(view -> {
            onShowCandidatesChanged(!prefUtil.getBoolean(R.string.prefShowCandidates, true));
            controlViewListener.showCandidatesRequested( prefUtil.getBoolean(R.string.prefShowCandidates) );
        });

        Button btStat = LayoutUtil.createButton(this, "Statistic");
        viewDetailsMap.put(btStat, new float[]{67,27,32,23,8});
        btStat.setOnClickListener(v -> new StatisticDialog().showStatisticDialog(context));

        Button btInitCandidates = LayoutUtil.createButton(this, "Init\nCandidates");
        viewDetailsMap.put(btInitCandidates, new float[]{1,52,32,23,8});
        btInitCandidates.setOnClickListener(view -> controlViewListener.initCandidatesRequested());

        Button btUndo = LayoutUtil.createButton(this, "Undo");
        viewDetailsMap.put(btUndo, new float[]{34,52,32,23,8});
        btUndo.setOnClickListener(v -> controlViewListener.undoRequested());

        Button btClearMarker = LayoutUtil.createButton(this, "Clear\nMarker");
        viewDetailsMap.put(btClearMarker, new float[]{67,52,32,23,8});
        btClearMarker.setOnClickListener(view -> controlViewListener.clearMarkerRequested());
    }

    public void layout(int width, int height){
        this.setMinimumWidth(width);
        this.setMinimumHeight(height);
        for (Map.Entry<TextView, float[]> entry : viewDetailsMap.entrySet()){
            LayoutUtil.layout(width, height, entry.getKey(), entry.getValue());
        }
    }

    private void onShowCandidatesChanged(boolean newShowCandidates){
        prefUtil.putBoolean(R.string.prefShowCandidates,newShowCandidates);
        controlViewListener.repaintRequested();
        btShowCandidates.setText(getShowCandidateText());
    }

    private void startNewGame(boolean showCandidates){
        onShowCandidatesChanged(showCandidates);
        controlViewListener.newGameRequested(
                prefUtil.getInt(R.string.prefModelDimension),
                GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) ));
    }

    public void  showLevelDialog(Button btLevel){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        GameLevel gameLevel = GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) );
        alertDialogBuilder.setTitle("Title");
        alertDialogBuilder
                .setCancelable(false)
                .setSingleChoiceItems(GameLevel.stringValues(), gameLevel.ordinal(),null)
                .setPositiveButton("OK", (dialog, id) -> {
                    ListView lw = ((AlertDialog) dialog).getListView();
                    String sLevel = lw.getAdapter().getItem(lw.getCheckedItemPosition()).toString();
                    prefUtil.putString(R.string.prefLevel, sLevel);
                    btLevel.setText(getLevelText());
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
