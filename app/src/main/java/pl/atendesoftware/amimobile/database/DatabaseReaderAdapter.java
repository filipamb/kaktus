package pl.atendesoftware.amimobile.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.maps.model.LatLngBounds;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import pl.atendesoftware.amimobile.model.MeterPointLocation;


public class DatabaseReaderAdapter {

    // Database variables
    private SQLiteDatabase sqlDb;
    private Context context;
    private DatabaseHelper databaseHelper;

    // constructor passing context
    public DatabaseReaderAdapter(Context context) {
        this.context = context;
    }

    // open connection
    public DatabaseReaderAdapter open() throws android.database.SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqlDb = databaseHelper.getWritableDatabase();
        return this;
    }

    // close connection
    public void close() {
        databaseHelper.close();
    }

    public Set<MeterPointLocation> getAllMeterPointLocations() {
        Set<MeterPointLocation> meterPointLocations = new HashSet<>();
        Cursor cursor = sqlDb.query(DatabaseStatics.TABLE_METER_POINT_LOC, DatabaseStatics.allColumns, null, null, null, null, null);
        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            meterPointLocations.add(DatabaseStatics.cursorToMeterPointLocation(cursor));
        }
        cursor.close();
        return meterPointLocations;
    }

    public Set<MeterPointLocation> getMeterPointsByBounds(LatLngBounds bounds) {
        Set<MeterPointLocation> meterPointLocations = new HashSet<>();

        String queryColumns = "( " + DatabaseStatics.COLUMN_LATITUDE + " BETWEEN ? AND ? ) AND ("
                + DatabaseStatics.COLUMN_LONGITUDE + " BETWEEN ? AND ? )";
        String[] queryValues = new String[] {
                String.valueOf(BigDecimal.valueOf(bounds.southwest.latitude).multiply(BigDecimal.valueOf(1000000)).setScale(0,BigDecimal.ROUND_DOWN)),
                String.valueOf(BigDecimal.valueOf(bounds.northeast.latitude).multiply(BigDecimal.valueOf(1000000)).setScale(0,BigDecimal.ROUND_DOWN)),
                String.valueOf(BigDecimal.valueOf(bounds.southwest.longitude).multiply(BigDecimal.valueOf(1000000)).setScale(0,BigDecimal.ROUND_DOWN)),
                String.valueOf(BigDecimal.valueOf(bounds.northeast.longitude).multiply(BigDecimal.valueOf(1000000)).setScale(0,BigDecimal.ROUND_DOWN))
        };

        for(String s:queryValues){
            Log.i(this.getClass().getName(),s);
        }


        Cursor cursor = sqlDb.query(DatabaseStatics.TABLE_METER_POINT_LOC, DatabaseStatics.allColumns, queryColumns, queryValues, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            meterPointLocations.add(DatabaseStatics.cursorToMeterPointLocation(cursor));
        }
        cursor.close();
        return meterPointLocations;

    }

}
