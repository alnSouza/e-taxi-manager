package com.alnsdev.e_taxi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MarginLayoutParamsCompat;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.entities.Fuell;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.alnsdev.e_taxi.domain.repository.CostRepository;
import com.alnsdev.e_taxi.domain.repository.FuelRepository;
import com.opencsv.CSVReader;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;
import static com.alnsdev.e_taxi.R.string.abc_shareactionprovider_share_with;
import static com.alnsdev.e_taxi.R.string.app_name;

public class BackupActivity extends AppCompatActivity {
    public String[] dirs;
    public String path_application;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private String folderSeleceted;
    private String fileSelected;

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables", "WrongConstant", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        Toolbar toolbar = findViewById(R.id.toolbarbkp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable dr = getResources().getDrawable(R.drawable.cloud_min_ic, getTheme());
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(),
                Bitmap.createScaledBitmap(bitmap, 30, 30, true));
        toolbar.setLogo(d);
        toolbar.setTitleTextColor(R.color.colorAccent);
        toolbar.setForegroundGravity(Gravity.RIGHT);
        toolbar.setTitleMargin(10, 1, 2, 5);
        // create connection with db
        createConn();

        // buscar folders no diretorio ETAXIBKP
        String[] folders = searchFolderIn();

        LinearLayout linearLayout = findViewById(R.id.foldersBkps);
        int i = 0;
        while(i < folders.length) {
            LinearLayout option = new LinearLayout(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(200, 240);
            option.setLayoutParams(lp);
            setMargins(option, 10, 0, 0, 0);
            option.setOrientation(1);

            option.setPadding(10,10,10,10);
            option.setBackgroundResource(R.drawable.radius);

            // set width and height to option
            ImageButton img = new ImageButton(this);
            switch (dirs[i]) {
                case "races":
                    img.setBackgroundResource(R.drawable.race_ic);
                    break;
                case "maintences":
                    img.setBackgroundResource(R.drawable.maintence_ic);
                    break;
                case "fuels":
                    img.setBackgroundResource(R.drawable.fuel_ic);
                    break;

            }

            img.setPadding(20,20,20,20);
            final int finalI = i;
            img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    switch(dirs[finalI]){
                        case "races":
                            Toast.makeText(getApplicationContext(), "Tocou em corridas!", LENGTH_LONG).show();
                            folderSeleceted = dirs[finalI];
                            listingFiles(dirs[finalI]);
                            break;
                        case "maintences":
                            Toast.makeText(getApplicationContext(), "Tocou em manutenção!", LENGTH_LONG).show();
                            folderSeleceted = dirs[finalI];
                            listingFiles(dirs[finalI]);
                            break;
                        case "fuels":
                            Toast.makeText(getApplicationContext(), "Tocou em combustivel!", LENGTH_LONG).show();
                            folderSeleceted = dirs[finalI];
                            listingFiles(dirs[finalI]);
                            break;
                    }
                }
            });

            TextView text = new TextView(this);
            text.setGravity(Gravity.CENTER);
            text.setText(dirs[i]);
            text.setWidth(140);
            text.setHeight(80);
            text.setTextColor(Color.BLACK);
            option.addView(img);
            option.addView(text);
            linearLayout.addView(option);
            i++;
        }
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
        }
    }


    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            // convert the DP into pixel
            int l =  (int)(left * scale + 0.5f);
            int r =  (int)(right * scale + 0.5f);
            int t =  (int)(top * scale + 0.5f);
            int b =  (int)(bottom * scale + 0.5f);

            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    private String[] searchFolderIn() {
        path_application = Environment.getExternalStorageDirectory()+"/ETAXIBKP";
        File file = new File(path_application);
        dirs = file.list();
        return dirs;
    }

    private void listingFiles(String directory) {
        TextView infoBkp = findViewById(R.id.bkpInfo);
        infoBkp.setText("Listando arquivos de "+directory);
        File files = new File(path_application+"/"+directory);
        files.list();
        Log.d("msd", ""+files.list().length);
        LinearLayout linear = findViewById(R.id.files);
        ListView listView = findViewById(R.id.listFilesBkp);
        final ArrayList<String> listFilesName = new ArrayList<String>();
        int i = 0;
        while(i < files.list().length) {
            listFilesName.add(files.list()[i]);
            i++;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listFilesName);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(), ""+folderSeleceted+" | "+listFilesName.get(i), Toast.LENGTH_SHORT).show();
                fileSelected = listFilesName.get(i);
                restoreBkp(folderSeleceted, fileSelected);
            }
        });
    }

    private void restoreBkp(String folder, String file) {
        String filePathAndName = Environment.getExternalStorageDirectory()+"/ETAXIBKP/"+folder+"/"+file;
        File bkpFile = new File(filePathAndName);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        View viewn = LayoutInflater.from(BackupActivity.this).inflate(R.layout.bkp_select_restore, null);
        TextView file_name = viewn.findViewById(R.id.bkp_file_name);
        TextView qtd_registrs = viewn.findViewById(R.id.registrs);

        if (bkpFile.exists()) {
            try {
                CSVReader csvReader = new CSVReader(new FileReader(bkpFile.getAbsolutePath()));
                List<String[]> c = (List<String[]>) csvReader.readAll();
                qtd_registrs.setText(c.size()+"");
                file_name.setText(file);
                alert.setPositiveButton("Restaurar", new AlertDialog.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            String filePathAndName = Environment.getExternalStorageDirectory()+"/ETAXIBKP/"+folderSeleceted+"/"+fileSelected;
                            File bkpFile = new File(filePathAndName);
                            CSVReader csvReader = new CSVReader(new FileReader(bkpFile.getAbsolutePath()));
                            switch (folderSeleceted) {
                                case "races":
                                    String[] nextLine;
                                    ClientRepository clientRepository = new ClientRepository(conn);
                                    while ((nextLine = csvReader.readNext()) != null) {
                                        Client cl = new Client();
                                        cl.mylocation = nextLine[1];
                                        cl.destiny = nextLine[2];
                                        cl.description = nextLine[3];
                                        cl.data = String.valueOf(nextLine[4]);
                                        cl.price = Integer.valueOf(nextLine[5]);
                                        if (nextLine[6] == "true")
                                        {
                                            cl.pay = true;
                                        } else {
                                            cl.pay = false;
                                        }
                                        cl.day = Integer.valueOf(nextLine[7]);
                                        cl.mounth = Integer.valueOf(nextLine[8]);
                                        cl.year = Integer.valueOf(nextLine[9]);
                                        clientRepository.insert(
                                                cl
                                        );
                                    }
                                    bkpSuccessRestored();
                                    break;
                                case "fuels":
                                    String[] nextLineFuel;
                                    FuelRepository fuelRepository = new FuelRepository(conn);
                                    while ((nextLineFuel = csvReader.readNext()) != null) {
                                        Fuell fuel = new Fuell();
                                        fuel.liters = Float.parseFloat(nextLineFuel[1]);
                                        fuel.data = nextLineFuel[2];
                                        fuel.price = Double.parseDouble(nextLineFuel[3]);
                                        fuel.day = Integer.valueOf(nextLineFuel[4]);
                                        fuel.mounth = Integer.valueOf(nextLineFuel[5]);
                                        fuel.year = Integer.valueOf(nextLineFuel[6]);
                                        fuelRepository.insert(
                                                fuel
                                        );
                                    }
                                    bkpSuccessRestored();
                                    break;
                                case "maintences":
                                    String[] nextLineCost;
                                    CostRepository costRepository = new CostRepository(conn);
                                    while ((nextLineCost = csvReader.readNext()) != null) {
                                        Cost cost = new Cost();
                                        cost.name = nextLineCost[1];
                                        cost.data = nextLineCost[2];
                                        cost.price = Double.valueOf(nextLineCost[3]);
                                        cost.day = Integer.valueOf(nextLineCost[4]);
                                        cost.mounth = Integer.valueOf(nextLineCost[5]);
                                        cost.year = Integer.valueOf(nextLineCost[6]);
                                        costRepository.insert(
                                                cost
                                        );
                                    }
                                    bkpSuccessRestored();
                                    break;

                            }
                        } catch (Exception e) {
                            // tratar erro aqui
                           Log.d("erro: ", e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                alert.setMessage("Erro: "+e.getMessage());
            }
        } else {

        }
        alert.setTitle(folder+" - "+file);
        alert.setCancelable(true);
        alert.setView(viewn);
        alert.show();

//        if (bkpFile.exists()) {
//            try {
//                CSVReader csvReader = new CSVReader(new FileReader(bkpFile.getAbsolutePath()));
//                String[] nextLine;
//                ClientRepository clientRepository = new ClientRepository(conn);
//                while ((nextLine = csvReader.readNext()) != null) {
//                    Client cl = new Client();
//                    cl.mylocation = nextLine[1];
//                    cl.destiny = nextLine[2];
//                    cl.data = nextLine[3];
//                    cl.price = Integer.valueOf(nextLine[4]);
//                    cl.day = Integer.valueOf(nextLine[5]);
//                    cl.mounth = Integer.valueOf(nextLine[6]);
//                    cl.year = Integer.valueOf(nextLine[7]);
//                    clientRepository.insert(
//                            cl
//                    );
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Toast.makeText(this, "Não existe backup", Toast.LENGTH_SHORT).show();
//
//        }

    }

    private void bkpSuccessRestored() {
        Toast.makeText(this, "Bkp restaurado!", Toast.LENGTH_SHORT).show();
    }

}