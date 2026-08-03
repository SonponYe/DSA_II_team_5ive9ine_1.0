public class GreedyAssignment {

    // Assign one available resource to one request
    // Uses Graph.getShortestDistance() to find nearest worker
    // Returns the Resource assigned, or null if none available
    public static Resource assignNearest(ServiceRequest request, Resource[] allResources, Graph campusGraph) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Assign all NEW requests greedily, one by one
    // Returns a 2D array: each row is [requestId, resourceId]
    public static String[][] assignAll(ServiceRequest[] requests, Resource[] allResources, Graph campusGraph) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // The counterexample — show where greedy fails
    // Returns a String explaining the scenario and why greedy is suboptimal
    public static String demonstrateCounterexample() {
        throw new UnsupportedOperationException("TODO: implement");
    }
}
