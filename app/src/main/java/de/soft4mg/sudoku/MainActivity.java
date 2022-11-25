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

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import de.soft4mg.sudoku.R;

public class MainActivity extends AppCompatActivity {

    public static String LABEL = "MGS";
    Process pLogcat;
    MainControl mainControl;

    public void startLogging(File logDir){
        try {
            String cmd = "logcat "+ LABEL+":d *:W -f "+logDir.getAbsolutePath()+"/log.txt -r 10000 -n10";
            Log.i(LABEL, " Start Logging: "+cmd);
            pLogcat = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.e(LABEL, e.getMessage(), e);
        }
        Log.i(LABEL," Starting Logger finished.");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startLogging(getExternalFilesDir(null));

        setContentView(R.layout.main_sudoku_layout2);
        ViewGroup vg =  findViewById(android.R.id.content);
        MainView mainView = (MainView) vg.getChildAt(0);
        mainView.init(this);
        mainView.invalidate();
        mainControl = new MainControl(this, mainView);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mainControl.onResume();
    }

    @Override
    protected void onPause() {
        mainControl.onPause();
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        pLogcat.destroy();
    }



}