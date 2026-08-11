import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PriorityQueueTest {

    private ServiceRequest createRequest(String id, int urgencyScore) {
        return new ServiceRequest(id, "src", "dst", "general", "CRITICAL", urgencyScore,
                "2026-08-11T10:00:00", "2026-08-11T12:00:00", "PENDING");
    }

    @Test
    void insertAndExtractMinOrdersByUrgency() {
        PriorityQueue queue = new PriorityQueue();
        ServiceRequest low = createRequest("low", 4);
        ServiceRequest high = createRequest("high", 1);
        ServiceRequest medium = createRequest("medium", 2);

        queue.insert(low);
        queue.insert(high);
        queue.insert(medium);

        assertEquals(3, queue.size());
        assertSame(high, queue.extractMin());
        assertSame(medium, queue.extractMin());
        assertSame(low, queue.extractMin());
        assertTrue(queue.isEmpty());
    }

    @Test
    void peekDoesNotRemove() {
        PriorityQueue queue = new PriorityQueue();
        ServiceRequest high = createRequest("high", 1);
        ServiceRequest low = createRequest("low", 4);

        queue.insert(high);
        queue.insert(low);

        assertSame(high, queue.peek());
        assertEquals(2, queue.size());
        assertSame(high, queue.peek());
    }

    @Test
    void heapifyBulkLoad() {
        ServiceRequest request1 = createRequest("one", 3);
        ServiceRequest request2 = createRequest("two", 1);
        ServiceRequest request3 = createRequest("three", 2);

        PriorityQueue queue = new PriorityQueue();
        queue.heapify(new ServiceRequest[]{request1, request2, request3});

        assertEquals(3, queue.size());
        assertSame(request2, queue.extractMin());
        assertSame(request3, queue.extractMin());
        assertSame(request1, queue.extractMin());
    }

    @Test
    void isEmptyAndSize() {
        PriorityQueue queue = new PriorityQueue();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        queue.insert(createRequest("first", 1));
        assertFalse(queue.isEmpty());
        assertEquals(1, queue.size());

        queue.extractMin();
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }
}
