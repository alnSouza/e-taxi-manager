package com.alnsdev.e_taxi;

import android.content.Context;
import android.os.Build;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class TollBox {

    public TextView newTextView(Context context, String text, int color, int textSize, int gravity, int width)
    {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(color);
        textView.setTextSize(textSize);
        textView.setGravity(gravity);
        textView.setWidth(width);
        return textView;
    }

    public TableRow newTableRow(Context context)
    {
        TableRow tableRow = new TableRow(context);
        tableRow.setPadding(2,2,2,2);
        return tableRow;
    }

    public TextView customTableRowOf(Context context, String columnName, int color, int height, int gravity)
    {
        TextView view = new TextView(context);
        view.setText(columnName);
        view.setHeight(height);
        view.setTextColor(color);
        view.setGravity(gravity);

        return view;
    }

    public ImageButton newImageButton(Context context)
    {
        ImageButton view = new ImageButton(context);
        return view;
    }

    public ArrayList getFillMonths() {
        ArrayList<String> months = new ArrayList<>();
        months.add("Janeiro");
        months.add("Fevereiro");
        months.add("Março");
        months.add("Abril");
        months.add("Maio");
        months.add("Junho");
        months.add("Julho");
        months.add("Agosto");
        months.add("Setembro");
        months.add("Outubro");
        months.add("Novembro");
        months.add("Dezembro");

        return months;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDateTime getDateTime()
    {
        return LocalDateTime.now();
    }


}
