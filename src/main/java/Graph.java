// The campus map. Locations are nodes, roads are edges. Campus roads are
// treated as two-way, so every road is stored on both endpoints' adjacency
// lists. Built entirely from the team's own DynamicArray/HashTable/Queue/
// Stack/DisjointSet — no java.util collections.
public class Graph {

    private static class Edge {
        final int to;
        final Road road;

        Edge(int to, Road road) {
            this.to = to;
            this.road = road;
        }
    }

    private final DynamicArray<Location> locations = new DynamicArray<>();
    private final HashTable<String, Integer> locationIndex = new HashTable<>(64);
    private final DynamicArray<DynamicArray<Edge>> adjacency = new DynamicArray<>();
    private final DynamicArray<Road> allRoads = new DynamicArray<>(); // one entry per road, for Kruskal

    public void addLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("location cannot be null");
        }
        locationIndex.put(location.getLocationId(), locations.size());
        locations.add(location);
        adjacency.add(new DynamicArray<>());
    }

    public void addRoad(Road road) {
        if (road == null) {
            throw new IllegalArgumentException("road cannot be null");
        }
        Integer fromIndex = locationIndex.get(road.getFromLocationId());
        Integer toIndex = locationIndex.get(road.getToLocationId());
        if (fromIndex == null || toIndex == null) {
            throw new IllegalArgumentException(
                    "road " + road.getRoadId() + " references a location that was never added");
        }
        adjacency.get(fromIndex).add(new Edge(toIndex, road));
        adjacency.get(toIndex).add(new Edge(fromIndex, road));
        allRoads.add(road);
    }

    private int requireLocationIndex(String locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("locationId cannot be null");
        }
        Integer index = locationIndex.get(locationId);
        if (index == null) {
            throw new IllegalArgumentException("unknown locationId: " + locationId);
        }
        return index;
    }

    // ── printing ─────────────────────────────────────────────────────────

    public void printAdjacencyList() {
        for (int i = 0; i < locations.size(); i++) {
            StringBuilder line = new StringBuilder(locations.get(i).getLocationId()).append(" -> ");
            DynamicArray<Edge> edges = adjacency.get(i);
            for (int j = 0; j < edges.size(); j++) {
                if (j > 0) {
                    line.append(", ");
                }
                line.append(locations.get(edges.get(j).to).getLocationId());
            }
            System.out.println(line);
        }
    }

    public void printAdjacencyMatrix() {
        int n = locations.size();
        double[][] matrix = new double[n][n]; // defaults to 0.0 where there is no direct road
        for (int i = 0; i < n; i++) {
            DynamicArray<Edge> edges = adjacency.get(i);
            for (int j = 0; j < edges.size(); j++) {
                Edge edge = edges.get(j);
                matrix[i][edge.to] = edge.road.effectiveWeight();
            }
        }

        System.out.print("        ");
        for (int i = 0; i < n; i++) {
            System.out.printf("%8s", locations.get(i).getLocationId());
        }
        System.out.println();

        for (int row = 0; row < n; row++) {
            System.out.printf("%8s", locations.get(row).getLocationId());
            for (int col = 0; col < n; col++) {
                System.out.printf("%8.2f", matrix[row][col]);
            }
            System.out.println();
        }
    }

    // ── BFS / DFS ────────────────────────────────────────────────────────

    private String[] toStringArray(DynamicArray<String> values) {
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    public String[] bfs(String startLocationId) {
        int startIndex = requireLocationIndex(startLocationId);
        boolean[] visited = new boolean[locations.size()];
        DynamicArray<String> order = new DynamicArray<>();
        Queue<Integer> queue = new Queue<>();

        visited[startIndex] = true;
        queue.enqueue(startIndex);

        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            order.add(locations.get(current).getLocationId());

            DynamicArray<Edge> edges = adjacency.get(current);
            for (int i = 0; i < edges.size(); i++) {
                int neighbor = edges.get(i).to;
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.enqueue(neighbor);
                }
            }
        }
        return toStringArray(order);
    }

    public String[] dfs(String startLocationId) {
        int startIndex = requireLocationIndex(startLocationId);
        boolean[] visited = new boolean[locations.size()];
        DynamicArray<String> order = new DynamicArray<>();
        Stack<Integer> stack = new Stack<>();

        stack.push(startIndex);

        while (!stack.isEmpty()) {
            int current = stack.pop();
            if (visited[current]) {
                continue; // already visited via a different path before it got popped
            }
            visited[current] = true;
            order.add(locations.get(current).getLocationId());

            DynamicArray<Edge> edges = adjacency.get(current);
            for (int i = 0; i < edges.size(); i++) {
                int neighbor = edges.get(i).to;
                if (!visited[neighbor]) {
                    stack.push(neighbor);
                }
            }
        }
        return toStringArray(order);
    }

    public boolean isFullyConnected(String startLocationId) {
        return bfs(startLocationId).length == locations.size();
    }

    // ── Dijkstra ─────────────────────────────────────────────────────────
    // Simple O(V^2) array-based version: no heap, just scan for the closest
    // unvisited node each round.

    private double[] dijkstraDistances(int startIndex, int[] previous) {
        int n = locations.size();
        double[] distance = new double[n];
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            distance[i] = Double.POSITIVE_INFINITY;
            previous[i] = -1;
        }
        distance[startIndex] = 0.0;

        for (int step = 0; step < n; step++) {
            int current = -1;
            double best = Double.POSITIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                if (!visited[i] && distance[i] < best) {
                    best = distance[i];
                    current = i;
                }
            }
            if (current == -1) {
                break; // everything left is unreachable
            }
            visited[current] = true;

            DynamicArray<Edge> edges = adjacency.get(current);
            for (int i = 0; i < edges.size(); i++) {
                Edge edge = edges.get(i);
                double candidate = distance[current] + edge.road.effectiveWeight();
                if (candidate < distance[edge.to]) {
                    distance[edge.to] = candidate;
                    previous[edge.to] = current;
                }
            }
        }
        return distance;
    }

    public String[] dijkstra(String fromLocationId, String toLocationId) {
        int fromIndex = requireLocationIndex(fromLocationId);
        int toIndex = requireLocationIndex(toLocationId);

        int[] previous = new int[locations.size()];
        double[] distance = dijkstraDistances(fromIndex, previous);

        if (distance[toIndex] == Double.POSITIVE_INFINITY) {
            return new String[0]; // no path exists
        }

        Stack<String> path = new Stack<>(); // walk back from the destination, then reverse
        int step = toIndex;
        while (step != -1) {
            path.push(locations.get(step).getLocationId());
            step = previous[step];
        }

        String[] result = new String[path.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = path.pop();
        }
        return result;
    }

    public double getShortestDistance(String fromLocationId, String toLocationId) {
        int fromIndex = requireLocationIndex(fromLocationId);
        int toIndex = requireLocationIndex(toLocationId);
        int[] previous = new int[locations.size()];
        double[] distance = dijkstraDistances(fromIndex, previous);
        return distance[toIndex];
    }

    // ── MST: Kruskal and Prim ───────────────────────────────────────────

    private Road[] toRoadArray(DynamicArray<Road> values) {
        Road[] result = new Road[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        return result;
    }

    private void sortRoadsByWeight(Road[] roads) { // selection sort, ascending effectiveWeight()
        for (int i = 0; i < roads.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < roads.length; j++) {
                if (roads[j].effectiveWeight() < roads[min].effectiveWeight()) {
                    min = j;
                }
            }
            Road temp = roads[i];
            roads[i] = roads[min];
            roads[min] = temp;
        }
    }

    public Road[] kruskal() {
        Road[] sorted = toRoadArray(allRoads);
        sortRoadsByWeight(sorted);

        DisjointSet sets = new DisjointSet(locations.size());
        DynamicArray<Road> mst = new DynamicArray<>();

        for (Road road : sorted) {
            int fromIndex = locationIndex.get(road.getFromLocationId());
            int toIndex = locationIndex.get(road.getToLocationId());
            if (!sets.connected(fromIndex, toIndex)) {
                sets.union(fromIndex, toIndex);
                mst.add(road);
            }
        }
        return toRoadArray(mst);
    }

    public Road[] prim(String startLocationId) {
        int startIndex = requireLocationIndex(startLocationId);
        int n = locations.size();
        boolean[] inTree = new boolean[n];
        inTree[startIndex] = true;
        DynamicArray<Road> mst = new DynamicArray<>();

        for (int step = 0; step < n - 1; step++) {
            Road bestRoad = null;
            int bestTo = -1;

            for (int from = 0; from < n; from++) {
                if (!inTree[from]) {
                    continue;
                }
                DynamicArray<Edge> edges = adjacency.get(from);
                for (int i = 0; i < edges.size(); i++) {
                    Edge edge = edges.get(i);
                    if (!inTree[edge.to] && (bestRoad == null || edge.road.effectiveWeight() < bestRoad.effectiveWeight())) {
                        bestRoad = edge.road;
                        bestTo = edge.to;
                    }
                }
            }

            if (bestRoad == null) {
                break; // remaining locations are unreachable from the tree built so far
            }
            inTree[bestTo] = true;
            mst.add(bestRoad);
        }
        return toRoadArray(mst);
    }

    public double getMSTCost(Road[] mstEdges) {
        if (mstEdges == null) {
            throw new IllegalArgumentException("mstEdges cannot be null");
        }
        double total = 0.0;
        for (Road road : mstEdges) {
            total += road.effectiveWeight();
        }
        return total;
    }

    // ── timing, for the performance lab ─────────────────────────────────

    public long timeBFS(String startLocationId) {
        long start = System.nanoTime();
        bfs(startLocationId);
        return System.nanoTime() - start;
    }

    public long timeDFS(String startLocationId) {
        long start = System.nanoTime();
        dfs(startLocationId);
        return System.nanoTime() - start;
    }

    public long timeDijkstra(String from, String to) {
        long start = System.nanoTime();
        dijkstra(from, to);
        return System.nanoTime() - start;
    }

    public long timeKruskal() {
        long start = System.nanoTime();
        kruskal();
        return System.nanoTime() - start;
    }

    public long timePrim(String startLocationId) {
        long start = System.nanoTime();
        prim(startLocationId);
        return System.nanoTime() - start;
    }
}
