public class CircularQueue<T> {

    // Node represents one item in the circular queue
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;
    private final int capacity;

    public CircularQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }

        this.capacity = capacity;
        this.front = null;
        this.rear = null;
        this.size = 0;
    }

    // Adds an item to the rear of the queue
    public void enqueue(T item) {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }

        Node<T> newNode = new Node<>(item);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;

            rear.next = front;
        } else {
            newNode.next = front;

            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    // Removes and returns the item at the front
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        T item = front.data;

        if (size == 1) {
            front = null;
            rear = null;
        } else {
            front = front.next;
            rear.next = front;
        }

        size--;

        return item;
    }

    // Returns the item at the front without removing it
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }

        return front.data;
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