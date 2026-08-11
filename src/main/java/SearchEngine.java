public class SearchEngine {

    private static final String[] SEARCHABLE_FIELDS = {
            "requestId", "sourceLocationId", "destinationLocationId", "category",
            "urgency", "urgencyScore", "timeSubmitted", "deadline", "status"
    };

    private static volatile ServiceRequest[] linearSink;
    private static volatile ServiceRequest binarySink;

    private static final int WARMUP_ITERATIONS = 10_000;
    private static final int TIMED_ITERATIONS = 1_000;

    private static String fieldValue(ServiceRequest request, String field) {
        return switch (field) {
            case "requestId" -> request.getRequestId();
            case "sourceLocationId" -> request.getSourceLocationId();
            case "destinationLocationId" -> request.getDestinationLocationId();
            case "category" -> request.getCategory();
            case "urgency" -> request.getUrgency();
            case "urgencyScore" -> String.valueOf(request.getUrgencyScore());
            case "timeSubmitted" -> request.getTimeSubmitted();
            case "deadline" -> request.getDeadline();
            case "status" -> request.getStatus();
            default -> throw new IllegalArgumentException("Invalid field type: " + field);
        };
    }

    private static boolean matches(ServiceRequest request, String field, String value) {
        if (request == null) return false;
        return value.equals(fieldValue(request, field));
    }

    private static void requireSearchableField(String field) {
        for (String known : SEARCHABLE_FIELDS) {
            if (known.equals(field)) return;
        }
        throw new IllegalArgumentException(
                "Invalid field type: " + field + " — expected one of " + String.join(", ", SEARCHABLE_FIELDS));
    }


    // Search through ALL requests — does NOT require sorted input
    // field: any ServiceRequest field — see SEARCHABLE_FIELDS
    // value: e.g. "CRITICAL", "Plumbing", "L001", "NEW"
    public static ServiceRequest[] linearSearch(ServiceRequest[] requests, String field, String value) {
        if (requests == null) throw new IllegalArgumentException("requests must not be null");
        if (value == null) throw new IllegalArgumentException("value must not be null");
        requireSearchableField(field);

        int count = 0;
        for (ServiceRequest request : requests) {
            if (matches(request, field, value)) {
                count++;
            }
        }

        ServiceRequest[] matchedRequests = new ServiceRequest[count];
        int index = 0;
        for (ServiceRequest request : requests) {
            if (matches(request, field, value)) {
                matchedRequests[index] = request;
                index++;
            }
        }
        return matchedRequests;
    }



    // Search by requestId only — array MUST be sorted by requestId first
    public static boolean isSortedByRequestId(ServiceRequest[] requests) {
        if (requests == null) return false;
        for (int i = 0; i < requests.length - 1; i++) {
            if (requests[i] == null || requests[i + 1] == null) return false;
            if (requests[i].getRequestId().compareTo(requests[i + 1].getRequestId()) > 0) {
                return false;
            }
        }
        return true;
    }

    public static ServiceRequest binarySearch(ServiceRequest[] sortedRequests, String requestId) {
        if(sortedRequests == null) throw new IllegalArgumentException("sortedRequests must not be null");
        if(requestId == null) throw new IllegalArgumentException("requestId must not be null");

        int low = 0;
        int high = sortedRequests.length-1;

        while (low <= high){
            int mid = low + (high-low)/2;

            ServiceRequest candidate = sortedRequests[mid];
            if (candidate == null) {
                throw new IllegalArgumentException(
                        "sortedRequests must not contain null — found one at index " + mid);
            }

            int comparison = candidate.getRequestId().compareTo(requestId);

            if(comparison== 0){
                return candidate;
            }
            else if (comparison<  0){
                low = mid+1;
            }
            else{
                high = mid-1;
            }
        }
        return null;

    }

    public static long timeLinearSearch(ServiceRequest[] requests, String field, String value) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            linearSink = linearSearch(requests, field, value);
        }

        long start = System.nanoTime();
        for (int i = 0; i < TIMED_ITERATIONS; i++) {
            linearSink = linearSearch(requests, field, value);
        }
        long elapsed = System.nanoTime() - start;

        return elapsed / TIMED_ITERATIONS;
    }

    public static long timeBinarySearch(ServiceRequest[] sortedRequests, String requestId) {
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            binarySink = binarySearch(sortedRequests, requestId);
        }

        long start = System.nanoTime();
        for (int i = 0; i < TIMED_ITERATIONS; i++) {
            binarySink = binarySearch(sortedRequests, requestId);
        }
        long elapsed = System.nanoTime() - start;

        return elapsed / TIMED_ITERATIONS;
    }
}
