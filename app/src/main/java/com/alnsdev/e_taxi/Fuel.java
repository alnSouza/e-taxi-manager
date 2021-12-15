package com.alnsdev.e_taxi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.entities.Fuell;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.alnsdev.e_taxi.domain.repository.FuelRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Fuel extends AppCompatActivity {
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ConstraintLayout fuelscreen;
    private ShowMessages messages;
    TollBox tollBox = new TollBox();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuel);

        fuelscreen = findViewById(R.id.fuelscreen);

        FloatingActionButton fab = findViewById(R.id.fabFuel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateFuel(view);

            }
        });

        createConn();
        addDataTable();
    }

    private void createConn() {
        try {
            dadosOpenHelper  = new DataOpenHelper(this);
            conn = dadosOpenHelper.getWritableDatabase();
        } catch (SQLException ex) {
            messages.alertMessage(this, "Erro", ex.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDataTable() {
        TableLayout tab = findViewById(R.id.tableFuel);
        tab.setVerticalScrollBarEnabled(true);
        tab.removeAllViewsInLayout();
        FuelRepository fuelRepository = new FuelRepository(conn);
        final List<Fuell> fuel = fuelRepository.searchAll();
        TableRow header = tollBox.newTableRow(this);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("LITROS");
        labels.add("DATA/HORA");
        labels.add("PRECO");
        labels.add("DIA");
        labels.add("MES");
        labels.add("ANO");
        labels.add("ACAO");

        int i = 0;
        while(i < labels.size())
        {
            TextView column = tollBox.newTextView(this, labels.get(i), Color.BLACK, 17, Gravity.CENTER, 150);
            header.addView(column);
            i++;
        }

        tab.addView(header);
        ArrayList<String> contents = new ArrayList<>();

        for (i = 0; i < fuel.size(); i++ ) {
            TableRow row = tollBox.newTableRow(this);

            ImageButton btnDel = new ImageButton(this);
            btnDel.setImageResource(android.R.drawable.ic_menu_delete);

            final int finalI = i;
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                    dlg.setTitle("Aviso!");
                    dlg.setMessage("Tem certeza que deseja remover este item?");
                    dlg.setNeutralButton("Sim", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FuelRepository fuelRepository = new FuelRepository(conn);
                            fuelRepository.delete(fuel.get(finalI).id);
                            addDataTable();
                        }
                    }));
                    dlg.setNegativeButton("Cancelar", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addDataTable();
                        }
                    }));
                    dlg.show();
                }
            });

            contents.add(fuel.get(i).liters+"");
            contents.add(fuel.get(i).data);
            contents.add(fuel.get(i).price+"");
            contents.add(fuel.get(i).day+"");
            contents.add(fuel.get(i).mounth+"");
            contents.add(fuel.get(i).year+"");

            int in = 0;
            while(in < contents.size())
            {
                TextView content = tollBox.newTextView(this, contents.get(in), Color.BLACK, 15, Gravity.CENTER, 150);
                row.addView(content);
                in++;
            }
            contents.clear();

            row.addView(btnDel);
            tab.addView(row);
        }
    }

    public void onCreateFuel(View view) {
        Intent intent = new Intent(this, CreateFuel.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bkp, menu);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.update_table) {
            addDataTable();
            return true;
        } else if ( id == R.id.relatory ) {
            Intent intent = new Intent(this, Relatory.class);
            startActivity(intent);
            return true;
        } else if ( id == R.id.newBackup) {
            exportFuelsToCsv();
        } else if ( id == R.id.restoreBkp) {
            Intent intent = new Intent(this, BackupActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportFuelsToCsv() {
        File folder = new File(Environment.getExternalStorageDirectory()+"/ETAXIBKP/fuels/");
        boolean folderExists = false;
        if ( !folder.exists() ) {
            Toast.makeText(this, ""+folder.mkdir(), Toast.LENGTH_LONG).show();
            folderExists = folder.mkdir();
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String ddtt = String.valueOf(dtf.format(now));

        String fileName = ddtt+".csv";
        String filePathAndName = folder.toString() + "/" + fileName;
        ArrayList<Fuell> fuels = new ArrayList<>();
        fuels.clear();
        fuels = (ArrayList<Fuell>) new FuelRepository(conn).searchAll();

        try {
            FileWriter fw = new FileWriter(filePathAndName);
            for ( int i = 0; i<fuels.size();i++) {
                fw.append(""+fuels.get(i).id);
                fw.append(",");
                fw.append(""+fuels.get(i).liters);
                fw.append(",");
                fw.append(""+fuels.get(i).data);
                fw.append(",");
                fw.append(""+fuels.get(i).price);
                fw.append(",");
                fw.append(""+fuels.get(i).day);
                fw.append(",");
                fw.append(""+fuels.get(i).mounth);
                fw.append(",");
                fw.append(""+fuels.get(i).year);
                fw.append("\n");
            }

            fw.flush();
            fw.close();

            Toast.makeText(this, "Nova copia feita de FUELS", Toast.LENGTH_LONG).show();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
            messages.alertMessage(this, "Erro ao realizar o backup.", e.getMessage());
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
        }
    }

}