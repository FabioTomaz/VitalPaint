package com.icm.projeto.vitalpaint.Data;

/**
 * Created by Bruno Silva on 11/10/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDBHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "vitalPaintUsers.db";

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //criar tabela
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + UserContract.UserEntry.TABLE_NAME + " (" +
                UserContract.UserEntry.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" +
                UserContract.UserEntry.COLUMN_USER_NAME + " VARCHAR(30) UNIQUE NOT NULL," +
                UserContract.UserEntry.COLUMN_N_DEATHS + " INTEGER DEFAULT 0, " +
                UserContract.UserEntry.COLUMN_N_WINS + " INTEGER  DEFAULT 0, " +
                UserContract.UserEntry.COLUMN_N_MATCHES + " INTEGER  DEFAULT 0, " +
                UserContract.UserEntry.COLUMN_KILL_DEATH_RATIO + " DECIMAL(10, 2)  DEFAULT 0.0 " +
                " );";

        final String SQL_CREATE_LOCATIONS_TABLE = "CREATE TABLE " + UserContract.UserStatisticsEntry.TABLE_NAME + " (" +

                UserContract.UserStatisticsEntry.COLUMN_USER + " INTEGER ," +
                UserContract.UserStatisticsEntry.COLUMN_LOCATIONS + "VARCHAR(50), " +
                "FOREIGN KEY("+"UserContract.UserStatisticsEntry.COLUMN_USER"+") REFERENCES "+
                UserContract.UserEntry.TABLE_NAME+"("+ UserContract.UserEntry.COLUMN_USER_ID+")"+ //chave estrsngeirs
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        /*sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);*/
    }
}
