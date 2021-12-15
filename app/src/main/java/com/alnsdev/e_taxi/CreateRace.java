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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreateRace extends AppCompatActivity {

    private EditText destinyField;
    private EditText priceField;
    private EditText mylocation;
    private EditText description;
    private CheckBox payField;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ShowMessages messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_race);
        messages = new ShowMessages();
        createConn();
        destinyField = findViewById(R.id.destiny);
        description = findViewById(R.id.fieldObs);
        priceField = findViewById(R.id.valueprice);
        payField = findViewById(R.id.fieldPay);
        mylocation = findViewById(R.id.mylocation);

        Button saveBtn = findViewById(R.id.buttonSave);
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
        Boolean pay = payField.isChecked();
        String mlocation = mylocation.getText().toString();
        String obs = description.getText().toString();
        if (ress = isEmptyField(destiny)) {
            destinyField.requestFocus();
        } else if (ress = isEmptyField(price)) {
            priceField.requestFocus();
        } else if (ress = isEmptyField(obs)) {
            description.requestFocus();
        } else {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            Client client = new Client();
            client.destiny = destiny;
            client.mylocation = mlocation;
            client.description = obs;
            client.price = Integer.parseInt(price);
            client.pay = pay;
            client.data = String.valueOf(dtf.format(now));

            String[] arr = client.data.split("/");
            String[] yearextracted = arr[arr.length - 1].split("-");
            String year = yearextracted[0].replaceAll("\\s+","");
            client.day = Integer.parseInt(arr[0]);
            client.mounth = Integer.parseInt(arr[1]);
            client.year = Integer.parseInt(year);

            ClientRepository clientRepo = new ClientRepository(conn);

            String response = clientRepo.insert(client);
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