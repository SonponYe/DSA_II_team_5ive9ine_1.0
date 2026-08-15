public class CircularQueue<T> {

    private T[] queue;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.queue = (T[]) new Object[capacity];
        this.front = 0;
        this.rear = 0;
        this.size = 0;
    }

    // Adds an item to the rear of the queue
    public void enqueue(T item) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }

        queue[rear] = item;
        rear = (rear + 1) % capacity;

        size++;
    }

    // Removes and returns the item at the front
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        T item = queue[front];
        queue[front] = null;
        front = (front + 1) % capacity;
        size--;

        return item;
    }

    // Returns the item at the front without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        return queue[front];
    }

    // Checks whether the queue is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Checks whether the queue is full
    public boolean isFull() {
        return size == capacity;
    }

    // Returns the number of items in the queue
    public int size() {
        return size;
    }
}