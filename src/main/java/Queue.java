public class Queue<T> {

    
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

    // Adds an item to the rear of the queue
    public void enqueue(T item) {
        Node<T> newNode = new Node<>(item);

        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
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
        front = front.next;
        size--;

        if (size == 0) {
            rear = null;
        }

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

    // Returns the number of items in the queue
    public int size() {
        return size;
    }
}