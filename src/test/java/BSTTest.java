import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class BSTTest {

    @Test
    void insertAndSearch() {
        BST<Integer> tree = new BST<>();

        // normal case
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        assertEquals(50, tree.search(50));
        assertEquals(20, tree.search(20));

        // boundary case: searching an empty tree, and duplicate insert is a no-op
        BST<Integer> empty = new BST<>();
        assertNull(empty.search(1));
        int sizeBefore = tree.height();
        tree.insert(50);
        assertEquals(sizeBefore, tree.height());

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> tree.insert(null));
        assertThrows(IllegalArgumentException.class, () -> tree.search(null));
        assertNull(tree.search(999));
    }

    @Test
    void delete() {
        BST<Integer> tree = new BST<>();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);

        // normal case: delete a leaf, a one-child node, and a two-child node
        tree.delete(20);                 // leaf
        assertNull(tree.search(20));
        tree.delete(30);                 // now has only right child (40)
        assertNull(tree.search(30));
        assertEquals(40, tree.search(40));
        tree.delete(70);                 // two children (60, 80)
        assertNull(tree.search(70));
        assertEquals(60, tree.search(60));
        assertEquals(80, tree.search(80));

        // boundary case: deleting the only remaining node empties the tree
        BST<Integer> single = new BST<>();
        single.insert(1);
        single.delete(1);
        assertTrue(single.isEmpty());

        // invalid input: null rejected; deleting a missing item is a safe no-op
        assertThrows(IllegalArgumentException.class, () -> tree.delete(null));
        assertDoesNotThrow(() -> tree.delete(-12345));
    }

    @Test
    void inorderTraversalIsSorted() {
        BST<Integer> tree = new BST<>();
        tree.insert(50);
        tree.insert(20);
        tree.insert(80);
        tree.insert(10);
        tree.insert(30);

        // normal case: inorder traversal prints ascending sorted order
        assertEquals("10 20 30 50 80", capture(tree::inorderTraversal));

        // boundary case: empty tree prints nothing
        BST<Integer> empty = new BST<>();
        assertEquals("", capture(empty::inorderTraversal));
    }

    @Test
    void height() {
        // boundary case: empty tree has height -1, single node has height 0
        BST<Integer> tree = new BST<>();
        assertEquals(-1, tree.height());
        tree.insert(50);
        assertEquals(0, tree.height());

        // normal case: a left-leaning chain of 3 nodes has height 2
        tree.insert(30);
        tree.insert(10);
        assertEquals(2, tree.height());
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
