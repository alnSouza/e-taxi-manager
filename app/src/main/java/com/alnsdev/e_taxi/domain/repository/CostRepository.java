package com.alnsdev.e_taxi.domain.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alnsdev.e_taxi.domain.entities.Cost;
import com.alnsdev.e_taxi.domain.entities.Fuell;

import java.util.ArrayList;
import java.util.List;

public class CostRepository {
    private SQLiteDatabase conn;

    public CostRepository(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public String insert(Cost cost) {
        String response;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("NAME", cost.name);
            contentValues.put("DATA", cost.data);
            contentValues.put("PRICE", cost.price);
            contentValues.put("DAY", cost.day);
            contentValues.put("MOUNTH", cost.mounth);
            contentValues.put("YEAR", cost.year);
            conn.insertOrThrow("COSTS", null, contentValues);
            response = null;
        } catch (SQLException ex) {
            response = ex.getMessage();
        }

        return response;
    }

    public void delete(int id) {
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        conn.delete("COSTS", "ID = ?", params);
    }

    public void update(Cost cost) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", cost.name);
        contentValues.put("PRICE", cost.price);

        String[] params = new String[1];
        params[0] = String.valueOf(cost.id);
        conn.update("COSTS", contentValues, "ID = ?", params);
    }

    public List<Cost> searchAll() {
        List<Cost> results = new ArrayList<Cost>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM COSTS");
        Cursor resultsql = conn.rawQuery(sql.toString(), null);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Cost cost = new Cost();
                cost.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                cost.name = resultsql.getString(resultsql.getColumnIndexOrThrow("NAME"));
                cost.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                cost.price = resultsql.getDouble(resultsql.getColumnIndexOrThrow("PRICE"));
                cost.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                cost.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                cost.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(cost);
            } while (resultsql.moveToNext());
        }

        return results;
    }

    public List<Cost> searchOfYear(int mounth, int year) {
        List<Cost> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM COSTS WHERE MOUNTH <= ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(mounth);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Cost cost = new Cost();
                cost.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                cost.name = resultsql.getString(resultsql.getColumnIndexOrThrow("NAME"));
                cost.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                cost.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                cost.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                cost.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                cost.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(cost);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public List<Cost> searchByMonthYear(Integer month, Integer year) {
        List<Cost> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM COSTS WHERE MOUNTH = ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(month);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Cost cost = new Cost();
                cost.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                cost.name = resultsql.getString(resultsql.getColumnIndexOrThrow("NAME"));
                cost.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                cost.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                cost.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                cost.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                cost.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(cost);
            } while (resultsql.moveToNext());
        }
        return results;
    }
}
