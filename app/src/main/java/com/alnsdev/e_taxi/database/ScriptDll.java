package com.alnsdev.e_taxi.database;

public class ScriptDll {

    public static String getCreateTableDespesas() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS COSTS ( ");
        sql.append("    ID INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("    NAME VARCHAR (250) NOT NULL DEFAULT (''), ");
        sql.append("    DATA VARCHAR (255) NOT NULL DEFAULT (''), ");
        sql.append("    PRICE INTEGER NOT NULL DEFAULT (0),");
        sql.append("    DAY INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    MOUNTH INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    YEAR INTEGER NOT NULL DEFAULT (0) ) ");

        return sql.toString();
    }

    public static String getCreateTableCash() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS CASH ( ");
        sql.append("    ID INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("    MONEY DECIMAL NOT NULL DEFAULT (0) )");

        return sql.toString();
    }

    public static String getCreateTableFuel() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS FUEL ( ");
        sql.append("    ID INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("    LITERS DECIMAL NOT NULL DEFAULT (0), ");
        sql.append("    DATA VARCHAR (255) NOT NULL DEFAULT (''), ");
        sql.append("    PRICE DECIMAL NOT NULL DEFAULT (0), ");
        sql.append("    DAY INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    MOUNTH INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    YEAR INTEGER NOT NULL DEFAULT (0) )");
        return sql.toString();
    }

    public static String getAlterTableClient() {
        StringBuilder sql = new StringBuilder();
//        sql.append("ALTER TABLE CLIENT ADD COLUMN DAY INTEGER DEFAULT 0;");
//        sql.append("ALTER TABLE CLIENT ADD COLUMN MOUNTH INTEGER DEFAULT 0;");
//        sql.append("ALTER TABLE CLIENT ADD COLUMN YEAR INTEGER DEFAULT 0;");
        sql.append("ALTER TABLE CLIENT ADD COLUMN PAY BOOLEAN NOT NULL DEFAULT (1);");
        return sql.toString();
    }

    public static String getCreateTableClient() {
        StringBuilder sql = new StringBuilder();
        sql.append(" CREATE TABLE IF NOT EXISTS CLIENT ( ");
        sql.append("    ID INTEGER     PRIMARY KEY AUTOINCREMENT NOT NULL, ");
        sql.append("    DESTINY VARCHAR (250) NOT NULL DEFAULT (''), ");
        sql.append("    MYLOCATION VARCHAR (250) NOT NULL DEFAULT (''), ");
        sql.append("    DATA VARCHAR (255) NOT NULL DEFAULT (''), ");
        sql.append("    PRICE INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    DAY INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    MOUNTH INTEGER NOT NULL DEFAULT (0), ");
        sql.append("    YEAR INTEGER NOT NULL DEFAULT (0) ) ");

        return sql.toString();
    }

    public static String getAlterTableCosts() {
        StringBuilder sql = new StringBuilder();
//        db.execSQL("ALTER TABLE COSTS ADD COLUMN DAY INTEGER DEFAULT 0;");
//        db.execSQL("ALTER TABLE COSTS ADD COLUMN MOUNTH INTEGER DEFAULT 0;");
//        db.execSQL("ALTER TABLE COSTS ADD COLUMN YEAR INTEGER DEFAULT 0;");

        return sql.toString();
    }


}
