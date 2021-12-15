package com.alnsdev.e_taxi.domain.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.alnsdev.e_taxi.domain.entities.Client;
import com.alnsdev.e_taxi.domain.entities.Fuell;

import java.util.ArrayList;
import java.util.List;

public class FuelRepository {
    private SQLiteDatabase conn;

    public FuelRepository(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public String insert(Fuell fuel) {
        String response;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("LITERS", fuel.liters);
            contentValues.put("DATA", fuel.data);
            contentValues.put("PRICE", fuel.price);
            contentValues.put("DAY", fuel.day);
            contentValues.put("MOUNTH", fuel.mounth);
            contentValues.put("YEAR", fuel.year);
            conn.insertOrThrow("FUEL", null, contentValues);
            response = null;
        } catch (SQLException ex) {
            response = ex.getMessage();
        }
        return response;
    }

    public void delete(int id) {
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        conn.delete("FUEL", "ID = ?", params);
    }

    public void update(Fuell fuel) {

        String response;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("LITERS", fuel.liters);
            contentValues.put("PRICE", fuel.price);
            String[] params = new String[3];
            params[0] = String.valueOf(fuel.id);
            conn.update("CLIENT", contentValues, "ID = ?", params);
            response = null;
        } catch (SQLException ex) {
            response = ex.getMessage();
        }
    }

    public List<Fuell> searchAll() {
        List<Fuell> results = new ArrayList<Fuell>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FUEL");
        Cursor resultsql = conn.rawQuery(sql.toString(), null);
//        Log.d("r:=====", resultsql.getColumnNames().length+"");
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Fuell fuel = new Fuell();
                fuel.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                fuel.liters = resultsql.getInt(resultsql.getColumnIndexOrThrow("LITERS"));
                fuel.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                fuel.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                fuel.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                fuel.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                fuel.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));

                results.add(fuel);
            } while (resultsql.moveToNext());
        }

        return results;
    }

    public List<Fuell> searchOfYear(int mounth, int year) {
        List<Fuell> results = new ArrayList<Fuell>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FUEL WHERE MOUNTH <= ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(mounth);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Fuell fuell = new Fuell();
                fuell.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                fuell.liters = resultsql.getInt(resultsql.getColumnIndexOrThrow("LITERS"));
                fuell.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                fuell.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                fuell.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                fuell.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                fuell.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(fuell);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public List<Fuell> searchByMonthYear(Integer month, Integer year) {
        List<Fuell> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FUEL WHERE MOUNTH = ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(month);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Fuell fuell = new Fuell();
                fuell.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                fuell.liters = resultsql.getInt(resultsql.getColumnIndexOrThrow("LITERS"));
                fuell.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                fuell.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                fuell.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                fuell.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                fuell.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(fuell);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public List<Fuell> searchByDay(int day, int month, int year)
    {
        List<Fuell> fuells = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FUEL WHERE DAY = ? AND MOUNTH = ? AND YEAR = ?");
        String[] params = new String[3];
        params[0] = String.valueOf(day);
        params[1] = String.valueOf(month);
        params[2] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if (resultsql.getCount() > 0)
        {
            resultsql.moveToFirst();
            do {
                Fuell fuell = new Fuell();
                fuell.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                fuell.liters = resultsql.getInt(resultsql.getColumnIndexOrThrow("LITERS"));
                fuell.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                fuell.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                fuell.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                fuell.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                fuell.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                fuells.add(fuell);
            } while (resultsql.moveToNext());
        }
        return fuells;
    }

    public Fuell search(int id) {
        Fuell fuel = new Fuell();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM FUEL WHERE DATA = ? ");
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);

        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            fuel.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
            fuel.liters = resultsql.getInt(resultsql.getColumnIndexOrThrow("LITERS"));
            fuel.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
            fuel.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
            fuel.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
            fuel.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
            fuel.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));

            return fuel;
        }

        return null;
    }
}
