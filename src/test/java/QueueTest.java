import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueueTest {

    @Test
    void enqueueAndDequeue() {
        Queue<Integer> queue = new Queue<>();

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        assertEquals(10, queue.dequeue());
        assertEquals(20, queue.dequeue());
        assertEquals(30, queue.dequeue());
    }

    @Test
    void peek() {
        Queue<String> queue = new Queue<>();

        queue.enqueue("A");
        queue.enqueue("B");

        assertEquals("A", queue.peek());
        assertEquals(2, queue.size());
    }

    @Test
    void isEmptyAndSize() {
        Queue<Integer> queue = new Queue<>();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());

        queue.enqueue(1);
        queue.enqueue(2);

        assertFalse(queue.isEmpty());
        assertEquals(2, queue.size());

        queue.dequeue();
        queue.dequeue();

        assertTrue(queue.isEmpty());
        assertEquals(0, queue.size());
    }

    @Test
    void dequeueEmptyQueueThrowsException() {
        Queue<Integer> queue = new Queue<>();

        assertThrows(
                IllegalStateException.class,
                queue::dequeue
        );
    }

    @Test
    void peekEmptyQueueThrowsException() {
        Queue<Integer> queue = new Queue<>();

        assertThrows(
                IllegalStateException.class,
                queue::peek
        );
    }
}