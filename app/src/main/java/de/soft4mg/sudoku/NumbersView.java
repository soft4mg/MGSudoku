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

import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Map;

import de.soft4mg.sudoku.R;

public class NumbersView extends RelativeLayout {

    TextDetails textDetails;

    Map<NumberAction, Button> btMap = new HashMap<>();
    PrefUtil prefUtil;
    NumberAction selectedNumberAction;
    GameModel gameModel;

    public NumbersView(CommonViewDetails details, TextDetails textDetails, GameState gameState, NumbersListener numbersListener){
        super(details.context);
        this.textDetails = textDetails;
        prefUtil = new PrefUtil(textDetails.context);

        setBackgroundColor(getResources().getColor(R.color.sd_bg, details.context.getTheme()) );
        gameModel = gameState.gameModel;

        selectedNumberAction = NumberAction.valueOf( prefUtil.getString(R.string.prefNumberAction, NumberAction.SET_NUMBER.name() ) );
        btMap.put(NumberAction.SET_NUMBER, textDetails.createButton(this, 0,0, 25, 15, "SET\nNUMBER", 4f));
//        btMap.get(NumberAction.SET_NUMBER).setOnClickListener(view -> {
//            selectedNumberAction = NumberAction.SET_NUMBER;
//            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
//            textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
//        });
        btMap.put(NumberAction.SET_CANDIDATE, textDetails.createButton(this,25,0, 25, 15, "SET\nCANDIDATE", 4f));
//        btMap.get(NumberAction.SET_CANDIDATE).setOnClickListener(view -> {
//            selectedNumberAction = NumberAction.SET_CANDIDATE;
//            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
//            textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
//        });
        btMap.put(NumberAction.MARK_CANDIDATE_1, textDetails.createButton(this,50,0, 25, 15, "MARK\nBLUE", 4f));
//        btMap.get(NumberAction.MARK_CANDIDATE_1).setOnClickListener(view -> {
//            selectedNumberAction = NumberAction.MARK_CANDIDATE_1;
//            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
//            textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
//        });
        btMap.put(NumberAction.MARK_CANDIDATE_2, textDetails.createButton(this,75,0, 25, 15, "MARK\nGREEN", 4f));
//        btMap.get(NumberAction.MARK_CANDIDATE_2).setOnClickListener(view -> {
//            selectedNumberAction = NumberAction.MARK_CANDIDATE_2;
//            prefUtil.putString(R.string.prefNumberAction, selectedNumberAction.name());
//            textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );
//        });

        for (NumberAction numberAction : btMap.keySet()){
            Button button = btMap.get(numberAction);
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

        textDetails.refreshSelected( btMap.get( selectedNumberAction ), btMap.values() );




        for (int i = 1; i <= gameModel.dimension2; i++){
            final int value = i;
            CellModel cellModel = new CellModel(gameModel.dimension, 0, i);
            cellModel.setValue(value);
            cellModel.setEnabled(gameModel.getNumValue(value) != gameModel.dimension2);
            CellView cellView = new CellView(details, gameState, cellModel);


            if (gameModel.dimension <= 3){
                // one row
                cellView.setX( (i-1)*details.cellDimension + ((i-1)/gameModel.dimension)*details.baseBorder  +2*details.baseBorder);
                cellView.setY( textDetails.heightPercentToPx(25) );

            } else {
                // two rows
                int rowCount = gameModel.dimension2 / 2;
                cellView.setX( (((i-1)%rowCount))* details.cellDimension * 2  +details.baseBorder + details.cellDimension/2);
                cellView.setY( textDetails.heightPercentToPx(21) + 2*details.baseBorder + ((i-1)/rowCount)*details.cellDimension*2 );

            }
            cellView.setWidth((int)details.cellDimension+1);
            cellView.setHeight((int)details.cellDimension+1);
            cellView.setOnClickListener(v -> {
                if (v instanceof CellView) {
                    CellView cellView12 = (CellView) v;
                    numbersListener.numberPressed(cellView12.cellModel.getValue(), selectedNumberAction);
//                    cellModel.setEnabled(gameModel.getNumValue(value) != gameModel.dimension2);
                }
            });
            cellView.setOnLongClickListener(v -> {
                if (v instanceof CellView) {
                    CellView cellView1 = (CellView) v;
                    numbersListener.numberPressedLong(cellView1.cellModel.getValue(), selectedNumberAction);
                    cellModel.setEnabled(gameModel.getNumValue(value) != gameModel.dimension2);
                }
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
                cellModel.setEnabled(gameModel.getNumValue(cellModel.value) != gameModel.dimension2);
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
