import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SortEngineTest {

    private static final String[] ALL_KEYS = {"urgencyScore", "deadline", "timeSubmitted", "requestId"};
    private static final String[] ALL_ALGORITHMS = {"selection", "insertion", "merge", "quick", "quickLastPivot"};


    private static ServiceRequest make(String id, String source, String category, String urgency,
                                       int urgencyScore, String submitted, String deadline, String status) {
        return new ServiceRequest(id, source, "L036", category, urgency, urgencyScore, submitted, deadline, status);
    }

    // Deliberately out of order on every sort key, with a tie on urgencyScore
    // (Q003 then Q002, both score 3) so stability can be checked.
    private static ServiceRequest[] fixture() {
        return new ServiceRequest[]{
                make("Q003", "L047", "Electrical", "MEDIUM",   3, "2026-07-03T08:15:00", "2026-07-04T08:15:00", "NEW"),
                make("Q001", "L016", "Structural", "LOW",      4, "2026-07-02T08:06:00", "2026-07-05T08:06:00", "NEW"),
                make("Q004", "L006", "Plumbing",   "HIGH",     2, "2026-07-02T11:29:00", "2026-07-02T15:29:00", "DISPATCHED"),
                make("Q002", "L023", "Plumbing",   "MEDIUM",   3, "2026-07-01T17:41:00", "2026-07-02T17:41:00", "NEW"),
                make("Q005", "L016", "Electrical", "CRITICAL", 1, "2026-07-04T06:00:00", "2026-07-04T09:00:00", "NEW"),
        };
    }

    // 37 is coprime with 200, so (i * 37) % 200 visits every index exactly once —
    // a deterministic shuffle with no duplicate request IDs.
    private static ServiceRequest[] manyRequests() {
        ServiceRequest[] arr = new ServiceRequest[200];
        for (int i = 0; i < arr.length; i++) {
            int id = (i * 37) % arr.length;
            arr[i] = make(String.format("Q%04d", id), "L001", "Plumbing", "LOW",
                    id % 4 + 1, "2026-07-01T00:00:00", "2026-07-02T00:00:00", "NEW");
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

    // urgencyScore is zero-padded so it compares numerically as a string, the way
    // the other three keys already compare correctly as ISO-8601 / fixed-width IDs.
    private static String keyOf(ServiceRequest request, String sortBy) {
        return switch (sortBy) {
            case "urgencyScore" -> String.format("%09d", request.getUrgencyScore());
            case "deadline" -> request.getDeadline();
            case "timeSubmitted" -> request.getTimeSubmitted();
            case "requestId" -> request.getRequestId();
            default -> throw new IllegalArgumentException("Unknown key: " + sortBy);
        };
    }

    private static void assertAscending(ServiceRequest[] arr, String sortBy) {
        for (int i = 0; i + 1 < arr.length; i++) {
            assertTrue(keyOf(arr[i], sortBy).compareTo(keyOf(arr[i + 1], sortBy)) <= 0,
                    "not ascending by " + sortBy + " at index " + i);
        }
    }

    // A sort must be a permutation: nothing invented, nothing dropped.
    private static void assertSameElements(ServiceRequest[] before, ServiceRequest[] after) {
        String[] expected = ids(before);
        String[] actual = ids(after);
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertArrayEquals(expected, actual, "sort lost or duplicated elements");
    }

    private static void runSort(String algorithm, ServiceRequest[] arr, String sortBy) {
        switch (algorithm) {
            case "selection" -> SortEngine.selectionSort(arr, sortBy);
            case "insertion" -> SortEngine.insertionSort(arr, sortBy);
            case "merge" -> SortEngine.mergeSort(arr, sortBy);
            case "quick" -> SortEngine.quickSort(arr, sortBy);
            case "quickLastPivot" -> SortEngine.quickSortLastPivot(arr, sortBy);
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
    }



    @Test
    void selectionSort() {
        ServiceRequest[] arr = fixture();
        SortEngine.selectionSort(arr, "requestId");
        assertArrayEquals(new String[]{"Q001", "Q002", "Q003", "Q004", "Q005"}, ids(arr));
    }

    @Test
    void insertionSort() {
        ServiceRequest[] arr = fixture();
        SortEngine.insertionSort(arr, "deadline");
        assertArrayEquals(new String[]{"Q004", "Q002", "Q003", "Q005", "Q001"}, ids(arr));
    }

    @Test
    void mergeSort() {
        ServiceRequest[] arr = fixture();
        SortEngine.mergeSort(arr, "timeSubmitted");
        assertArrayEquals(new String[]{"Q002", "Q001", "Q004", "Q003", "Q005"}, ids(arr));
    }

    @Test
    void quickSort() {
        ServiceRequest[] arr = fixture();
        SortEngine.quickSort(arr, "urgencyScore");
        assertAscending(arr, "urgencyScore");
        assertSameElements(fixture(), arr);
    }

    @Test
    void everyAlgorithmSortsEveryKey() {
        for (String algorithm : ALL_ALGORITHMS) {
            for (String key : ALL_KEYS) {
                ServiceRequest[] arr = manyRequests();
                runSort(algorithm, arr, key);
                assertAscending(arr, key);
                assertSameElements(manyRequests(), arr);
            }
        }
    }

    @Test
    void mergeSortIsStable() {
        // Q003 and Q002 both score 3 and appear in that order in the fixture.
        ServiceRequest[] arr = fixture();
        SortEngine.mergeSort(arr, "urgencyScore");
        assertArrayEquals(new String[]{"Q005", "Q004", "Q003", "Q002", "Q001"}, ids(arr));
    }

    @Test
    void insertionSortIsStable() {
        ServiceRequest[] arr = fixture();
        SortEngine.insertionSort(arr, "urgencyScore");
        assertArrayEquals(new String[]{"Q005", "Q004", "Q003", "Q002", "Q001"}, ids(arr));
    }

    // ── boundary cases ─────────────────────────────────────────────────────────

    @Test
    void quickSortWorstCaseSortedInput() {
        // Already-sorted input is quickSortLastPivot's O(n^2) worst case, because every
        // partition splits 1-vs-(n-1). It must still sort correctly, and must not blow
        // the stack — quickSortRecursive recurses into the smaller side to guarantee that.
        // Deliberately no timing assertion here: wall-clock is too flaky for CI.
        ServiceRequest[] arr = manyRequests();
        SortEngine.mergeSort(arr, "requestId");
        String[] expected = ids(arr);

        ServiceRequest[] pinnedPivot = arr.clone();
        SortEngine.quickSortLastPivot(pinnedPivot, "requestId");
        assertArrayEquals(expected, ids(pinnedPivot));

        ServiceRequest[] randomPivot = arr.clone();
        SortEngine.quickSort(randomPivot, "requestId");
        assertArrayEquals(expected, ids(randomPivot));
    }

    @Test
    void emptyArrayIsAlreadySorted() {
        // Must not throw: SearchEngine.linearSearch returns an empty array when nothing
        // matches, and filter-then-sort is the normal menu flow.
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] empty = new ServiceRequest[0];
            assertDoesNotThrow(() -> runSort(algorithm, empty, "requestId"));
            assertEquals(0, empty.length);
        }
    }

    @Test
    void singleElementArrayIsUnchanged() {
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] one = {fixture()[0]};
            runSort(algorithm, one, "requestId");
            assertArrayEquals(new String[]{"Q003"}, ids(one));
        }
    }

    @Test
    void alreadySortedInputStaysSorted() {
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] arr = fixture();
            SortEngine.mergeSort(arr, "requestId");
            runSort(algorithm, arr, "requestId");
            assertArrayEquals(new String[]{"Q001", "Q002", "Q003", "Q004", "Q005"}, ids(arr));
        }
    }

    @Test
    void reverseSortedInputIsHandled() {
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] arr = fixture();
            SortEngine.mergeSort(arr, "requestId");
            for (int i = 0; i < arr.length / 2; i++) {
                ServiceRequest temp = arr[i];
                arr[i] = arr[arr.length - 1 - i];

                arr[arr.length - 1 - i] = temp;
            }
            runSort(algorithm, arr, "requestId");
            assertArrayEquals(new String[]{"Q001", "Q002", "Q003", "Q004", "Q005"}, ids(arr));
        }
    }

    @Test
    void allEqualKeysAreHandled() {
        // Every comparison returns 0 — the degenerate case for Lomuto partitioning.
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] arr = new ServiceRequest[50];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = make("Q" + i, "L001", "Plumbing", "MEDIUM", 3,
                        "2026-07-01T00:00:00", "2026-07-02T00:00:00", "NEW");
            }
            ServiceRequest[] copy = arr.clone();
            runSort(algorithm, arr, "urgencyScore");
            assertSameElements(copy, arr);
        }
    }



    // The nulls below are the point of the test — the suppression stops the IDE
    // flagging them as mistakes via its inferred null-contracts.
    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    @Test
    void rejectsNullArray() {
        for (String algorithm : ALL_ALGORITHMS) {
            assertThrows(IllegalArgumentException.class, () -> runSort(algorithm, null, "requestId"));
        }
    }

    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    @Test
    void rejectsNullSortBy() {
        for (String algorithm : ALL_ALGORITHMS) {
            assertThrows(IllegalArgumentException.class, () -> runSort(algorithm, fixture(), null));
        }
    }

    @Test
    void rejectsUnknownSortBy() {
        for (String algorithm : ALL_ALGORITHMS) {
            // "urgency" is a real ServiceRequest field but is NOT a valid sort key.
            assertThrows(IllegalArgumentException.class, () -> runSort(algorithm, fixture(), "urgency"));
        }
    }

    @Test
    void rejectsNullElementBeforeTouchingTheArray() {
        // Caught up front rather than mid-sort: selection and quick swap in place, so an
        // NPE escaping halfway through would leave the caller's array partly permuted.
        for (String algorithm : ALL_ALGORITHMS) {
            ServiceRequest[] arr = fixture();
            String[] original = ids(arr);
            arr[2] = null;

            assertThrows(IllegalArgumentException.class, () -> runSort(algorithm, arr, "requestId"));

            for (int i = 0; i < arr.length; i++) {
                if (i != 2) {
                    assertEquals(original[i], arr[i].getRequestId(), "array was reordered before validation failed");
                }
            }
        }
    }



    @Test
    void timeSortForEachAlgorithm() {
        for (String algorithm : ALL_ALGORITHMS) {
            long nanos = SortEngine.timeSort(algorithm, manyRequests(), "requestId");
            assertTrue(nanos > 0, algorithm + " reported " + nanos + " ns");
        }
    }

    @Test
    void timeSortLeavesTheCallerArrayUntouched() {
        ServiceRequest[] arr = fixture();
        String[] before = ids(arr);
        SortEngine.timeSort("merge", arr, "requestId");
        assertArrayEquals(before, ids(arr), "timeSort must sort copies, not the caller's array");
    }

    @SuppressWarnings({"DataFlowIssue", "ConstantConditions"})
    @Test
    void timeSortRejectsInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> SortEngine.timeSort("bubble", fixture(), "requestId"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.timeSort(null, fixture(), "requestId"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.timeSort("merge", null, "requestId"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.timeSort("merge", fixture(), "urgency"));
    }
}
