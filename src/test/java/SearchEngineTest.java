import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {

    private static ServiceRequest req(String id, String sourceLocationId, String category,
                                       String urgency, int urgencyScore, String timeSubmitted,
                                       String deadline, String status) {
        return new ServiceRequest(id, sourceLocationId, "L036", category, urgency, urgencyScore,
                timeSubmitted, deadline, status);
    }

    private static ServiceRequest[] sample() {
        return new ServiceRequest[] {
                req("Q001", "L016", "Structural", "LOW", 4, "2026-07-02T08:06:00", "2026-07-05T08:06:00", "NEW"),
                req("Q002", "L023", "Plumbing", "MEDIUM", 3, "2026-07-01T17:41:00", "2026-07-02T17:41:00", "NEW"),
                req("Q003", "L047", "Electrical", "MEDIUM", 3, "2026-07-03T08:15:00", "2026-07-04T08:15:00", "IN_PROGRESS"),
                req("Q004", "L006", "Plumbing", "HIGH", 2, "2026-07-02T11:29:00", "2026-07-02T15:29:00", "NEW"),
                req("Q005", "L023", "Electrical", "CRITICAL", 1, "2026-07-04T09:00:00", "2026-07-04T11:00:00", "RESOLVED"),
        };
    }

    @Test
    void linearSearchUnsortedInput() {
        ServiceRequest[] requests = sample();

        // normal case: multiple matches, order preserved as in the source array
        ServiceRequest[] plumbing = SearchEngine.linearSearch(requests, "category", "Plumbing");
        assertEquals(2, plumbing.length);
        assertSame(requests[1], plumbing[0]);
        assertSame(requests[3], plumbing[1]);

        // boundary case: no matches -> empty array, not null
        ServiceRequest[] none = SearchEngine.linearSearch(requests, "urgency", "LOW-ISH");
        assertNotNull(none);
        assertEquals(0, none.length);

        // boundary case: single match by a different field
        ServiceRequest[] byLocation = SearchEngine.linearSearch(requests, "sourceLocationId", "L006");
        assertEquals(1, byLocation.length);
        assertSame(requests[3], byLocation[0]);

        // invalid input: unknown field, null requests/field/value
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(requests, "notAField", "x"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(null, "category", "Plumbing"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(requests, null, "Plumbing"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(requests, "category", null));
    }

    @Test
    void binarySearchRequiresSortedInput() {
        ServiceRequest[] requests = sample();
        SortEngine.mergeSort(requests, "requestId");

        // normal case: match in the middle
        assertSame(requests[2], SearchEngine.binarySearch(requests, "Q003"));

        // boundary case: first and last elements
        assertSame(requests[0], SearchEngine.binarySearch(requests, "Q001"));
        assertSame(requests[4], SearchEngine.binarySearch(requests, "Q005"));

        // boundary case: single-element array
        ServiceRequest[] single = { requests[0] };
        assertSame(single[0], SearchEngine.binarySearch(single, "Q001"));
    }

    @Test
    void binarySearchNotFound() {
        ServiceRequest[] requests = sample();
        SortEngine.mergeSort(requests, "requestId");

        // normal case: id not present
        assertNull(SearchEngine.binarySearch(requests, "Q999"));

        // boundary case: empty array
        assertNull(SearchEngine.binarySearch(new ServiceRequest[0], "Q001"));

        // invalid input: null array/id
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(null, "Q001"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(requests, null));
    }

    @Test
    void timingMethodsReturnPositiveNanos() {
        ServiceRequest[] requests = sample();
        SortEngine.mergeSort(requests, "requestId");

        long linearTime = SearchEngine.timeLinearSearch(requests, "category", "Plumbing");
        long binaryTime = SearchEngine.timeBinarySearch(requests, "Q003");

        assertTrue(linearTime >= 0);
        assertTrue(binaryTime >= 0);

        // boundary case: timing an empty array still returns a non-negative duration
        assertTrue(SearchEngine.timeLinearSearch(new ServiceRequest[0], "category", "Plumbing") >= 0);
    }

    @Test
    void searchSkipsNullRequestsAndHandlesEmptyInput() {
        ServiceRequest[] requests = sample();
        ServiceRequest[] withNulls = { requests[0], null, requests[2], null, requests[4] };

        ServiceRequest[] structural = SearchEngine.linearSearch(withNulls, "category", "Structural");
        assertEquals(1, structural.length);
        assertSame(requests[0], structural[0]);

        assertNull(SearchEngine.binarySearch(new ServiceRequest[0], "Q001"));

        ServiceRequest[] invalidSorted = { requests[0], null, requests[2] };
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(invalidSorted, "Q001"));
    }
}
