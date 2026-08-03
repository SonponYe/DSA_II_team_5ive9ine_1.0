public class Graph {

    // Build the graph from loaded data
    public void addLocation(Location location) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public void addRoad(Road road) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Print both representations (needed as evidence)
    public void printAdjacencyList() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public void printAdjacencyMatrix() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // BFS — returns locations in order visited
    public String[] bfs(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // DFS — returns locations in order visited
    public String[] dfs(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Returns true if every location is reachable from startLocationId
    public boolean isFullyConnected(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Dijkstra — returns the shortest path as an ordered array of location IDs
    // e.g. ["L036", "L046", "L035", "L001"]
    public String[] dijkstra(String fromLocationId, String toLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Returns total effective cost of the shortest path (for display)
    public double getShortestDistance(String fromLocationId, String toLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // MST algorithms — return list of roads in the MST
    public Road[] kruskal() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public Road[] prim(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // Total cost of an MST result — sum of effectiveWeight() of all edges
    public double getMSTCost(Road[] mstEdges) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    // For performance lab — returns time in nanoseconds
    public long timeBFS(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public long timeDFS(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public long timeDijkstra(String from, String to) {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public long timeKruskal() {
        throw new UnsupportedOperationException("TODO: implement");
    }

    public long timePrim(String startLocationId) {
        throw new UnsupportedOperationException("TODO: implement");
    }
}
