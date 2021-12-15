package com.alnsdev.e_taxi.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataOpenHelper extends SQLiteOpenHelper {
    public DataOpenHelper(@Nullable Context context) {
        super(context, "ETAXIENV", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( ScriptDll.getCreateTableClient() );
        db.execSQL( ScriptDll.getCreateTableCash() );
        db.execSQL( ScriptDll.getCreateTableDespesas() );
        db.execSQL( ScriptDll.getCreateTableFuel());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int oldversion) {
//        db.execSQL( ScriptDll.getCreateTableCash() );
//        db.execSQL( ScriptDll.getCreateTableDespesas() );
//        db.execSQL( ScriptDll.getCreateTableFuel());
        db.execSQL( ScriptDll.getAlterTableClient() );
    }


}
