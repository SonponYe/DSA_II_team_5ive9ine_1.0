public class Location {
    private final String locationId;
    private final String name;
    private final String area;
    private final String locationType;
    private final double latitude;
    private final double longitude;

    public Location(String locationId, String name, String area, String locationType,
                     double latitude, double longitude) {
        this.locationId = locationId;
        this.name = name;
        this.area = area;
        this.locationType = locationType;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocationId() { return locationId; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public String getLocationType() { return locationType; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
}
