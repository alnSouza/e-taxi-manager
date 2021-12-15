package com.alnsdev.e_taxi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.background.Processes;
import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.entities.Fuell;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.alnsdev.e_taxi.domain.repository.FuelRepository;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LocationManager manager;
    private static final int STORAGE_REQUEST_CODE_EXPORT = 1;
    private static final int STORAGE_REQUEST_CODE_IMPORT = 2;
    private String[] storagePermissions;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ShowMessages messages;
    public Processes processes;
    private ProgressBar loadmoney, loadfuel, loadliquid;
    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createConn();
        processes = new Processes();
        Log.d("processos", processes.thrs.size()+"");
        messages = new ShowMessages();
//        this.deleteDatabase("DADOS");
//        this.deleteDatabase("ETAXIENV");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

//        Drawable dr = getResources().getDrawable(R.drawable.ic_gol_min, getTheme());
//        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
//        Drawable d = new BitmapDrawable(getResources(),
//                Bitmap.createScaledBitmap(bitmap, 30, 30, true));
//        toolbar.setLogo(d);
//        toolbar.setTitleTextColor(R.color.colorAccent);
//        toolbar.setTitleMargin(55, 1, 2, 5);

        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ImageButton races = findViewById(R.id.races);
        ImageButton maintence = findViewById(R.id.maintence);
        ImageButton fuel = findViewById(R.id.fuel);
        ImageButton bkp = findViewById(R.id.cloudbkp);
        ImageButton homebtn = findViewById(R.id.home);

        checkTodayFature();

        races.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abilitySupportFragmentManager(R.id.frame_container, new RacesFragment());
//                Intent intent = new Intent(view.getContext(), Races.class);
//                startActivity(intent);
            }
        });

        maintence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Costs.class);
                startActivity(intent);
            }
        });

        fuel.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Fuel.class);
                startActivity(intent);
            }
        });

        bkp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BackupActivity.class);
                startActivity(intent);

            }
        });

        // add fragment in view
        if (savedInstanceState == null)
        {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frame_container, new HomeFragment())
                    .commit();

        }

        homebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadmoney.setVisibility(View.VISIBLE);
                loadfuel.setVisibility(View.VISIBLE);
                loadliquid.setVisibility(View.VISIBLE);
                if (processes.thrs.size() > 0)
                {
                    processes.thrs.get(0).interrupt();
                    processes.thrs.remove(0);

                    Log.d("thrs", processes.thrs.size()+"");
                }
                abilitySupportFragmentManager(R.id.frame_container, new HomeFragment());
                checkTodayFature();
            }
        });

    }

    private void checkTodayFature()
    {
        Thread updateCards = new Thread(new Runnable() {
            @Override
            public void run() {
                ClientRepository races = new ClientRepository(conn);
                FuelRepository fuels = new FuelRepository(conn);

                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                final int month = cal.get(Calendar.MONTH);
                final int year = cal.get(Calendar.YEAR);
                final List<Client> resultsOfDay = races.searchByDay(dayOfMonth, month+1, year);
                final List<Fuell> resultsOfFuelDay = fuels.searchByDay(dayOfMonth, month+1, year);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView day = findViewById(R.id.daytotal);
                        TextView fuelCosts = findViewById(R.id.fuelcost);
                        TextView liquidDay = findViewById(R.id.dayliquid);
                        int i = 0;
                        int moneyDay = 0;
                        double fuelDay = 0;
                        while(i < resultsOfDay.size())
                        {
                            moneyDay = moneyDay + resultsOfDay.get(i).price;
                            i++;
                        }

                        i = 0;
                        while (i < resultsOfFuelDay.size())
                        {

                            fuelDay = fuelDay + resultsOfFuelDay.get(i).price;
                            i++;
                        }

                        int liquid = (int) (moneyDay - fuelDay);
                        day.setText("R$"+moneyDay);
                        fuelCosts.setText("R$"+fuelDay);
                        liquidDay.setText("R$"+liquid);

                        loadmoney = findViewById(R.id.loadmoney);
                        loadfuel = findViewById(R.id.loadfuel);
                        loadliquid = findViewById(R.id.loadliquid);

                        loadmoney.setVisibility(View.INVISIBLE);
                        loadfuel.setVisibility(View.INVISIBLE);
                        loadliquid.setVisibility(View.INVISIBLE);
                        processes.thrs.get(0).interrupt();
                    }
                });
            }
        });
        updateCards.start();
        processes.addToRow(0, updateCards);
    }

    private void abilitySupportFragmentManager(int view, Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(view, fragment)
                .commit();
    }

    private void createConn() {
        try {
            dadosOpenHelper  = new DataOpenHelper(this);
            conn = dadosOpenHelper.getWritableDatabase();
        } catch (SQLException ex) {
            ShowMessages showMessages = new ShowMessages();
            showMessages.alertMessage(this, "Erro", ex.getMessage());
        }
    }



    ////////////////////////////////////////////////////////////////////===== NÃO NESCESSARIO =====\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermissionImport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_IMPORT);
    }

    private void requestStoragePermissionExport() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE_EXPORT);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @SuppressLint("WrongConstant")
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//
//        if (id == R.id.relatory) {
//            Intent intent = new Intent(this, Relatory.class);
//            startActivity(intent);
//            return true;
//        } else if (id == R.id.restoreBkp) {
//            if (checkStoragePermission()) {
//                importCsv();
//            } else {
//                requestStoragePermissionImport();
//            }
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // lida com o resultado da permissão solicitada
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case STORAGE_REQUEST_CODE_EXPORT:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    // permissão autorizada
                    exportCsv();
                } else {
                    Toast.makeText(this, "Acesso ao amarzenamento é requerido.", Toast.LENGTH_SHORT).show();
                }

                break;
            
            case STORAGE_REQUEST_CODE_IMPORT:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    // permissão autorizada
                    importCsv();
                } else {
                    Toast.makeText(this, "Acesso ao amarzenamento é requerido.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    private void importCsv() {
        String filePathAndName = Environment.getExternalStorageDirectory()+"/ETAXIBKP/bkp.csv";
        File file = new File(filePathAndName);
        if (file.exists()) {
            try {
                CSVReader csvReader = new CSVReader(new FileReader(file.getAbsolutePath()));
                String[] nextLine;
                ClientRepository clientRepository = new ClientRepository(conn);
                while ((nextLine = csvReader.readNext()) != null) {
                    Client cl = new Client();
                    cl.mylocation = nextLine[1];
                    cl.destiny = nextLine[2];
                    cl.data = nextLine[3];
                    cl.price = Integer.valueOf(nextLine[4]);
                    cl.day = Integer.valueOf(nextLine[5]);
                    cl.mounth = Integer.valueOf(nextLine[6]);
                    cl.year = Integer.valueOf(nextLine[7]);
                    clientRepository.insert(
                            cl
                    );

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Não existe backup", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportCsv() {
        File folder = new File(Environment.getExternalStorageDirectory()+"/ETAXIBKP");
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
        ArrayList<Client> races = new ArrayList<>();
        races.clear();
        races = (ArrayList<Client>) new ClientRepository(conn).searchAll();

        try {
            FileWriter fw = new FileWriter(filePathAndName);
            for ( int i = 0; i<races.size();i++) {
                fw.append(""+races.get(i).id);
                fw.append(",");
                fw.append(""+races.get(i).mylocation);
                fw.append(",");
                fw.append(""+races.get(i).destiny);
                fw.append(",");
                fw.append(""+races.get(i).data);
                fw.append(",");
                fw.append(""+races.get(i).price);
                fw.append(",");
                fw.append(""+races.get(i).day);
                fw.append(",");
                fw.append(""+races.get(i).mounth);
                fw.append(",");
                fw.append(""+races.get(i).year);
                fw.append("\n");
            }

            fw.flush();
            fw.close();

            Toast.makeText(this, "Nova copia feita de RACES", Toast.LENGTH_SHORT).show();
            conn.close();
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Erro no backup");
            alert.setMessage(""+e.getMessage());
            alert.setCancelable(true);
            alert.show();
        }
    }
}