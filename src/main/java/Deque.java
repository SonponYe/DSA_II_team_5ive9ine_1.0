public class Deque<T> {

    // Node represents one item in the deque
    private static class Node<T> {
        T data;
        Node<T> next;
        Node<T> previous;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> front;
    private Node<T> rear;
    private int size;

    // Adds an item to the front.
    // Critical requests use this.
    public void addFront(T item) {
        Node<T> newNode = new Node<>(item);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            newNode.next = front;
            front.previous = newNode;
            front = newNode;
        }

        size++;
    }

    // Adds an item to the rear.
    // Normal requests use this.
    public void addRear(T item) {
        Node<T> newNode = new Node<>(item);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            newNode.previous = rear;
            rear.next = newNode;
            rear = newNode;
        }

        size++;
    }

    // Removes and returns the item at the front.
    public T removeFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        T item = front.data;

        front = front.next;

        if (front == null) {
            rear = null;
        } else {
            front.previous = null;
        }

        size--;

        return item;
    }

    // Removes and returns the item at the rear.
    public T removeRear() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        T item = rear.data;

        rear = rear.previous;

        if (rear == null) {
            front = null;
        } else {
            rear.next = null;
        }

        size--;

        return item;
    }

    // Returns the item at the front without removing it.
    public T peekFront() {
        if (isEmpty()) {
            throw new IllegalStateException("Deque is empty");
        }

        return front.data;
    }

    // Checks whether the deque is empty.
    public boolean isEmpty() {
        return size == 0;
    }

    // Returns the number of items in the deque.
    public int size() {
        return size;
    }
}