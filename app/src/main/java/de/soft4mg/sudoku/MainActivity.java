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

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Process pLogcat;
    MainControl mainControl;

    public void startLogging(File logDir){
        try {
            String cmd = "logcat *:i -f "+logDir.getAbsolutePath()+"/log.txt -r 10000 -n10";
            Log.i(MainActivity.class.getName(), " Start Logging: "+cmd);
            pLogcat = Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            Log.e(MainActivity.class.getName(), e.getMessage(), e);
        }
        Log.i(MainActivity.class.getName()," Starting Logger finished.");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startLogging(getExternalFilesDir(null));
        setContentView(R.layout.main_sudoku_layout);
        // don't change orientation when device is rotated
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        mainControl = new MainControl(this);
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