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
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.Locale;

public class StatisticDialog {

    HashMap<LocalDate, Integer> daysMap = new HashMap<>();
    Integer[] bestOfWeek = new Integer[]{0,0,0};
    Integer[] bestOfMonth = new Integer[]{0,0,0};
    Integer[] bestOfYear = new Integer[]{0,0,0};
    Integer[] bestOfAll = new Integer[]{0,0,0};

    AlertDialog alertDialog = null;

    @SuppressLint("SetTextI18n")
    public void showStatisticDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context );
        Context dialogContext = builder.getContext();

        LocalDate today = LocalDate.now();

        evaluateResults(context, today);

        LinearLayout linearLayout = new LinearLayout(dialogContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        {
            TextView headline = new TextView(dialogContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            headline.setLayoutParams(params);
            headline.setGravity(Gravity.CENTER_HORIZONTAL);
            headline.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            headline.setTypeface(null, Typeface.BOLD);
            headline.setText("MGSudoku Statistics");
            headline.setTextColor(context.getColor(R.color.black));
            headline.setPadding(5, 15, 5, 15);
            headline.setBackgroundColor(context.getColor(R.color.sd_bg_sel_value));
            linearLayout.addView(headline);
        }


        TableLayout table_dialog = new TableLayout(dialogContext);
        table_dialog.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        {
            TableRow row = new TableRow(dialogContext);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            row.setBackgroundColor(context.getColor(R.color.sd_bg));

            createTextView(row, "Todays points", 20);
            createTextView(row, ""+daysMap.getOrDefault(today, 0), 20);
            table_dialog.addView(row);
        }
        {
            TableRow row = new TableRow(dialogContext);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            row.setBackgroundColor(context.getColor(R.color.sd_bg));

            createTextView(row, "Day statistics", 20);
            table_dialog.addView(row);
        }
        {
            TableRow row = new TableRow(dialogContext);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            row.setBackgroundColor(context.getColor(R.color.sd_bg));

            createTextView(row, "Best of", 20);
            createTextView(row, "1st", 20);
            createTextView(row, "2nd", 20);
            createTextView(row, "3rd", 20);
            table_dialog.addView(row);
        }
        createRow(table_dialog, "week", bestOfWeek);
        createRow(table_dialog, "month", bestOfMonth);
        createRow(table_dialog, "year", bestOfYear);
        createRow(table_dialog, "all", bestOfAll);
        linearLayout.addView(table_dialog);

        {
            LinearLayout row = new LinearLayout(context);
            row.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setBackgroundColor(context.getColor(R.color.sd_bg));
            row.setPadding(0,50,0,0);

            createTextView(row, "", 65);
            TextView tvClose = createTextView(row, "CLOSE", 20);
            tvClose.setOnClickListener(v -> alertDialog.dismiss());
            tvClose.setTypeface(null, Typeface.BOLD);

            linearLayout.addView(row);
        }



        builder.setView(linearLayout);

        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private TextView createTextView(ViewGroup parent, String text, float weight){
        TextView tv = new TextView(parent.getContext());
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        params.weight = weight;
        params.setMargins(10,20,10,20);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(5, 15, 5, 15);
        tv.setText(text);
        tv.setTextColor(parent.getContext().getColor(R.color.black));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        parent.addView(tv);
        tv.setBackgroundColor(parent.getContext().getColor(R.color.sd_bg_sel_row_col));
        return tv;
    }

    private void createRow(TableLayout table, String text, Integer[] values){
        TableRow row = new TableRow(table.getContext());
        row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        row.setBackgroundColor(table.getContext().getColor(R.color.sd_bg));

        createTextView(row, text, 20);
        createTextView(row, ""+values[0], 20);
        createTextView(row, ""+values[1], 20);
        createTextView(row, ""+values[2], 20);
        table.addView(row);
    }

    @SuppressWarnings("ConstantConditions")
    private void evaluateResults(Context context, LocalDate today){

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (String prefName : preferences.getAll().keySet()){
            try {
                if (prefName.startsWith("result_")){
                    GameResult gameResult = JSON.parseObject(preferences.getString(prefName,""), GameResult.class);
                    if (gameResult.timestamp != 0){
                        LocalDate ld = getDay(gameResult.timestamp);
                        daysMap.put(ld, daysMap.getOrDefault(ld, 0) + gameResult.points);
                    }
                }
            } catch (Exception e) {
                Log.e(StatisticDialog.class.getName(), e.getMessage(), e);
            }
        }
        for (LocalDate ld : daysMap.keySet()){
            int value = daysMap.get(ld);
            if (getWeekStart(ld).equals(getWeekStart(today))){
                evaluateBest(bestOfWeek, value);
            }
            if (getMonthStart(ld).equals(getMonthStart(today))){
                evaluateBest(bestOfMonth, value);
            }
            if (getYearStart(ld).equals(getYearStart(today))){
                evaluateBest(bestOfYear, value);
            }
            evaluateBest(bestOfAll, value);
        }
    }

    private void evaluateBest(Integer[] bests, int value){
            try {
                if ((bests[0]==null) || (value > bests[0])){
                    bests[2] = bests[1];
                    bests[1] = bests[0];
                    bests[0] = value;
                } else if ((bests[1]==null) || (value > bests[1])){
                    bests[2] = bests[1];
                    bests[1] = value;
                } else if ((bests[2]==null) || (value > bests[2])){
                    bests[2] = value;
                }
            } catch (Exception e) {
                Log.e(StatisticDialog.class.getName(), e.getMessage(), e);
            }
    }

    public LocalDate getDay(long millis){
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDate getWeekStart(LocalDate ld){
        return ld.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
    }
    public LocalDate getMonthStart(LocalDate ld){
        return ld.withDayOfMonth( 1);
    }
    public LocalDate getYearStart(LocalDate ld){
        return ld.withDayOfMonth( 1).withMonth(1);
    }

}
