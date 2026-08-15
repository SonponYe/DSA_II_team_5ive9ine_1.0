// Hash-based set built from scratch (own separate-chaining buckets, plain
// Object[] as raw storage — no java.util.HashSet/TreeSet).
public class CustomSet<T> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T>[] buckets;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomSet() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = (Node<T>[]) new Node[capacity];
        this.size = 0;
    }

    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        int index = indexFor(item, capacity);
        Node<T> current = buckets[index];
        while (current != null) {
            if (current.value.equals(item)) {
                return;                              // already present — no-op
            }
            current = current.next;
        }
        buckets[index] = new Node<>(item, buckets[index]);
        size++;

        if ((double) size / capacity > LOAD_FACTOR_THRESHOLD) {
            resize(capacity * 2);
        }
    }

    public boolean contains(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        Node<T> current = buckets[indexFor(item, capacity)];
        while (current != null) {
            if (current.value.equals(item)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void remove(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null");
        }
        int index = indexFor(item, capacity);
        Node<T> current = buckets[index];
        Node<T> previous = null;
        while (current != null) {
            if (current.value.equals(item)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return;
            }
            previous = current;
            current = current.next;
        }
        // item not present — no-op, matches void return contract
    }

    public int size() {
        return size;
    }

    private int indexFor(T item, int cap) {
        return (item.hashCode() & 0x7fffffff) % cap;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Node<T>[] newBuckets = (Node<T>[]) new Node[newCapacity];
        for (int i = 0; i < capacity; i++) {
            Node<T> current = buckets[i];
            while (current != null) {
                Node<T> next = current.next;
                int index = indexFor(current.value, newCapacity);
                current.next = newBuckets[index];
                newBuckets[index] = current;
                current = next;
            }
        }
        buckets = newBuckets;
        capacity = newCapacity;
    }
}
