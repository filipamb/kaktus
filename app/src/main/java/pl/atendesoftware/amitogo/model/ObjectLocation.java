package pl.atendesoftware.amitogo.model;


public class ObjectLocation {

    private long objectId;
    private Double latitude = null;
    private Double longitude = null;

    public ObjectLocation() {
    }

    public ObjectLocation(long objectId, Double latitude, Double longitude) {
        this.objectId = objectId;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public long getObjectId() {
        return objectId;
    }

    public void setObjectId(long objectId) {
        this.objectId = objectId;
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

    @Override
    public String toString() {
        return "ObjectLocation{" +
                "objectId=" + objectId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
