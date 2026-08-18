import java.util.Scanner;

// Section 4: Dijkstra, BFS, DFS, Kruskal, Prim.
public class RoutingMenu {

    private final Scanner scanner;
    private final Graph graph;

    public RoutingMenu(Scanner scanner, Graph graph) {
        this.scanner = scanner;
        this.graph = graph;
    }

    public void run() {
        System.out.println();
        System.out.println("-- ROUTING --");
        System.out.println("a. Shortest path - Dijkstra");
        System.out.println("b. Reachable locations - BFS");
        System.out.println("c. Campus connectivity - DFS");
        System.out.println("d. Minimum road network - Kruskal");
        System.out.println("e. Minimum road network - Prim");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": shortestPath(); break;
            case "b": reachable(); break;
            case "c": connectivity(); break;
            case "d": kruskal(); break;
            case "e": prim(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    private void shortestPath() {
        System.out.print("From location ID: ");
        String from = scanner.nextLine().trim();
        System.out.print("To location ID: ");
        String to = scanner.nextLine().trim();

        String[] path = graph.dijkstra(from, to);
        if (path.length == 0) {
            System.out.println("No path exists between " + from + " and " + to + ".");
            return;
        }
        double cost = graph.getShortestDistance(from, to);
        System.out.println("Path: " + String.join(" -> ", path));
        System.out.println("Cost: " + cost);
    }

    private void reachable() {
        System.out.print("Start location ID: ");
        String start = scanner.nextLine().trim();
        String[] reached = graph.bfs(start);
        System.out.println("Reachable from " + start + ": " + String.join(", ", reached));
    }

    private void connectivity() {
        System.out.print("Start location ID: ");
        String start = scanner.nextLine().trim();
        boolean connected = graph.isFullyConnected(start);
        System.out.println(connected ? "Campus is fully connected from " + start + "."
                : "Campus is NOT fully connected from " + start + ".");
    }

    private void kruskal() {
        Road[] mst = graph.kruskal();
        printMst(mst);
    }

    private void prim() {
        System.out.print("Start location ID: ");
        String start = scanner.nextLine().trim();
        Road[] mst = graph.prim(start);
        printMst(mst);
    }

    private void printMst(Road[] mst) {
        for (Road road : mst) {
            System.out.println(road.getRoadId() + ": " + road.getFromLocationId() + " -> "
                    + road.getToLocationId() + " (" + road.effectiveWeight() + ")");
        }
        System.out.println("Total cost: " + graph.getMSTCost(mst));
    }
}
