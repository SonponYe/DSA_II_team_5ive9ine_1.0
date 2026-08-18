// B-Tree (order-way search tree) built from scratch — plain arrays as raw
// node storage, splitting logic is ours (no java.util collection classes).
// Deletion is out of scope per METHOD_SIGNATURES.md (insert/search/printTree only).
//
// Decision: order 4 (max 3 keys, 4 children per node) is final for wherever
// this class is instantiated (Menu classes, tests) — the constructor itself
// stays generic and respects whatever order is passed in.
public class BTree {

    private static class Node {
        final boolean leaf;
        final String[] keys;
        final ServiceRequest[] values;
        final Node[] children;
        int numKeys;

        Node(int order, boolean leaf) {
            this.leaf = leaf;
            this.keys = new String[order - 1];
            this.values = new ServiceRequest[order - 1];
            this.children = new Node[order];
            this.numKeys = 0;
        }
    }

    private final int order;                        // max children per node
    private Node root;

    public BTree(int order) {                      // e.g. order 3 = max 2 keys per node
        if (order < 3) {
            throw new IllegalArgumentException("order must be at least 3");
        }
        this.order = order;
        this.root = null;
    }

    public void insert(String key, ServiceRequest value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }

        if (root == null) {
            root = new Node(order, true);
            root.keys[0] = key;
            root.values[0] = value;
            root.numKeys = 1;
            return;
        }

        if (updateIfPresent(root, key, value)) {
            return;                                  // key already existed — updated in place
        }

        if (root.numKeys == order - 1) {
            Node newRoot = new Node(order, false);
            newRoot.children[0] = root;
            splitChild(newRoot, 0);
            root = newRoot;
        }
        insertNonFull(root, key, value);
    }

    private boolean updateIfPresent(Node node, String key, ServiceRequest value) {
        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        if (i < node.numKeys && key.equals(node.keys[i])) {
            node.values[i] = value;
            return true;
        }
        if (node.leaf) {
            return false;
        }
        return updateIfPresent(node.children[i], key, value);
    }

    private void insertNonFull(Node node, String key, ServiceRequest value) {
        int i = node.numKeys - 1;
        if (node.leaf) {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.numKeys++;
        } else {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                i--;
            }
            i++;
            if (node.children[i].numKeys == order - 1) {
                splitChild(node, i);
                if (key.compareTo(node.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key, value);
        }
    }

    // Splits the full child at parent.children[index], pushing the median
    // key/value up into parent at position index.
    private void splitChild(Node parent, int index) {
        Node fullChild = parent.children[index];
        int mid = (order - 1) / 2;

        Node right = new Node(order, fullChild.leaf);
        int rightCount = fullChild.numKeys - mid - 1;
        for (int j = 0; j < rightCount; j++) {
            right.keys[j] = fullChild.keys[mid + 1 + j];
            right.values[j] = fullChild.values[mid + 1 + j];
        }
        if (!fullChild.leaf) {
            for (int j = 0; j <= rightCount; j++) {
                right.children[j] = fullChild.children[mid + 1 + j];
            }
        }
        right.numKeys = rightCount;

        String medianKey = fullChild.keys[mid];
        ServiceRequest medianValue = fullChild.values[mid];
        fullChild.numKeys = mid;

        for (int j = parent.numKeys; j > index; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[index + 1] = right;

        for (int j = parent.numKeys - 1; j >= index; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }
        parent.keys[index] = medianKey;
        parent.values[index] = medianValue;
        parent.numKeys++;
    }

    public ServiceRequest search(String key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        return search(root, key);
    }

    private ServiceRequest search(Node node, String key) {
        if (node == null) {
            return null;
        }
        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        if (i < node.numKeys && key.equals(node.keys[i])) {
            return node.values[i];
        }
        if (node.leaf) {
            return null;
        }
        return search(node.children[i], key);
    }

    public void printTree() {                        // show node structure for evidence
        if (root == null) {
            System.out.println("(empty tree)");
            return;
        }
        printTree(root, 0);
    }

    private void printTree(Node node, int depth) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        StringBuilder keys = new StringBuilder("[");
        for (int i = 0; i < node.numKeys; i++) {
            keys.append(node.keys[i]);
            if (i < node.numKeys - 1) {
                keys.append(", ");
            }
        }
        keys.append("]");
        System.out.println(indent + keys.toString());

        if (!node.leaf) {
            for (int i = 0; i <= node.numKeys; i++) {
                printTree(node.children[i], depth + 1);
            }
        }
    }
}
