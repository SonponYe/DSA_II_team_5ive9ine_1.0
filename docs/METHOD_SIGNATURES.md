# Method Signatures Agreement
## UG Campus Maintenance Service Operations Optimizer
### READ THIS BEFORE WRITING ANY CODE

This document lists the exact method names, parameters, and return types
every group must implement. If your group's method is in this list, build
it exactly as written. If another group calls your method, they are using
the name written here — changing it breaks their code.

**Last updated: Week 1, Day 1**
**Owner: G1 Lead (update and re-share if anything changes)**

---

## G1 — DatabaseManager.java

Every group calls these methods to read from and write to the database.
G1 must deliver this class by end of Day 2, Week 1.

```java
// Load all data from database into arrays/lists
public Location[]       getAllLocations();
public Road[]           getAllRoads();
public ServiceRequest[] getAllRequests();
public Resource[]       getAllResources();

// Save a new request submitted from the console menu
public void saveRequest(ServiceRequest request);

// Update a request's status — called when dispatched or completed
public void updateRequestStatus(String requestId, String newStatus);

// Update a resource's availability — called when assigned or freed
public void updateResourceStatus(String resourceId, String newStatus);

// Save one performance experiment result
public void saveAlgorithmRun(String algorithmName, int inputSize,
                             long timeNs, double memoryKb);

// Save one audit event to the audit_events table
public void saveAuditEvent(String eventType, String description,
                           String performedBy);

// Export algorithm_runs table to CSV for graphing
public void exportAlgorithmRunsToCSV(String filename);
```

---

## G2 — Linear Data Structures

These are the building blocks. G3 uses DynamicArray for the heap.
G5 uses Stack for iterative DFS. G6 uses LinkedList for the audit log.

```java
// ── DynamicArray.java ──────────────────────────────────────────
public class DynamicArray<T> {
    public void   add(T item);              // add to end
    public void   add(int index, T item);   // insert at index
    public T      get(int index);
    public void   set(int index, T item);
    public T      remove(int index);
    public int    size();
    public boolean isEmpty();
    public void   clear();
}

// ── LinkedList.java ────────────────────────────────────────────
public class LinkedList<T> implements Iterable<T> {
    public void addFirst(T item);
    public void addLast(T item);
    public void insertAfter(T target, T newItem);
    public T    removeFirst();
    public T    removeLast();
    public boolean remove(T item);
    public T    getFirst();
    public int  size();
    public boolean isEmpty();
    public Iterator<T> iterator();   // enables for-each loops
}

// ── Stack.java ─────────────────────────────────────────────────
public class Stack<T> {
    public void push(T item);
    public T    pop();
    public T    peek();
    public boolean isEmpty();
    public int  size();
}

// ── Queue.java ─────────────────────────────────────────────────
public class Queue<T> {
    public void enqueue(T item);
    public T    dequeue();
    public T    peek();
    public boolean isEmpty();
    public int  size();
}

// ── CircularQueue.java ─────────────────────────────────────────
public class CircularQueue<T> {
    public CircularQueue(int capacity);  // fixed size
    public void enqueue(T item);         // wraps around when full
    public T    dequeue();
    public T    peek();
    public boolean isEmpty();
    public boolean isFull();
    public int  size();
}

// ── Deque.java ─────────────────────────────────────────────────
public class Deque<T> {
    public void addFront(T item);        // CRITICAL requests use this
    public void addRear(T item);         // normal requests use this
    public T    removeFront();
    public T    removeRear();
    public T    peekFront();
    public boolean isEmpty();
    public int  size();
}
```

---

## G3 — Priority Queue, Trees & Hash Table

The PriorityQueue is the most important class in the system.
G6 calls it when dispatching requests. G4 calls HashTable for lookups.

```java
// ── PriorityQueue.java (Min-Heap) ──────────────────────────────
public class PriorityQueue {
    public void           insert(ServiceRequest request);
    public ServiceRequest extractMin();      // removes and returns most urgent
    public ServiceRequest peek();            // returns most urgent, does NOT remove
    public boolean        isEmpty();
    public int            size();
    public void           heapify(ServiceRequest[] requests); // bulk load
}

// ── HashTable.java ─────────────────────────────────────────────
public class HashTable<K, V> {
    public HashTable(int capacity);
    public void put(K key, V value);
    public V    get(K key);               // returns null if not found
    public V    remove(K key);
    public boolean containsKey(K key);
    public int  size();
    public int  getCollisionCount();      // needed for performance experiment
}

// ── BST.java ───────────────────────────────────────────────────
public class BST<T extends Comparable<T>> {
    public void insert(T item);
    public T    search(T item);           // returns null if not found
    public void delete(T item);
    public void inorderTraversal();       // prints nodes in sorted order
    public int  height();
    public boolean isEmpty();
}

// ── BalancedBST.java (Red-Black or AVL) ───────────────────────
public class BalancedBST<T extends Comparable<T>> {
    public void insert(T item);
    public T    search(T item);
    public void delete(T item);
    public void inorderTraversal();
    public int  height();                 // must stay low — show vs BST
}

// ── BTree.java (or BTreeIndex.java) ───────────────────────────
public class BTree {
    public BTree(int order);             // e.g. order 3 = max 2 keys per node
    public void   insert(String key, ServiceRequest value);
    public ServiceRequest search(String key);
    public void   printTree();           // show node structure for evidence
}

// ── CustomSet.java ─────────────────────────────────────────────
public class CustomSet<T> {
    public void    add(T item);
    public boolean contains(T item);
    public void    remove(T item);
    public int     size();
}

// ── CustomMap.java ─────────────────────────────────────────────
public class CustomMap<K, V> {
    public void put(K key, V value);
    public V    get(K key);
    public boolean containsKey(K key);
    public void remove(K key);
    public int  size();
}
```

---

## G4 — Searching & Sorting

G6 calls these for the performance experiments.
All methods take ServiceRequest arrays — use G1's class directly.

```java
// ── SearchEngine.java ──────────────────────────────────────────
public class SearchEngine {

    // Search through ALL requests — does NOT require sorted input
    public static ServiceRequest[] linearSearch(
        ServiceRequest[] requests,
        String field,     // "urgency", "category", "sourceLocationId", "status"
        String value      // e.g. "CRITICAL", "Plumbing", "L001", "NEW"
    );

    // Search by requestId only — array MUST be sorted by requestId first
    public static ServiceRequest binarySearch(
        ServiceRequest[] sortedRequests,
        String requestId
    );

    // For performance lab — returns time taken in nanoseconds
    public static long timeLinearSearch(ServiceRequest[] requests,
                                        String field, String value);
    public static long timeBinarySearch(ServiceRequest[] sortedRequests,
                                        String requestId);
}

// ── SortEngine.java ────────────────────────────────────────────
public class SortEngine {

    // All sort methods take an array and a sort key
    // sortBy: "urgencyScore", "deadline", "timeSubmitted", "requestId"
    public static void selectionSort(ServiceRequest[] arr, String sortBy);
    public static void insertionSort(ServiceRequest[] arr, String sortBy);
    public static void mergeSort    (ServiceRequest[] arr, String sortBy);
    public static void quickSort    (ServiceRequest[] arr, String sortBy);

    // For performance lab — sorts a copy, returns time in nanoseconds
    public static long timeSort(String algorithm,
                                ServiceRequest[] arr,
                                String sortBy);
    // algorithm values: "selection", "insertion", "merge", "quick"
}
```

---

## G5 — Graph & Routing

G6 calls getShortestDistance() for the Greedy algorithm.
The console menu calls all public methods directly.

```java
// ── Graph.java ─────────────────────────────────────────────────
public class Graph {

    // Build the graph from loaded data
    public void addLocation(Location location);
    public void addRoad(Road road);

    // Print both representations (needed as evidence)
    public void printAdjacencyList();
    public void printAdjacencyMatrix();

    // BFS — returns locations in order visited
    public String[] bfs(String startLocationId);

    // DFS — returns locations in order visited
    public String[] dfs(String startLocationId);

    // Returns true if every location is reachable from startLocationId
    public boolean isFullyConnected(String startLocationId);

    // Dijkstra — returns the shortest path as an ordered array of location IDs
    // e.g. ["L036", "L046", "L035", "L001"]
    public String[] dijkstra(String fromLocationId, String toLocationId);

    // Returns total effective cost of the shortest path (for display)
    public double   getShortestDistance(String fromLocationId,
                                        String toLocationId);

    // MST algorithms — return list of roads in the MST
    public Road[] kruskal();
    public Road[] prim(String startLocationId);

    // Total cost of an MST result — sum of effectiveWeight() of all edges
    public double getMSTCost(Road[] mstEdges);

    // For performance lab — returns time in nanoseconds
    public long timeBFS      (String startLocationId);
    public long timeDFS      (String startLocationId);
    public long timeDijkstra (String from, String to);
    public long timeKruskal  ();
    public long timePrim     (String startLocationId);
}

// ── DisjointSet.java ───────────────────────────────────────────
public class DisjointSet {
    public DisjointSet(int size);
    public void makeSet(int element);
    public int  find(int element);            // with path compression
    public void union(int elementA, int elementB);  // by rank
    public boolean connected(int elementA, int elementB);
}
```

---

## G6 — Optimisation, Testing & Report

```java
// ── GreedyAssignment.java ──────────────────────────────────────
public class GreedyAssignment {

    // Assign one available resource to one request
    // Uses Graph.getShortestDistance() to find nearest worker
    // Returns the Resource assigned, or null if none available
    public static Resource assignNearest(ServiceRequest request,
                                         Resource[]     allResources,
                                         Graph          campusGraph);

    // Assign all NEW requests greedily, one by one
    // Returns a 2D array: each row is [requestId, resourceId]
    public static String[][] assignAll(ServiceRequest[] requests,
                                       Resource[]       allResources,
                                       Graph            campusGraph);

    // The counterexample — show where greedy fails
    // Returns a String explaining the scenario and why greedy is suboptimal
    public static String demonstrateCounterexample();
}

// ── DPKnapsack.java ────────────────────────────────────────────
public class DPKnapsack {

    // Solve the shift optimisation problem
    // Each request has a time cost (hours) and urgency value (points)
    // totalHours = total staff-hours available this shift
    // Returns the optimal selection of request IDs
    public static String[] solve(ServiceRequest[] requests, int totalHours);

    // Print the full DP table — required as evidence
    // Rows = requests, Columns = remaining hours 0..totalHours
    public static void printDPTable(ServiceRequest[] requests, int totalHours);

    // Return total urgency points of the optimal solution
    public static int getOptimalValue(ServiceRequest[] requests, int totalHours);
}
```

---

## Summary — Who Delivers What and When

| Class | Group | Due |
|-------|-------|-----|
| ServiceRequest.java | G1 | Day 1, Week 1 |
| Location.java | G1 | Day 1, Week 1 |
| Resource.java | G1 | Day 1, Week 1 |
| Road.java | G1 | Day 1, Week 1 |
| DatabaseManager.java | G1 | Day 2, Week 1 |
| DynamicArray.java | G2 | Day 3, Week 1 |
| LinkedList.java | G2 | Day 3, Week 1 |
| Stack.java | G2 | Day 4, Week 1 |
| Queue.java | G2 | Day 4, Week 1 |
| CircularQueue.java | G2 | Day 5, Week 1 |
| Deque.java | G2 | Day 2, Week 2 |
| PriorityQueue.java | G3 | Day 3, Week 2 |
| HashTable.java | G3 | Day 4, Week 2 |
| BST.java | G3 | Day 2, Week 3 |
| BalancedBST.java | G3 | Day 3, Week 3 |
| BTree.java | G3 | Day 4, Week 3 |
| CustomSet.java | G3 | Day 4, Week 3 |
| CustomMap.java | G3 | Day 4, Week 3 |
| SearchEngine.java | G4 | Day 3, Week 2 |
| SortEngine.java | G4 | Day 5, Week 2 |
| Graph.java | G5 | Day 3, Week 2 |
| DisjointSet.java | G5 | Day 4, Week 2 |
| GreedyAssignment.java | G6 | Day 2, Week 3 |
| DPKnapsack.java | G6 | Day 4, Week 3 |

---

## Golden Rules

1. **Never change a method signature after other groups have started using it.**
   If you must change something, tell the whole team first.

2. **Never return null silently.** If a search finds nothing, return an
   empty array. If a dispatch finds no available worker, return null AND
   print a clear message. Null pointer crashes during the demo lose marks.

3. **All classes go in the same default package** (no package declarations)
   unless the team agrees otherwise. Keep it simple.

4. **Test your class before handing it off.** If G2 gives G3 a broken
   DynamicArray, G3 loses a week. Run your own unit tests first.

5. **Field names in ServiceRequest, Location, Resource, Road are frozen.**
   G1 owns them. Everyone else reads from them. No one edits them.
