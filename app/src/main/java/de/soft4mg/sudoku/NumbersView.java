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
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ViewConstructor")
public class NumbersView extends RelativeLayout {

    TextDetails textDetails;

    Map<NumberAction, Button> btMap = new HashMap<>();
    PrefUtil prefUtil;
    NumberAction selectedNumberAction;
    GameModel gameModel;

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    public NumbersView(CommonViewDetails details, TextDetails textDetails, GameState gameState, NumbersListener numbersListener){
        super(details.context);
        this.textDetails = textDetails;
        prefUtil = new PrefUtil(textDetails.context);

        setBackgroundColor(getResources().getColor(R.color.sd_bg, details.context.getTheme()) );
        gameModel = gameState.getGameModel();

        selectedNumberAction = NumberAction.valueOf( prefUtil.getString(R.string.prefNumberAction, NumberAction.SET_NUMBER.name() ) );
        btMap.put(NumberAction.SET_NUMBER, textDetails.createButton(this, 0,0, 25, 15, "SET\nNUMBER", 4f));
        btMap.put(NumberAction.SET_CANDIDATE, textDetails.createButton(this,25,0, 25, 15, "SET\nCANDIDATE", 4f));
        btMap.put(NumberAction.MARK_CANDIDATE_1, textDetails.createButton(this,50,0, 25, 15, "MARK\nBLUE", 4f));
        btMap.put(NumberAction.MARK_CANDIDATE_2, textDetails.createButton(this,75,0, 25, 15, "MARK\nGREEN", 4f));

        for (NumberAction numberAction : btMap.keySet()){
            Button button = btMap.get(numberAction);
            if (button != null){
                button.setOnClickListener(view -> {
                    selectedNumberAction = numberAction;
                    prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
                    textDetails.refreshSelected( view, btMap.values() );
                    numbersListener.buttonPressed(numberAction);
                });
                button.setOnLongClickListener(v -> {
                    numbersListener.buttonPressedLong(numberAction);
                    return true;
                });
            }
        }

        textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );

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
                cellView.setX( (i-1)*details.cellDimension + ((i-1)/gameModel.dimension)*details.baseBorder  +2*details.baseBorder);
                cellView.setY( textDetails.heightPercentToPx(25) );
            } else {
                // two rows
                int rowCount = gameModel.dimension2 / 2;
                int colCount = ((i-1)%rowCount);
                cellView.setX( colCount* details.cellDimension * 2  +details.baseBorder*colCount + details.cellDimension/2);
                cellView.setY( textDetails.heightPercentToPx(18) + ((i-1)/rowCount)*((int)(details.cellDimension*1.8)+details.baseBorder) );

                TextView v = new TextView(getContext());
                v.setX( (((i-1)%rowCount))* details.cellDimension * 2  +details.baseBorder*colCount);
                v.setY( textDetails.heightPercentToPx(18) + ((i-1)/rowCount)*((int)(details.cellDimension*1.9))+details.baseBorder -details.cellDimension/2);
                v.setWidth((int)details.cellDimension*2);
                v.setHeight((int)(details.cellDimension*1.8));
//                v.setBackgroundColor(0x80E0FFE0);
                this.addView(v);
                oclView = v;
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

    public void setNumberAction(NumberAction newNumberAction){
        if (newNumberAction != selectedNumberAction){
            selectedNumberAction = newNumberAction;
            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
            textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
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
