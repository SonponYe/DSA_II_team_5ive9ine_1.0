import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DisjointSetTest {

    @Test
    void makeSetFindUnion() {
        DisjointSet sets = new DisjointSet(5);

        // normal case: every element starts as its own root
        for (int i = 0; i < 5; i++) {
            assertEquals(i, sets.find(i));
        }

        // normal case: after union, both elements share one root
        sets.union(0, 1);
        assertEquals(sets.find(0), sets.find(1));

        // boundary case: makeSet resets an element back to being its own
        // root, independent of any union it was previously part of
        sets.makeSet(1);
        assertEquals(1, sets.find(1));
    }

    @Test
    void connectedAfterUnion() {
        DisjointSet sets = new DisjointSet(6);

        // normal case: unconnected elements report false
        assertFalse(sets.connected(0, 1));

        sets.union(0, 1);
        assertTrue(sets.connected(0, 1));

        // boundary case: connectivity is transitive across a chain of unions
        sets.union(1, 2);
        sets.union(2, 3);
        assertTrue(sets.connected(0, 3));

        // elements never unioned into the chain stay separate
        assertFalse(sets.connected(0, 4));
        assertFalse(sets.connected(4, 5));
    }

    @Test
    void pathCompressionAndUnionByRank() {
        DisjointSet sets = new DisjointSet(5);

        // Build a chain 0-1-2-3-4 via repeated unions.
        sets.union(0, 1);
        sets.union(1, 2);
        sets.union(2, 3);
        sets.union(3, 4);

        // normal case: after find() runs (with path compression), every
        // element in the chain reports the same root and is connected.
        int root = sets.find(4);
        for (int i = 0; i < 5; i++) {
            assertEquals(root, sets.find(i));
        }
        assertTrue(sets.connected(0, 4));

        // boundary case: unioning two elements already in the same set is a
        // harmless no-op
        assertDoesNotThrow(() -> sets.union(0, 4));
        assertTrue(sets.connected(0, 4));
    }
}
