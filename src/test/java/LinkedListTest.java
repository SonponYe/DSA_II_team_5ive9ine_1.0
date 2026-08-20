import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void addFirstAndAddLast() {
        LinkedList<String> list = new LinkedList<>();

        // normal case
        list.addLast("b");
        list.addFirst("a");
        list.addLast("c");
        assertEquals(3, list.size());
        assertEquals("a", list.getFirst());

        // boundary case: adding to an empty list sets both head and tail
        LinkedList<String> empty = new LinkedList<>();
        empty.addFirst("only");
        assertEquals("only", empty.getFirst());
        assertEquals(1, empty.size());
    }

    @Test
    void insertAfter() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("c");

        // normal case: insert in the middle
        list.insertAfter("a", "b");
        assertEquals(3, list.size());

        // boundary case: insert after the current tail, tail must update
        list.insertAfter("c", "d");
        list.addLast("after-d-check");
        assertEquals(5, list.size());

        // invalid input: target not present
        assertThrows(IllegalStateException.class, () -> list.insertAfter("missing", "x"));
    }

    @Test
    void removeFirstAndRemoveLast() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        // normal case
        assertEquals("a", list.removeFirst());
        assertEquals("c", list.removeLast());
        assertEquals(1, list.size());
        assertEquals("b", list.getFirst());

        // boundary case: removing the only element empties the list
        assertEquals("b", list.removeFirst());
        assertTrue(list.isEmpty());

        // invalid input: removing from an empty list
        assertThrows(IllegalStateException.class, list::removeFirst);
        assertThrows(IllegalStateException.class, list::removeLast);
    }

    @Test
    void remove() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");
        list.addLast("c");

        // normal case: remove from the middle
        assertTrue(list.remove("b"));
        assertEquals(2, list.size());

        // boundary case: remove the head, then remove the (new) tail
        assertTrue(list.remove("a"));
        list.addLast("d");
        assertTrue(list.remove("d"));

        // invalid input: item not present, and removing from an empty list
        assertFalse(list.remove("missing"));
        list.removeFirst();
        assertTrue(list.isEmpty());
        assertFalse(list.remove("anything"));
    }

    @Test
    void getFirst() {
        LinkedList<String> list = new LinkedList<>();
        list.addLast("a");
        list.addLast("b");

        // normal case
        assertEquals("a", list.getFirst());

        // invalid input: empty list
        LinkedList<String> empty = new LinkedList<>();
        assertThrows(IllegalStateException.class, empty::getFirst);
    }

    @Test
    void iterator() {
        LinkedList<Integer> list = new LinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addLast(3);

        // normal case: for-each visits every element in order
        int expected = 1;
        for (int value : list) {
            assertEquals(expected, value);
            expected++;
        }
        assertEquals(4, expected);

        // boundary case: an empty list's iterator has no next element
        LinkedList<Integer> empty = new LinkedList<>();
        assertFalse(empty.iterator().hasNext());
        assertThrows(IllegalStateException.class, () -> empty.iterator().next());
    }
}
