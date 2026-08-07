public class SearchEngine {

    private static String fieldValue(ServiceRequest request, String field) {
        switch (field) {
            case "urgency": return request.getUrgency();
            case "category": return request.getCategory();
            case "sourceLocationId": return request.getSourceLocationId();
            case "status": return request.getStatus();
            default: throw new IllegalArgumentException("Unknown search field: " + field);
        }
    }

    // Search through ALL requests — does NOT require sorted input
    // field: "urgency", "category", "sourceLocationId", "status"
    // value: e.g. "CRITICAL", "Plumbing", "L001", "NEW"
    public static ServiceRequest[] linearSearch(ServiceRequest[] requests, String field, String value) {
        if (requests == null || field == null || value == null) {
            throw new IllegalArgumentException("requests, field and value must not be null");
        }
        int matchCount = 0;
        for (ServiceRequest request : requests) {
            if (value.equals(fieldValue(request, field))) {
                matchCount++;
            }
        }

        ServiceRequest[] matches = new ServiceRequest[matchCount];
        int index = 0;
        for (ServiceRequest request : requests) {
            if (value.equals(fieldValue(request, field))) {
                matches[index++] = request;
            }
        }
        return matches;
    }

    // Search by requestId only — array MUST be sorted by requestId first
    public static ServiceRequest binarySearch(ServiceRequest[] sortedRequests, String requestId) {
        if (sortedRequests == null || requestId == null) {
            throw new IllegalArgumentException("sortedRequests and requestId must not be null");
        }
        int low = 0;
        int high = sortedRequests.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = sortedRequests[mid].getRequestId().compareTo(requestId);
            if (cmp == 0) {
                return sortedRequests[mid];
            } else if (cmp < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return null;
    }

    // For performance lab — returns time taken in nanoseconds
    public static long timeLinearSearch(ServiceRequest[] requests, String field, String value) {
        long start = System.nanoTime();
        linearSearch(requests, field, value);
        return System.nanoTime() - start;
    }

    public static long timeBinarySearch(ServiceRequest[] sortedRequests, String requestId) {
        long start = System.nanoTime();
        binarySearch(sortedRequests, requestId);
        return System.nanoTime() - start;
    }
}
