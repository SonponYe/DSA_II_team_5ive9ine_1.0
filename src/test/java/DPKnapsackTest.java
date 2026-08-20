import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DPKnapsackTest {

    // Plumbing = 2 hours, Structural = 4 hours (DPKnapsack.hoursFor).
    // Value = 5 - urgencyScore, so CRITICAL (1) is worth 4, LOW (4) is worth 1.
    private ServiceRequest request(String id, String category, int urgencyScore) {
        return new ServiceRequest(id, "A", "WORKSHOP", category, "urgency", urgencyScore,
                "2026-08-01T08:00:00", "2026-08-02T08:00:00", "NEW");
    }

    private boolean contains(String[] array, String value) {
        for (String s : array) {
            if (s.equals(value)) return true;
        }
        return false;
    }

    @Test
    void solveMaximisesUrgencyWithinHours() {
        ServiceRequest critical = request("Q1", "Plumbing", 1);      // 2h, value 4
        ServiceRequest low = request("Q2", "Plumbing", 4);           // 2h, value 1
        ServiceRequest structural = request("Q3", "Structural", 2);  // 4h, value 3
        ServiceRequest[] requests = {critical, low, structural};

        // normal case: with only 2 hours, the single most valuable request that fits wins
        String[] chosenTight = DPKnapsack.solve(requests, 2);
        assertEquals(1, chosenTight.length);
        assertEquals("Q1", chosenTight[0]);

        // normal case: with 4 hours, taking both Plumbing requests (value 4+1=5)
        // beats taking the Structural request alone (value 3)
        String[] chosenRoomy = DPKnapsack.solve(requests, 4);
        assertEquals(2, chosenRoomy.length);
        assertTrue(contains(chosenRoomy, "Q1"));
        assertTrue(contains(chosenRoomy, "Q2"));

        // boundary case: zero hours available means nothing can be chosen
        assertEquals(0, DPKnapsack.solve(requests, 0).length);

        // boundary case: an empty request list
        assertEquals(0, DPKnapsack.solve(new ServiceRequest[0], 10).length);

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> DPKnapsack.solve(null, 5));
        assertThrows(IllegalArgumentException.class, () -> DPKnapsack.solve(requests, -1));
        ServiceRequest[] withNull = {critical, null};
        assertThrows(IllegalArgumentException.class, () -> DPKnapsack.solve(withNull, 5));
    }

    @Test
    void optimalValueMatchesDPTable() {
        ServiceRequest critical = request("Q1", "Plumbing", 1);      // 2h, value 4
        ServiceRequest low = request("Q2", "Plumbing", 4);           // 2h, value 1
        ServiceRequest structural = request("Q3", "Structural", 2);  // 4h, value 3
        ServiceRequest[] requests = {critical, low, structural};

        // normal case: matches the hand-worked-out optimum for each capacity
        assertEquals(4, DPKnapsack.getOptimalValue(requests, 2));
        assertEquals(5, DPKnapsack.getOptimalValue(requests, 4));

        // boundary case: getOptimalValue and solve() must agree on how much
        // value the chosen set actually adds up to
        String[] chosen = DPKnapsack.solve(requests, 4);
        int total = 0;
        for (String id : chosen) {
            for (ServiceRequest request : requests) {
                if (request.getRequestId().equals(id)) {
                    total += 5 - request.getUrgencyScore();
                }
            }
        }
        assertEquals(DPKnapsack.getOptimalValue(requests, 4), total);

        // printDPTable is evidence output only (no return value) - just
        // confirm it runs without throwing for the same input.
        assertDoesNotThrow(() -> DPKnapsack.printDPTable(requests, 4));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> DPKnapsack.getOptimalValue(null, 5));
        assertThrows(IllegalArgumentException.class, () -> DPKnapsack.getOptimalValue(requests, -1));
    }
}
