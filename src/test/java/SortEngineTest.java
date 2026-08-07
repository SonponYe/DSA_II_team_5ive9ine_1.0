import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortEngineTest {

    private static ServiceRequest req(String id, String category, int urgencyScore,
                                       String timeSubmitted, String deadline) {
        return new ServiceRequest(id, "L001", "L036", category, "MEDIUM", urgencyScore,
                timeSubmitted, deadline, "NEW");
    }

    private static ServiceRequest[] sample() {
        return new ServiceRequest[] {
                req("Q003", "Structural", 3, "2026-07-03T08:15:00", "2026-07-04T08:15:00"),
                req("Q001", "Plumbing", 4, "2026-07-01T17:41:00", "2026-07-05T08:06:00"),
                req("Q005", "Electrical", 1, "2026-07-04T09:00:00", "2026-07-04T11:00:00"),
                req("Q004", "Plumbing", 2, "2026-07-02T11:29:00", "2026-07-02T15:29:00"),
                req("Q002", "Electrical", 3, "2026-07-02T08:06:00", "2026-07-02T17:41:00"),
        };
    }

    private static void assertSortedByUrgencyScore(ServiceRequest[] arr) {
        for (int i = 1; i < arr.length; i++) {
            assertTrue(arr[i - 1].getUrgencyScore() <= arr[i].getUrgencyScore());
        }
    }

    @Test
    void selectionSort() {
        ServiceRequest[] arr = sample();
        SortEngine.selectionSort(arr, "urgencyScore");
        assertSortedByUrgencyScore(arr);
        assertEquals("Q005", arr[0].getRequestId());

        // boundary case: empty and single-element arrays
        ServiceRequest[] empty = new ServiceRequest[0];
        assertDoesNotThrow(() -> SortEngine.selectionSort(empty, "urgencyScore"));
        ServiceRequest[] single = { req("Q001", "Plumbing", 4, "t", "d") };
        SortEngine.selectionSort(single, "urgencyScore");
        assertEquals("Q001", single[0].getRequestId());

        // invalid input: unknown sort key, null array
        assertThrows(IllegalArgumentException.class, () -> SortEngine.selectionSort(sample(), "notAKey"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.selectionSort(null, "urgencyScore"));
    }

    @Test
    void insertionSort() {
        ServiceRequest[] arr = sample();
        SortEngine.insertionSort(arr, "requestId");
        assertEquals("Q001", arr[0].getRequestId());
        assertEquals("Q005", arr[4].getRequestId());
        for (int i = 1; i < arr.length; i++) {
            assertTrue(arr[i - 1].getRequestId().compareTo(arr[i].getRequestId()) <= 0);
        }

        // boundary case: already-sorted input stays sorted
        SortEngine.insertionSort(arr, "requestId");
        assertEquals("Q001", arr[0].getRequestId());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> SortEngine.insertionSort(sample(), "notAKey"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.insertionSort(null, "requestId"));
    }

    @Test
    void mergeSort() {
        ServiceRequest[] arr = sample();
        SortEngine.mergeSort(arr, "deadline");
        for (int i = 1; i < arr.length; i++) {
            assertTrue(arr[i - 1].getDeadline().compareTo(arr[i].getDeadline()) <= 0);
        }

        // boundary case: empty and single-element arrays don't throw
        assertDoesNotThrow(() -> SortEngine.mergeSort(new ServiceRequest[0], "deadline"));
        ServiceRequest[] single = { req("Q001", "Plumbing", 4, "t", "d") };
        assertDoesNotThrow(() -> SortEngine.mergeSort(single, "deadline"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> SortEngine.mergeSort(sample(), "notAKey"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.mergeSort(null, "deadline"));
    }

    @Test
    void quickSortWorstCaseSortedInput() {
        // worst case for a naive fixed-pivot quicksort: input already sorted ascending
        ServiceRequest[] arr = sample();
        SortEngine.mergeSort(arr, "requestId");
        SortEngine.quickSort(arr, "requestId");
        for (int i = 1; i < arr.length; i++) {
            assertTrue(arr[i - 1].getRequestId().compareTo(arr[i].getRequestId()) <= 0);
        }

        // normal case: unsorted input
        ServiceRequest[] unsorted = sample();
        SortEngine.quickSort(unsorted, "timeSubmitted");
        for (int i = 1; i < unsorted.length; i++) {
            assertTrue(unsorted[i - 1].getTimeSubmitted().compareTo(unsorted[i].getTimeSubmitted()) <= 0);
        }

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> SortEngine.quickSort(sample(), "notAKey"));
        assertThrows(IllegalArgumentException.class, () -> SortEngine.quickSort(null, "requestId"));
    }

    @Test
    void timeSortForEachAlgorithm() {
        String[] algorithms = { "selection", "insertion", "merge", "quick" };
        for (String algorithm : algorithms) {
            ServiceRequest[] original = sample();
            ServiceRequest[] beforeSnapshot = sample();

            long elapsed = SortEngine.timeSort(algorithm, original, "urgencyScore");

            assertTrue(elapsed >= 0, algorithm + " should report a non-negative duration");
            // timeSort must sort a copy — the caller's array is left untouched
            for (int i = 0; i < original.length; i++) {
                assertEquals(beforeSnapshot[i].getRequestId(), original[i].getRequestId());
            }
        }

        // invalid input: unknown algorithm name
        assertThrows(IllegalArgumentException.class,
                () -> SortEngine.timeSort("bogus", sample(), "urgencyScore"));
        assertThrows(IllegalArgumentException.class,
                () -> SortEngine.timeSort(null, sample(), "urgencyScore"));
        assertThrows(IllegalArgumentException.class,
                () -> SortEngine.timeSort("quick", null, "urgencyScore"));
    }
}
