# Weekly Development Log

Required evidence (Team Rules, Section 12): keep this updated every week with
progress, challenges, and decisions made.

## Week 1 — Foundation

### 2026-07-27 – 2026-08-02
- Progress: Team met to discuss project scope and progress; created the GitHub
  repository; hashed out the project folder/file structure (Maven layout,
  data/sql/docs/results folders) so every group knows where their work goes.
- Challenges:
- Decisions:

### 2026-08-03 – present
- Progress: Seeded `data/*.csv` with real Ghanaian campus data (locations,
  roads, service requests, resources — all at or above the minimum record
  counts). Starting implementation of most of the required data structures
  and algorithms across G1-G6.
- Challenges:
- Decisions:

### 2026-08-07
- Progress (G4): Implemented `SearchEngine` (`linearSearch`, `binarySearch`,
  timing wrappers) and `SortEngine` (selection, insertion, merge, quick sort,
  `timeSort`) per `docs/METHOD_SIGNATURES.md`. Filled in
  `SearchEngineTest`/`SortEngineTest` with normal/boundary/invalid-input
  cases for each method. Opened as branch `feature/g4-search-sort` / PR.
- Challenges: quicksort's naive fixed-pivot version degrades to O(n^2)
  recursion depth on already-sorted input (the exact case
  `quickSortWorstCaseSortedInput` exercises) — switched to median-of-three
  pivot selection to keep it well-behaved. Maven isn't installed in the dev
  environment used for this change, so the JUnit suite couldn't be run
  directly; verified logic with a standalone `javac`/`java` harness against
  the real classes instead, plus a full `javac` compile of `src/main/java`.
- Decisions: all four sort algorithms sort ascending by the given key
  (`urgencyScore`, `deadline`, `timeSubmitted`, `requestId` all compare
  naturally in ascending order); unknown `field`/`sortBy`/`algorithm` values
  and null arguments throw `IllegalArgumentException`.

## Week 2 — Graph and Routing Engine
- Progress:
- Challenges:
- Decisions:

## Week 3 — Testing and Evidence
- Progress:
- Challenges:
- Decisions:

## Week 4 — Polish and Defence
- Progress:
- Challenges:
- Decisions:
