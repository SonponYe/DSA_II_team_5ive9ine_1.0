import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @Test
    void getAllLocationsRoadsRequestsResources() {
        DatabaseManager db = new DatabaseManager();

        // normal case: the seeded dataset meets the project's minimums
        assertTrue(db.getAllLocations().length >= 50);
        assertTrue(db.getAllRoads().length >= 100);
        assertTrue(db.getAllRequests().length >= 300);
        assertTrue(db.getAllResources().length >= 30);

        // boundary case: every row read back has its required fields populated
        for (Location location : db.getAllLocations()) {
            assertNotNull(location.getLocationId());
            assertNotNull(location.getName());
        }
    }

    @Test
    void saveRequestPersists() {
        DatabaseManager db = new DatabaseManager();
        String id = "TEST_SAVE_" + System.nanoTime();

        // normal case: a saved request shows up in getAllRequests()
        ServiceRequest request = new ServiceRequest(id, "L001", "L036", "Plumbing",
                "MEDIUM", 3, "2026-08-01T08:00:00", "2026-08-02T08:00:00", "NEW");
        db.saveRequest(request);

        boolean found = false;
        for (ServiceRequest r : db.getAllRequests()) {
            if (r.getRequestId().equals(id)) {
                found = true;
                assertEquals("Plumbing", r.getCategory());
                assertEquals("NEW", r.getStatus());
            }
        }
        assertTrue(found, "saved request was not returned by getAllRequests()");

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> db.saveRequest(null));
    }

    @Test
    void updateRequestStatus() {
        DatabaseManager db = new DatabaseManager();
        String id = "TEST_STATUS_" + System.nanoTime();
        db.saveRequest(new ServiceRequest(id, "L001", "L036", "Plumbing",
                "MEDIUM", 3, "2026-08-01T08:00:00", "2026-08-02T08:00:00", "NEW"));

        // normal case
        db.updateRequestStatus(id, "IN_PROGRESS");
        assertEquals("IN_PROGRESS", requestStatus(db, id));

        // boundary case: updating again to a different status
        db.updateRequestStatus(id, "DONE");
        assertEquals("DONE", requestStatus(db, id));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> db.updateRequestStatus(null, "DONE"));
        assertThrows(IllegalArgumentException.class, () -> db.updateRequestStatus(id, null));
    }

    private String requestStatus(DatabaseManager db, String requestId) {
        for (ServiceRequest r : db.getAllRequests()) {
            if (r.getRequestId().equals(requestId)) {
                return r.getStatus();
            }
        }
        throw new AssertionError("request not found: " + requestId);
    }

    @Test
    void updateResourceStatus() {
        DatabaseManager db = new DatabaseManager();
        Resource[] resources = db.getAllResources();
        assertTrue(resources.length > 0);
        String resourceId = resources[0].getResourceId();
        String original = resources[0].getAvailabilityStatus();
        String changedTo = "AVAILABLE".equals(original) ? "BUSY" : "AVAILABLE";

        // normal case
        db.updateResourceStatus(resourceId, changedTo);
        assertEquals(changedTo, resourceStatus(db, resourceId));

        // boundary case: put it back so repeated test runs don't drift the
        // shared dataset
        db.updateResourceStatus(resourceId, original);
        assertEquals(original, resourceStatus(db, resourceId));

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> db.updateResourceStatus(null, "BUSY"));
        assertThrows(IllegalArgumentException.class, () -> db.updateResourceStatus(resourceId, null));
    }

    private String resourceStatus(DatabaseManager db, String resourceId) {
        for (Resource r : db.getAllResources()) {
            if (r.getResourceId().equals(resourceId)) {
                return r.getAvailabilityStatus();
            }
        }
        throw new AssertionError("resource not found: " + resourceId);
    }

    @Test
    void saveAlgorithmRunAndExportCsv() throws IOException {
        DatabaseManager db = new DatabaseManager();
        String algorithmName = "unitTestAlgorithm_" + System.nanoTime();

        // normal case
        db.saveAlgorithmRun(algorithmName, 100, 12345L, 6.5);

        File out = File.createTempFile("algorithm_runs_test", ".csv");
        out.deleteOnExit();
        db.exportAlgorithmRunsToCSV(out.getAbsolutePath());

        List<String> lines = Files.readAllLines(out.toPath());
        assertEquals("run_id,algorithm_name,input_size,time_ns,memory_kb,date_run", lines.get(0));

        boolean found = false;
        for (String line : lines) {
            if (line.contains(algorithmName)) {
                found = true;
            }
        }
        assertTrue(found, "exported CSV did not contain the run we just saved");

        // invalid input
        assertThrows(IllegalArgumentException.class, () -> db.saveAlgorithmRun(null, 1, 1L, 1.0));
        assertThrows(IllegalArgumentException.class, () -> db.exportAlgorithmRunsToCSV(null));
    }

    @Test
    void saveAuditEvent() {
        DatabaseManager db = new DatabaseManager();

        // normal case: does not throw
        assertDoesNotThrow(() -> db.saveAuditEvent("TEST_EVENT", "unit test event", "junit"));

        // boundary case: performedBy is allowed to be null (system-generated events)
        assertDoesNotThrow(() -> db.saveAuditEvent("TEST_EVENT", "no performer", null));

        // invalid input
        assertThrows(IllegalArgumentException.class,
                () -> db.saveAuditEvent(null, "description", "junit"));
        assertThrows(IllegalArgumentException.class,
                () -> db.saveAuditEvent("TYPE", null, "junit"));
    }
}
