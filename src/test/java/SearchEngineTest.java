import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {


    private static ServiceRequest make(String id, String source, String category, String urgency,
                                       int urgencyScore, String submitted, String deadline, String status) {
        return new ServiceRequest(id, source, "L036", category, urgency, urgencyScore, submitted, deadline, status);
    }

    // Deliberately NOT in requestId order — linearSearch must not care.
    private static ServiceRequest[] unsortedFixture() {
        return new ServiceRequest[]{
                make("Q003", "L047", "Electrical", "MEDIUM",   3, "2026-07-03T08:15:00", "2026-07-04T08:15:00", "NEW"),
                make("Q001", "L016", "Structural", "LOW",      4, "2026-07-02T08:06:00", "2026-07-05T08:06:00", "NEW"),
                make("Q004", "L006", "Plumbing",   "HIGH",     2, "2026-07-02T11:29:00", "2026-07-02T15:29:00", "DISPATCHED"),
                make("Q002", "L023", "Plumbing",   "MEDIUM",   3, "2026-07-01T17:41:00", "2026-07-02T17:41:00", "NEW"),
                make("Q005", "L016", "Electrical", "CRITICAL", 1, "2026-07-04T06:00:00", "2026-07-04T09:00:00", "NEW"),
        };
    }

    // Built in sorted order by hand so these tests do not depend on SortEngine.
    private static ServiceRequest[] sortedFixture() {
        return new ServiceRequest[]{
                make("Q001", "L016", "Structural", "LOW",      4, "2026-07-02T08:06:00", "2026-07-05T08:06:00", "NEW"),
                make("Q002", "L023", "Plumbing",   "MEDIUM",   3, "2026-07-01T17:41:00", "2026-07-02T17:41:00", "NEW"),
                make("Q003", "L047", "Electrical", "MEDIUM",   3, "2026-07-03T08:15:00", "2026-07-04T08:15:00", "NEW"),
                make("Q004", "L006", "Plumbing",   "HIGH",     2, "2026-07-02T11:29:00", "2026-07-02T15:29:00", "DISPATCHED"),
                make("Q005", "L016", "Electrical", "CRITICAL", 1, "2026-07-04T06:00:00", "2026-07-04T09:00:00", "NEW"),
        };
    }

    private static ServiceRequest[] manySortedRequests() {
        ServiceRequest[] arr = new ServiceRequest[200];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = make(String.format("Q%04d", i), "L001", "Plumbing", "LOW",
                    i % 4 + 1, "2026-07-01T00:00:00", "2026-07-02T00:00:00", "NEW");
        }
        return arr;
    }

    private static String[] ids(ServiceRequest[] arr) {
        String[] out = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            out[i] = arr[i].getRequestId();
        }
        return out;
    }



    @Test
    void linearSearchUnsortedInput() {
        // Four of the five are NEW; results keep the original array order.
        ServiceRequest[] found = SearchEngine.linearSearch(unsortedFixture(), "status", "NEW");
        assertArrayEquals(new String[]{"Q003", "Q001", "Q002", "Q005"}, ids(found));
    }

    @Test
    void linearSearchMatchesEverySupportedField() {
        ServiceRequest[] requests = unsortedFixture();

        assertArrayEquals(new String[]{"Q004"}, ids(SearchEngine.linearSearch(requests, "requestId", "Q004")));
        assertArrayEquals(new String[]{"Q001", "Q005"}, ids(SearchEngine.linearSearch(requests, "sourceLocationId", "L016")));
        assertArrayEquals(new String[]{"Q004", "Q002"}, ids(SearchEngine.linearSearch(requests, "category", "Plumbing")));
        assertArrayEquals(new String[]{"Q005"}, ids(SearchEngine.linearSearch(requests, "urgency", "CRITICAL")));
        assertArrayEquals(new String[]{"Q004"}, ids(SearchEngine.linearSearch(requests, "status", "DISPATCHED")));
        assertEquals(5, SearchEngine.linearSearch(requests, "destinationLocationId", "L036").length);
    }

    @Test
    void linearSearchComparesUrgencyScoreAsAString() {
        // urgencyScore is an int on ServiceRequest, but the search API is all strings.
        ServiceRequest[] found = SearchEngine.linearSearch(unsortedFixture(), "urgencyScore", "3");
        assertArrayEquals(new String[]{"Q003", "Q002"}, ids(found));
    }

    @Test
    void linearSearchReadsLiveStatus() {
        // status is the one mutable field — updated in place after a dispatch.
        ServiceRequest[] requests = unsortedFixture();
        assertEquals(4, SearchEngine.linearSearch(requests, "status", "NEW").length);

        requests[0].setStatus("DISPATCHED");
        assertEquals(3, SearchEngine.linearSearch(requests, "status", "NEW").length);
    }



    @Test
    void linearSearchReturnsEmptyArrayWhenNothingMatches() {
        // Golden Rule 2: never return null silently. The menu sorts this result
        // in the next step, so it has to be a real (empty) array.
        ServiceRequest[] found = SearchEngine.linearSearch(unsortedFixture(), "urgency", "TRIVIAL");
        assertNotNull(found);
        assertEquals(0, found.length);
    }

    @Test
    void linearSearchHandlesEmptyInput() {
        ServiceRequest[] found = SearchEngine.linearSearch(new ServiceRequest[0], "status", "NEW");
        assertNotNull(found);
        assertEquals(0, found.length);
    }

    @Test
    void linearSearchSkipsNullElements() {
        ServiceRequest[] requests = unsortedFixture();
        requests[1] = null;
        ServiceRequest[] found = SearchEngine.linearSearch(requests, "status", "NEW");
        assertArrayEquals(new String[]{"Q003", "Q002", "Q005"}, ids(found));
    }

    @Test
    void linearSearchIgnoresRequestsWithNullFieldValues() {
        // A null field VALUE (e.g. urgency never set) is different from a null
        // element — matches() must fall through to "no match", not throw.
        ServiceRequest[] requests = unsortedFixture();
        ServiceRequest nullUrgency = make("Q006", "L010", "Electrical", null,
                2, "2026-07-05T10:00:00", "2026-07-05T12:00:00", "NEW");
        ServiceRequest[] withNullField = {requests[0], nullUrgency, requests[2]};

        ServiceRequest[] found = SearchEngine.linearSearch(withNullField, "urgency", "MEDIUM");
        assertArrayEquals(new String[]{"Q003"}, ids(found));
    }



    // The nulls below are the point of the test — the suppression stops the IDE
    // flagging them as mistakes via its inferred null-contracts.
    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    @Test
    void linearSearchRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(null, "status", "NEW"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(unsortedFixture(), "status", null));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(unsortedFixture(), "assignedWorker", "W001"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.linearSearch(unsortedFixture(), null, "NEW"));
    }


    @Test
    void binarySearchRequiresSortedInput() {
        ServiceRequest[] sorted = sortedFixture();
        for (String id : new String[]{"Q001", "Q002", "Q003", "Q004", "Q005"}) {
            ServiceRequest found = SearchEngine.binarySearch(sorted, id);
            assertNotNull(found, "did not find " + id);
            assertEquals(id, found.getRequestId());
        }
    }

    @Test
    void binarySearchFindsEveryElementOfALargeArray() {
        ServiceRequest[] sorted = manySortedRequests();
        for (int i = 0; i < sorted.length; i++) {
            String id = String.format("Q%04d", i);
            ServiceRequest found = SearchEngine.binarySearch(sorted, id);
            assertNotNull(found, "did not find " + id);
            assertEquals(id, found.getRequestId());
        }
    }



    @Test
    void binarySearchNotFound() {
        // Absent in the middle, below the first element, and above the last.
        ServiceRequest[] sorted = sortedFixture();
        assertNull(SearchEngine.binarySearch(sorted, "Q0025"));
        assertNull(SearchEngine.binarySearch(sorted, "Q000"));
        assertNull(SearchEngine.binarySearch(sorted, "Q999"));
        assertNull(SearchEngine.binarySearch(sorted, ""));
    }

    @Test
    void binarySearchFindsFirstAndLastElements() {
        ServiceRequest[] sorted = manySortedRequests();

        ServiceRequest first = SearchEngine.binarySearch(sorted, "Q0000");
        assertNotNull(first, "did not find the first element");
        assertEquals("Q0000", first.getRequestId());

        ServiceRequest last = SearchEngine.binarySearch(sorted, "Q0199");
        assertNotNull(last, "did not find the last element");
        assertEquals("Q0199", last.getRequestId());
    }

    @Test
    void binarySearchHandlesTinyArrays() {
        assertNull(SearchEngine.binarySearch(new ServiceRequest[0], "Q001"));

        ServiceRequest[] one = {sortedFixture()[0]};
        ServiceRequest found = SearchEngine.binarySearch(one, "Q001");
        assertNotNull(found, "did not find the only element");
        assertEquals("Q001", found.getRequestId());
        assertNull(SearchEngine.binarySearch(one, "Q002"));
    }


    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    @Test
    void binarySearchRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(null, "Q001"));
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(sortedFixture(), null));
    }

    @Test
    void binarySearchRejectsNullElementInsteadOfThrowingNPE() {
        // A one-element array guarantees the very first probe lands on the null.
        assertThrows(IllegalArgumentException.class,
                () -> SearchEngine.binarySearch(new ServiceRequest[]{null}, "Q001"));
    }


    @Test
    void isSortedByRequestIdDetectsOrdering() {
        assertTrue(SearchEngine.isSortedByRequestId(sortedFixture()));
        assertTrue(SearchEngine.isSortedByRequestId(manySortedRequests()));
        assertFalse(SearchEngine.isSortedByRequestId(unsortedFixture()));
    }

    @Test
    void isSortedByRequestIdHandlesEdgeCases() {
        assertTrue(SearchEngine.isSortedByRequestId(new ServiceRequest[0]));
        assertTrue(SearchEngine.isSortedByRequestId(new ServiceRequest[]{sortedFixture()[0]}));

        // Returns false rather than throwing — it is a predicate, not a validator.
        assertFalse(SearchEngine.isSortedByRequestId(null));
        assertFalse(SearchEngine.isSortedByRequestId(new ServiceRequest[]{null, null}));
    }


    @Test
    void timingMethodsReturnPositiveNanos() {
        ServiceRequest[] sorted = manySortedRequests();
        assertTrue(SearchEngine.timeLinearSearch(sorted, "status", "NEW") > 0);
        assertTrue(SearchEngine.timeBinarySearch(sorted, "Q0100") > 0);
        // A miss still has to be measurable — it is the full log n descent.
        assertTrue(SearchEngine.timeBinarySearch(sorted, "Q9999") > 0);
    }
}
