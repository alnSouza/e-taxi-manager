package com.alnsdev.e_taxi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.alnsdev.e_taxi.background.Processes;

public class Races extends AppCompatActivity {
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ShowMessages messages;
    private Thread thr;
    private Processes processes;
    private List<Client> races;
    private ProgressBar progressBar;
    private TableLayout tab;
    private Handler handler;
    private boolean status = false;

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_races);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateRace(view);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        tab = findViewById(R.id.table);
        handler = new Handler();
        messages = new ShowMessages();
        processes = new Processes();

        createConn();
        checkk();
        doStep();

    }

    private void checkk()
    {
        Thread check = new Thread() {
            @Override
            public void run() {
                do {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        Log.d("status", status + "");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (status != true);

                synchronized (this) {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            try {
                                addDataTable();
                                progressBar.setVisibility(View.INVISIBLE);
                                status = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                processes.getTread(0).join();
                                processes.handleThreads().remove(0);
                                checkk();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };
        check.start();
        processes.addToRow(0, check);
    }

    private void doStep() {

        thr = new Thread(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                ClientRepository clientRepository = new ClientRepository(conn);
                if (races != null) {
                    races.clear();
                }
                races = clientRepository.searchAll();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
//                        messages.snackMessage(findViewById(R.id.racesview), "Ok", 3000);
                status = true;
                thr.interrupt();
            };

        };
        thr.start();
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
    public void addDataTable() throws JSONException {
        Log.d("here", "=-----------=");
        tab.setVerticalScrollBarEnabled(true);
        tab.removeAllViewsInLayout();

        JSONObject attributes = new JSONObject();
        attributes.put("gHeader", Gravity.CENTER);
        attributes.put("gRow", Gravity.CENTER);
        attributes.put("width", 150);
        attributes.put("height", 50);
        attributes.put("blackColor", Color.BLACK);
        attributes.put("greenColor", Color.GREEN);

        TableRow header = new TableRow(this);

        ArrayList<String> headerColumns = new ArrayList<>();
        headerColumns.add("L.SAIDA");
        headerColumns.add("DESTINO");
        headerColumns.add("DESCRICAO");
        headerColumns.add("DATA/HORA");
        headerColumns.add("PRECO");
        headerColumns.add("DIA");
        headerColumns.add("MES");
        headerColumns.add("ANO");
        headerColumns.add("PAY");
        headerColumns.add("ACAO");

        int in = 0;
        while(in < headerColumns.size())
        {
            header.addView(newHeaderColumnOf(
                    this,
                    headerColumns.get(in),
                    attributes.getInt("width"),
                    attributes.getInt("height"),
                    attributes.getInt("gHeader")));
            in++;
        }
        tab.addView(header);
        for (int i = 0; i < races.size(); i++ )
        {
            TableRow row = new TableRow(this);
            TextView txt1 = new TextView(this);
            ImageButton btnDel = new ImageButton(this);
            ImageButton btnEdit = new ImageButton(this);
            btnEdit.setImageResource(android.R.drawable.ic_menu_edit);
            btnDel.setImageResource(android.R.drawable.ic_menu_delete);

            final int finalI = i;
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onEditRace(races.get(finalI).id);
                }
            });
            btnDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                    dlg.setTitle("Aviso!");
                    dlg.setMessage("Tem certeza que deseja remover este item?");
                    dlg.setNeutralButton("Sim", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClientRepository clientRepository = new ClientRepository(conn);
                            clientRepository.delete(races.get(finalI).id);
                            doStep();
                        }
                    }));
                    dlg.setNegativeButton("Cancelar", (new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            doStep();
                        }
                    }));
                    dlg.show();
                }
            });
            row.setPadding(2,2,2,2);
            if (!races.get(i).pay) {
                row.setBackgroundColor(Color.RED);
            }
            row.addView(newTableRowOf(this, races.get(i).mylocation, attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).destiny, attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).description, attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).data, attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).price.toString(), attributes.getInt("greenColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).day.toString(), attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).mounth.toString(), attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, races.get(i).year.toString(), attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(newTableRowOf(this, String.valueOf(races.get(i).pay), attributes.getInt("blackColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(btnEdit);
            row.addView(btnDel);
            tab.addView(row);
        }
    }

    public TextView newHeaderColumnOf(Context context, String columnName, int width, int height, int gravity)
    {
        TextView view = new TextView(context);
        view.setText(columnName);
        view.setWidth(width);
        view.setGravity(gravity);
        return view;
    }

    public TextView newTableRowOf(Context context, String columnName, int color, int height, int gravity)
    {
        TextView view = new TextView(context);
        view.setText(columnName);
        view.setHeight(height);
        view.setTextColor(color);
        view.setGravity(gravity);

        return view;
    }

    public void onCreateRace(View view) {
        Intent intent = new Intent(this, CreateRace.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void onEditRace(int id) {
        Intent intent = new Intent(this, UpdateRace.class);
        intent.putExtra("ID", id);
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

        // noinspection SimplifiableIfStatement

        if (id == R.id.update_table) {
            doStep();
            return true;
        } else if ( id == R.id.relatory ) {
            Intent intent = new Intent(this, Relatory.class);
            startActivity(intent);
            return true;
        } else if ( id == R.id.newBackup ) {
            // fazer um novo backup das corridas
            exportRacesToCsv();
        } else if ( id == R.id.restoreBkp ) {
            // ir para a tela de bkps!
            Intent intent = new Intent(this, BackupActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportRacesToCsv() {
        ConstraintLayout view = findViewById(R.id.mainscreen);

        File folder = new File(Environment.getExternalStorageDirectory() + "/ETAXIBKP/races/");
        if (!folder.exists()) {
            Toast.makeText(this, "" + folder.mkdir(), Toast.LENGTH_LONG).show();
            messages.snackMessage(view, ""+folder.mkdir(), 3000);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String ddtt = String.valueOf(dtf.format(now));

        String fileName = ddtt + ".csv";
        String filePathAndName = folder.toString() + "/" + fileName;
        ArrayList<Client> races = new ArrayList<>();
        races.clear();
        races = (ArrayList<Client>) new ClientRepository(conn).searchAll();

        try {
            FileWriter fw = new FileWriter(filePathAndName);
            for (int i = 0; i < races.size(); i++) {
                fw.append("" + races.get(i).id);
                fw.append(",");
                fw.append("" + races.get(i).mylocation);
                fw.append(",");
                fw.append("" + races.get(i).destiny);
                fw.append(",");
                fw.append("" + races.get(i).description);
                fw.append(",");
                fw.append("" + races.get(i).data);
                fw.append(",");
                fw.append("" + races.get(i).price);
                fw.append(",");
                fw.append(""+races.get(i).pay);
                fw.append(",");
                fw.append("" + races.get(i).day);
                fw.append(",");
                fw.append("" + races.get(i).mounth);
                fw.append(",");
                fw.append("" + races.get(i).year);
                fw.append("\n");
            }

            fw.flush();
            fw.close();

            messages.snackMessage(view, "Novo bkp concluido de RACES.", 3000);
        } catch (IOException e) {
            e.printStackTrace();
            messages.alertMessage(this, "Erro", e.getMessage());
        }
    }
}