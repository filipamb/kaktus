package pl.atendesoftware.amimobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseStatics.DATABASE_NAME, null, DatabaseStatics.DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // creating required tables
        db.execSQL(DatabaseStatics.CREATE_TABLE_METER_POINT_LOC);
        db.execSQL(DatabaseStatics.CREATE_TABLE_STATION_LOC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStatics.TABLE_METER_POINT_LOC);
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseStatics.TABLE_STATION_LOC);
        // create new tables
        onCreate(db);
    }
}