import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void putAndGet() {
        HashTable<String, Integer> table = new HashTable<>(8);

        // normal case
        table.put("R001", 1);
        table.put("R002", 2);
        assertEquals(1, table.get("R001"));
        assertEquals(2, table.get("R002"));

        // boundary case: overwriting an existing key updates value, not size
        table.put("R001", 99);
        assertEquals(99, table.get("R001"));
        assertEquals(2, table.size());

        // invalid input: null key, missing key
        assertThrows(IllegalArgumentException.class, () -> table.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> table.get(null));
        assertNull(table.get("MISSING"));
    }

    @Test
    void remove() {
        HashTable<String, Integer> table = new HashTable<>(8);
        table.put("R001", 1);
        table.put("R002", 2);

        // normal case
        assertEquals(1, table.remove("R001"));
        assertFalse(table.containsKey("R001"));
        assertEquals(1, table.size());

        // boundary case: removing the last remaining entry
        assertEquals(2, table.remove("R002"));
        assertEquals(0, table.size());

        // invalid input: null key and missing key both reject
        assertThrows(IllegalArgumentException.class, () -> table.remove(null));
        assertThrows(NoSuchElementException.class, () -> table.remove("GONE"));
    }

    @Test
    void containsKey() {
        HashTable<String, Integer> table = new HashTable<>(8);
        table.put("R001", 1);

        // normal case
        assertTrue(table.containsKey("R001"));

        // boundary case: key absent from an otherwise non-empty table
        assertFalse(table.containsKey("R002"));

        // invalid input: null key
        assertThrows(IllegalArgumentException.class, () -> table.containsKey(null));
    }

    @Test
    void collisionCountAtHighLoadFactor() {
        // boundary case: keys 1, 11, 21 all hash to the same bucket (index 1)
        // in a capacity-10 table, and the load factor stays well under the
        // resize threshold, so every insert after the first is a collision
        HashTable<Integer, String> forcedCollisions = new HashTable<>(10);
        forcedCollisions.put(1, "a");
        forcedCollisions.put(11, "b");
        forcedCollisions.put(21, "c");
        assertEquals(2, forcedCollisions.getCollisionCount());
        assertEquals(3, forcedCollisions.size());

        // normal case: a well-spread table with room to grow has zero collisions
        HashTable<Integer, String> spread = new HashTable<>(16);
        spread.put(1, "a");
        spread.put(2, "b");
        assertEquals(0, spread.getCollisionCount());

        // boundary case / resize trigger: pushing past the load factor
        // threshold must resize without losing any entries
        HashTable<Integer, Integer> resizing = new HashTable<>(4);
        for (int i = 0; i < 100; i++) {
            resizing.put(i, i * i);
        }
        assertEquals(100, resizing.size());
        for (int i = 0; i < 100; i++) {
            assertEquals(i * i, resizing.get(i));
        }

        // invalid input: non-positive capacity is rejected at construction
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, String>(0));
        assertThrows(IllegalArgumentException.class, () -> new HashTable<String, String>(-1));
    }
}
