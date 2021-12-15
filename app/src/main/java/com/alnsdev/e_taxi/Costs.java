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
import android.util.Log;
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
import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.alnsdev.e_taxi.domain.repository.CostRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Costs extends AppCompatActivity {
    private ConstraintLayout layoutmain;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ShowMessages messages;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costs);

        layoutmain = (ConstraintLayout)findViewById(R.id.costscreen);
        FloatingActionButton fab = findViewById(R.id.fabcosts);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                Intent intent = new Intent(view.getContext(), CreateCost.class);
                startActivity(intent);
            }
        });
        messages = new ShowMessages();
        createConn();
        addDataTable();
    }

    private void createConn() {
        try {
            dadosOpenHelper  = new DataOpenHelper(this);
            conn = dadosOpenHelper.getWritableDatabase();
//            Snackbar.make(layoutmain, "Conexão criada com sucesso!", Snackbar.LENGTH_SHORT).setAction("OK", null).show();

        } catch (SQLException ex) {
            messages.alertMessage(this, "Erro", ex.getMessage());
        }
    }

    public void addDataTable() {
        TableLayout tab = findViewById(R.id.tablecosts);
        tab.setVerticalScrollBarEnabled(true);
        tab.removeAllViewsInLayout();
        CostRepository costRepository = new CostRepository(conn);
        final List<Cost> costs = costRepository.searchAll();
        TableRow header = new TableRow(this);
        TextView h1 = new TextView(this);
        h1.setText("DESCRICAO");
        h1.setWidth(150);
        h1.setGravity(Gravity.CENTER);
        header.addView(h1);
        TextView h2 = new TextView(this);
        h2.setText("DATA/HORA");
        h2.setWidth(150);
        h2.setGravity(Gravity.CENTER);
        header.addView(h2);
        TextView h3 = new TextView(this);
        h3.setText("PRECO");
        h3.setWidth(150);
        h3.setGravity(Gravity.CENTER);
        header.addView(h3);
        TextView h4 = new TextView(this);
        h4.setText("DIA");
        h4.setWidth(150);
        h4.setGravity(Gravity.CENTER);
        header.addView(h4);
        TextView h5 = new TextView(this);
        h5.setText("MES");
        h5.setWidth(150);
        h5.setGravity(Gravity.CENTER);
        header.addView(h5);
        TextView h6 = new TextView(this);
        h6.setText("ANO");
        h6.setWidth(150);
        h6.setGravity(Gravity.CENTER);
        header.addView(h6);
        TextView h8 = new TextView(this);
        h8.setText("ACAO");
        h8.setWidth(150);
        h8.setGravity(Gravity.CENTER);
        header.addView(h8);
        tab.addView(header);
        for (int i = 0; i < costs.size(); i++ ) {
            TableRow row = new TableRow(this);
            TextView txt1 = new TextView(this);
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
                            CostRepository costRepository = new CostRepository(conn);
                            costRepository.delete(costs.get(finalI).id);
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
            row.setPadding(2,2,2,2);
            txt1.setText(costs.get(i).name);
            txt1.setHeight(50);
            txt1.setTextColor(Color.BLACK);
            txt1.setGravity(Gravity.CENTER);
            row.addView(txt1);
            TextView txt2 = new TextView(this);
            txt2.setText(costs.get(i).data);
            txt2.setHeight(50);
            txt2.setTextColor(Color.BLACK);
            txt2.setGravity(Gravity.CENTER);
            row.addView(txt2);
            TextView txt3 = new TextView(this);
            txt3.setText(String.valueOf(costs.get(i).price));
            txt3.setHeight(50);
            txt3.setTextColor(Color.RED);
            txt3.setGravity(Gravity.CENTER);
            row.addView(txt3);
            TextView txt4 = new TextView(this);
            txt4.setText(String.valueOf(costs.get(i).day));
            txt4.setHeight(50);
            txt4.setTextColor(Color.BLACK);
            txt4.setGravity(Gravity.CENTER);
            row.addView(txt4);
            TextView txt5 = new TextView(this);
            txt5.setText(String.valueOf(costs.get(i).mounth));
            txt5.setHeight(50);
            txt5.setTextColor(Color.BLACK);
            txt5.setGravity(Gravity.CENTER);
            row.addView(txt5);
            TextView txt6 = new TextView(this);
            txt6.setText(String.valueOf(costs.get(i).year));
            txt6.setHeight(50);
            txt6.setTextColor(Color.BLACK);
            txt6.setGravity(Gravity.CENTER);
            row.addView(txt6);
            row.addView(btnDel);
            tab.addView(row);
        }
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

        if ( id == R.id.relatory ) {
            Intent intent = new Intent(this, Relatory.class);
            startActivity(intent);
            return true;
        } else if ( id == R.id.newBackup) {
            exportCostsToCsv();
        } else if ( id == R.id.restoreBkp) {
            Intent intent = new Intent(this, BackupActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportCostsToCsv() {
        File folder = new File(Environment.getExternalStorageDirectory()+"/ETAXIBKP/maintences/");
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
        ArrayList<Cost> costs = new ArrayList<>();
        costs.clear();
        costs = (ArrayList<Cost>) new CostRepository(conn).searchAll();

        try {
            FileWriter fw = new FileWriter(filePathAndName);
            for ( int i = 0; i<costs.size();i++) {
                fw.append(""+costs.get(i).id);
                fw.append(",");
                fw.append(""+costs.get(i).name);
                fw.append(",");
                fw.append(""+costs.get(i).data);
                fw.append(",");
                fw.append(""+costs.get(i).price);
                fw.append(",");
                fw.append(""+costs.get(i).day);
                fw.append(",");
                fw.append(""+costs.get(i).mounth);
                fw.append(",");
                fw.append(""+costs.get(i).year);
                fw.append("\n");
            }

            fw.flush();
            fw.close();

            //Toast.makeText(this, "Nova copia feita de CUSTOS", Toast.LENGTH_LONG).show();
            View view = findViewById(R.id.costscreen);
            messages.snackMessage(view, "Bkp concluido.", 2000);
            //conn.close();
        } catch (IOException e) {
            e.printStackTrace();
            messages.alertMessage(this, "Erro ao realizar o bkp!", e.getMessage());
        }
    }

}