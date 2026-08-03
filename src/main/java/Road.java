public class Road {
    private final String roadId;
    private final String fromLocationId;
    private final String toLocationId;
    private final double distanceKm;
    private final double travelTimeMin;
    private final double roadConditionWeight;

    public Road(String roadId, String fromLocationId, String toLocationId,
                double distanceKm, double travelTimeMin, double roadConditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.travelTimeMin = travelTimeMin;
        this.roadConditionWeight = roadConditionWeight;
    }

    public String getRoadId() { return roadId; }
    public String getFromLocationId() { return fromLocationId; }
    public String getToLocationId() { return toLocationId; }
    public double getDistanceKm() { return distanceKm; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public double getRoadConditionWeight() { return roadConditionWeight; }

    // Used by Graph.getMSTCost() — distance penalised by road condition.
    public double effectiveWeight() {
        return distanceKm * roadConditionWeight;
    }
}
