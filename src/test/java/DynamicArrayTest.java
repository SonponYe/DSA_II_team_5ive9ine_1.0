import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void add_toEnd() {
        DynamicArray<String> arr = new DynamicArray<>();
        arr.add("a");
        arr.add("b");
        arr.add("c");
        assertEquals(3, arr.size());
        assertEquals("a", arr.get(0));
        assertEquals("b", arr.get(1));
        assertEquals("c", arr.get(2));

        // boundary case: adding past the default capacity forces a resize,
        // and everything already there must survive it
        DynamicArray<Integer> small = new DynamicArray<>(2);
        for (int i = 0; i < 20; i++) {
            small.add(i);
        }
        assertEquals(20, small.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, small.get(i));
        }

        // invalid input: capacity must be positive
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray<Integer>(0));
        assertThrows(IllegalArgumentException.class, () -> new DynamicArray<Integer>(-1));
    }

    @Test
    void add_atIndex() {
        DynamicArray<String> arr = new DynamicArray<>();
        arr.add("a");
        arr.add("c");

        // normal case: insert in the middle shifts everything after it right
        arr.insert(1, "b");
        assertEquals(3, arr.size());
        assertEquals("a", arr.get(0));
        assertEquals("b", arr.get(1));
        assertEquals("c", arr.get(2));

        // boundary case: insert at index 0 and at index == size (append)
        arr.insert(0, "start");
        assertEquals("start", arr.get(0));
        arr.insert(arr.size(), "end");
        assertEquals("end", arr.get(arr.size() - 1));

        // invalid input: negative index and index beyond size
        assertThrows(IndexOutOfBoundsException.class, () -> arr.insert(-1, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.insert(arr.size() + 1, "x"));
    }

    @Test
    void get() {
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.add(10);
        arr.add(20);

        // normal case
        assertEquals(10, arr.get(0));
        assertEquals(20, arr.get(1));

        // invalid input: empty array and out-of-range index
        DynamicArray<Integer> empty = new DynamicArray<>();
        assertThrows(IndexOutOfBoundsException.class, () -> empty.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.get(2));
    }

    @Test
    void set() {
        DynamicArray<String> arr = new DynamicArray<>();
        arr.add("a");
        arr.add("b");

        // normal case
        arr.set(1, "B");
        assertEquals("B", arr.get(1));
        assertEquals(2, arr.size()); // set never changes size

        // invalid input
        assertThrows(IndexOutOfBoundsException.class, () -> arr.set(-1, "x"));
        assertThrows(IndexOutOfBoundsException.class, () -> arr.set(2, "x"));
    }

    @Test
    void remove() {
        DynamicArray<String> arr = new DynamicArray<>();
        arr.add("a");
        arr.add("b");
        arr.add("c");

        // normal case: removing from the middle shifts the tail left
        assertEquals("b", arr.remove(1));
        assertEquals(2, arr.size());
        assertEquals("a", arr.get(0));
        assertEquals("c", arr.get(1));

        // boundary case: removing the last remaining element empties the array
        arr.remove(1);
        assertEquals("a", arr.remove(0));
        assertTrue(arr.isEmpty());

        // invalid input
        assertThrows(IndexOutOfBoundsException.class, () -> arr.remove(0));
        DynamicArray<String> single = new DynamicArray<>();
        single.add("only");
        assertThrows(IndexOutOfBoundsException.class, () -> single.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> single.remove(1));
    }

    @Test
    void sizeAndIsEmpty() {
        DynamicArray<String> arr = new DynamicArray<>();

        // boundary case: brand new array is empty
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());

        // normal case: size grows and shrinks with add/remove
        arr.add("a");
        arr.add("b");
        assertFalse(arr.isEmpty());
        assertEquals(2, arr.size());

        arr.clear();
        assertTrue(arr.isEmpty());
        assertEquals(0, arr.size());
    }
}
