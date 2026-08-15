import java.util.Scanner;

// Section 3: linear/binary search, choice of sort algorithm.
public class SearchSortMenu {

    private final Scanner scanner;
    private final DatabaseManager db;

    public SearchSortMenu(Scanner scanner, DatabaseManager db) {
        this.scanner = scanner;
        this.db = db;
    }

    public void run() {
        System.out.println();
        System.out.println("-- SEARCH & SORT --");
        System.out.println("a. Linear search by location");
        System.out.println("b. Binary search by ID");
        System.out.println("c. Sort requests");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "a": linearSearchByLocation(); break;
            case "b": binarySearchById(); break;
            case "c": sortRequests(); break;
            default: System.out.println("Not a valid option.");
        }
    }

    private void linearSearchByLocation() {
        System.out.print("Source location ID: ");
        String locationId = scanner.nextLine().trim();
        ServiceRequest[] found = SearchEngine.linearSearch(db.getAllRequests(), "sourceLocationId", locationId);
        printRequests(found);
    }

    private void binarySearchById() {
        System.out.print("Request ID: ");
        String id = scanner.nextLine().trim();
        ServiceRequest[] requests = db.getAllRequests();
        SortEngine.mergeSort(requests, "requestId");
        ServiceRequest found = SearchEngine.binarySearch(requests, id);
        if (found == null) {
            System.out.println("No request with ID " + id + ".");
        } else {
            printRequests(new ServiceRequest[]{found});
        }
    }

    private void sortRequests() {
        System.out.println("Sort by: 1=Selection 2=Insertion 3=Merge 4=Quick");
        String choice = scanner.nextLine().trim();
        System.out.print("Sort key (urgencyScore/deadline/timeSubmitted/requestId): ");
        String sortBy = scanner.nextLine().trim();

        ServiceRequest[] requests = db.getAllRequests();
        switch (choice) {
            case "1": SortEngine.selectionSort(requests, sortBy); break;
            case "2": SortEngine.insertionSort(requests, sortBy); break;
            case "3": SortEngine.mergeSort(requests, sortBy); break;
            case "4": SortEngine.quickSort(requests, sortBy); break;
            default: System.out.println("Not a valid option."); return;
        }
        printRequests(requests);
    }

    private void printRequests(ServiceRequest[] requests) {
        for (ServiceRequest request : requests) {
            System.out.println(request.getRequestId() + " | " + request.getCategory() + " | "
                    + request.getUrgency() + " | " + request.getStatus());
        }
    }
}
