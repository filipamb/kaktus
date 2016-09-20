package pl.atendesoftware.amitogo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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

    public long createMeterPointLocation(long id, Double latitude, Double longitude) {
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
}
