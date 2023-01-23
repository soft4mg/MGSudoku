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
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class NumbersView extends RelativeLayout {

    HashMap<View, float[]> viewDetailsMap = new HashMap<>();


    Map<NumberAction, Button> btMap = new HashMap<>();
    PrefUtil prefUtil;
    NumberAction selectedNumberAction;
    GameModel gameModel;

    public NumbersView(Context context, CommonViewDetails details, GameState gameState, NumbersListener numbersListener){
        super(details.context);
        prefUtil = new PrefUtil(context);

        setBackgroundColor(getResources().getColor(R.color.sd_bg, details.context.getTheme()) );
        gameModel = gameState.getGameModel();

        selectedNumberAction = NumberAction.valueOf( prefUtil.getString(R.string.prefNumberAction, NumberAction.SET_NUMBER.name() ) );
        Button btSetNumber = LayoutUtil.createButton(this, "SET\nNUMBER");
        viewDetailsMap.put(btSetNumber, new float[]{1f,3,23.75f,25,8});
        btMap.put(NumberAction.SET_NUMBER, btSetNumber);
        Button btSetCandidate = LayoutUtil.createButton(this, "SET\nCANDIDATE");
        viewDetailsMap.put(btSetCandidate, new float[]{25.75f,3,23.75f,25,8});
        btMap.put(NumberAction.SET_CANDIDATE, btSetCandidate);
        Button btMarkBlue = LayoutUtil.createButton(this, "MARK\nBLUE");
        viewDetailsMap.put(btMarkBlue, new float[]{50.5f,3,23.75f,25,8});
        btMap.put(NumberAction.MARK_CANDIDATE_1, btMarkBlue);
        Button btMarkGreen = LayoutUtil.createButton(this, "MARK\nGREEN");
        viewDetailsMap.put(btMarkGreen, new float[]{75.25f,3,23.75f,25,8});
        btMap.put(NumberAction.MARK_CANDIDATE_2, btMarkGreen);

        for (NumberAction numberAction : btMap.keySet()){
            Button button = btMap.get(numberAction);
            if (button != null){
                button.setOnClickListener(view -> {
                    selectedNumberAction = numberAction;
                    prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
                    LayoutUtil.refreshSelected( view, btMap.values() );
                    numbersListener.buttonPressed(numberAction);
                });
                button.setOnLongClickListener(v -> {
                    numbersListener.buttonPressedLong(numberAction);
                    return true;
                });
            }
        }

        LayoutUtil.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );

        for (int i = 1; i <= gameModel.dimension2; i++){
            final int value = i;
            final CellModel cellModel = new CellModel(gameModel.dimension, 0, i);
            cellModel.setValue(value);
            cellModel.setEnabled(gameModel.getNumValue(value) != gameModel.dimension2);
            final CellView cellView = new CellView(details, gameState, cellModel);
            cellView.setContentDescription("ActionButton "+i);
            View oclView = cellView;

            if (gameModel.dimension <= 3){
                // one row
                viewDetailsMap.put(cellView, new float[]{(i-1)*11+0.5f, 40, details.cellDimension+1, details.cellDimension+1, 1});
            } else {
                // two rows
                int rowCount = gameModel.dimension2 / 2;
                int colCount = ((i-1)%rowCount);
                viewDetailsMap.put(cellView, new float[]{(colCount)*12.5f+4, (((i-1)/rowCount)==0)?43:71, details.cellDimension+1, details.cellDimension+1, 1});

                oclView = new TextView(getContext()); // extra view for clicking (larger than cellview)
                oclView.setBackgroundColor(0x80B0FFB0);
                this.addView(oclView);
                viewDetailsMap.put(oclView, new float[]{(colCount)*12.5f+1, (((i-1)/rowCount)==0)?36:66, 12, 30, 1});
            }
            cellView.setWidth((int)details.cellDimension+1);
            cellView.setHeight((int)details.cellDimension+1);
            oclView.setOnClickListener(v -> numbersListener.numberPressed(cellView.cellModel.getValue(), selectedNumberAction));
            oclView.setOnLongClickListener(v -> {
                numbersListener.numberPressedLong(cellView.cellModel.getValue(), selectedNumberAction);
                cellModel.setEnabled(gameModel.getNumValue(value) != gameModel.dimension2);
                return true;
            });
            this.addView(cellView);
        }
    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(NumbersView.class.getName(), "NumbersView.onLayout changed="+changed+" width="+getWidth()+" height="+getHeight());
        super.onLayout(changed, l, t, r, b);

        if (changed){
            if ((getWidth() > 0) && (getHeight() > 0)){
                for (Map.Entry<View, float[]> entry : viewDetailsMap.entrySet()) {
                    if (entry.getKey() instanceof TextView) {
                        TextView textView = (TextView) entry.getKey();
                        LayoutUtil.layout(this, textView, entry.getValue());
                    }
                    if (entry.getKey() instanceof CellView) {
                        CellView cellView = (CellView) entry.getKey();
                        LayoutUtil.layout(this, cellView, entry.getValue());
                    }
                }
            }
        }
    }


    public void setNumberAction(NumberAction newNumberAction){
        if (newNumberAction != selectedNumberAction){
            selectedNumberAction = newNumberAction;
            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
            LayoutUtil.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        for (int cIdx=0; cIdx < getChildCount(); cIdx++){
            if (getChildAt(cIdx) instanceof CellView) {
                CellView cellView = (CellView) getChildAt(cIdx);
                CellModel cellModel = cellView.cellModel;
                cellModel.setEnabled(gameModel.getNumValue(cellModel.getValue()) != gameModel.dimension2);
                cellView.invalidate();
            }
        }
    }

    public String getTextOfNumberAction(NumberAction numberAction){
        Button button = btMap.get(numberAction);
        if (button != null){
            String text = button.getText().toString();
            return text.replaceAll("\n", " ");
        }
        return "";
    }


}
