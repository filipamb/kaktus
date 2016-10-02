package pl.atendesoftware.amimobile.model;

public class MeterPointLocationToDatabase {

    private long meterId;
    private long latitude;
    private long longitude;

    public MeterPointLocationToDatabase(long meterId, long latitude, long longitude) {
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

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
