import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

// Min-Heap keyed on ServiceRequest urgency (CRITICAL=1 ... LOW=4).
public class PriorityQueue {

    private final List<ServiceRequest> heap = new ArrayList<>();

    public void insert(ServiceRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        heap.add(request);
        siftUp(heap.size() - 1);
    }

    public ServiceRequest extractMin() {        // removes and returns most urgent
        if (heap.isEmpty()) {
            throw new NoSuchElementException("PriorityQueue is empty");
        }
        ServiceRequest result = heap.get(0);
        ServiceRequest last = heap.remove(heap.size() - 1);
        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }
        return result;
    }

    public ServiceRequest peek() {               // returns most urgent, does NOT remove
        if (heap.isEmpty()) {
            throw new NoSuchElementException("PriorityQueue is empty");
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
        Objects.requireNonNull(requests, "requests must not be null");
        heap.clear();
        for (ServiceRequest request : requests) {
            Objects.requireNonNull(request, "request array must not contain null elements");
            heap.add(request);
        }
        for (int index = parentIndex(heap.size() - 1); index >= 0; index--) {
            siftDown(index);
        }
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = parentIndex(index);
            if (compare(heap.get(index), heap.get(parent)) >= 0) {
                break;
            }
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        int size = heap.size();
        while (true) {
            int left = leftChildIndex(index);
            int right = rightChildIndex(index);
            int smallest = index;

            if (left < size && compare(heap.get(left), heap.get(smallest)) < 0) {
                smallest = left;
            }
            if (right < size && compare(heap.get(right), heap.get(smallest)) < 0) {
                smallest = right;
            }
            if (smallest == index) {
                break;
            }
            swap(index, smallest);
            index = smallest;
        }
    }

    private int compare(ServiceRequest left, ServiceRequest right) {
        return Integer.compare(left.getUrgencyScore(), right.getUrgencyScore());
    }

    private int parentIndex(int index) {
        return (index - 1) / 2;
    }

    private int leftChildIndex(int index) {
        return index * 2 + 1;
    }

    private int rightChildIndex(int index) {
        return index * 2 + 2;
    }

    private void swap(int first, int second) {
        ServiceRequest tmp = heap.get(first);
        heap.set(first, heap.get(second));
        heap.set(second, tmp);
    }
}
