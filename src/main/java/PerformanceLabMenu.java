import java.util.Scanner;

// Section 6: timed experiments, exports via DatabaseManager.exportAlgorithmRunsToCSV.
public class PerformanceLabMenu {

    private final Scanner scanner;
    private final DatabaseManager db;
    private final Graph graph;

    public PerformanceLabMenu(Scanner scanner, DatabaseManager db, Graph graph) {
        this.scanner = scanner;
        this.db = db;
        this.graph = graph;
    }

    public void run() {
        System.out.println();
        System.out.println("-- PERFORMANCE LAB --");
        System.out.println("a. Search comparison experiment");
        System.out.println("b. Sorting comparison experiment");
        System.out.println("c. Graph algorithm experiment");
        System.out.println("d. Export results to CSV");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": searchExperiment(); break;
            case "b": sortExperiment(); break;
            case "c": graphExperiment(); break;
            case "d": exportResults(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    // A rough, simple memory reading: current heap usage right after the
    // timed call. Not a precise per-call delta, but enough to plot a trend.
    private double measureMemoryKb() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / 1024.0;
    }

    private void searchExperiment() {
        ServiceRequest[] requests = db.getAllRequests();
        SortEngine.mergeSort(requests, "requestId");

        long linearTime = SearchEngine.timeLinearSearch(requests, "status", "NEW");
        double linearMemory = measureMemoryKb();
        db.saveAlgorithmRun("linearSearch", requests.length, linearTime, linearMemory);
        System.out.println("Linear search: " + linearTime + " ns");

        if (requests.length > 0) {
            long binaryTime = SearchEngine.timeBinarySearch(requests, requests[0].getRequestId());
            double binaryMemory = measureMemoryKb();
            db.saveAlgorithmRun("binarySearch", requests.length, binaryTime, binaryMemory);
            System.out.println("Binary search: " + binaryTime + " ns");
        }
    }

    private void sortExperiment() {
        String[] algorithms = {"selection", "insertion", "merge", "quick"};
        for (String algorithm : algorithms) {
            ServiceRequest[] requests = db.getAllRequests(); // fresh copy per algorithm
            long time = SortEngine.timeSort(algorithm, requests, "urgencyScore");
            double memory = measureMemoryKb();
            db.saveAlgorithmRun(algorithm + "Sort", requests.length, time, memory);
            System.out.println(algorithm + " sort: " + time + " ns");
        }
    }

    private void graphExperiment() {
        System.out.print("Start location ID: ");
        String start = scanner.nextLine().trim();
        System.out.print("Destination location ID (for Dijkstra): ");
        String destination = scanner.nextLine().trim();

        long bfsTime = graph.timeBFS(start);
        db.saveAlgorithmRun("bfs", 0, bfsTime, measureMemoryKb());
        System.out.println("BFS: " + bfsTime + " ns");

        long dfsTime = graph.timeDFS(start);
        db.saveAlgorithmRun("dfs", 0, dfsTime, measureMemoryKb());
        System.out.println("DFS: " + dfsTime + " ns");

        long dijkstraTime = graph.timeDijkstra(start, destination);
        db.saveAlgorithmRun("dijkstra", 0, dijkstraTime, measureMemoryKb());
        System.out.println("Dijkstra: " + dijkstraTime + " ns");

        long kruskalTime = graph.timeKruskal();
        db.saveAlgorithmRun("kruskal", 0, kruskalTime, measureMemoryKb());
        System.out.println("Kruskal: " + kruskalTime + " ns");

        long primTime = graph.timePrim(start);
        db.saveAlgorithmRun("prim", 0, primTime, measureMemoryKb());
        System.out.println("Prim: " + primTime + " ns");
    }

    private void exportResults() {
        System.out.print("Output CSV filename: ");
        String filename = scanner.nextLine().trim();
        db.exportAlgorithmRunsToCSV(filename);
        System.out.println("Exported to " + filename);
    }
}
