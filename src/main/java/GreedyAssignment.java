public class GreedyAssignment {

    // Assign one available resource to one request
    // Uses Graph.getShortestDistance() to find nearest worker
    // Returns the Resource assigned, or null if none available
    public static Resource assignNearest(ServiceRequest request, Resource[] allResources, Graph campusGraph) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (allResources == null) {
            throw new IllegalArgumentException("allResources cannot be null");
        }
        if (campusGraph == null) {
            throw new IllegalArgumentException("campusGraph cannot be null");
        }

        Resource nearest = null;
        double nearestDistance = Double.POSITIVE_INFINITY;

        for (Resource resource : allResources) {
            if (resource == null || !"AVAILABLE".equals(resource.getAvailabilityStatus())) {
                continue;
            }
            double distance = campusGraph.getShortestDistance(resource.getHomeLocationId(), request.getSourceLocationId());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = resource;
            }
        }
        return nearest; // null: no resource is both available and reachable
    }

    // Assign all NEW requests greedily, one by one
    // Returns a 2D array: each row is [requestId, resourceId]
    public static String[][] assignAll(ServiceRequest[] requests, Resource[] allResources, Graph campusGraph) {
        if (requests == null) {
            throw new IllegalArgumentException("requests cannot be null");
        }
        if (allResources == null) {
            throw new IllegalArgumentException("allResources cannot be null");
        }
        if (campusGraph == null) {
            throw new IllegalArgumentException("campusGraph cannot be null");
        }

        DynamicArray<String[]> assignments = new DynamicArray<>();

        for (ServiceRequest request : requests) {
            if (request == null || !"NEW".equals(request.getStatus())) {
                continue;
            }
            Resource assigned = assignNearest(request, allResources, campusGraph);
            if (assigned != null) {
                assignments.add(new String[]{request.getRequestId(), assigned.getResourceId()});
                // Greedy: taken immediately so the next request in this loop can't double-book it.
                assigned.setAvailabilityStatus("BUSY");
            }
        }

        String[][] result = new String[assignments.size()][];
        for (int i = 0; i < assignments.size(); i++) {
            result[i] = assignments.get(i);
        }
        return result;
    }

    // The counterexample — show where greedy fails
    // Returns a String explaining the scenario and why greedy is suboptimal
    public static String demonstrateCounterexample() {
        return "Counterexample: request R1 (LOW urgency) is at location A, next to worker W1 (also at A). "
                + "Request R2 (CRITICAL urgency) is at location B, reachable only by W1 or by W2, who is much "
                + "further away. If R1 happens to be processed first, greedy assigns the nearby W1 to it purely "
                + "because it minimises distance for that one request. When R2 is processed next, W1 is already "
                + "BUSY, so the far-away W2 must be sent to the CRITICAL job instead. Total distance travelled "
                + "and the wait time for the more urgent request are both worse than if W1 had gone to R2 and W2 "
                + "to R1. Greedy nearest-worker assignment only ever looks at distance for the request currently "
                + "being processed; it never looks ahead at which future request most needs that worker, so a "
                + "planner that considers urgency alongside distance can always beat it.";
    }
}
