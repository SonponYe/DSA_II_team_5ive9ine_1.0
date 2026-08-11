import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomMapTest {

    @Test
    void putAndGet() {
        CustomMap<String, Integer> map = new CustomMap<>();

        // normal case
        map.put("R001", 1);
        map.put("R002", 2);
        assertEquals(1, map.get("R001"));
        assertEquals(2, map.get("R002"));

        // boundary case: putting an existing key overwrites the value, not size
        map.put("R001", 99);
        assertEquals(99, map.get("R001"));
        assertEquals(2, map.size());

        // invalid input: null key rejected; missing key returns null,
        // matching HashTable.get's contract
        assertThrows(IllegalArgumentException.class, () -> map.put(null, 1));
        assertThrows(IllegalArgumentException.class, () -> map.get(null));
        assertNull(map.get("MISSING"));
    }

    @Test
    void containsKey() {
        CustomMap<String, Integer> map = new CustomMap<>();
        map.put("R001", 1);

        // normal case
        assertTrue(map.containsKey("R001"));

        // boundary case: key absent from a non-empty map
        assertFalse(map.containsKey("R002"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> map.containsKey(null));
    }

    @Test
    void remove() {
        CustomMap<String, Integer> map = new CustomMap<>();
        map.put("R001", 1);
        map.put("R002", 2);

        // normal case
        map.remove("R001");
        assertFalse(map.containsKey("R001"));
        assertEquals(1, map.size());

        // boundary case: removing a missing key is a safe no-op; removing the
        // last entry empties the map
        assertDoesNotThrow(() -> map.remove("NEVER_ADDED"));
        map.remove("R002");
        assertEquals(0, map.size());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> map.remove(null));
    }
}
