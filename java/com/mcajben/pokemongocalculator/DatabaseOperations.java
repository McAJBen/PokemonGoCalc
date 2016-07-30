package com.mcajben.pokemongocalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by billb on 7/27/2016.
 */
public class DatabaseOperations extends SQLiteOpenHelper{

    public static final int database_version = 1;

    public DatabaseOperations(Context context) {
        super(context, "user_info", null, database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sdb) {
        sdb.execSQL("CREATE TABLE USER_SAVES_NAMES(NAMES TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createTable(String Table) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.execSQL("CREATE TABLE " + Table + "(LEVEL INTEGER, STAMINA INTEGER, ATTACK INTEGER, DEFENCE INTEGER );");

        for (int i = 0; i < IVDatabase.size(); i++) {
            int[] ivs = IVDatabase.get(i);
            ContentValues cv = new ContentValues();
            cv.put("LEVEL", ivs[0]);
            cv.put("STAMINA", ivs[1]);
            cv.put("ATTACK", ivs[2]);
            cv.put("DEFENCE", ivs[3]);
            sdb.insert(Table, null, cv);
        }
    }

    public Cursor getInformation(String Table) {
        SQLiteDatabase SQ = getReadableDatabase();
        String[] columns = {"LEVEL", "STAMINA", "ATTACK", "DEFENCE"};
        Cursor CR = SQ.query(Table, columns, null, null, null, null, null);
        return CR;
    }

    public void deleteTable(String table) {
        SQLiteDatabase sdb = this.getWritableDatabase();
        sdb.execSQL("DROP TABLE " + table);
    }
}
