package pl.atendesoftware.amitogo.database;

import android.database.Cursor;

import pl.atendesoftware.amitogo.model.MeterPointLocation;
import pl.atendesoftware.amitogo.model.StationLocation;

public class DatabaseStatics {

    // Logcat tag
    public static final String LOG = "DatabaseWriterAdapter";

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "amitogo";

    // Table Names
    public static final String TABLE_METER_POINT_LOC = "meter_point_loc";
    public static final String TABLE_STATION_LOC = "station_loc";

    // Common column names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    // All columns
    public static String[] allColumns = { COLUMN_ID,COLUMN_LATITUDE,COLUMN_LONGITUDE};

    // Table Create Statements
    // Meter point loc table
    public static final String CREATE_TABLE_METER_POINT_LOC ="create table " + TABLE_METER_POINT_LOC + " ( "
            + COLUMN_ID + " integer, "
            + COLUMN_LATITUDE+ " real, "
            + COLUMN_LONGITUDE + " real "
            + ");";

    // Station loc table
    public static final String CREATE_TABLE_STATION_LOC = "CREATE TABLE "
            + TABLE_STATION_LOC + "(" + COLUMN_ID + " INTEGER,"
            + COLUMN_LATITUDE + " REAL, "
            + COLUMN_LONGITUDE + " REAL );";


    public static MeterPointLocation cursorToMeterPointLocation(Cursor cursor) {
        return new MeterPointLocation(
                cursor.getLong(cursor.getColumnIndex(DatabaseStatics.COLUMN_ID)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseStatics.COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseStatics.COLUMN_LONGITUDE)));
    }

    public static StationLocation cursorToStationLocation(Cursor cursor) {
        return new StationLocation(
                cursor.getLong(cursor.getColumnIndex(DatabaseStatics.COLUMN_ID)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseStatics.COLUMN_LATITUDE)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseStatics.COLUMN_LONGITUDE)));
    }
}
