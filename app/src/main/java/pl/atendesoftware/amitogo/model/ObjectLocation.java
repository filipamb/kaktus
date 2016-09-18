package pl.atendesoftware.amitogo.model;

import android.os.Parcel;
import android.os.Parcelable;


public class ObjectLocation {

    private long meterId;
    private double latitude;
    private double longitude;

    public ObjectLocation() {
    }

    public ObjectLocation(long meterId, double latitude, double longitude) {
        this.meterId = meterId;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long getMeterId() {
        return meterId;
    }

    public void setMeterId(long meterId) {
        this.meterId = meterId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
