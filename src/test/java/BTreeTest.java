import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class BTreeTest {

    private ServiceRequest req(String id) {
        return new ServiceRequest(id, "L001", "L002", "Plumbing", "MEDIUM",
                2, "2026-08-07T08:00:00", "2026-08-08T08:00:00", "NEW");
    }

    @Test
    void insertAndSearch() {
        BTree tree = new BTree(4);                    // order 4: max 3 keys/node

        // normal case: enough inserts to force multiple internal splits
        String[] ids = { "R010", "R020", "R005", "R030", "R015", "R025", "R001", "R040", "R035" };
        for (String id : ids) {
            tree.insert(id, req(id));
        }
        for (String id : ids) {
            assertEquals(id, tree.search(id).getRequestId());
        }

        // boundary case: search on an empty tree returns null; single-entry tree
        BTree emptyTree = new BTree(3);
        assertNull(emptyTree.search("MISSING"));
        emptyTree.insert("ONLY", req("ONLY"));
        assertEquals("ONLY", emptyTree.search("ONLY").getRequestId());
        assertNull(emptyTree.search("MISSING"));

        // re-inserting an existing key updates its value rather than duplicating it
        ServiceRequest updated = req("R010");
        tree.insert("R010", updated);
        assertSame(updated, tree.search("R010"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> tree.insert(null, req("X")));
        assertThrows(IllegalArgumentException.class, () -> tree.insert("X", null));
        assertThrows(IllegalArgumentException.class, () -> tree.search(null));
        assertThrows(IllegalArgumentException.class, () -> new BTree(2));
    }

    @Test
    void printTreeShowsNodeSplits() {
        // order 3 (max 2 keys/node): a third insert forces the root to split
        BTree tree = new BTree(3);
        tree.insert("B", req("B"));
        tree.insert("A", req("A"));

        // boundary case: single-node tree prints exactly one line
        String beforeSplit = capture(tree::printTree);
        assertEquals(1, beforeSplit.split("\n").length);

        // normal case: forcing a split produces evidence of multiple nodes
        tree.insert("C", req("C"));
        String afterSplit = capture(tree::printTree);
        String[] lines = afterSplit.split("\n");
        assertTrue(lines.length >= 3, "expected root + 2 children after split, got:\n" + afterSplit);

        // boundary case: an empty tree still prints something rather than crashing
        BTree empty = new BTree(4);
        assertDoesNotThrow(empty::printTree);
    }

    private String capture(Runnable action) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try {
            action.run();
        } finally {
            System.setOut(originalOut);
        }
        return buffer.toString().trim();
    }
}
