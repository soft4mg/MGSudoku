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

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.AppCompatTextView;

import java.util.Observable;
import java.util.Observer;

public class CellView extends AppCompatTextView implements Observer {

    GameState gameState;
    CellModel cellModel;
    CommonViewDetails details;

    Typeface face = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);

    int gDim = 0;     // dimension of game
    float cDim = 0;   // cellDimension in pixel
    float cBod2 = 0;  // half of borderWidth in pixel
    float cDimS = 0;  // dimension of subcell for candidates

    SharedPreferences preferences;


    public CellView(CommonViewDetails details, GameState gameState, CellModel cellModel) {
        super(details.context);
        this.details = details;
        this.gameState = gameState;
        this.cellModel = cellModel;
//        this.cellModel.addObserver(this);
        setTypeface(face);
        setWillNotDraw(false);
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gDim = cellModel.getDimension();  // game dimension
        cDim = details.cellDimension;             // cell dimension
        cBod2 = details.cellBorder / 2.0f;        // cell border half
        cDimS = (cDim -4* cBod2) / gDim;

        drawBg(canvas);
        if (cellModel.value != 0){
            drawValue(canvas);
        } else if ( preferences.getBoolean(getResources().getString(R.string.prefShowCandidates), true)){
            drawCandidates(canvas);
        }
        drawBorder(canvas);
    }

    private void drawValue(Canvas canvas){
        int colorId = cellModel.isInitial()?R.color.sd_number_init:R.color.sd_number;
        colorId = ((cellModel.value!=cellModel.solution)&&(cellModel.solution!=0))?R.color.sd_number_error:colorId;
        if (!cellModel.enabled)
            colorId = R.color.sd_number_disabled;
        else colorId = colorId;
        canvas.drawText(cellModel.getText(),
                cDim / 2 - details.charWidthL/2,
                cDim / 2 + details.charDimsL[cellModel.value].height()/2,
                details.paintWithColor(details.paintTextL, colorId));
    }
    private void drawCandidates(Canvas canvas){
        for (int i = 1; i<=(gDim * gDim); i++){
            if (cellModel.isCandidate(i)){
                if (gDim <= 3){
                    if (gameState.isSelectedValue(i)){
                        drawCandidateSquare(canvas, i, details.paintWithColor(details.paintBg, R.color.sd_bg_sel_value));
                    }
                    if (cellModel.isCandidateIn(i,cellModel.mark1)){
                        drawCandidateMark2(canvas, i, details.paintWithColor(details.paintBg, R.color.sd_candidate_mark_blue));
                    }
                    if (cellModel.isCandidateIn(i,cellModel.mark2)){
                        drawCandidateMark1(canvas, i, details.paintWithColor(details.paintBg, R.color.sd_candidate_mark_green));
                    }
                    canvas.drawText(CellModel.getText(i),
                            2* cBod2 + ((i-1)% gDim)* cDimS + cDimS / 2 - details.charWidthS/2,
                            2* cBod2 + ((i-1)/ gDim)* cDimS + cDimS / 2 + details.charDimsS[i].height()/2,
                            details.paintTextS);

                } else {
                    drawCandidateSquare(canvas, i, details.paintWithColor(details.paintBg, gameState.isSelectedValue(i)?R.color.sd_bg_sel_cell:R.color.sd_candidate_small));
                }

            }
        }
    }

    private void drawCandidateSquare(Canvas canvas, int candidate, Paint paint){
        float x = 2* cBod2 + ((candidate-1)% gDim)* cDimS;
        float y = 2* cBod2 + ((candidate-1)/ gDim)* cDimS;

        Path p = new Path();
        p.moveTo(x,y);
        p.lineTo(x,y+ cDimS);
        p.lineTo(x+ cDimS,y+ cDimS);
        p.lineTo(x+ cDimS, y);
        p.lineTo(x,y);
        canvas.drawPath(p, paint);
    }
    private void drawCandidateMark1(Canvas canvas, int candidate, Paint paint){
        float x = 2* cBod2 + ((candidate-1)% gDim)* cDimS;
        float y = 2* cBod2 + ((candidate-1)/ gDim)* cDimS;

        float m = cDimS/10;
        Path p = new Path();
        p.moveTo(x,y);
        p.lineTo(x+2*m,y);
        p.lineTo(x+ 2*m,y+ cDimS);
        p.lineTo(x, y+cDimS);
        p.lineTo(x,y);
        canvas.drawPath(p, paint);
    }
    private void drawCandidateMark2(Canvas canvas, int candidate, Paint paint){
        float x = 2* cBod2 + ((candidate-1)% gDim)* cDimS;
        float y = 2* cBod2 + ((candidate-1)/ gDim)* cDimS;

        float m = cDimS/10;
        Path p = new Path();
        p.moveTo(x+cDimS, y);
        p.lineTo(x+cDimS-2*m,y);
        p.lineTo(x+cDimS- 2*m,y+ cDimS);
        p.lineTo(x+cDimS,y+cDimS);
        p.lineTo(x+cDimS, y);
        canvas.drawPath(p, paint);
    }

    private void drawBg(Canvas canvas){
        Path p = new Path();
        p.moveTo(cBod2, cBod2);
        p.lineTo(cBod2, cDim - cBod2);
        p.lineTo(cDim - cBod2, cDim - cBod2);
        p.lineTo(cDim - cBod2, cBod2);
        p.lineTo(cBod2, cBod2);
        int colorId = gameState.isSelectedRowOrColumn(cellModel)?R.color.sd_bg_sel_row_col:R.color.sd_bg;
        colorId = gameState.isSelectedValue(cellModel.value)?R.color.sd_bg_sel_value:colorId;
        colorId = gameState.isSelected(cellModel)?R.color.sd_bg_sel_cell:colorId;
        colorId = cellModel.row==0?R.color.sd_bg:colorId; // for numbersView
        canvas.drawPath(p, details.paintWithColor(details.paintBg, colorId));
    }
    private void drawBorder(Canvas canvas){
        Path p = new Path();
        p.moveTo(cBod2, cBod2);
        p.lineTo(cBod2, cDim - cBod2);
        p.lineTo(cDim - cBod2, cDim - cBod2);
        p.lineTo(cDim - cBod2, cBod2);
        p.lineTo(cBod2, cBod2);
        canvas.drawPath(p, details.paintBorderCell);
    }


    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof CellModel) {
            CellModel cellModel = (CellModel) observable;
            this.invalidate();
        }
    }



}
