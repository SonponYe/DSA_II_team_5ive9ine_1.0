
public class Stack<T> {

    /** Internal singly-linked node. Each node only needs to know what's below it. */
    private static class Node<T> {
        final T value;
        Node<T> next;

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public Stack() {
        this.top = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }

    /** Pushes a value onto the top of the stack. O(1). */
    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

   
    public T pop() {
        if (isEmpty()) {
            throw new NoSuchElementException("pop() called on empty stack");
        }
        T value = top.value;
        top = top.next;
        size--;
        return value;
    }

    
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("peek() called on empty stack");
        }
        return top.value;
    }

    
    public void printState(String label) {
        StringBuilder sb = new StringBuilder();
        sb.append(label).append(" | top -> [");
        Node<T> current = top;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("] <- bottom  (size=").append(size).append(")");
        System.out.println(sb);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = top;
        while (current != null) {
            sb.append(current.value);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }

    
    public static void main(String[] args) {
        System.out.println("=== Stack undo-log trace demo (push 3, pop 2, show state) ===");
        Stack<String> undoLog = new Stack<>();

        undoLog.push("SUBMIT Q101 (Mensah Sarbah - flooded bathroom)");
        undoLog.printState("After push 1");

        undoLog.push("ASSIGN W014 to Q101");
        undoLog.printState("After push 2");

        undoLog.push("STATUS Q101 -> IN_PROGRESS");
        undoLog.printState("After push 3");

        String undone1 = undoLog.pop();
        System.out.println("Undo -> reverted: " + undone1);
        undoLog.printState("After pop 1");

        String undone2 = undoLog.pop();
        System.out.println("Undo -> reverted: " + undone2);
        undoLog.printState("After pop 2 (final state)");

        System.out.println("\npeek() now = " + undoLog.peek());
        System.out.println("isEm
}
