import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DequeTest {

    @Test
    void addFrontForCritical() {
        Deque<Integer> deque = new Deque<>();

        deque.addFront(10);
        deque.addFront(20);

        assertEquals(20, deque.peekFront());
        assertEquals(2, deque.size());
    }

    @Test
    void addRearForNormal() {
        Deque<Integer> deque = new Deque<>();

        deque.addRear(10);
        deque.addRear(20);

        assertEquals(10, deque.peekFront());
        assertEquals(2, deque.size());
    }

    @Test
    void removeFrontAndRemoveRear() {
        Deque<Integer> deque = new Deque<>();

        deque.addRear(10);
        deque.addRear(20);
        deque.addRear(30);

        assertEquals(10, deque.removeFront());
        assertEquals(30, deque.removeRear());
        assertEquals(1, deque.size());
    }

    @Test
    void peekFront() {
        Deque<Integer> deque = new Deque<>();

        deque.addRear(50);

        assertEquals(50, deque.peekFront());
        assertEquals(1, deque.size());
    }
}