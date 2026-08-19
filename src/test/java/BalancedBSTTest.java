import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class BalancedBSTTest {

    @Test
    void insertKeepsHeightLowVersusBST() {
        BalancedBST<Integer> avl = new BalancedBST<>();
        BST<Integer> plain = new BST<>();

        // normal case: inserting a sorted run degenerates the plain BST into
        // a chain but the AVL tree must stay balanced (height <= ~1.44*log2(n))
        for (int i = 1; i <= 15; i++) {
            avl.insert(i);
            plain.insert(i);
        }
        assertEquals(14, plain.height());
        assertTrue(avl.height() <= 4, "AVL height should stay low, was " + avl.height());
        assertTrue(avl.height() < plain.height());

        // boundary case: single-node tree has height 0
        BalancedBST<Integer> single = new BalancedBST<>();
        single.insert(1);
        assertEquals(0, single.height());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> avl.insert(null));
    }

    @Test
    void searchAndDelete() {
        BalancedBST<Integer> avl = new BalancedBST<>();
        int[] values = { 50, 30, 70, 20, 40, 60, 80, 10 };
        for (int v : values) {
            avl.insert(v);
        }

        // normal case
        assertEquals(40, avl.search(40));
        avl.delete(40);
        assertNull(avl.search(40));
        assertEquals(50, avl.search(50));
        assertTrue(avl.height() <= 4, "AVL height should stay low after delete, was " + avl.height());

        // boundary case: deleting the only node empties the tree
        BalancedBST<Integer> single = new BalancedBST<>();
        single.insert(1);
        single.delete(1);
        assertNull(single.search(1));
        assertEquals(-1, single.height());

        // invalid input: null rejected, missing-item delete is a safe no-op
        assertThrows(IllegalArgumentException.class, () -> avl.search(null));
        assertThrows(IllegalArgumentException.class, () -> avl.delete(null));
        assertDoesNotThrow(() -> avl.delete(-99999));
    }

    @Test
    void inorderTraversalIsSorted() {
        BalancedBST<Integer> avl = new BalancedBST<>();
        for (int v : new int[] { 50, 20, 80, 10, 30 }) {
            avl.insert(v);
        }

        // normal case: inorder traversal is ascending regardless of rotations
        assertEquals("10 20 30 50 80", capture(avl::inorderTraversal));

        // boundary case: empty tree prints nothing
        BalancedBST<Integer> empty = new BalancedBST<>();
        assertEquals("", capture(empty::inorderTraversal));
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
