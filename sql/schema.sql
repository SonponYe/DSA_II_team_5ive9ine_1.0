-- ============================================
-- UG Campus Maintenance Service Optimizer
-- Database Schema — SQLite
-- ============================================

CREATE TABLE IF NOT EXISTS locations (
    location_id     TEXT PRIMARY KEY,
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    location_type   TEXT NOT NULL,
    latitude        REAL NOT NULL,
    longitude       REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS roads (
    road_id                 TEXT PRIMARY KEY,
    from_location_id        TEXT NOT NULL,
    to_location_id          TEXT NOT NULL,
    distance_km             REAL NOT NULL,
    travel_time_min         REAL NOT NULL,
    road_condition_weight   REAL NOT NULL DEFAULT 1.0,
    FOREIGN KEY (from_location_id) REFERENCES locations(location_id),
    FOREIGN KEY (to_location_id)   REFERENCES locations(location_id)
);

CREATE TABLE IF NOT EXISTS service_requests (
    request_id              TEXT PRIMARY KEY,
    source_location_id      TEXT NOT NULL,
    destination_location_id TEXT NOT NULL,
    category                TEXT NOT NULL,
    urgency                 TEXT NOT NULL,
    urgency_score           INTEGER NOT NULL,
    time_submitted          TEXT NOT NULL,
    deadline                TEXT NOT NULL,
    status                  TEXT NOT NULL DEFAULT 'NEW',
    FOREIGN KEY (source_location_id)      REFERENCES locations(location_id),
    FOREIGN KEY (destination_location_id) REFERENCES locations(location_id)
);

CREATE TABLE IF NOT EXISTS resources (
    resource_id         TEXT PRIMARY KEY,
    resource_type       TEXT NOT NULL,
    home_location_id    TEXT NOT NULL,
    capacity            INTEGER NOT NULL DEFAULT 1,
    availability_status TEXT NOT NULL DEFAULT 'AVAILABLE',
    FOREIGN KEY (home_location_id) REFERENCES locations(location_id)
);

CREATE TABLE IF NOT EXISTS algorithm_runs (
    run_id          TEXT PRIMARY KEY,
    algorithm_name  TEXT NOT NULL,
    input_size      INTEGER NOT NULL,
    time_ns         INTEGER NOT NULL,
    memory_kb       REAL NOT NULL,
    date_run        TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS audit_events (
    event_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type      TEXT NOT NULL,
    description     TEXT NOT NULL,
    timestamp       TEXT NOT NULL,
    performed_by    TEXT
);
