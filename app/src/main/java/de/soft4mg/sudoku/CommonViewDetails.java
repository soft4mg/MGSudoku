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

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;

public class CommonViewDetails {

    Context context;
    float pxWidth, pxHeight;
    float cellDimension;
    TextPaint paintTextL = new TextPaint();
    TextPaint paintTextS = new TextPaint();
    Paint paintBorder = new Paint();
    Paint paintBorderCell = new Paint();
    Paint paintBg = new Paint();

    float baseBorder = 8;
    float cellBorder = baseBorder/4;

    float charWidthL;
    float charWidthS;
    Rect[] charDimsL;
    Rect[] charDimsS;


    public CommonViewDetails(Context context, float width, float height, int modelDimension){
        this.context = context;
        this.pxWidth = width;
        this.pxHeight = height;
        this.cellDimension = (pxWidth - (baseBorder*(modelDimension+3.0f))) / (modelDimension*modelDimension);

        Typeface monoface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL);
        paintTextL.setTextSize( cellDimension * 0.95f );
        paintTextL.setTypeface(monoface);

        paintTextS.setTextSize( (cellDimension*0.95f / modelDimension ) );
        paintTextS.setTypeface(monoface);

        paintBorder.setStyle(Paint.Style.STROKE);
        paintBorder.setStrokeWidth(baseBorder);

        paintBorderCell.setStyle(Paint.Style.STROKE);
        paintBorderCell.setStrokeWidth(cellBorder);


        charWidthL = paintTextL.measureText(" ");
        charDimsL = new Rect[modelDimension*modelDimension+1];

        charWidthS = paintTextS.measureText(" ");
        charDimsS = new Rect[modelDimension*modelDimension+1];

        for (int i=1; i<=modelDimension*modelDimension; i++){
            charDimsL[i] = new Rect();
            paintTextL.getTextBounds(CellModel.getText(i),0,1, charDimsL[i]);
            charDimsS[i] = new Rect();
            paintTextS.getTextBounds(CellModel.getText(i),0,1, charDimsS[i]);
        }
    }

    public Paint paintWithColor(Paint paint, int colorId){
        paint.setColor(context.getResources().getColor(colorId, context.getTheme()));
        return paint;
    }

}
