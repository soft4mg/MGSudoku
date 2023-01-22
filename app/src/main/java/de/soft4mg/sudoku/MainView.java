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
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
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

    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    public void init(Context context){
        Resources myResources = context.getResources();
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        int idStatusBarHeight = myResources.getIdentifier( "status_bar_height", "dimen", "android");
        int statusBarHeight = (idStatusBarHeight > 0)?myResources.getDimensionPixelSize(idStatusBarHeight):160;
        int idNavBarHeight = myResources.getIdentifier( "navigation_bar_height", "dimen", "android");
        int navigationBarHeight = (idNavBarHeight > 0)?myResources.getDimensionPixelSize(idNavBarHeight):160;
        Log.i(MainActivity.LABEL,"XXX width="+width+" height="+height+" statusBarHeight="+statusBarHeight+" navigationBarHeight"+navigationBarHeight);

        height -= statusBarHeight;

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

    void initControlView(ControlViewListener controlViewListener){
        controlView.setControlViewListener(controlViewListener);
    }



    void initNewGame(GameState gameState, NumbersListener numbersListener ){
        details = new CommonViewDetails(getContext(), width, height, gameState.getGameModel().dimension);

        controlView.setPoints(gameState.getGamePoints());
        controlView.setGameErrors(gameState.getErrorCounter());

        gameView = new GameView(details, gameState);
        gameView.setMinimumWidth(width);
        //noinspection SuspiciousNameCombination
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
