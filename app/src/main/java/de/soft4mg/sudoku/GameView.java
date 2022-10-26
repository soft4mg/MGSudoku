package de.soft4mg.sudoku;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;

import de.soft4mg.sudoku.R;

public class GameView extends RelativeLayout{

    CommonViewDetails details;
    GameState gameState;
    GameModel gameModel;

    public GameView(CommonViewDetails details, GameState gameState) {
        super(details.context);
        setKeepScreenOn(true);
        this.details = details;
        this.gameState = gameState;
        this.gameModel = gameState.getGameModel();

        int mDim = gameModel.getDimension();
        int mDim2 = mDim * mDim;

        View.OnClickListener ocl = view -> {
            if (view instanceof CellView) {
                CellView cellView = (CellView) view;
                gameState.selectedCell = cellView.cellModel;
                invalidate();
            }
        };


        for (int i = 1; i <= mDim2; i++){
            for (int j = 1; j <= mDim2; j++){
                float cellDimension = details.cellDimension;
                CellView cv = new CellView(details, gameState, gameModel.getCellModel(i,j));
                cv.setX( (j-1)*cellDimension + (((j-1)/mDim) + 2)*details.baseBorder);
                cv.setY( (i-1)*cellDimension + (((i-1)/mDim) + 2)*details.baseBorder);
                cv.setWidth((int)cellDimension+1);
                cv.setHeight((int)cellDimension+1);
                cv.setMinimumWidth((int)cellDimension+1);
                cv.setMinimumHeight((int)cellDimension+1);
                cv.setOnClickListener(ocl);
                this.addView(cv);
            }
        }


        AppCompatTextView vBorder = new AppCompatTextView(details.context){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float cellDimension = details.cellDimension;
                drawBorder(canvas, 0, 0, details.pxWidth, details.paintBorder);
                float baseBorder = details.baseBorder;
                for (int i=0; i<gameModel.dimension; i++){
                    for (int j=0; j<gameModel.dimension; j++){
                        drawBorder(canvas,
                                baseBorder+i*(baseBorder+gameModel.dimension*cellDimension),
                                baseBorder+j*(baseBorder+gameModel.dimension*cellDimension),
                                baseBorder*2+cellDimension*gameModel.dimension,
                                details.paintWithColor(details.paintBorder, R.color.sd_border2));
                    } // for j
                } // for i
            } // onDraw
        };
        vBorder.setX(0);
        vBorder.setY(0);
        vBorder.setWidth((int)details.pxWidth);
        vBorder.setHeight((int)details.pxWidth);
        addView(vBorder);
    }

    void drawBorder(Canvas canvas, float x, float y, float d, Paint paint){
        float w = paint.getStrokeWidth();
        float w2 = w/2;
        Path p = new Path();
        p.moveTo(x+w2, y+w2);
        p.lineTo(x+w2, y+d-w2);
        p.lineTo(x+d-w2, y+d-w2);
        p.lineTo(x+d-w2, y+w2);
        p.lineTo(x+w2, y+w2);
        canvas.drawPath(p, paint);
    }


    @Override
    public void invalidate() {
        super.invalidate();
        for (int cIdx=0; cIdx < getChildCount(); cIdx++){
            getChildAt(cIdx).invalidate();
        }
    }

}
