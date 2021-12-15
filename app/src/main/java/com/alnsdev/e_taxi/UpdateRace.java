package com.alnsdev.e_taxi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.BoringLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static android.view.View.*;

public class UpdateRace extends AppCompatActivity {
    private ShowMessages messages;
    private ClientRepository races;
    private Client race;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private EditText mlocation, destiny, price, description;
    private CheckBox payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_race);
        createConn();


        mlocation = findViewById(R.id.mylocation);
        destiny = findViewById(R.id.destiny);
        price = findViewById(R.id.valueprice);
        payment = findViewById(R.id.fieldPay);
        description = findViewById(R.id.fieldObs);

        Button saveBtn = findViewById(R.id.buttonSave);

        int id = getIntent().getIntExtra("ID", 0);
        races = new ClientRepository(conn);
        race = races.searchRace(id);
        mlocation.setText(race.mylocation);
        destiny.setText(race.destiny);
        price.setText(String.valueOf(race.price));
        if (race.pay) {
            payment.setChecked(true);
        }
        description.setText(race.description);

        saveBtn.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                validFields();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void validFields() {
        boolean ress = false;
        String destinyy = destiny.getText().toString();
        String value = price.getText().toString();
        Boolean pay = payment.isChecked();
        String mylocation = mlocation.getText().toString();
        String obs = description.getText().toString();
        if (ress = isEmptyField(destinyy)) {
            destiny.requestFocus();
        } else if (ress = isEmptyField(value)) {
            price.requestFocus();
        } else if (ress = isEmptyField(obs)) {
            description.requestFocus();
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            Client client = new Client();
            client.id = getIntent().getIntExtra("ID", 0);
            client.destiny = destinyy;
            client.mylocation = mylocation;
            client.description = obs;
            client.price = Integer.parseInt(value);
            client.pay = pay;
            client.data = String.valueOf(dtf.format(now));

            String[] arr = client.data.split("/");
            String[] yearextracted = arr[arr.length - 1].split("-");
            String year = yearextracted[0].replaceAll("\\s+","");


            String response = races.update(client);
            if ( response == null) {
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
                ConstraintLayout view = findViewById(R.id.createRace);
                messages.snackMessage(view, "erro: "+response, 5000);
            }
        }

        if (ress) {
            messages.alertMessage(this, "Aviso!", "Preencha os campos.");
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
            messages.alertMessage(this, "Erro", ex.getMessage());
        }
    }
}