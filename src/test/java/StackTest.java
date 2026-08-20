import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class StackTest {

    @Test
    void pushAndPop() {
        Stack<String> stack = new Stack<>();

        // normal case: LIFO order
        stack.push("a");
        stack.push("b");
        stack.push("c");
        assertEquals("c", stack.pop());
        assertEquals("b", stack.pop());
        assertEquals("a", stack.pop());

        // boundary case: after popping everything, the stack is empty
        assertTrue(stack.isEmpty());

        // invalid input: popping an empty stack
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    void peek() {
        Stack<String> stack = new Stack<>();
        stack.push("a");
        stack.push("b");

        // normal case: peek returns the top without removing it
        assertEquals("b", stack.peek());
        assertEquals(2, stack.size());
        assertEquals("b", stack.peek());

        // boundary case: single-element stack
        Stack<String> single = new Stack<>();
        single.push("only");
        assertEquals("only", single.peek());

        // invalid input: peeking an empty stack
        Stack<String> empty = new Stack<>();
        assertThrows(NoSuchElementException.class, empty::peek);
    }

    @Test
    void isEmptyAndSize() {
        Stack<Integer> stack = new Stack<>();

        // boundary case: brand new stack is empty
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());

        // normal case: size grows and shrinks with push/pop
        stack.push(1);
        stack.push(2);
        assertFalse(stack.isEmpty());
        assertEquals(2, stack.size());

        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
}
