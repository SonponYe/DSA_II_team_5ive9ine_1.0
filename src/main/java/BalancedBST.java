// AVL tree (rotation-based self-balancing BST) — no java.util.TreeMap/TreeSet.
//
// Decision: AVL, not Red-Black. AVL enforces a stricter balance invariant
// (|balance factor| <= 1 at every node), which gives a bigger, more visible
// height improvement over the plain BST for the report's comparison section,
// and it has fewer edge cases to get right than Red-Black under our timeline.
// This is final for the project, not a placeholder.
public class BalancedBST<T extends Comparable<T>> {

    private static class Node<T> {
        T value;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T value) {
            this.value = value;
            this.height = 0;
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
        } else {
            return node;                           // duplicate — no-op
        }
        return rebalance(node);
    }

    public T search(T item) {                     // returns null if not found
        // Deliberate exception to rule 5's "only HashTable.get/BST.search
        // may return null" default: BalancedBST is directly compared
        // against, and may be swapped live for, BST — so it must share
        // BST.search's null-on-miss contract rather than throwing.
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
        return rebalance(node);
    }

    public void inorderTraversal() {
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

    public int height() {                          // must stay low — show vs plain BST
        return height(root);
    }

    private Node<T> rebalance(Node<T> node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }
        return node;
    }

    private Node<T> rotateRight(Node<T> node) {
        Node<T> newRoot = node.left;
        node.left = newRoot.right;
        newRoot.right = node;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private Node<T> rotateLeft(Node<T> node) {
        Node<T> newRoot = node.right;
        node.right = newRoot.left;
        newRoot.left = node;
        updateHeight(node);
        updateHeight(newRoot);
        return newRoot;
    }

    private void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(Node<T> node) {
        return height(node.left) - height(node.right);
    }

    private int height(Node<T> node) {
        return node == null ? -1 : node.height;
    }
}
