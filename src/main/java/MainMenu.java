import java.util.Scanner;

// Section 7 of the project guide — the top-level console loop. Not part of
// docs/METHOD_SIGNATURES.md, which only covers G1-G6's core logic classes;
// this class just wires those classes together.
public class MainMenu {

    public void run() {
        Scanner scanner = new Scanner(System.in);
        DatabaseManager db = new DatabaseManager();

        Graph graph = buildGraph(db);
        PriorityQueue queue = buildQueue(db);

        DatabaseMenu databaseMenu = new DatabaseMenu(scanner, db);
        ServiceRequestMenu serviceRequestMenu = new ServiceRequestMenu(scanner, db, queue, graph);
        SearchSortMenu searchSortMenu = new SearchSortMenu(scanner, db);
        RoutingMenu routingMenu = new RoutingMenu(scanner, graph);
        OptimisationMenu optimisationMenu = new OptimisationMenu(scanner, db, graph);
        PerformanceLabMenu performanceLabMenu = new PerformanceLabMenu(scanner, db, graph);

        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println("=== Campus Maintenance Service Operations Optimizer ===");
            System.out.println("1. DATABASE");
            System.out.println("2. SERVICE REQUESTS");
            System.out.println("3. SEARCH & SORT");
            System.out.println("4. ROUTING");
            System.out.println("5. OPTIMISATION");
            System.out.println("6. PERFORMANCE LAB");
            System.out.println("7. EXIT");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": databaseMenu.run(); break;
                case "2": serviceRequestMenu.run(); break;
                case "3": searchSortMenu.run(); break;
                case "4": routingMenu.run(); break;
                case "5": optimisationMenu.run(); break;
                case "6": performanceLabMenu.run(); break;
                case "7": running = false; break;
                default: System.out.println("Not a valid option, try again.");
            }
        }

        System.out.println("Goodbye.");
    }

    private Graph buildGraph(DatabaseManager db) {
        Graph graph = new Graph();
        for (Location location : db.getAllLocations()) {
            graph.addLocation(location);
        }
        for (Road road : db.getAllRoads()) {
            graph.addRoad(road);
        }
        return graph;
    }

    private PriorityQueue buildQueue(DatabaseManager db) {
        PriorityQueue queue = new PriorityQueue();
        for (ServiceRequest request : db.getAllRequests()) {
            if ("NEW".equals(request.getStatus())) {
                queue.insert(request);
            }
        }
        return queue;
    }
}
