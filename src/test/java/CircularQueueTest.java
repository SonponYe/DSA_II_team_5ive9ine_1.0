import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircularQueueTest {

    @Test
    void enqueueThrowsWhenFull() {
        CircularQueue<String> queue = new CircularQueue<>(2);

        // normal case
        queue.enqueue("a");
        queue.enqueue("b");
        assertTrue(queue.isFull());

        // invalid input: enqueue on a full queue
        assertThrows(IllegalStateException.class, () -> queue.enqueue("c"));

        // boundary case: after dequeuing, the freed slot is reused (the
        // "wrap-around" behaviour) instead of staying permanently full
        queue.dequeue();
        assertDoesNotThrow(() -> queue.enqueue("c"));

        // invalid input: capacity must be positive
        assertThrows(IllegalArgumentException.class, () -> new CircularQueue<String>(0));
    }

    @Test
    void dequeue() {
        CircularQueue<String> queue = new CircularQueue<>(3);
        queue.enqueue("a");
        queue.enqueue("b");

        // normal case: FIFO order
        assertEquals("a", queue.dequeue());
        assertEquals("b", queue.dequeue());

        // boundary case: dequeue the very last element, then reuse the queue
        queue.enqueue("c");
        assertEquals("c", queue.dequeue());
        assertTrue(queue.isEmpty());

        // invalid input: dequeue on an empty queue
        assertThrows(IllegalStateException.class, queue::dequeue);
    }

    @Test
    void isFull() {
        CircularQueue<Integer> queue = new CircularQueue<>(2);

        // boundary case: brand new queue is not full
        assertFalse(queue.isFull());

        // normal case: fills up as capacity is reached
        queue.enqueue(1);
        assertFalse(queue.isFull());
        queue.enqueue(2);
        assertTrue(queue.isFull());

        // boundary case: dequeuing makes it not-full again
        queue.dequeue();
        assertFalse(queue.isFull());
    }

    @Test
    void isEmptyAndSize() {
        CircularQueue<Integer> queue = new CircularQueue<>(5);

        // boundary case: brand new queue is empty
        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        // normal case: size tracks enqueue/dequeue
        queue.enqueue(1);
        queue.enqueue(2);
        assertEquals(2, queue.size());
        assertFalse(queue.isEmpty());

        queue.dequeue();
        assertEquals(1, queue.size());

        // invalid input: peek on an empty queue after draining it
        queue.dequeue();
        assertTrue(queue.isEmpty());
        assertThrows(IllegalStateException.class, queue::peek);
    }
}
