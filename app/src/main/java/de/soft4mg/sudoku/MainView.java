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
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class MainView extends LinearLayout {

    RelativeLayout controlViewArea;
    RelativeLayout gameViewArea;
    RelativeLayout numberViewArea;

    int width;
    int height;
    int height1;
    int height2;

    CommonViewDetails details;
    TextDetails textDetails;

    ControlView controlView;
    GameView gameView;
    NumbersView numbersView;

    public MainView(Context context) {
        super(context);
    }

    public MainView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(Context context){
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        height1 = 50*(height-width)/100;
        height2 = 50*(height-width)/100;

        textDetails = new TextDetails(context, width, height);

        controlViewArea = new RelativeLayout(context);
        this.addView(controlViewArea);
        controlViewArea.setBackgroundColor(0xFFFFAAAA);
        controlView = new ControlView(textDetails, 100, 50);
        controlViewArea.addView(controlView);

        gameViewArea = new RelativeLayout(context);
        this.addView(gameViewArea);
        gameViewArea.setBackgroundColor(0xFFAAFFAA);

        numberViewArea = new RelativeLayout(context);
        this.addView(numberViewArea);
        numberViewArea.setBackgroundColor(0xFFAAFFFF);
    }

    void initControlView(Context context, ControlViewListener controlViewListener){
        controlView.setControlViewListener(controlViewListener);
    }



    void initNewGame(GameState gameState, NumbersListener numbersListener ){
        details = new CommonViewDetails(getContext(), width, height, gameState.gameModel.dimension);

        controlView.setPoints(gameState.getGamePoints(null));
        controlView.setGameErrors(gameState.errorCounter);

        gameView = new GameView(details, gameState);
        gameView.setMinimumWidth(width);
        gameView.setMinimumHeight(width);
        gameViewArea.removeAllViews();
        gameViewArea.addView(gameView);

        numbersView = new NumbersView(details, textDetails, gameState, numbersListener);
        numbersView.setMinimumWidth(width);
        numbersView.setMinimumHeight(height2);
        numberViewArea.removeAllViews();
        numberViewArea.addView(numbersView);
    }

    void invalidateGameAndNumbers(){
        for (int i=0; i<gameViewArea.getChildCount(); i++){
            gameViewArea.getChildAt(i).invalidate();
        }
        for (int i=0; i<numberViewArea.getChildCount(); i++){
            numberViewArea.getChildAt(i).invalidate();
        }
    }

}
