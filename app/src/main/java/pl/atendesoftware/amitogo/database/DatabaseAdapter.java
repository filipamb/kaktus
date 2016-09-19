package pl.atendesoftware.amitogo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pl.atendesoftware.amitogo.model.MeterPointLocation;

public class DatabaseAdapter {

    // Database variables
    private SQLiteDatabase sqlDb;
    private Context context;
    private DatabaseHelper databaseHelper;

    // Logcat tag
    private static final String LOG = "DatabaseAdapter";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "amitogo";

    // Table Names
    private static final String TABLE_METER_POINT_LOC = "meter_point_loc";
    private static final String TABLE_STATION_LOC = "station_loc";

    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    // All columns
    private String[] allColumns = { COLUMN_ID,COLUMN_LATITUDE,COLUMN_LONGITUDE};


    // Table Create Statements
    // Meter point loc table
    private static final String CREATE_TABLE_METER_POINT_LOC ="create table " + TABLE_METER_POINT_LOC + " ( "
            + COLUMN_ID + " integer, "
            + COLUMN_LATITUDE+ " real, "
            + COLUMN_LONGITUDE + " real "
            + ");";

    // Station loc table
    private static final String CREATE_TABLE_STATION_LOC = "CREATE TABLE "
            + TABLE_STATION_LOC + "(" + COLUMN_ID + " INTEGER,"
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL );";

    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    public DatabaseAdapter open() throws android.database.SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqlDb = databaseHelper.getWritableDatabase();
        return this;
    }


    public long createMeterPointLocation(long id, Double latitude, Double longitude) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID,id);
        values.put(COLUMN_LATITUDE,latitude);
        values.put(COLUMN_LONGITUDE,longitude);

        long insertId = sqlDb.insert(TABLE_METER_POINT_LOC,null,values);

        return insertId;
    }

    public long createStationLocation(long id, Double latitude, Double longitude) {

        ContentValues values = new ContentValues();

        values.put(COLUMN_ID,id);
        values.put(COLUMN_LATITUDE,latitude);
        values.put(COLUMN_LONGITUDE,longitude);

        long insertId = sqlDb.insert(TABLE_STATION_LOC,null,values);

        return insertId;
    }

    public Set<MeterPointLocation> getAllMeterPointLocations() {

        Set<MeterPointLocation> meterPointLocations = new HashSet<>();


        Cursor cursor = sqlDb.query(TABLE_METER_POINT_LOC,allColumns,null,null,null,null,null);

        for(cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()){


            MeterPointLocation meterPointLocation = new MeterPointLocation(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)));

            meterPointLocations.add(meterPointLocation);
        }

        cursor.close();

        return meterPointLocations;
    }

    public void close() {
        databaseHelper.close();
    }











    // -------------- Database Helper --------------- //
    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {

            // creating required tables
            db.execSQL(CREATE_TABLE_METER_POINT_LOC);
            db.execSQL(CREATE_TABLE_STATION_LOC);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // on upgrade drop older tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_METER_POINT_LOC);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATION_LOC);
            // create new tables
            onCreate(db);
        }
    }

}
