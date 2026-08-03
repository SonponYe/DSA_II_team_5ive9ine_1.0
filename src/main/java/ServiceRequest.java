public class ServiceRequest {
    private final String requestId;
    private final String sourceLocationId;
    private final String destinationLocationId;
    private final String category;
    private final String urgency;
    private final int urgencyScore;
    private final String timeSubmitted;
    private final String deadline;
    private String status;

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId,
                           String category, String urgency, int urgencyScore,
                           String timeSubmitted, String deadline, String status) {
        this.requestId = requestId;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.category = category;
        this.urgency = urgency;
        this.urgencyScore = urgencyScore;
        this.timeSubmitted = timeSubmitted;
        this.deadline = deadline;
        this.status = status;
    }

    public String getRequestId() { return requestId; }
    public String getSourceLocationId() { return sourceLocationId; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public String getCategory() { return category; }
    public String getUrgency() { return urgency; }
    public int getUrgencyScore() { return urgencyScore; }
    public String getTimeSubmitted() { return timeSubmitted; }
    public String getDeadline() { return deadline; }
    public String getStatus() { return status; }

    // Mutated in place after DatabaseManager.updateRequestStatus() succeeds.
    public void setStatus(String status) { this.status = status; }
}
