// Classic 0/1 knapsack: pick the set of requests that maximises total
// urgency value without exceeding the shift's available hours.
public class DPKnapsack {

    // ServiceRequest has no "hours to resolve" field (fields are frozen by
    // G1), so this is a team-chosen, documented estimate by category —
    // tune these numbers if the team agrees on different ones.
    private static int hoursFor(ServiceRequest request) {
        switch (request.getCategory()) {
            case "Electrical": return 2;
            case "Plumbing": return 2;
            case "Structural": return 4;
            case "Equipment Movement": return 1;
            default: return 2;
        }
    }

    // Urgency points: CRITICAL (score 1) is worth the most, LOW (score 4) the least.
    private static int valueFor(ServiceRequest request) {
        return 5 - request.getUrgencyScore();
    }

    private static void validate(ServiceRequest[] requests, int totalHours) {
        if (requests == null) {
            throw new IllegalArgumentException("requests cannot be null");
        }
        if (totalHours < 0) {
            throw new IllegalArgumentException("totalHours cannot be negative");
        }
        for (ServiceRequest request : requests) {
            if (request == null) {
                throw new IllegalArgumentException("requests cannot contain null elements");
            }
        }
    }

    // dp[i][capacity] = best total value achievable using the first i requests
    // with "capacity" hours of shift time left.
    private static int[][] buildTable(ServiceRequest[] requests, int totalHours) {
        int n = requests.length;
        int[][] dp = new int[n + 1][totalHours + 1];

        for (int i = 1; i <= n; i++) {
            int hours = hoursFor(requests[i - 1]);
            int value = valueFor(requests[i - 1]);
            for (int capacity = 0; capacity <= totalHours; capacity++) {
                dp[i][capacity] = dp[i - 1][capacity]; // option 1: skip this request
                if (hours <= capacity) {
                    int withRequest = dp[i - 1][capacity - hours] + value;
                    if (withRequest > dp[i][capacity]) {
                        dp[i][capacity] = withRequest; // option 2: take this request
                    }
                }
            }
        }
        return dp;
    }

    // Solve the shift optimisation problem
    // Returns the optimal selection of request IDs
    public static String[] solve(ServiceRequest[] requests, int totalHours) {
        validate(requests, totalHours);
        int[][] dp = buildTable(requests, totalHours);

        // Walk back through the table: if this row's value differs from the
        // row above at the same capacity, this request was taken.
        DynamicArray<String> chosen = new DynamicArray<>();
        int capacity = totalHours;
        for (int i = requests.length; i > 0; i--) {
            if (dp[i][capacity] != dp[i - 1][capacity]) {
                chosen.add(requests[i - 1].getRequestId());
                capacity -= hoursFor(requests[i - 1]);
            }
        }

        String[] result = new String[chosen.size()];
        for (int i = 0; i < chosen.size(); i++) {
            result[i] = chosen.get(i);
        }
        return result;
    }

    // Print the full DP table — required as evidence
    public static void printDPTable(ServiceRequest[] requests, int totalHours) {
        validate(requests, totalHours);
        int[][] dp = buildTable(requests, totalHours);

        System.out.print("        ");
        for (int capacity = 0; capacity <= totalHours; capacity++) {
            System.out.printf("%4d", capacity);
        }
        System.out.println();

        System.out.printf("%8s", "(none)");
        for (int capacity = 0; capacity <= totalHours; capacity++) {
            System.out.printf("%4d", dp[0][capacity]);
        }
        System.out.println();

        for (int i = 1; i <= requests.length; i++) {
            System.out.printf("%8s", requests[i - 1].getRequestId());
            for (int capacity = 0; capacity <= totalHours; capacity++) {
                System.out.printf("%4d", dp[i][capacity]);
            }
            System.out.println();
        }
    }

    // Return total urgency points of the optimal solution
    public static int getOptimalValue(ServiceRequest[] requests, int totalHours) {
        validate(requests, totalHours);
        int[][] dp = buildTable(requests, totalHours);
        return dp[requests.length][totalHours];
    }
}
