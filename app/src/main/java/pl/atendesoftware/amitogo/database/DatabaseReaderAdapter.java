package pl.atendesoftware.amitogo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

import pl.atendesoftware.amitogo.model.MeterPointLocation;

/**
 * Created by Filip on 20.09.2016.
 */
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


}
