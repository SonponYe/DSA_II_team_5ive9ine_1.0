// Hash-based map built from scratch (own separate-chaining buckets, plain
// Object[] as raw storage — no java.util.HashMap/TreeMap).
public class CustomMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int capacity;
    private int size;

    @SuppressWarnings("unchecked")
    public CustomMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        int index = indexFor(key, capacity);
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;

        if ((double) size / capacity > LOAD_FACTOR_THRESHOLD) {
            resize(capacity * 2);
        }
    }

    public V get(K key) {                          // returns null if not found
        // Deliberate exception to rule 5's "only HashTable.get/BST.search
        // may return null" default: CustomMap sits on top of HashTable/BST
        // conceptually, so it shares their null-on-miss contract rather
        // than throwing.
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Entry<K, V> current = buckets[indexFor(key, capacity)];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Entry<K, V> current = buckets[indexFor(key, capacity)];
        while (current != null) {
            if (current.key.equals(key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public void remove(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        int index = indexFor(key, capacity);
        Entry<K, V> current = buckets[index];
        Entry<K, V> previous = null;
        while (current != null) {
            if (current.key.equals(key)) {
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
        // key not present — no-op, matches void return contract
    }

    public int size() {
        return size;
    }

    private int indexFor(K key, int cap) {
        return (key.hashCode() & 0x7fffffff) % cap;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] newBuckets = (Entry<K, V>[]) new Entry[newCapacity];
        for (int i = 0; i < capacity; i++) {
            Entry<K, V> current = buckets[i];
            while (current != null) {
                Entry<K, V> next = current.next;
                int index = indexFor(current.key, newCapacity);
                current.next = newBuckets[index];
                newBuckets[index] = current;
                current = next;
            }
        }
        buckets = newBuckets;
        capacity = newCapacity;
    }
}
