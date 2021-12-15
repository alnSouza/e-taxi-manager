package com.alnsdev.e_taxi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class ShowMessages extends AppCompatActivity {

    public void alertMessage(Context context, String title, String message)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setNeutralButton("OK", null);
        alert.show();
    }

    public void snackMessage(View view, String message, int duration)
    {
        Snackbar.make(view, message, duration).show();
    }

    public void snackActionMessage(View view, String message, int duration, View.OnClickListener action)
    {
        Snackbar.make(view, message, duration).setAction("Executar", action).show();
    }

    // mensagem de alerta de salvamento
    public void alertSuccessMessage(Context context, String title, String message)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.alert_success, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textMessage = view.findViewById(R.id.messageAlert);
        textMessage.setText(message);
        AlertDialog alert = builder.create();
        alert.setView(view);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();
        alert.getWindow().setLayout(600, 450);
    }

    // mensagem de alerta de aviso
    public AlertDialog.Builder alertWarningMessage(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        return builder;
    }
}
