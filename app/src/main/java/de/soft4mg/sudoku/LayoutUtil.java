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
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import java.util.Collection;

@SuppressWarnings("SameParameterValue")
public class LayoutUtil {

    static float widthPercentToPx(ViewGroup parent, float percent){
        return (parent.getWidth())*percent/100;
    }
    static float heightPercentToPx(ViewGroup parent, float percent){
        return parent.getHeight() *percent/100;
    }

    static Button createButton(ViewGroup parent, String text){
        Context context = parent.getContext();
        Button button = new Button(context);
        button.setText(text);
        button.setPadding(0,0,0,0);
        button.setTextColor( context.getResources().getColor(R.color.sd_tv_text, context.getTheme()) );
        parent.addView(button);
        modifyBgColor(button, false);
        return button;
    }

    static TextView createTextView(ViewGroup parent, String text){
        Context context = parent.getContext();
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setPadding(0,0,0,0);
        textView.setTextColor( context.getResources().getColor(R.color.sd_tv_text, context.getTheme()) );
        parent.addView(textView);
        return textView;
    }

    /**
     *
     * @param parent parent ViewGroup
     * @param textView textView to layout
     * @param layoutProps 0 - x, 1 - y, 2 - dx, 3 - dy, 4 - ty
     */
    public static void layout(ViewGroup parent, TextView textView, float[] layoutProps){
//        Log.i(LayoutUtil.class.getName(), "height="+parent.getHeight()+" "+textView.getText()+ " y="+layoutProps[1]+"% y="+heightPercentToPx(parent, layoutProps[1])+"px dy="+layoutProps[3]+"% dy="+heightPercentToPx(parent, layoutProps[3]));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, heightPercentToPx(parent, layoutProps[4]));
        textView.setX(widthPercentToPx(parent, layoutProps[0]));
        textView.setY(heightPercentToPx(parent, layoutProps[1]));
        int width = (int)widthPercentToPx(parent, layoutProps[2]);
        textView.setMinimumWidth(width);
        textView.setWidth(width);
        int height = (int)heightPercentToPx(parent, layoutProps[3]);
        if ("".contentEquals(textView.getText())){
            height = Math.min(width, height);
        }
        textView.setMinimumHeight(height);
        textView.setHeight(height);
        textView.setPadding(0,0,0,0);
    }
    /**
     *
     * @param parent parent ViewGroup
     * @param cellView cellView to layout
     * @param layoutProps 0 - x, 1 - y, 2 - dx, 3 - dy, 4 - ty // dx,dy in Pixel, not in percent
     */
    public static void layout(ViewGroup parent, CellView cellView, float[] layoutProps){
        cellView.setX(widthPercentToPx(parent, layoutProps[0]));
        cellView.setY(heightPercentToPx(parent, layoutProps[1]));
        cellView.setMinimumWidth((int)layoutProps[2]);
        cellView.setWidth((int)layoutProps[2]);
        cellView.setMinimumHeight((int)layoutProps[3]);
        cellView.setHeight((int)layoutProps[3]);
        cellView.setPadding(0,0,0,0);
    }

    static void refreshSelected(View view1, Collection<? extends View> views){
        for (View view : views){
            modifyBgColor(view, false);
        }
        modifyBgColor(view1, true);
    }
    static void modifyBgColor(View view, boolean selected){
        Context context = view.getContext();
        view.setBackground(ResourcesCompat.getDrawable(context.getResources(), selected?R.drawable.shape_sel:R.drawable.shape_unsel, context.getTheme()));
    }

}
