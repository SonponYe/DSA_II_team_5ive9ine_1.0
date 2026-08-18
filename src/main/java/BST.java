// Unbalanced binary search tree, built from scratch with a private Node
// class (no java.util.TreeMap/TreeSet). Height of an empty tree is -1,
// a single-node tree has height 0 — used as the baseline for the
// height comparison against BalancedBST (AVL) in the report.
public class BST<T extends Comparable<T>> {

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root;

    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        root = insert(root, item);
    }

    private Node<T> insert(Node<T> node, T item) {
        if (node == null) {
            return new Node<>(item);
        }
        int cmp = item.compareTo(node.value);
        if (cmp < 0) {
            node.left = insert(node.left, item);
        } else if (cmp > 0) {
            node.right = insert(node.right, item);
        }
        // cmp == 0: duplicate, tree already contains an equal item — no-op
        return node;
    }

    public T search(T item) {                     // returns null if not found
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        Node<T> current = root;
        while (current != null) {
            int cmp = item.compareTo(current.value);
            if (cmp == 0) {
                return current.value;
            }
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    public void delete(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        root = delete(root, item);
    }

    private Node<T> delete(Node<T> node, T item) {
        if (node == null) {
            return null;                           // item not present — no-op
        }
        int cmp = item.compareTo(node.value);
        if (cmp < 0) {
            node.left = delete(node.left, item);
        } else if (cmp > 0) {
            node.right = delete(node.right, item);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node<T> successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.value = successor.value;
            node.right = delete(node.right, successor.value);
        }
        return node;
    }

    public void inorderTraversal() {                // prints nodes in sorted order
        inorderTraversal(root);
        System.out.println();
    }

    private void inorderTraversal(Node<T> node) {
        if (node == null) {
            return;
        }
        inorderTraversal(node.left);
        System.out.print(node.value + " ");
        inorderTraversal(node.right);
    }

    public int height() {
        return height(root);
    }

    private int height(Node<T> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }

    public boolean isEmpty() {
        return root == null;
    }
}
