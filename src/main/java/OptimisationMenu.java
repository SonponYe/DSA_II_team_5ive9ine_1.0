import java.util.Scanner;

// Section 5: greedy assignment, DP knapsack.
public class OptimisationMenu {

    private final Scanner scanner;
    private final DatabaseManager db;
    private final Graph graph;

    public OptimisationMenu(Scanner scanner, DatabaseManager db, Graph graph) {
        this.scanner = scanner;
        this.db = db;
        this.graph = graph;
    }

    public void run() {
        System.out.println();
        System.out.println("-- OPTIMISATION --");
        System.out.println("a. Assign nearest worker - Greedy");
        System.out.println("b. Optimise shift workload - DP");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": greedyAssign(); break;
            case "b": dpOptimise(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    private void greedyAssign() {
        ServiceRequest[] requests = db.getAllRequests();
        Resource[] resources = db.getAllResources();
        String[][] assignments = GreedyAssignment.assignAll(requests, resources, graph);

        for (String[] assignment : assignments) {
            System.out.println("Request " + assignment[0] + " -> Resource " + assignment[1]);
        }
        System.out.println();
        System.out.println("Counterexample: " + GreedyAssignment.demonstrateCounterexample());
    }

    private void dpOptimise() {
        System.out.print("Total staff-hours available this shift: ");
        int totalHours = Integer.parseInt(scanner.nextLine().trim());

        ServiceRequest[] requests = db.getAllRequests();
        DPKnapsack.printDPTable(requests, totalHours);

        String[] chosen = DPKnapsack.solve(requests, totalHours);
        int value = DPKnapsack.getOptimalValue(requests, totalHours);

        System.out.println("Chosen requests: " + String.join(", ", chosen));
        System.out.println("Total urgency value resolved: " + value);
    }
}
