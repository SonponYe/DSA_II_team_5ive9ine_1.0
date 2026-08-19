import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    private ServiceRequest req(String id, int urgencyScore) {
        return new ServiceRequest(id, "L001", "L002", "Plumbing", "MEDIUM",
                urgencyScore, "2026-08-07T08:00:00", "2026-08-08T08:00:00", "NEW");
    }

    @Test
    void insertAndExtractMinOrdersByUrgency() {
        PriorityQueue pq = new PriorityQueue();
        pq.insert(req("R1", 3));
        pq.insert(req("R2", 1));
        pq.insert(req("R3", 4));
        pq.insert(req("R4", 2));

        // normal case: extraction order follows ascending urgency score
        assertEquals("R2", pq.extractMin().getRequestId());
        assertEquals("R4", pq.extractMin().getRequestId());
        assertEquals("R1", pq.extractMin().getRequestId());
        assertEquals("R3", pq.extractMin().getRequestId());
        assertTrue(pq.isEmpty());

        // boundary case: single element
        PriorityQueue single = new PriorityQueue();
        single.insert(req("ONLY", 1));
        assertEquals("ONLY", single.extractMin().getRequestId());
        assertTrue(single.isEmpty());

        // invalid input: null request rejected, empty queue extraction rejected
        PriorityQueue empty = new PriorityQueue();
        assertThrows(IllegalArgumentException.class, () -> empty.insert(null));
        assertThrows(NoSuchElementException.class, empty::extractMin);
    }

    @Test
    void peekDoesNotRemove() {
        PriorityQueue pq = new PriorityQueue();
        pq.insert(req("R1", 5));
        pq.insert(req("R2", 1));

        // normal case: peek returns most urgent without removing it
        assertEquals("R2", pq.peek().getRequestId());
        assertEquals(2, pq.size());
        assertEquals("R2", pq.peek().getRequestId());

        // boundary case: single element queue
        PriorityQueue single = new PriorityQueue();
        single.insert(req("ONLY", 9));
        assertEquals("ONLY", single.peek().getRequestId());
        assertEquals(1, single.size());

        // invalid input: peeking an empty queue throws
        PriorityQueue empty = new PriorityQueue();
        assertThrows(NoSuchElementException.class, empty::peek);
    }

    @Test
    void heapifyBulkLoad() {
        PriorityQueue pq = new PriorityQueue();

        // normal case: bulk load out-of-order array, extraction comes out sorted
        ServiceRequest[] requests = new ServiceRequest[] {
                req("R1", 4), req("R2", 2), req("R3", 5), req("R4", 1), req("R5", 3)
        };
        pq.heapify(requests);
        assertEquals(5, pq.size());
        int previous = Integer.MIN_VALUE;
        while (!pq.isEmpty()) {
            ServiceRequest next = pq.extractMin();
            assertTrue(next.getUrgencyScore() >= previous);
            previous = next.getUrgencyScore();
        }

        // boundary case: heapify with an empty array clears the queue
        pq.insert(req("LEFTOVER", 1));
        pq.heapify(new ServiceRequest[0]);
        assertTrue(pq.isEmpty());

        // invalid input: null array and null elements are rejected
        assertThrows(IllegalArgumentException.class, () -> pq.heapify(null));
        assertThrows(IllegalArgumentException.class,
                () -> pq.heapify(new ServiceRequest[] { req("R1", 1), null }));
    }

    @Test
    void isEmptyAndSize() {
        PriorityQueue pq = new PriorityQueue();

        // boundary case: brand new queue is empty
        assertTrue(pq.isEmpty());
        assertEquals(0, pq.size());

        // normal case: size grows and shrinks with insert/extractMin
        pq.insert(req("R1", 1));
        pq.insert(req("R2", 2));
        assertFalse(pq.isEmpty());
        assertEquals(2, pq.size());

        pq.extractMin();
        assertEquals(1, pq.size());
        pq.extractMin();
        assertEquals(0, pq.size());
        assertTrue(pq.isEmpty());

        // invalid input: extractMin on an empty queue throws rather than
        // returning a silent null
        assertThrows(NoSuchElementException.class, pq::extractMin);
    }
}
