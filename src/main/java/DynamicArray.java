public class DynamicArray<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] data;
    private int size;
    private boolean traceResize; // when true, resize() prints a trace line

    public DynamicArray() {
        this(DEFAULT_CAPACITY);
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
        this.traceResize = false;
    }

    
    public void setTraceResize(boolean traceResize) {
        this.traceResize = traceResize;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void checkIndexForAccess(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " out of bounds for size " + size);
        }
    }

    private void checkIndexForInsert(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Insert index " + index + " out of bounds for size " + size);
        }
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        checkIndexForAccess(index);
        return (T) data[index];
    }

    public void set(int index, T value) {
        checkIndexForAccess(index);
        data[index] = value;
    }

    
    public void add(T value) {
        insert(size, value);
    }

    
    public void insert(int index, T value) {
        checkIndexForInsert(index);
        ensureCapacity(size + 1);
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = value;
        size++;
    }

    
    @SuppressWarnings("unchecked")
    public T remove(int index) {
        checkIndexForAccess(index);
        T removed = (T) data[index];
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null; // avoid memory leak
        size--;
        return removed;
    }

    
    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= data.length) {
            return;
        }
        int oldCapacity = data.length;
        int newCapacity = Math.max(oldCapacity * 2, minCapacity);
        Object[] newData = new Object[newCapacity];
        // use System.arraycopy for efficient array copy
        if (size > 0) {
            System.arraycopy(data, 0, newData, 0, size);
        }
        if (traceResize) {
            System.out.println("[RESIZE] capacity " + oldCapacity + " -> " + newCapacity
                    + " (size at resize time = " + size + ")");
        }
        data = newData;
    }

    
    public int indexOf(T value) {
        for (int i = 0; i < size; i++) {
            Object element = data[i];
            if (value == null ? element == null : value.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    public boolean contains(T value) {
        return indexOf(value) != -1;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

    
