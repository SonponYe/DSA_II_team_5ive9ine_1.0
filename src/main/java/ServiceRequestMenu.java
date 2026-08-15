import java.time.LocalDateTime;
import java.util.Scanner;

// Section 2: submit, peek, dispatch, complete a request.
public class ServiceRequestMenu {

    private final Scanner scanner;
    private final DatabaseManager db;
    private final PriorityQueue queue;
    private final Graph graph;

    public ServiceRequestMenu(Scanner scanner, DatabaseManager db, PriorityQueue queue, Graph graph) {
        this.scanner = scanner;
        this.db = db;
        this.queue = queue;
        this.graph = graph;
    }

    public void run() {
        System.out.println();
        System.out.println("-- SERVICE REQUESTS --");
        System.out.println("a. Submit new request");
        System.out.println("b. View next request (peek)");
        System.out.println("c. Dispatch next request");
        System.out.println("d. Mark request as complete");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": submitRequest(); break;
            case "b": peekNext(); break;
            case "c": dispatchNext(); break;
            case "d": markComplete(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    private void submitRequest() {
        System.out.print("Request ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Source location ID: ");
        String source = scanner.nextLine().trim();
        System.out.print("Destination (workshop) location ID: ");
        String destination = scanner.nextLine().trim();
        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        System.out.print("Urgency (CRITICAL/HIGH/MEDIUM/LOW): ");
        String urgency = scanner.nextLine().trim();
        int urgencyScore = urgencyScoreFor(urgency);
        System.out.print("Deadline (e.g. 2026-08-20T10:00:00): ");
        String deadline = scanner.nextLine().trim();

        String timeSubmitted = LocalDateTime.now().toString();
        ServiceRequest request = new ServiceRequest(id, source, destination, category,
                urgency, urgencyScore, timeSubmitted, deadline, "NEW");

        db.saveRequest(request);
        queue.insert(request);
        db.saveAuditEvent("REQUEST_SUBMITTED", "Request " + id + " submitted", "console");
        System.out.println("Request " + id + " submitted.");
    }

    private int urgencyScoreFor(String urgency) {
        switch (urgency) {
            case "CRITICAL": return 1;
            case "HIGH": return 2;
            case "MEDIUM": return 3;
            case "LOW": return 4;
            default: throw new IllegalArgumentException("unknown urgency: " + urgency);
        }
    }

    private void peekNext() {
        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        ServiceRequest next = queue.peek();
        System.out.println("Next up: " + next.getRequestId() + " (" + next.getUrgency() + ")");
    }

    private void dispatchNext() {
        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
            return;
        }
        ServiceRequest request = queue.extractMin();

        Resource assigned = GreedyAssignment.assignNearest(request, db.getAllResources(), graph);
        if (assigned == null) {
            System.out.println("No available worker for request " + request.getRequestId() + ".");
            return;
        }

        String[] path = graph.dijkstra(assigned.getHomeLocationId(), request.getSourceLocationId());
        double distance = graph.getShortestDistance(assigned.getHomeLocationId(), request.getSourceLocationId());

        db.updateRequestStatus(request.getRequestId(), "IN_PROGRESS");
        db.updateResourceStatus(assigned.getResourceId(), "BUSY");
        db.saveAuditEvent("REQUEST_DISPATCHED",
                "Request " + request.getRequestId() + " dispatched to " + assigned.getResourceId(), "console");

        System.out.println("Dispatched " + assigned.getResourceId() + " to request " + request.getRequestId()
                + " (distance " + distance + ")");
        System.out.print("Route: ");
        for (int i = 0; i < path.length; i++) {
            if (i > 0) System.out.print(" -> ");
            System.out.print(path[i]);
        }
        System.out.println();
    }

    private void markComplete() {
        System.out.print("Request ID to mark complete: ");
        String id = scanner.nextLine().trim();
        db.updateRequestStatus(id, "DONE");
        db.saveAuditEvent("REQUEST_COMPLETED", "Request " + id + " marked complete", "console");
        System.out.println("Request " + id + " marked complete.");
    }
}
