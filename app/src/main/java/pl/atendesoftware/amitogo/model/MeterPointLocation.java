package pl.atendesoftware.amitogo.model;

import android.os.Parcel;
import android.os.Parcelable;


public class MeterPointLocation {

    private long meterId;
    private Double latitude;
    private Double longitude;

    public MeterPointLocation() {
    }

    public MeterPointLocation(long meterId, Double latitude, Double longitude) {
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
