public class SearchEngine {

    // Search through ALL requests — does NOT require sorted input
    // field: "urgency", "category", "sourceLocationId", "status"
    // value: e.g. "CRITICAL", "Plumbing", "L001", "NEW"
    public static ServiceRequest[] linearSearch(ServiceRequest[] requests, String field, String value) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Search by requestId only — array MUST be sorted by requestId first
    public static ServiceRequest binarySearch(ServiceRequest[] sortedRequests, String requestId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // For performance lab — returns time taken in nanoseconds
    public static long timeLinearSearch(ServiceRequest[] requests, String field, String value) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public static long timeBinarySearch(ServiceRequest[] sortedRequests, String requestId) {
        throw new UnsupportedOperationException("TODO: implement");
    }
}
