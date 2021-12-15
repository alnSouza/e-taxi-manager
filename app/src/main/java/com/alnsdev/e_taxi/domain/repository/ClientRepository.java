package com.alnsdev.e_taxi.domain.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alnsdev.e_taxi.domain.entities.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private SQLiteDatabase conn;

    public ClientRepository(SQLiteDatabase conn) {
        this.conn = conn;
    }

    public String insert(Client client) {
        String response;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("MYLOCATION", client.mylocation);
            contentValues.put("DESTINY", client.destiny);
            contentValues.put("DESCRIPTION", client.description);
            contentValues.put("DATA", client.data);
            contentValues.put("PRICE", client.price);
            contentValues.put("PAY", client.pay);
            contentValues.put("DAY", client.day);
            contentValues.put("MOUNTH", client.mounth);
            contentValues.put("YEAR", client.year);
            conn.insertOrThrow("CLIENT", null, contentValues);
            response = null;
        } catch (SQLException ex) {
            response = ex.getMessage();
        }
        return response;
    }

    public void delete(int id) {
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        conn.delete("CLIENT", "ID = ?", params);
    }

    public String update(Client client) {
        String response;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("MYLOCATION", client.mylocation);
            contentValues.put("DESTINY", client.destiny);
            contentValues.put("DESCRIPTION", client.description);
            contentValues.put("PRICE", client.price);
            contentValues.put("PAY", client.pay);
            String[] params = new String[1];
            params[0] = String.valueOf(client.id);
            conn.update("CLIENT", contentValues, "ID = ?", params);
            response = null;
        } catch (SQLException ex) {
            response = ex.getMessage();
        }
        return response;
    }

    public List<Client> searchAll() {
        List<Client> results = new ArrayList<Client>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT");
        Cursor resultsql = conn.rawQuery(sql.toString(), null);
//        Log.d("r:=====", resultsql.getColumnNames().length+"");
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Client race = new Client();
                race.id = resultsql.getInt(getValueOf(resultsql, "ID"));
                race.mylocation = resultsql.getString(getValueOf(resultsql, "MYLOCATION"));
                race.destiny = resultsql.getString(getValueOf(resultsql, "DESTINY"));
                race.description = resultsql.getString(getValueOf(resultsql, "DESCRIPTION"));
                race.data = resultsql.getString(getValueOf(resultsql,"DATA"));
                race.price = resultsql.getInt(getValueOf(resultsql,"PRICE"));
                if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                    race.pay = true;
                } else {
                    race.pay = false;
                }
                race.day = resultsql.getInt(getValueOf(resultsql,"DAY"));
                race.mounth = resultsql.getInt(getValueOf(resultsql,"MOUNTH"));
                race.year = resultsql.getInt(getValueOf(resultsql,"YEAR"));

                results.add(race);
            } while (resultsql.moveToNext());
        }

        return results;
    }

    private int getValueOf(Cursor cursor, String column)
    {
        return cursor.getColumnIndexOrThrow(column);
    }


    public List<Client> searchOfYear(int mounth, int year) {
        List<Client> results = new ArrayList<Client>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT WHERE MOUNTH <= ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(mounth);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Client race = new Client();
                race.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                race.mylocation = resultsql.getString(resultsql.getColumnIndexOrThrow("MYLOCATION"));
                race.destiny = resultsql.getString(resultsql.getColumnIndexOrThrow("DESTINY"));
                race.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                race.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                    race.pay = true;
                } else {
                    race.pay = false;
                }
                race.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                race.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                race.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(race);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public List<Client> searchByMonthYear(Integer month, Integer year) {
        List<Client> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT WHERE MOUNTH = ? AND YEAR = ?");
        String[] params = new String[2];
        params[0] = String.valueOf(month);
        params[1] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Client race = new Client();
                race.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                race.mylocation = resultsql.getString(resultsql.getColumnIndexOrThrow("MYLOCATION"));
                race.destiny = resultsql.getString(resultsql.getColumnIndexOrThrow("DESTINY"));
                race.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                race.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                    race.pay = true;
                } else {
                    race.pay = false;
                }
                race.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                race.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                race.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(race);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public List<Client> searchByDay(int day, int month, int year)
    {
        List<Client> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT WHERE DAY = ? AND MOUNTH = ? AND YEAR = ?");
        String[] params = new String[3];
        params[0] = String.valueOf(day);
        params[1] = String.valueOf(month);
        params[2] = String.valueOf(year);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            do {
                Client race = new Client();
                race.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
                race.mylocation = resultsql.getString(resultsql.getColumnIndexOrThrow("MYLOCATION"));
                race.destiny = resultsql.getString(resultsql.getColumnIndexOrThrow("DESTINY"));
                race.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
                race.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
                if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                    race.pay = true;
                } else {
                    race.pay = false;
                }
                race.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
                race.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
                race.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));
                results.add(race);
            } while (resultsql.moveToNext());
        }
        return results;
    }

    public Client searchRace(int id)
    {
        Client race = new Client();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT WHERE ID = ? ");
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);
        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            race.id = resultsql.getInt(getValueOf(resultsql, "ID"));
            race.mylocation = resultsql.getString(getValueOf(resultsql, "MYLOCATION"));
            race.destiny = resultsql.getString(getValueOf(resultsql, "DESTINY"));
            race.description = resultsql.getString(getValueOf(resultsql, "DESCRIPTION"));
            race.data = resultsql.getString(getValueOf(resultsql,"DATA"));
            race.price = resultsql.getInt(getValueOf(resultsql,"PRICE"));
            if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                race.pay = true;
            } else {
                race.pay = false;
            }
            race.day = resultsql.getInt(getValueOf(resultsql,"DAY"));
            race.mounth = resultsql.getInt(getValueOf(resultsql,"MOUNTH"));
            race.year = resultsql.getInt(getValueOf(resultsql,"YEAR"));
            return race;
        }
        return null;
    }

    public Client search(int id) {
        Client race = new Client();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM CLIENT WHERE DATA = ? ");
        String[] params = new String[1];
        params[0] = String.valueOf(id);
        Cursor resultsql = conn.rawQuery(sql.toString(), params);

        if(resultsql.getCount() > 0) {
            resultsql.moveToFirst();
            race.id = resultsql.getInt(resultsql.getColumnIndexOrThrow("ID"));
            race.mylocation = resultsql.getString(resultsql.getColumnIndexOrThrow("MYLOCATION"));
            race.destiny = resultsql.getString(resultsql.getColumnIndexOrThrow("DESTINY"));
            race.data = resultsql.getString(resultsql.getColumnIndexOrThrow("DATA"));
            race.price = resultsql.getInt(resultsql.getColumnIndexOrThrow("PRICE"));
            if (resultsql.getInt(getValueOf(resultsql, "PAY")) == 1) {
                race.pay = true;
            } else {
                race.pay = false;
            }
            race.day = resultsql.getInt(resultsql.getColumnIndexOrThrow("DAY"));
            race.mounth = resultsql.getInt(resultsql.getColumnIndexOrThrow("MOUNTH"));
            race.year = resultsql.getInt(resultsql.getColumnIndexOrThrow("YEAR"));

            return race;
        }

        return null;
    }
}
