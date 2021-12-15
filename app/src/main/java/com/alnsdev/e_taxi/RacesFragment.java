package com.alnsdev.e_taxi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.background.Processes;
import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.DomainCombiner;
import java.sql.Time;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RacesFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TableLayout tab;
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ShowMessages messages;
    private TollBox tollBox;
    private Thread thr;
    private List<Client> races;
    private ProgressBar progressBar;
    private Handler handler;
    private boolean status = false;
    private Button newBtnBkp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RacesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RacesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RacesFragment newInstance(String param1, String param2) {
        RacesFragment fragment = new RacesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_races, container, false);
        tab = view.findViewById(R.id.table);
        newBtnBkp = view.findViewById(R.id.newBkpSecurityRaces);
        newBtnBkp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportRacesToCsv();
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateRace(view);
            }
        });

        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        tollBox = new TollBox();
        handler = new Handler();
        messages = new ShowMessages();

        createConn();
        checkk();
        doStep(tollBox.getDateTime().getMonthValue(), tollBox.getDateTime().getYear());

        populateSpinners(view, R.id.years, getYearsRaces());
        populateSpinners(view, R.id.months, getMonthsOfYear());

        return view;
    }

    private void populateSpinners(View view, int spinnerid, ArrayList list)
    {
        Collections.reverse(list);
        Spinner spinner = view.findViewById(spinnerid);
        ArrayAdapter adapter = new ArrayAdapter(
                getActivity().getApplicationContext(),
                R.layout.spinner_layout,
                R.id.spinnerContent,
                list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> getMonthsOfYear()
    {
        ArrayList<String> result = new ArrayList<>();
        ClientRepository clientRepository = new ClientRepository(conn);
        List<Client> races = clientRepository.searchOfYear(tollBox.getDateTime().getMonthValue(), tollBox.getDateTime().getYear());
        String m = "";
        int i = 0;
        while (i < races.size())
        {

            if (result.size() == 0)
            {
                 m = (String) tollBox.getFillMonths().get(races.get(i).mounth - 1);
                result.add(m);
            } else if (!races.get(i).mounth.equals(races.get(i-1).mounth))
            {
                m = (String) tollBox.getFillMonths().get(races.get(i).mounth - 1);
                result.add(m);
            }
            i++;
        }
        return result;
    }

    private ArrayList<String> getYearsRaces()
    {
        ClientRepository clientRepository = new ClientRepository(conn);
        List<Client> races = clientRepository.searchAll();
        ArrayList<String> result = new ArrayList<>();
        int i = 0;
        while(i < races.size())
        {
            if( result.size() == 0)
            {
                result.add(races.get(i).year.toString());
            } else if (!races.get(i).year.equals(races.get(i-1).year))
            {
                result.add(races.get(i).year.toString());
            }
            i++;
        }

        return result;
    }

    public void onCreateRace(View view) {
        Intent intent = new Intent(getActivity().getApplicationContext(), CreateRace.class);
//        EditText editText = (EditText) findViewById(R.id.editText);
//        String message = editText.getText().toString();
//        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }


    private void checkk()
    {
        Thread check = new Thread() {
            @Override
            public void run() {
                do {

                } while (status != true);

                    getActivity().runOnUiThread(new Runnable() {
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
                                ((MainActivity)getActivity()).processes.getTread(0).join();
                                ((MainActivity)getActivity()).processes.handleThreads().remove(0);
                                checkk();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
        };
        check.start();
        ((MainActivity)getActivity()).processes.addToRow(0, check);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addDataTable() throws JSONException {
        tab.setVerticalScrollBarEnabled(true);
        //tab.removeAllViewsInLayout();
        Context context = getActivity().getApplicationContext();
        JSONObject attributes = new JSONObject();
        attributes.put("gHeader", Gravity.CENTER);
        attributes.put("gRow", Gravity.CENTER);
        attributes.put("width", 150);
        attributes.put("height", 50);
        attributes.put("blackColor", Color.BLACK);
        attributes.put("greenColor", Color.GREEN);
        attributes.put("whiteColor", Color.WHITE);

        TableRow header = tollBox.newTableRow(context);

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
            header.addView(tollBox.newTextView(
                    context,
                    headerColumns.get(in),
                    Color.WHITE,
                    15,
                    attributes.getInt("gHeader"),
                    attributes.getInt("width")
            ));
            in++;
        }
        tab.addView(header);

        for (int i = 0; i < races.size(); i++ )
        {
            TableRow row = tollBox.newTableRow(context);
            ImageButton btnDel = tollBox.newImageButton(context);
            ImageButton btnEdit = tollBox.newImageButton(context);
            btnDel.setBackground(new ColorDrawable(android.R.drawable.screen_background_dark_transparent));
            btnEdit.setBackground(new ColorDrawable(android.R.drawable.screen_background_dark_transparent));
            btnDel.setMinimumWidth(100);
            btnEdit.setImageResource(R.drawable.edit);
            btnDel.setImageResource(R.drawable.trash_);

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
                    View v = LayoutInflater.from(getActivity()).inflate(R.layout.alert_warning, null);
                    AlertDialog.Builder builder = messages.alertWarningMessage(getActivity(), "", "Tem certeza que deseja remover este item?");
                    Button cancelable = v.findViewById(R.id.cancelable);
                    Button proceed = v.findViewById(R.id.proceed);
                    TextView textMessage = v.findViewById(R.id.messageAlert);
                    textMessage.setText("Tem certeza que deseja remover este item?");

                    builder.setCancelable(false);
                    final AlertDialog alert = builder.create();
                    alert.setView(v);
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();
                    alert.getWindow().setLayout(650, 550);

                    cancelable.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alert.dismiss();
                        }
                    });

                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ClientRepository clientRepository = new ClientRepository(conn);
                            clientRepository.delete(races.get(finalI).id);
                            alert.dismiss();
                            messages.alertSuccessMessage(getActivity(), "", "Item removido com sucesso!");
                            doStep(tollBox.getDateTime().getMonthValue(), tollBox.getDateTime().getYear());
                        }
                    });
                }
            });
            row.setPadding(2,2,2,2);

            if (!races.get(i).pay)
            {
                row.setBackgroundColor(Color.RED);
            }

            row.addView(tollBox.customTableRowOf(context, races.get(i).mylocation, attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).destiny, attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).description, attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).data, attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).price.toString(), attributes.getInt("greenColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).day.toString(), attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).mounth.toString(), attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, races.get(i).year.toString(), attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(tollBox.customTableRowOf(context, String.valueOf(races.get(i).pay), attributes.getInt("whiteColor"), attributes.getInt("height"), attributes.getInt("gRow")));
            row.addView(btnEdit);
            row.addView(btnDel);
            tab.addView(row);
        }
    }

    private void doStep(final int mnth, final int yer) {
        tab.removeAllViewsInLayout();
        status = false;
        thr = new Thread(){

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                ClientRepository clientRepository = new ClientRepository(conn);
                if (races != null) {
                    races.clear();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                races = clientRepository.searchByMonthYear(mnth, yer);
                status = true;
                thr.interrupt();
            }
        };
        thr.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onEditRace(int id) {
        Intent intent = new Intent(getActivity().getApplicationContext(), UpdateRace.class);
        intent.putExtra("ID", id);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = (String) adapterView.getItemAtPosition(i);
        ArrayList<String> months = tollBox.getFillMonths();

        int m = 0;
        while(m < months.size())
        {
            if (item == months.get(m))
            {
                doStep(m+1, tollBox.getDateTime().getYear());
                break;
            }
            m++;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void exportRacesToCsv() {

        File folder = new File(Environment.getExternalStorageDirectory() + "/ETAXIBKP/races/");
        if (!folder.exists()) {
            messages.snackMessage(getView(), ""+folder.mkdir(), 3000);
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

            //messages.snackMessage(getView(), "Novo bkp concluido de RACES.", 3000);
            messages.alertSuccessMessage(getActivity(),"", "Dados salvos com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            messages.alertMessage(getActivity().getApplicationContext(), "Erro", e.getMessage());
        }
    }

    private void createConn() {
        try {
            dadosOpenHelper  = new DataOpenHelper(getActivity().getApplicationContext());
            conn = dadosOpenHelper.getWritableDatabase();
        } catch (SQLException ex) {
            messages.alertMessage(getActivity().getApplicationContext(), "Erro", ex.getMessage());
        }
    }

}