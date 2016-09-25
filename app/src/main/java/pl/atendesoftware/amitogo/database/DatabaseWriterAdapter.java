package pl.atendesoftware.amitogo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.List;

import pl.atendesoftware.amitogo.model.MeterPointLocation;
import pl.atendesoftware.amitogo.model.MeterPointLocationToDatabase;

public class DatabaseWriterAdapter {

    // Database variables
    private SQLiteDatabase sqlDb;
    private Context context;
    private DatabaseHelper databaseHelper;

    // constructor passing context
    public DatabaseWriterAdapter(Context context) {
        this.context = context;
    }

    // open connection
    public DatabaseWriterAdapter open() throws android.database.SQLException {
        databaseHelper = new DatabaseHelper(context);
        sqlDb = databaseHelper.getWritableDatabase();
        return this;
    }

    // close connection
    public void close() {
        databaseHelper.close();
    }

    public long createMeterPointLocation(long id, long latitude, long longitude) {
        ContentValues values = new ContentValues();
        values.put(DatabaseStatics.COLUMN_ID, id);
        values.put(DatabaseStatics.COLUMN_LATITUDE, latitude);
        values.put(DatabaseStatics.COLUMN_LONGITUDE, longitude);
        long insertId = sqlDb.insert(DatabaseStatics.TABLE_METER_POINT_LOC, null, values);
        return insertId;
    }

    public long createStationLocation(long id, Double latitude, Double longitude) {

        ContentValues values = new ContentValues();

        values.put(DatabaseStatics.COLUMN_ID, id);
        values.put(DatabaseStatics.COLUMN_LATITUDE, latitude);
        values.put(DatabaseStatics.COLUMN_LONGITUDE, longitude);

        long insertId = sqlDb.insert(DatabaseStatics.TABLE_STATION_LOC, null, values);

        return insertId;
    }

    public void bulkInsertMeterPointLocations(List<MeterPointLocationToDatabase> meterPointLocationList) {
        String sql = "INSERT INTO "+ DatabaseStatics.TABLE_METER_POINT_LOC
                + "(" + DatabaseStatics.COLUMN_ID + ","
                + DatabaseStatics.COLUMN_LATITUDE + ","
                + DatabaseStatics.COLUMN_LONGITUDE + ")"
                +" VALUES (?,?,?);";

        Log.i(getClass().getName(),sql);
        Log.i(getClass().getName(),"size of list " + meterPointLocationList.size());
        SQLiteStatement statement = sqlDb.compileStatement(sql);
        sqlDb.beginTransaction();
        for (MeterPointLocationToDatabase mpl :meterPointLocationList) {
            statement.clearBindings();
            //Log.i(getClass().getName(),"Insert info: meter id - " + mpl.getMeterId() + " latitude - " + mpl.getLatitude() + " longitude - " + mpl.getLongitude());

            statement.bindLong(1, mpl.getMeterId());
            statement.bindLong(2, mpl.getLatitude());
            statement.bindLong(3, mpl.getLongitude());
            statement.execute();
        }
        sqlDb.setTransactionSuccessful();
        sqlDb.endTransaction();
    }
}
