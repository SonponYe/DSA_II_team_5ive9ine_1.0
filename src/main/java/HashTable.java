import java.util.NoSuchElementException;

// Separate-chaining hash table built from scratch (own Entry linked chains,
// plain Entry[] bucket array as raw storage — no java.util.HashMap).
public class HashTable<K, V> {

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
    private int collisionCount;

    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }
        this.capacity = capacity;
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.collisionCount = 0;
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

        if (buckets[index] != null) {
            collisionCount++;
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;

        if ((double) size / capacity > LOAD_FACTOR_THRESHOLD) {
            resize(capacity * 2);
        }
    }

    public V get(K key) {                        // returns null if not found
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

    public V remove(K key) {
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
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        throw new NoSuchElementException("key not found: " + key);
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

    public int size() {
        return size;
    }

    public int getCollisionCount() {              // needed for performance experiment
        return collisionCount;
    }

    private int indexFor(K key, int cap) {
        int hash = key.hashCode() & 0x7fffffff;
        return hash % cap;
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
