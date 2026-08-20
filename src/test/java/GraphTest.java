import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    // A small campus: A-B-C form a connected triangle (a cycle), C-D hangs
    // off it, and E is deliberately left disconnected from everything else.
    //   A --1-- B
    //    \      |
    //     5     1
    //      \    |
    //        C -+-- 1 -- D
    //   E (isolated)
    private Graph fixture() {
        Graph graph = new Graph();
        graph.addLocation(new Location("A", "A", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("B", "B", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("C", "C", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("D", "D", "Zone", "Type", 0.0, 0.0));
        graph.addLocation(new Location("E", "E", "Zone", "Type", 0.0, 0.0));

        graph.addRoad(new Road("R1", "A", "B", 1.0, 1.0, 1.0));  // effective weight 1
        graph.addRoad(new Road("R2", "B", "C", 1.0, 1.0, 1.0));  // effective weight 1
        graph.addRoad(new Road("R3", "A", "C", 5.0, 1.0, 1.0));  // effective weight 5, closes the triangle
        graph.addRoad(new Road("R4", "C", "D", 1.0, 1.0, 1.0));  // effective weight 1
        return graph;
    }

    private boolean contains(String[] array, String value) {
        for (String s : array) {
            if (s.equals(value)) return true;
        }
        return false;
    }

    @Test
    void bfsReachability() {
        Graph graph = fixture();

        // normal case: BFS from A reaches everything except the isolated E
        String[] reached = graph.bfs("A");
        assertEquals(4, reached.length);
        assertEquals("A", reached[0]);
        assertTrue(contains(reached, "B"));
        assertTrue(contains(reached, "C"));
        assertTrue(contains(reached, "D"));
        assertFalse(contains(reached, "E"));

        // boundary case: a node with no roads only visits itself
        assertArrayEquals(new String[]{"E"}, graph.bfs("E"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> graph.bfs("NOWHERE"));
    }

    @Test
    void dfsConnectivityAndCycleDetection() {
        Graph graph = fixture();

        // normal case: DFS reaches the same set BFS does, despite A-B-C-A
        // being a cycle (DFS must not loop forever or revisit nodes)
        String[] reached = graph.dfs("A");
        assertEquals(4, reached.length);
        assertTrue(contains(reached, "A"));
        assertTrue(contains(reached, "B"));
        assertTrue(contains(reached, "C"));
        assertTrue(contains(reached, "D"));

        // isFullyConnected reflects that E is unreachable from A
        assertFalse(graph.isFullyConnected("A"));

        // boundary case: a graph where every node is reachable
        Graph connected = new Graph();
        connected.addLocation(new Location("X", "X", "Zone", "Type", 0.0, 0.0));
        connected.addLocation(new Location("Y", "Y", "Zone", "Type", 0.0, 0.0));
        connected.addRoad(new Road("RX", "X", "Y", 1.0, 1.0, 1.0));
        assertTrue(connected.isFullyConnected("X"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> graph.dfs("NOWHERE"));
    }

    @Test
    void dijkstraShortestPath() {
        Graph graph = fixture();

        // normal case: A -> D goes via B and C (cost 3), not the direct A-C
        // edge (which alone costs 5 and still needs +1 more to reach D)
        String[] path = graph.dijkstra("A", "D");
        assertArrayEquals(new String[]{"A", "B", "C", "D"}, path);
        assertEquals(3.0, graph.getShortestDistance("A", "D"), 0.0001);

        // boundary case: shortest path from a location to itself
        assertArrayEquals(new String[]{"A"}, graph.dijkstra("A", "A"));
        assertEquals(0.0, graph.getShortestDistance("A", "A"), 0.0001);

        // boundary case: no path exists to the isolated location
        assertEquals(0, graph.dijkstra("A", "E").length);
        assertEquals(Double.POSITIVE_INFINITY, graph.getShortestDistance("A", "E"));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> graph.dijkstra("NOWHERE", "A"));
        assertThrows(IllegalArgumentException.class, () -> graph.dijkstra("A", "NOWHERE"));
    }

    @Test
    void kruskalMst() {
        Graph graph = fixture();

        // normal case: connects A, B, C, D with the 3 cheapest non-cycle
        // edges (A-B, B-C, C-D, each weight 1) and skips the expensive A-C
        Road[] mst = graph.kruskal();
        assertEquals(3, mst.length);
        assertEquals(3.0, graph.getMSTCost(mst), 0.0001);
    }

    @Test
    void primMst() {
        Graph graph = fixture();

        // normal case: starting from A, Prim finds the same minimum total
        // cost as Kruskal
        Road[] mst = graph.prim("A");
        assertEquals(3, mst.length);
        assertEquals(3.0, graph.getMSTCost(mst), 0.0001);

        // boundary case: starting from a node with no roads produces an
        // empty tree rather than throwing
        assertEquals(0, graph.prim("E").length);

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> graph.prim("NOWHERE"));
    }

    @Test
    void mstCostMatchesEffectiveWeightSum() {
        Graph graph = fixture();
        Road[] mst = graph.kruskal();

        // normal case: getMSTCost is just the sum of each edge's effectiveWeight()
        double expected = 0.0;
        for (Road road : mst) {
            expected += road.effectiveWeight();
        }
        assertEquals(expected, graph.getMSTCost(mst), 0.0001);

        // boundary case: an empty MST costs nothing
        assertEquals(0.0, graph.getMSTCost(new Road[0]), 0.0001);

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> graph.getMSTCost(null));
    }
}
