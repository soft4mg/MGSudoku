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
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class MainView extends LinearLayout {

    PrefUtil prefUtil;
    RelativeLayout controlViewArea;
    RelativeLayout gameViewArea;
    RelativeLayout numberViewArea;

    CommonViewDetails details;
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

    public void init(Activity context){
        prefUtil = new PrefUtil(context);


        controlViewArea = new RelativeLayout(context);
        this.addView(controlViewArea);
        controlViewArea.setBackgroundColor(0xFFFFAAAA);
        controlView = new ControlView(this.getContext());
        controlViewArea.addView(controlView);

        gameViewArea = new RelativeLayout(context);
        this.addView(gameViewArea);
        gameViewArea.setBackgroundColor(0xFFAAFFAA);

        numberViewArea = new RelativeLayout(context);
        this.addView(numberViewArea);
        numberViewArea.setBackgroundColor(0xFFAAFFFF);
    }

    void initControlView(ControlViewListener controlViewListener){
        controlView.setControlViewListener(controlViewListener);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed){
            int width = getWidth();
            int height = getHeight();
            Log.i(MainView.class.getName(),"XXX MainView.onLayout width="+width+" height="+height );

            details = new CommonViewDetails(getContext(), width, height, prefUtil.getInt( R.string.prefModelDimension, 3));

            if (controlView != null){
                controlView.setMinimumWidth(width);
                controlView.setMinimumHeight((height-width)/2);
            } else {
                Log.i(MainView.class.getName(),"controlView == null");
            }
            if (gameView != null){
                gameView.setMinimumWidth(width);
                //noinspection SuspiciousNameCombination
                gameView.setMinimumHeight(width);
            } else {
                Log.i(MainView.class.getName(),"gameView == null");
            }
            if (numbersView != null){
                numbersView.setMinimumWidth(width);
                numbersView.setMinimumHeight((height-width)/2);
            } else {
                Log.i(MainView.class.getName(),"numbersView == null");
            }

        }


    }

    void initNewGame(GameState gameState, NumbersListener numbersListener ){
        int width = Math.max(1080, getWidth());
        int height = Math.max(1920,getHeight());
        details = new CommonViewDetails(getContext(), width, height, gameState.getGameModel().dimension);

        controlView.setPoints(gameState.getGamePoints());
        controlView.setGameErrors(gameState.getErrorCounter());

        gameView = new GameView(details, gameState);
        gameView.setMinimumWidth(width);
        //noinspection SuspiciousNameCombination
        gameView.setMinimumHeight(width);
        gameViewArea.removeAllViews();
        gameViewArea.addView(gameView);

        numbersView = new NumbersView(getContext(), details, gameState, numbersListener);
        numbersView.setMinimumWidth(width);
        numbersView.setMinimumHeight((height-width)/2);
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
