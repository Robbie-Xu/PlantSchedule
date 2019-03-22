package com.example.plantschedule.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import com.example.plantschedule.R;

public class PlantDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = PlantDbHelper.class.getSimpleName();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "shelter.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PlantDbHelper}.
     *
     * @param context of the app
     */
    public PlantDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PLANTS_TABLE = "CREATE TABLE " + PlantContract.PlantEntry.TABLE_NAME + " ("
                + PlantContract.PlantEntry.COLUMN_PNAME + " TEXT PRIMARY KEY, "
                + PlantContract.PlantEntry.COLUMN_PSNAME + " TEXT, "
                + PlantContract.PlantEntry.COLUMN_PSPECIES + " TEXT, "
                + PlantContract.PlantEntry.COLUMN_PDESCRI + " TEXT, "
                + PlantContract.PlantEntry.COLUMN_PPICPATH + " TEXT, "
                + PlantContract.PlantEntry.COLUMN_PISMY + " INTEGER NOT NULL DEFAULT 0);";

        String SQL_CREATE_RECORDS_TABLE = "CREATE TABLE " + PlantContract.RecordEntry.TABLE_NAME + " ("
                + PlantContract.RecordEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PlantContract.RecordEntry.COLUMN_PNAME + " TEXT NOT NULL, "
                + PlantContract.RecordEntry.COLUMN_PDATE + " TEXT, "
                + PlantContract.RecordEntry.COLUMN_PISWATER + " INTEGER DEFAULT 0, "
                + PlantContract.RecordEntry.COLUMN_PISFERTILIZE + " INTEGER DEFAULT 0,  "
                + PlantContract.RecordEntry.COLUMN_PISUSINGDRUG + " INTEGER DEFAULT 0,  "
                + PlantContract.RecordEntry.COLUMN_PWEATHER + " TEXT, "
                + PlantContract.RecordEntry.COLUMN_PLOC_LON + " REAL, "
                + PlantContract.RecordEntry.COLUMN_PLOC_LAT + " REAL, "
                + PlantContract.RecordEntry.COLUMN_PEVENTS + " TEXT, "
                + PlantContract.RecordEntry.COLUMN_PEVENTS_TITLE + " TEXT); ";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PLANTS_TABLE);
        db.execSQL(SQL_CREATE_RECORDS_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }

}

