package com.alnsdev.e_taxi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.entities.Fuell;
import com.alnsdev.e_taxi.domain.repository.CostRepository;
import com.alnsdev.e_taxi.domain.repository.FuelRepository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateFuel extends AppCompatActivity {

    private EditText litersField;
    private EditText priceField;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fuel);
        createConn();

        litersField = findViewById(R.id.quantityLiters);
        priceField = findViewById(R.id.valueFuel);
        Button saveBtn = findViewById(R.id.buttonSaveFuel);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                validField();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validField() {
        boolean ress = false;
        ShowMessages messages = new ShowMessages();

        String liters = litersField.getText().toString();
        String price = priceField.getText().toString();
        if (ress = isEmptyField(liters)) {
            litersField.setBackgroundColor(Color.RED);
            litersField.requestFocus();
        } else if (ress = isEmptyField(price)) {
            priceField.setBackgroundColor(Color.RED);
            priceField.requestFocus();
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Fuell fuel = new Fuell();
            fuel.liters = Float.parseFloat(liters);
            fuel.price = Integer.parseInt(price);
            fuel.data = String.valueOf(dtf.format(now));
            String[] arr = fuel.data.split("/");
            String[] yearextracted = arr[arr.length - 1].split("-");
            String year = yearextracted[0].replaceAll("\\s+","");
            fuel.day = Integer.parseInt(arr[0]);
            fuel.mounth = Integer.parseInt(arr[1]);
            fuel.year = Integer.parseInt(year);
            FuelRepository fuelRepo = new FuelRepository(conn);
            String result = fuelRepo.insert(fuel);
            if ( result == null ) {
                messages.alertMessage(this, "Tudo pronto!", "Dados salvos.");
                conn.close();
                finish();
            } else {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            }
        }

        if (ress) {
            messages.alertMessage(this, "Aviso!", "Prencha os campos.");
        }
    }

    private boolean isEmptyField(String value) {
        boolean res = (TextUtils.isEmpty(value) || value.trim().isEmpty());
        return res;
    }

    private void createConn() {
        try {
            dadosOpenHelper  = new DataOpenHelper(this);
            conn = dadosOpenHelper.getWritableDatabase();
        } catch (SQLException ex) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Erro");
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton("OK", null);
            dlg.show();

//            Snackbar.make(layoutmain, ex.getMessage(), Snackbar.LENGTH_LONG).setAction("OK", null).show();
        }
    }
}