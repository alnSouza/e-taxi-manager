package com.alnsdev.e_taxi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.sql.*;

public class BackupPgsql extends AppCompatActivity {
    private ShowMessages messages;
    Connection conn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_pgsql);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        doStep();

        messages = new ShowMessages();


        Button connectButton = findViewById(R.id.pgsqlBkp);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // connectar ao banco de dados postgres!
            }
        });
    }

    private void doStep()
    {
        Tarefa task = new Tarefa();
        task.execute();
    }

}



