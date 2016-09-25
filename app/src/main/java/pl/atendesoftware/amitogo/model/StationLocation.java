package pl.atendesoftware.amitogo.model;


public class StationLocation {
    private long stationId;
    private Double latitude;
    private Double longitude;

    public StationLocation() {
    }

    public StationLocation(long stationId, Double latitude, Double longitude) {
        this.stationId = stationId;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long getStationId() {
        return stationId;
    }

    public void setStationId(long stationId) {
        this.stationId = stationId;
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
