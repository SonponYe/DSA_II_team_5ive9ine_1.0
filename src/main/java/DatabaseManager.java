public class DatabaseManager {

    // Load all data from database into arrays/lists
    public Location[] getAllLocations() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public Road[] getAllRoads() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public ServiceRequest[] getAllRequests() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public Resource[] getAllResources() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Save a new request submitted from the console menu
    public void saveRequest(ServiceRequest request) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Update a request's status — called when dispatched or completed
    public void updateRequestStatus(String requestId, String newStatus) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Update a resource's availability — called when assigned or freed
    public void updateResourceStatus(String resourceId, String newStatus) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Save one performance experiment result
    public void saveAlgorithmRun(String algorithmName, int inputSize, long timeNs, double memoryKb) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Save one audit event to the audit_events table
    public void saveAuditEvent(String eventType, String description, String performedBy) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Export algorithm_runs table to CSV for graphing
    public void exportAlgorithmRunsToCSV(String filename) {
        throw new UnsupportedOperationException("TODO: implement");
    }
}
