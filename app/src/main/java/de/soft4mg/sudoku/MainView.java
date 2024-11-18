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

    MainViewListener mainViewListener;
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

    public void init(Activity context, MainViewListener mainViewListener){
        prefUtil = new PrefUtil(context);

        this.mainViewListener = mainViewListener;
        controlViewArea = new RelativeLayout(context);
        this.addView(controlViewArea);
        controlViewArea.setBackgroundColor(0xFFFFAAAA);

        gameViewArea = new RelativeLayout(context);
        this.addView(gameViewArea);
        gameViewArea.setBackgroundColor(0xFFAAFFAA);

        numberViewArea = new RelativeLayout(context);
        this.addView(numberViewArea);
        numberViewArea.setBackgroundColor(0xFFAAFFFF);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(MainView.class.getName(),"MainView.onLayout l="+l+" t="+t+" r="+r+" b="+b );
        super.onLayout(changed, l, t, r, b);
        if (changed){
            int width = getWidth();
            int height = getHeight();
            int[] pos = new int[2];
            this.getLocationOnScreen(pos);
            Log.i(MainView.class.getName(),"MainView.onLayout width="+width+" height="+height +" pos="+pos[0]+" "+pos[1]);
            prefUtil.putInt(R.string.prefLastMainWidth, width);
            prefUtil.putInt(R.string.prefLastMainHeight, height);
            mainViewListener.layoutRequested();
        }
    }

    void initNewGame(GameState gameState, NumbersListener numbersListener, ControlViewListener controlViewListener ){

        int defaultWidth = prefUtil.getInt(R.string.prefDefaultMainWidth, 1080);
        int width = prefUtil.getInt(R.string.prefLastMainWidth, defaultWidth);
        int defaultHeight = prefUtil.getInt(R.string.prefDefaultMainHeight, 1920);
        int height = prefUtil.getInt(R.string.prefLastMainHeight, defaultHeight);
        Log.i(MainView.class.getName(),"MainView.initNewGame width="+width+" height="+height );
        int dimension = gameState.getGameModel().getDimension();
        details = new CommonViewDetails(getContext(), width, height, dimension);

        controlViewArea.removeAllViews();
        controlView = new ControlView(this.getContext(), controlViewListener);
        controlView.layout(width, (height-width)/2);
        controlViewArea.addView(controlView);
        controlView.setPoints(gameState.getGamePoints());
        controlView.setGameErrors(gameState.getErrorCounter());

        gameViewArea.removeAllViews();
        gameView = new GameView(details, gameState);
        gameView.setMinimumWidth(width);
        //noinspection SuspiciousNameCombination
        gameView.setMinimumHeight(width);
        gameViewArea.addView(gameView);

        numberViewArea.removeAllViews();
        numbersView = new NumbersView(getContext(), details, gameState, numbersListener);
        numbersView.layout(width,(height-width)/2);
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
