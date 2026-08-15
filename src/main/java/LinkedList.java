import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedList<T> implements Iterable<T> {

    // Node class
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    // Adds an item to the beginning of the list
    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item);

        newNode.next = head;
        head = newNode;

        if (tail == null) {
            tail = newNode;
        }
        size++;
    }

    // Adds an item to the end of the list
    public void addLast(T item) {
        Node<T> newNode = new Node<>(item);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }

        size++;
    }

    // Inserts newItem after the first occurrence of target
    public void insertAfter(T target, T newItem) {
        Node<T> current = head;

        while (current != null) {

            if ((target == null && current.data == null) ||
                (target != null && target.equals(current.data))) {

                Node<T> newNode = new Node<>(newItem);

                newNode.next = current.next;
                current.next = newNode;

                if (current == tail) {
                    tail = newNode;
                }

                size++;
                return;
            }

            current = current.next;
        }

        throw new NoSuchElementException("Target not found");
    }

    // Removes and returns the first item
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }

        T item = head.data;

        head = head.next;
        size--;

        if (size == 0) {
            tail = null;
        }

        return item;
    }

    // Removes and returns the last item
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }

        if (head == tail) {
            T item = head.data;

            head = null;
            tail = null;
            size--;

            return item;
        }

        Node<T> current = head;

        while (current.next != tail) {
            current = current.next;
        }

        T item = tail.data;
        tail = current;
        tail.next = null;
        size--;

        return item;
    }

    // Removes the first occurrence of item
    public boolean remove(T item) {
        if (isEmpty()) {
            return false;
        }

        if ((item == null && head.data == null) ||
            (item != null && item.equals(head.data))) {

            removeFirst();
            return true;
        }

        Node<T> current = head;

        while (current.next != null) {

            if ((item == null && current.next.data == null) ||
                (item != null && item.equals(current.next.data))) {

                if (current.next == tail) {
                    tail = current;
                }

                current.next = current.next.next;
                size--;

                return true;
            }

            current = current.next;
        }

        return false;
    }

    // Returns the first item without removing it
    public T getFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("List is empty");
        }

        return head.data;
    }

    // Returns the number of items
    public int size() {
        return size;
    }

    // Checks whether the list is empty
    public boolean isEmpty() {
        return size == 0;
    }

    // Enables for-each loops
    @Override
    public Iterator<T> iterator() {

        return new Iterator<T>() {

            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T item = current.data;
                current = current.next;

                return item;
            }
        };
    }
}