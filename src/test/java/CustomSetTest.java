import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomSetTest {

    @Test
    void addAndContains() {
        CustomSet<String> set = new CustomSet<>();

        // normal case
        set.add("L001");
        set.add("L002");
        assertTrue(set.contains("L001"));
        assertTrue(set.contains("L002"));

        // boundary case: adding a duplicate is a no-op
        set.add("L001");
        assertEquals(2, set.size());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> set.add(null));
        assertThrows(IllegalArgumentException.class, () -> set.contains(null));
        assertFalse(set.contains("MISSING"));
    }

    @Test
    void remove() {
        CustomSet<String> set = new CustomSet<>();
        set.add("L001");
        set.add("L002");

        // normal case
        set.remove("L001");
        assertFalse(set.contains("L001"));
        assertEquals(1, set.size());

        // boundary case: removing a missing item is a safe no-op
        assertDoesNotThrow(() -> set.remove("NEVER_ADDED"));
        assertEquals(1, set.size());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> set.remove(null));
    }

    @Test
    void size() {
        CustomSet<Integer> set = new CustomSet<>();

        // boundary case: brand new set is empty
        assertEquals(0, set.size());

        // normal case: size tracks adds/removes, including past a resize trigger
        for (int i = 0; i < 50; i++) {
            set.add(i);
        }
        assertEquals(50, set.size());
        for (int i = 0; i < 50; i++) {
            assertTrue(set.contains(i));
        }

        set.remove(0);
        assertEquals(49, set.size());
    }
}
