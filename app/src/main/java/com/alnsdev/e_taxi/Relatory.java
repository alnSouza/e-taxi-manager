package com.alnsdev.e_taxi;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.alnsdev.e_taxi.database.DataOpenHelper;
import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.entities.Fuell;
import com.alnsdev.e_taxi.domain.repository.ClientRepository;
import com.alnsdev.e_taxi.domain.repository.CostRepository;
import com.alnsdev.e_taxi.domain.repository.FuelRepository;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.BarChartRenderer;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.widget.CalendarView.*;
import static android.widget.Toast.LENGTH_SHORT;

public class Relatory extends AppCompatActivity {
    private SQLiteDatabase conn;
    private DataOpenHelper dadosOpenHelper;
    private ProgressBar progressBar;
    private Thread thr;
    private String alert;
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelsNames;
    ArrayList<MonthDalesData> monthDalesDataArrayList = new ArrayList<>();
    ArrayList<Integer> meses = new ArrayList<>();
    Integer[] racesByMonth;
    CalendarView calendarView;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatory);
        createConn();

        RadioButton radioRaces = findViewById(R.id.radioRaces);
        RadioButton radioFuel = findViewById(R.id.radioFuel);
        RadioButton radioMaintence = findViewById(R.id.radioMaintence);

        barChart = findViewById(R.id.barChart);
        barEntryArrayList = new ArrayList<>();
        labelsNames = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
         calendarView = findViewById(R.id.calendar);
        radioRaces.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                doStep("races");

            }
        });

        radioFuel.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                doStep("fuel");
            }
        });

        radioMaintence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                doStep("maintence");
            }
        });

        OnDateChangeListener onDateChangeListener = new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                // buscar todas as corridas no dia, mes e ano e todos os abastecimentos
                ClientRepository repository = new ClientRepository(conn);
                FuelRepository fuelRepository = new FuelRepository(conn);
                List<Client> races = repository.searchByDay(day, month+1, year);
                List<Fuell> fuells = fuelRepository.searchByDay(day, month+1, year);

                AlertDialog.Builder dialog = new AlertDialog.Builder(Relatory.this);
                View view = LayoutInflater.from(Relatory.this).inflate(R.layout.selected_day_relatory, null);
                dialog.setView(view);
                dialog.setNeutralButton("Ok", null);
                TextView title = view.findViewById(R.id.daymonth);
                title.setText("Dia "+day);
                TextView daytotal = view.findViewById(R.id.daytotal);
                TextView fuelC = view.findViewById(R.id.fuel);
                TextView liquidTotal = view.findViewById(R.id.liquidTotal);
                TableLayout table = view.findViewById(R.id.tableFromDay);

                int i = 0;
                int y = 0;
                int raceBruto = 0;
                int fuelCost = 0;
                while(i < races.size())
                {
                    int fuel = 0;
                    raceBruto = raceBruto + races.get(i).price;
                    while(y < fuells.size())
                    {
                        fuel = (int) fuells.get(y).price;
                        fuelCost = (int) (fuelCost + fuells.get(y).price);
                        y = y + 1;
                        break;
                    }
                    Log.d("corrida", "Valor: "+races.get(i).price+" / "+fuel);
                    TableRow row = new TableRow(Relatory.this);
                    TextView local = new TextView(Relatory.this);
                    local.setText(races.get(i).mylocation+" - "+races.get(i).destiny);
                    local.setTextColor(Color.WHITE);
                    local.setTextSize(24);
                    local.setGravity(Gravity.CENTER);
                    row.addView(local);
                    TextView dayRace = new TextView(Relatory.this);
                    dayRace.setText(""+day);
                    dayRace.setTextSize(24);
                    dayRace.setTextColor(Color.WHITE);
                    dayRace.setGravity(Gravity.CENTER);
                    row.addView(dayRace);
                    TextView gast = new TextView(Relatory.this);
                    gast.setText(races.get(i).price+"/"+fuel);
                    gast.setTextColor(Color.WHITE);
                    gast.setTextSize(24);
                    gast.setGravity(Gravity.CENTER);
                    row.addView(gast);
                    TextView liquid = new TextView(Relatory.this);
                    liquid.setText(""+(races.get(i).price - fuel));
                    liquid.setTextColor(Color.WHITE);
                    liquid.setTextSize(24);
                    liquid.setGravity(Gravity.CENTER);
                    row.addView(liquid);
                    table.addView(row);
                    i = i+1;
                }
                daytotal.setText("R$"+raceBruto);
                fuelC.setText("R$"+fuelCost);
                liquidTotal.setText("R$"+ (raceBruto - fuelCost));
                dialog.show();
            }
        };

        calendarView.setOnDateChangeListener(onDateChangeListener);
        progressBar.setVisibility(View.VISIBLE);
        doStep("races");
    }

    private long getDateBy(int year, int month, int day)
    {
        long milleseconds = 0;
        String string_date = year+"-"+month+"-"+day;
        SimpleDateFormat f = new SimpleDateFormat("yyyy-M-dd");

        try {
            Date d = f.parse(string_date);
            milleseconds = d.getTime();
            return milleseconds;

        } catch (ParseException e) {
            e.printStackTrace();
            return milleseconds;
        }

    }

    private void fillMonthSales() {

        for (int i = 0; i < monthDalesDataArrayList.size(); i++) {
            String month = monthDalesDataArrayList.get(i).getMonth();
            int sales = monthDalesDataArrayList.get(i).getSales();
            barEntryArrayList.add(new BarEntry(i, sales));
            labelsNames.add(month);
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Meses");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        BarData barData = new BarData(barDataSet);
        barChart.setFitBars(true);

        barChart.setData(barData);
        barChart.getDescription().setText("Relatorio TAXI");
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsNames));
        //set position of labels
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelsNames.size());
        xAxis.setLabelRotationAngle(270);
//        barChart.animateY(1000);
        barChart.invalidate();
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int x = barChart.getBarData().getDataSetForEntry(e).getEntryIndex((BarEntry) e);
                String month = monthDalesDataArrayList.get(x).getMonth();
//                String sales = NumberFormat.getCurrencyInstance().format(monthDalesDataArrayList.get(x).getSales());
                AlertDialog.Builder builder = new AlertDialog.Builder(Relatory.this);
                builder.setCancelable(true);
                View viewn = LayoutInflater.from(Relatory.this).inflate(R.layout.month_select_relatory, null);
                TextView month_r = viewn.findViewById(R.id.month_r);
                TextView races_r = viewn.findViewById(R.id.races_r);
                TextView receita_r = viewn.findViewById(R.id.receita_r);
                TextView fuel_r = viewn.findViewById(R.id.fuel_r);
                TextView maintence_r = viewn.findViewById(R.id.maintence_r);
                TextView liquidvalue = viewn.findViewById(R.id.liquidValue);

                ArrayList<String> list = getDataRelatoryResume(month, getYear());
                month_r.setText(month);
                races_r.setText(list.get(0));
                receita_r.setText("R$"+list.get(1));
                fuel_r.setText("R$"+list.get(2)+"/"+list.get(3)+"L");
                maintence_r.setText("R$"+list.get(4));
                liquidvalue.setText("R$"+list.get(5));
                builder.setView(viewn);
                AlertDialog alert = builder.create();
                alert.show();
                alert.getWindow().setLayout(700, 900);
                calendarView.setDate(getDateBy(2021, meses.get(x), 1), true, true);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private ArrayList<String> getDataRelatoryResume(String month, Integer year) {

        ArrayList<String> months = fillMonths();
        int i = 0;
        Integer newMonth = null;
        while ( i < months.size() ) {
            if ( months.get(i) == month ) {
                newMonth = i + 1;
                break;
            }
            i++;
        }
        Log.d("mes", newMonth.toString());
        ArrayList<String> datas = new ArrayList<>();

        // quantas corridas no mes e ano especificado?
        ClientRepository racesAndReceita = new ClientRepository(conn);
        List<Client> races = racesAndReceita.searchByMonthYear(newMonth, year);
        datas.add(String.valueOf(races.size()));
        Log.d("size", races.size()+"");
        // qual a receita no mes e ano especificado?
        int rec = 0;
        int liquidrec = 0;
        for(int p = 0; p < races.size(); p++) {
            rec = races.get(p).price + rec;
        }
        datas.add(String.valueOf(rec));

        // qual o gasto com combustivel no mes e ano especificado e os litros?
        FuelRepository fuelAndLiters = new FuelRepository(conn);
        List<Fuell> fuells = fuelAndLiters.searchByMonthYear(newMonth, year);

        double fuelOut = 0;
        double liters = 0;
        for(int p = 0; p < fuells.size(); p++) {
            fuelOut = fuells.get(p).price + fuelOut;
            liters = fuells.get(p).liters + liters;
        }

        String val = String.valueOf(fuelOut);
        datas.add(val);
        val = String.valueOf(liters);
        datas.add(val);
        // qual o gasto com a manutenção do veiculo no mes e ano especificado?
        CostRepository cost = new CostRepository(conn);
        List<Cost> costs = cost.searchByMonthYear(newMonth, year);

        double costOut = 0;
        for(int y = 0; y < costs.size(); y++){
            costOut = costs.get(y).price + costOut;
        }

        val = String.valueOf(costOut);
        datas.add(val);
        liquidrec = (int) (rec - costOut - fuelOut);
        val = String.valueOf(liquidrec);
        datas.add(val);

        return datas;
    }

    private void clearDataInChart() {
        monthDalesDataArrayList.clear();
        labelsNames.clear();
        meses.clear();
        barEntryArrayList.clear();
        barChart.setData(new BarData( new BarDataSet(barEntryArrayList, "Meses")));

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getMaintenceForMount() {
        clearDataInChart();
        CostRepository races = new CostRepository(conn);
        Integer year = getYear();

        // mes do ultimo registro do ano current
        int lastmounth;
        if ( races.searchAll().size() > 0 ) {
            lastmounth = races.searchAll().get(races.searchAll().size() - 1).mounth;

            // quais meses em registros?

            int h = 0;
            while ( h < races.searchOfYear(lastmounth, year).size() ) {
                if ( !meses.contains(races.searchOfYear(lastmounth, year).get(h).mounth) ) {
                    meses.add( races.searchOfYear(lastmounth, year).get(h).mounth );
                }
                h++;
            }

            // cada posicao do racesByMonth representa um mes especifico, o valor de cada posicao sera o total de corridas daquele mes

            int x = 0;
            while ( x < racesByMonth.length ) {
                racesByMonth[x] = 0;
                x++;
            }

            double out = 0;
            int y = 0;
            while ( y < races.searchOfYear(lastmounth, year).size() ) {

                int i = 0;
                while ( i < meses.size() ) {
                    if ( races.searchOfYear(lastmounth, year).get(y).mounth == meses.get(i) ) {
                        int v = racesByMonth[i];
                        v++;
                        racesByMonth[i] = v;
                    }

                    i++;
                }
                out = races.searchOfYear(lastmounth, year).get(y).price + out;
                y++;
            }

            setMonthDalesList(meses, racesByMonth);

        } else {
            alert = "Parece que não existem registros em MAINTENCE.";
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getFuelForMount()  {
        clearDataInChart();

        FuelRepository races = new FuelRepository(conn);

        Integer year = getYear();

        // mes do ultimo registro do ano current
        int lastmounth;
        if ( races.searchAll().size() > 0 ) {
            lastmounth = races.searchAll().get(races.searchAll().size() - 1).mounth;

            // quais meses em registros?

            int h = 0;
            while ( h < races.searchOfYear(lastmounth, year).size() ) {
                if ( !meses.contains(races.searchOfYear(lastmounth, year).get(h).mounth) ) {
                    meses.add( races.searchOfYear(lastmounth, year).get(h).mounth );
                }
                h++;
            }

            // cada posicao do racesByMonth representa um mes especifico, o valor de cada posicao sera o total de corridas daquele mes

            int x = 0;
            while ( x < racesByMonth.length ) {
                racesByMonth[x] = 0;
                x++;
            }

            double out = 0;
            int y = 0;
            while ( y < races.searchOfYear(lastmounth, year).size() ) {

                int i = 0;
                while ( i < meses.size() ) {
                    if ( races.searchOfYear(lastmounth, year).get(y).mounth == meses.get(i) ) {
                        int v = racesByMonth[i];
                        v++;
                        racesByMonth[i] = v;
                    }

                    i++;
                }
                out = races.searchOfYear(lastmounth, year).get(y).price + out;
                y++;
            }

            setMonthDalesList(meses, racesByMonth);

        } else {
            alert = "Parece que não existem registros em FUEL.";
        }

    }

    private void callAlert(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ops!");
        alert.setMessage(message);
        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int getYear() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        String[] arr = String.valueOf(dtf.format(now)).split("/");
        String[] yearextracted = arr[arr.length - 1].split("-");
        String yearS = yearextracted[0].replaceAll("\\s+","");
        int year = Integer.parseInt(yearS);

        return year;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getRacesForMount()  {
        clearDataInChart();
        ClientRepository races = new ClientRepository(conn);
        Integer year = getYear();


        // mes do ultimo registro do ano atual
        int lastmounth;
        if ( races.searchAll().size() > 0 ) {
           lastmounth = races.searchAll().get(races.searchAll().size() - 1).mounth;

            // quais meses em registros?

            int h = 0;
            while ( h < races.searchOfYear(lastmounth, year).size() ) {
                if ( !meses.contains(races.searchOfYear(lastmounth, year).get(h).mounth) ) {
                    meses.add( races.searchOfYear(lastmounth, year).get(h).mounth );
                }
                h++;
            }

            // cada posicao do racesByMonth representa um mes especifico, o valor de cada posicao sera o total de corridas daquele mes
            racesByMonth = new Integer[meses.size()];

            int x = 0;
            while ( x < racesByMonth.length ) {
                racesByMonth[x] = 0;
                x++;
            }

            int receita = 0;
            int y = 0;
            while ( y < races.searchOfYear(lastmounth, year).size() ) {

                int i = 0;
                while ( i < meses.size() ) {
                    if ( races.searchOfYear(lastmounth, year).get(y).mounth == meses.get(i) ) {
                        int v = racesByMonth[i];
                        v++;
                        racesByMonth[i] = v;
                    }

                    i++;
                }
                receita = races.searchOfYear(lastmounth, year).get(y).price + receita;
                y++;
            }
            setMonthDalesList(meses, racesByMonth);

        } else {
            alert = "Parece que não existem registros em RACES.";
        }


    }

    private void setMonthDalesList(ArrayList<Integer> months, Integer[] array){
        int xi = 0;
        while( xi < months.size()) {
            monthDalesDataArrayList.add( new MonthDalesData(fillMonths().get(months.get(xi) - 1).toString(), array[xi]));
            xi++;
        }

        fillMonthSales();
    }


    private void doStep(final String relation) {

       thr = new Thread(new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {

                switch (relation) {
                    case "fuel":
                        getFuelForMount();
                        break;
                    case "maintence":
                        getMaintenceForMount();
                        break;
                    default:
                        getRacesForMount();
                        break;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (alert != null) {
                            callAlert(alert);
                            alert = null;
                        }
                    }
                });
            }
        });
       thr.start();
    }

    private ArrayList fillMonths() {
        ArrayList<String> months = new ArrayList<>();
        months.add("Janeiro");
        months.add("Fevereiro");
        months.add("Março");
        months.add("Abril");
        months.add("Maio");
        months.add("Junho");
        months.add("Julho");
        months.add("Agosto");
        months.add("Setembro");
        months.add("Outubro");
        months.add("Novembro");
        months.add("Dezembro");

        return months;
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