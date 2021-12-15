package com.alnsdev.e_taxi;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Tarefa extends AsyncTask<String, Void, Connection>
{


    @Override
    protected void onPreExecute(){
        Log.d("Iniciando", "Aguarde por favor");
    }

    @Override
    protected Connection doInBackground(String... strings) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection("jdbc:postgresql://queenie.db.elephantsql.com/gtvekkwm?user=gtvekkwm&password=GxXRXJF38J3mbZHMeicoG706EcLDm-SD");
            //Statement st = conn.createStatement();
            //st.executeQuery("SELECT * FROM COMPARATORS");
            Log.d("hehe", "hehe");

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



}
