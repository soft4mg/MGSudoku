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

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import de.soft4mg.sudoku.R;

public class ControlView extends RelativeLayout {

    TextDetails textDetails;
    Context context;

    PrefUtil prefUtil;
    ControlViewListener controlViewListener = null;

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

    public ControlView(TextDetails textDetails, float widthPercent, float heightPercent) {
        super(textDetails.context);

        this.textDetails = textDetails;
        context = textDetails.context;
        prefUtil = new PrefUtil(textDetails.context);
        setMinimumWidth((int)textDetails.widthPercentToPx(widthPercent));
        setMinimumHeight((int)textDetails.heightPercentToPx(heightPercent));

        setBackgroundColor(getResources().getColor(R.color.sd_bg, textDetails.context.getTheme()) );

        textDetails.createTextView(this, 3, 42, 20, 15, "Points:", 4f);
        tvPoints = textDetails.createTextView(this, 18, 42, 20, 15, "0", 4f);
        textDetails.createTextView(this, 35, 42, 15, 15, "Time:", 4f);
        tvTime = textDetails.createTextView(this, 47, 42, 15, 15, "23:19", 4f);
        textDetails.createTextView(this, 68, 42, 15, 15, "Errors:", 4f);
        tvError = textDetails.createTextView(this, 82, 42, 15, 15, "0", 4f);

        Button btDim = textDetails.createButton(this,1,0, 33, 14, getDimText(), 4f);
        btDim.setOnClickListener(view -> {
            if (prefUtil.getInt(R.string.prefModelDimension,3) == 3){
                prefUtil.putInt(R.string.prefModelDimension, 4);
            } else {
                prefUtil.putInt(R.string.prefModelDimension, 3);
            }
            btDim.setText(getDimText());
        });
        GameLevel gameLevel = GameLevel.valueOf( prefUtil.getString(R.string.prefLevel, GameLevel.MEDIUM.toString()) );
        Button btLevel = textDetails.createButton(this,34,0, 32, 14, getLevelText(), 4f);
        btLevel.setOnClickListener(vaiew -> {
            showLevelDialog(btLevel);
        });

        Button btHelp = textDetails.createButton(this,66,0, 33, 14, "Help", 4f);
        btHelp.setOnClickListener(v -> {
            controlViewListener.showHelpRequested();
//            Toast.makeText(textDetails.context,"Not yet implemented",Toast.LENGTH_LONG).show();
        });



        Button btNewGame = textDetails.createButton(this,1,12, 33, 14, "New Game", 4f);
        btNewGame.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(textDetails.context);
            alertDialogBuilder.setTitle("New Game");
            alertDialogBuilder
                    .setCancelable(false)
                    .setSingleChoiceItems(GameLevel.stringValues(), gameLevel.ordinal(),null)
                    .setMessage("Don't use candidates and double points ????")
                    .setPositiveButton("Double Points", (dialog, id) -> {
                        startNewGame(false);
                    })
                    .setNegativeButton("No thanks", (dialog, id) -> {
                        startNewGame(true);
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        });



        btShowCandidates = textDetails.createButton(this,34,12, 32, 14, getShowCandidateText(), 4f);
        btShowCandidates.setOnClickListener(view -> {
            onShowCandidatesChanged(!prefUtil.getBoolean(R.string.prefShowCandidates, true));
            controlViewListener.showCandidatesRequested( prefUtil.getBoolean(R.string.prefShowCandidates) );
        });

        Button btStat = textDetails.createButton(this,66,12, 33, 14, "Statistic", 4f);
        btStat.setOnClickListener(v -> {
            new StatisticDialog().showStatisticDialog(context);
        });

        textDetails.createButton(this,1,24, 33, 14, "Init\nCandidates", 4f).setOnClickListener(view -> controlViewListener.initCandidatesRequested());

        Button btUndo = textDetails.createButton(this,34,24, 32, 14, "Undo", 4f);
        btUndo.setOnClickListener(v -> {
            controlViewListener.undoRequested();
        });

        textDetails.createButton(this,66,24, 33, 14, "Clear\nMarker", 4f).setOnClickListener(view -> controlViewListener.clearMarkerRequested());

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(textDetails.context);

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

    public void setControlViewListener(ControlViewListener controlViewListener){
        this.controlViewListener = controlViewListener;
    }

}
