import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GreedyAssignmentTest {

    // A is where the request comes from; B is a nearby worker's home, C is a
    // much farther one.
    private Graph smallGraph() {
        Graph graph = new Graph();
        graph.addLocation(new Location("A", "Source", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("B", "Near Worker", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("C", "Far Worker", "Zone", "Type", 0.0, 0.0));
        graph.addRoad(new Road("R1", "A", "B", 1.0, 1.0, 1.0));   // effective weight 1
        graph.addRoad(new Road("R2", "A", "C", 10.0, 1.0, 1.0));  // effective weight 10
        return graph;
    }

    private ServiceRequest request(String id, String source, String status) {
        return new ServiceRequest(id, source, "WORKSHOP", "Plumbing", "MEDIUM", 3,
                "2026-08-01T08:00:00", "2026-08-02T08:00:00", status);
    }

    private Resource resource(String id, String home, String availability) {
        return new Resource(id, "Plumber", home, 1, availability);
    }

    @Test
    void assignNearestPicksClosestAvailable() {
        Graph graph = smallGraph();
        ServiceRequest request = request("Q1", "A", "NEW");
        Resource near = resource("W1", "B", "AVAILABLE");
        Resource far = resource("W2", "C", "AVAILABLE");

        // normal case: picks the closer worker regardless of array order
        Resource assigned = GreedyAssignment.assignNearest(request, new Resource[]{far, near}, graph);
        assertSame(near, assigned);

        // boundary case: no resource available at all
        Resource busy = resource("W3", "B", "BUSY");
        assertNull(GreedyAssignment.assignNearest(request, new Resource[]{busy}, graph));

        // invalid input
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignNearest(null, new Resource[]{near}, graph));
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignNearest(request, null, graph));
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignNearest(request, new Resource[]{near}, null));
    }

    @Test
    void assignAllReturnsRequestResourcePairs() {
        Graph graph = smallGraph();
        ServiceRequest r1 = request("Q1", "A", "NEW");
        ServiceRequest r2 = request("Q2", "A", "NEW");
        ServiceRequest done = request("Q3", "A", "DONE"); // must be skipped: not NEW
        Resource onlyWorker = resource("W1", "B", "AVAILABLE");

        // normal case: the one available worker goes to the first NEW request
        String[][] assignments = GreedyAssignment.assignAll(
                new ServiceRequest[]{r1, r2, done}, new Resource[]{onlyWorker}, graph);

        assertEquals(1, assignments.length);
        assertEquals("Q1", assignments[0][0]);
        assertEquals("W1", assignments[0][1]);

        // boundary case: after assignAll, the worker is BUSY so it can't be
        // handed out twice within the same run
        assertEquals("BUSY", onlyWorker.getAvailabilityStatus());

        // invalid input
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignAll(null, new Resource[]{onlyWorker}, graph));
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignAll(new ServiceRequest[]{r1}, null, graph));
        assertThrows(IllegalArgumentException.class,
                () -> GreedyAssignment.assignAll(new ServiceRequest[]{r1}, new Resource[]{onlyWorker}, null));
    }

    @Test
    void counterexampleShowsGreedyIsSuboptimal() {
        String explanation = GreedyAssignment.demonstrateCounterexample();

        // normal case: a real, non-trivial explanation is returned
        assertNotNull(explanation);
        assertFalse(explanation.isBlank());
        assertTrue(explanation.toLowerCase().contains("greedy"));
    }
}
