import java.util.Scanner;

// Section 1: load from DB, view locations/requests/resources.
public class DatabaseMenu {

    private final Scanner scanner;
    private final DatabaseManager db;

    public DatabaseMenu(Scanner scanner, DatabaseManager db) {
        this.scanner = scanner;
        this.db = db;
    }

    public void run() {
        System.out.println();
        System.out.println("-- DATABASE --");
        System.out.println("a. Load all data from database");
        System.out.println("b. View all locations");
        System.out.println("c. View all service requests");
        System.out.println("d. View all resources");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": loadAllData(); break;
            case "b": viewLocations(); break;
            case "c": viewRequests(); break;
            case "d": viewResources(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    private void loadAllData() {
        Location[] locations = db.getAllLocations();
        Road[] roads = db.getAllRoads();
        ServiceRequest[] requests = db.getAllRequests();
        Resource[] resources = db.getAllResources();
        System.out.println("Loaded " + locations.length + " locations, " + roads.length + " roads, "
                + requests.length + " requests, " + resources.length + " resources.");
    }

    private void viewLocations() {
        for (Location location : db.getAllLocations()) {
            System.out.println(location.getLocationId() + " | " + location.getName() + " | "
                    + location.getArea() + " | " + location.getLocationType());
        }
    }

    private void viewRequests() {
        for (ServiceRequest request : db.getAllRequests()) {
            System.out.println(request.getRequestId() + " | " + request.getCategory() + " | "
                    + request.getUrgency() + " | " + request.getStatus());
        }
    }

    private void viewResources() {
        for (Resource resource : db.getAllResources()) {
            System.out.println(resource.getResourceId() + " | " + resource.getResourceType() + " | "
                    + resource.getAvailabilityStatus());
        }
    }
}
