package com.alnsdev.e_taxi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.repository.CostRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateCost extends AppCompatActivity {
    private EditText destinyField;
    private EditText priceField;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cost);
        createConn();
        destinyField = findViewById(R.id.descricaodespesa);
        priceField = findViewById(R.id.valuedespesa);
        Button saveBtn = findViewById(R.id.buttonSaveGasto);
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

        String destiny = destinyField.getText().toString();
        String price = priceField.getText().toString();
        if (ress = isEmptyField(destiny)) {
            destinyField.requestFocus();
        } else if (ress = isEmptyField(price)) {
            priceField.requestFocus();
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            Cost cost = new Cost();
            cost.name = destiny;
            cost.price = Integer.parseInt(price);
            cost.data = String.valueOf(dtf.format(now));
            String[] arr = cost.data.split("/");
            String[] yearextracted = arr[arr.length - 1].split("-");
            String year = yearextracted[0].replaceAll("\\s+","");
            cost.day = Integer.parseInt(arr[0]);
            cost.mounth = Integer.parseInt(arr[1]);
            cost.year = Integer.parseInt(year);
            CostRepository costRepo = new CostRepository(conn);
            String result = costRepo.insert(cost);
            if ( result == null ) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("Tudo pronto!");
                dlg.setMessage("Dados salvos.");
                dlg.setNeutralButton("OK", (new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }));
                dlg.show();
            } else {
                Toast.makeText(this, result, Toast.LENGTH_LONG).show();
            }
        }

        if (ress) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle("Aviso!");
            dlg.setMessage("Prencha os campos.");
            dlg.setNeutralButton("OK", null);
            dlg.show();
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