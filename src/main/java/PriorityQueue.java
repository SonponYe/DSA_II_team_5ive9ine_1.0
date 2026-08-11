import java.util.NoSuchElementException;

// Min-Heap keyed on ServiceRequest urgency (CRITICAL=1 ... LOW=4).
// Backed by G2's DynamicArray<ServiceRequest> per METHOD_SIGNATURES.md.
//
// NOTE (G3, 2026-08-07): DynamicArray.java is still an unimplemented stub
// (every method throws UnsupportedOperationException) at the time this class
// was written. This class compiles and its logic is complete, but
// PriorityQueueTest will fail at runtime until G2 delivers a working
// DynamicArray. Flagged to the team lead — chase G2.
public class PriorityQueue {

    private final DynamicArray<ServiceRequest> heap = new DynamicArray<>();

    public void insert(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        heap.add(request);
        siftUp(heap.size() - 1);
    }

    public ServiceRequest extractMin() {        // removes and returns most urgent
        if (isEmpty()) {
            throw new NoSuchElementException("cannot extractMin from an empty PriorityQueue");
        }
        ServiceRequest min = heap.get(0);
        int lastIndex = heap.size() - 1;
        ServiceRequest last = heap.remove(lastIndex);
        if (lastIndex > 0) {
            heap.set(0, last);
            siftDown(0);
        }
        return min;
    }

    public ServiceRequest peek() {               // returns most urgent, does NOT remove
        if (isEmpty()) {
            throw new NoSuchElementException("cannot peek an empty PriorityQueue");
        }
        return heap.get(0);
    }

    public boolean isEmpty() {
        return heap.isEmpty();
    }

    public int size() {
        return heap.size();
    }

    public void heapify(ServiceRequest[] requests) { // bulk load
        if (requests == null) {
            throw new IllegalArgumentException("requests cannot be null");
        }
        heap.clear();
        for (ServiceRequest request : requests) {
            if (request == null) {
                throw new IllegalArgumentException("requests cannot contain null elements");
            }
            heap.add(request);
        }
        for (int i = parent(heap.size() - 1); i >= 0; i--) {
            siftDown(i);
        }
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = parent(index);
            if (isMoreUrgent(heap.get(index), heap.get(parentIndex))) {
                swap(index, parentIndex);
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    private void siftDown(int index) {
        int size = heap.size();
        while (true) {
            int left = left(index);
            int right = right(index);
            int mostUrgent = index;

            if (left < size && isMoreUrgent(heap.get(left), heap.get(mostUrgent))) {
                mostUrgent = left;
            }
            if (right < size && isMoreUrgent(heap.get(right), heap.get(mostUrgent))) {
                mostUrgent = right;
            }
            if (mostUrgent == index) {
                break;
            }
            swap(index, mostUrgent);
            index = mostUrgent;
        }
    }

    private boolean isMoreUrgent(ServiceRequest a, ServiceRequest b) {
        return a.getUrgencyScore() < b.getUrgencyScore();
    }

    private void swap(int i, int j) {
        ServiceRequest temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    private int parent(int index) {
        return (index - 1) / 2;
    }

    private int left(int index) {
        return 2 * index + 1;
    }

    private int right(int index) {
        return 2 * index + 2;
    }
}
