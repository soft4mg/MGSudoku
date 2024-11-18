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
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

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
        Log.i(this.getClass().getName(),"lc onCreate");

        startLogging(Objects.requireNonNull(getExternalFilesDir(null)));
        setContentView(R.layout.main_sudoku_layout);
        // don't change orientation when device is rotated
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        mainControl = new MainControl(this);
        adoptLayout();
    }

    @Override
    protected void onResume() {
        Log.i(this.getClass().getName(),"lc onResume");
        super.onResume();

        mainControl.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(this.getClass().getName(),"lc onPause");
        mainControl.onPause();
        super.onPause();
        Log.i(this.getClass().getName(),"lc onPause2");
    }


    @Override
    protected void onDestroy() {
        Log.i(this.getClass().getName(),"lc onDestroy");
        super.onDestroy();
        pLogcat.destroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i(this.getClass().getName(),"lc onWindowFocusChanged");
        if (!hasFocus){
            mainControl.saveState();
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @SuppressLint({"InternalInsetResource", "DiscouragedApi"})
    private void adoptLayout(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            Resources myResources = this.getResources();
            int idStatusBarHeight = myResources.getIdentifier("status_bar_height", "dimen", "android");
            int statusBarHeight = (idStatusBarHeight > 0) ? this.getResources().getDimensionPixelSize(idStatusBarHeight) : dp(24);
            int idNavBarHeight = myResources.getIdentifier("navigation_bar_height", "dimen", "android");
            int navigationBarHeight = (idNavBarHeight > 0) ? this.getResources().getDimensionPixelSize(idNavBarHeight) : dp(24);

            LinearLayout ll = findViewById(R.id.main_sudoku_layout);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, statusBarHeight, 0, navigationBarHeight);
            ll.setLayoutParams(params);

            this.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
            WindowCompat.setDecorFitsSystemWindows(this.getWindow(), false);
            this.getWindow().setNavigationBarColor(0x00000000);

            int newUiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
            newUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            newUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            newUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        }
    }
    public int dp(float f){
        return (int) (f * this.getResources().getDisplayMetrics().density);
    }


}