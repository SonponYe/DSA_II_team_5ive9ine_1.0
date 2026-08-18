import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

// Owns the one JDBC connection to the SQLite database. Every other group
// reads/writes through this class's public methods rather than opening
// their own connection. Table definitions are kept in sync by hand with
// sql/schema.sql.
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:campus.db";

    private final Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            throw new RuntimeException("could not open database connection", e);
        }
        createTablesIfMissing();
        seedFromCsvIfEmpty();
    }

    // ── schema setup ─────────────────────────────────────────────────────

    private void createTablesIfMissing() {
        String[] statements = {
                "CREATE TABLE IF NOT EXISTS locations (" +
                        "location_id TEXT PRIMARY KEY, name TEXT NOT NULL, area TEXT NOT NULL, " +
                        "location_type TEXT NOT NULL, latitude REAL NOT NULL, longitude REAL NOT NULL)",

                "CREATE TABLE IF NOT EXISTS roads (" +
                        "road_id TEXT PRIMARY KEY, from_location_id TEXT NOT NULL, to_location_id TEXT NOT NULL, " +
                        "distance_km REAL NOT NULL, travel_time_min REAL NOT NULL, " +
                        "road_condition_weight REAL NOT NULL DEFAULT 1.0)",

                "CREATE TABLE IF NOT EXISTS service_requests (" +
                        "request_id TEXT PRIMARY KEY, source_location_id TEXT NOT NULL, " +
                        "destination_location_id TEXT NOT NULL, category TEXT NOT NULL, urgency TEXT NOT NULL, " +
                        "urgency_score INTEGER NOT NULL, time_submitted TEXT NOT NULL, deadline TEXT NOT NULL, " +
                        "status TEXT NOT NULL DEFAULT 'NEW')",

                "CREATE TABLE IF NOT EXISTS resources (" +
                        "resource_id TEXT PRIMARY KEY, resource_type TEXT NOT NULL, home_location_id TEXT NOT NULL, " +
                        "capacity INTEGER NOT NULL DEFAULT 1, availability_status TEXT NOT NULL DEFAULT 'AVAILABLE')",

                "CREATE TABLE IF NOT EXISTS algorithm_runs (" +
                        "run_id INTEGER PRIMARY KEY AUTOINCREMENT, algorithm_name TEXT NOT NULL, " +
                        "input_size INTEGER NOT NULL, time_ns INTEGER NOT NULL, memory_kb REAL NOT NULL, " +
                        "date_run TEXT NOT NULL)",

                "CREATE TABLE IF NOT EXISTS audit_events (" +
                        "event_id INTEGER PRIMARY KEY AUTOINCREMENT, event_type TEXT NOT NULL, " +
                        "description TEXT NOT NULL, timestamp TEXT NOT NULL, performed_by TEXT)"
        };

        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not create tables", e);
        }
    }

    // If the database is brand new, load the starting dataset from data/*.csv
    // so the console menu has something to show on first run.
    private void seedFromCsvIfEmpty() {
        if (countRows("locations") > 0) {
            return;
        }
        seedLocationsFromCsv("data/locations.csv");
        seedRoadsFromCsv("data/roads.csv");
        seedResourcesFromCsv("data/resources.csv");
        seedRequestsFromCsv("data/service_requests.csv");
    }

    private int countRows(String table) {
        String sql = "SELECT COUNT(*) FROM " + table;
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("could not count rows in " + table, e);
        }
    }

    private void seedLocationsFromCsv(String path) {
        String sql = "INSERT INTO locations (location_id, name, area, location_type, latitude, longitude) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setString(4, fields[3]);
                statement.setDouble(5, Double.parseDouble(fields[4]));
                statement.setDouble(6, Double.parseDouble(fields[5]));
                statement.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("could not seed locations from " + path, e);
        }
    }

    private void seedRoadsFromCsv(String path) {
        String sql = "INSERT INTO roads (road_id, from_location_id, to_location_id, distance_km, "
                + "travel_time_min, road_condition_weight) VALUES (?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setDouble(4, Double.parseDouble(fields[3]));
                statement.setDouble(5, Double.parseDouble(fields[4]));
                statement.setDouble(6, Double.parseDouble(fields[5]));
                statement.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("could not seed roads from " + path, e);
        }
    }

    private void seedResourcesFromCsv(String path) {
        String sql = "INSERT INTO resources (resource_id, resource_type, home_location_id, capacity, "
                + "availability_status) VALUES (?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setInt(4, Integer.parseInt(fields[3]));
                statement.setString(5, fields[4]);
                statement.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("could not seed resources from " + path, e);
        }
    }

    private void seedRequestsFromCsv(String path) {
        String sql = "INSERT INTO service_requests (request_id, source_location_id, destination_location_id, "
                + "category, urgency, urgency_score, time_submitted, deadline, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (BufferedReader reader = new BufferedReader(new FileReader(path));
             PreparedStatement statement = connection.prepareStatement(sql)) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] fields = line.split(",");
                statement.setString(1, fields[0]);
                statement.setString(2, fields[1]);
                statement.setString(3, fields[2]);
                statement.setString(4, fields[3]);
                statement.setString(5, fields[4]);
                statement.setInt(6, Integer.parseInt(fields[5]));
                statement.setString(7, fields[6]);
                statement.setString(8, fields[7]);
                statement.setString(9, fields[8]);
                statement.executeUpdate();
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("could not seed service requests from " + path, e);
        }
    }

    // ── reads ────────────────────────────────────────────────────────────

    public Location[] getAllLocations() {
        String sql = "SELECT location_id, name, area, location_type, latitude, longitude FROM locations";
        DynamicArray<Location> results = new DynamicArray<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                results.add(new Location(
                        resultSet.getString("location_id"),
                        resultSet.getString("name"),
                        resultSet.getString("area"),
                        resultSet.getString("location_type"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not load locations", e);
        }
        Location[] array = new Location[results.size()];
        for (int i = 0; i < results.size(); i++) array[i] = results.get(i);
        return array;
    }

    public Road[] getAllRoads() {
        String sql = "SELECT road_id, from_location_id, to_location_id, distance_km, travel_time_min, "
                + "road_condition_weight FROM roads";
        DynamicArray<Road> results = new DynamicArray<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                results.add(new Road(
                        resultSet.getString("road_id"),
                        resultSet.getString("from_location_id"),
                        resultSet.getString("to_location_id"),
                        resultSet.getDouble("distance_km"),
                        resultSet.getDouble("travel_time_min"),
                        resultSet.getDouble("road_condition_weight")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not load roads", e);
        }
        Road[] array = new Road[results.size()];
        for (int i = 0; i < results.size(); i++) array[i] = results.get(i);
        return array;
    }

    public ServiceRequest[] getAllRequests() {
        String sql = "SELECT request_id, source_location_id, destination_location_id, category, urgency, "
                + "urgency_score, time_submitted, deadline, status FROM service_requests";
        DynamicArray<ServiceRequest> results = new DynamicArray<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                results.add(new ServiceRequest(
                        resultSet.getString("request_id"),
                        resultSet.getString("source_location_id"),
                        resultSet.getString("destination_location_id"),
                        resultSet.getString("category"),
                        resultSet.getString("urgency"),
                        resultSet.getInt("urgency_score"),
                        resultSet.getString("time_submitted"),
                        resultSet.getString("deadline"),
                        resultSet.getString("status")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not load service requests", e);
        }
        ServiceRequest[] array = new ServiceRequest[results.size()];
        for (int i = 0; i < results.size(); i++) array[i] = results.get(i);
        return array;
    }

    public Resource[] getAllResources() {
        String sql = "SELECT resource_id, resource_type, home_location_id, capacity, availability_status "
                + "FROM resources";
        DynamicArray<Resource> results = new DynamicArray<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                results.add(new Resource(
                        resultSet.getString("resource_id"),
                        resultSet.getString("resource_type"),
                        resultSet.getString("home_location_id"),
                        resultSet.getInt("capacity"),
                        resultSet.getString("availability_status")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("could not load resources", e);
        }
        Resource[] array = new Resource[results.size()];
        for (int i = 0; i < results.size(); i++) array[i] = results.get(i);
        return array;
    }

    // ── writes ───────────────────────────────────────────────────────────

    public void saveRequest(ServiceRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        String sql = "INSERT INTO service_requests (request_id, source_location_id, destination_location_id, "
                + "category, urgency, urgency_score, time_submitted, deadline, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getRequestId());
            statement.setString(2, request.getSourceLocationId());
            statement.setString(3, request.getDestinationLocationId());
            statement.setString(4, request.getCategory());
            statement.setString(5, request.getUrgency());
            statement.setInt(6, request.getUrgencyScore());
            statement.setString(7, request.getTimeSubmitted());
            statement.setString(8, request.getDeadline());
            statement.setString(9, request.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("could not save request " + request.getRequestId(), e);
        }
    }

    public void updateRequestStatus(String requestId, String newStatus) {
        if (requestId == null || newStatus == null) {
            throw new IllegalArgumentException("requestId and newStatus cannot be null");
        }
        String sql = "UPDATE service_requests SET status = ? WHERE request_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setString(2, requestId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("could not update status for request " + requestId, e);
        }
    }

    public void updateResourceStatus(String resourceId, String newStatus) {
        if (resourceId == null || newStatus == null) {
            throw new IllegalArgumentException("resourceId and newStatus cannot be null");
        }
        String sql = "UPDATE resources SET availability_status = ? WHERE resource_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newStatus);
            statement.setString(2, resourceId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("could not update status for resource " + resourceId, e);
        }
    }

    public void saveAlgorithmRun(String algorithmName, int inputSize, long timeNs, double memoryKb) {
        if (algorithmName == null) {
            throw new IllegalArgumentException("algorithmName cannot be null");
        }
        String sql = "INSERT INTO algorithm_runs (algorithm_name, input_size, time_ns, memory_kb, date_run) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, algorithmName);
            statement.setInt(2, inputSize);
            statement.setLong(3, timeNs);
            statement.setDouble(4, memoryKb);
            statement.setString(5, LocalDateTime.now().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("could not save algorithm run for " + algorithmName, e);
        }
    }

    public void saveAuditEvent(String eventType, String description, String performedBy) {
        if (eventType == null || description == null) {
            throw new IllegalArgumentException("eventType and description cannot be null");
        }
        String sql = "INSERT INTO audit_events (event_type, description, timestamp, performed_by) "
                + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, eventType);
            statement.setString(2, description);
            statement.setString(3, LocalDateTime.now().toString());
            statement.setString(4, performedBy);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("could not save audit event", e);
        }
    }

    public void exportAlgorithmRunsToCSV(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        String sql = "SELECT run_id, algorithm_name, input_size, time_ns, memory_kb, date_run FROM algorithm_runs";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql);
             BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("run_id,algorithm_name,input_size,time_ns,memory_kb,date_run");
            writer.newLine();
            while (resultSet.next()) {
                writer.write(resultSet.getInt("run_id") + ","
                        + resultSet.getString("algorithm_name") + ","
                        + resultSet.getInt("input_size") + ","
                        + resultSet.getLong("time_ns") + ","
                        + resultSet.getDouble("memory_kb") + ","
                        + resultSet.getString("date_run"));
                writer.newLine();
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException("could not export algorithm runs to " + filename, e);
        }
    }
}
