package com.valtech.movenpick.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HotelPickupDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "hotelpickup.db";
    private static final int DATABASE_VERSION = 1;
    private final String[] DB_DDL_STATEMENTS;

    public HotelPickupDatabase(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        this.DB_DDL_STATEMENTS = new String[]{"CREATE  TABLE  IF NOT EXISTS \"usersettings\" (\"settingid\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , \"createddate\" DATETIME NOT NULL , \"key\" TEXT NOT NULL  UNIQUE , \"value\" TEXT)", "CREATE  TABLE  IF NOT EXISTS \"events\" (\"eventid\" INTEGER PRIMARY KEY  NOT NULL  UNIQUE , \"createddate\" DATETIME NOT NULL , \"eventname\" TEXT NOT NULL , \"allowable\" INTEGER NOT NULL  DEFAULT 1)"};
    }

    public void onCreate(SQLiteDatabase db) {
        String[] strArr = this.DB_DDL_STATEMENTS;
        int length = strArr.length;
        for (int i = 0; i < length; i += DATABASE_VERSION) {
            String query = strArr[i];
            db.execSQL(query);
            Log.i("HotelPickup.HotelPickupDatabase.onCreate", "Executed query: " + query);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static void init(Context ctx) {
        HotelPickupDatabase d = new HotelPickupDatabase(ctx);
        SQLiteDatabase db = d.getWritableDatabase();
        d.onCreate(db);
        db.close();
        d.close();
    }
}
