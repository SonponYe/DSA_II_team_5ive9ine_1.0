public class SortEngine {

    private static int compare(ServiceRequest a, ServiceRequest b, String sortBy) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("ServiceRequest entries must not be null");
        }
        if (sortBy == null) {
            throw new IllegalArgumentException("sortBy must not be null");
        }
        switch (sortBy) {
            case "urgencyScore": return Integer.compare(a.getUrgencyScore(), b.getUrgencyScore());
            case "deadline": return a.getDeadline().compareTo(b.getDeadline());
            case "timeSubmitted": return a.getTimeSubmitted().compareTo(b.getTimeSubmitted());
            case "requestId": return a.getRequestId().compareTo(b.getRequestId());
            default: throw new IllegalArgumentException("Unknown sort key: " + sortBy);
        }
    }

    private static void validateSortInput(ServiceRequest[] arr, String sortBy) {
        if (arr == null) {
            throw new IllegalArgumentException("arr must not be null");
        }
        if (sortBy == null) {
            throw new IllegalArgumentException("sortBy must not be null");
        }
    }

    private static void swap(ServiceRequest[] arr, int i, int j) {
        ServiceRequest temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // All sort methods take an array and a sort key
    // sortBy: "urgencyScore", "deadline", "timeSubmitted", "requestId"
    public static void selectionSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        for (int i = 0; i < arr.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (compare(arr[j], arr[minIndex], sortBy) < 0) {
                    minIndex = j;
                }
            }
            if (minIndex != i) swap(arr, i, minIndex);
        }
    }

    public static void insertionSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        for (int i = 1; i < arr.length; i++) {
            ServiceRequest key = arr[i];
            int j = i - 1;
            while (j >= 0 && compare(arr[j], key, sortBy) > 0) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    public static void mergeSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        if (arr.length < 2) return;
        mergeSortHelper(arr, 0, arr.length - 1, sortBy);
    }

    private static void mergeSortHelper(ServiceRequest[] arr, int low, int high, String sortBy) {
        if (low >= high) return;
        int mid = low + (high - low) / 2;
        mergeSortHelper(arr, low, mid, sortBy);
        mergeSortHelper(arr, mid + 1, high, sortBy);
        merge(arr, low, mid, high, sortBy);
    }

    private static void merge(ServiceRequest[] arr, int low, int mid, int high, String sortBy) {
        ServiceRequest[] left = new ServiceRequest[mid - low + 1];
        ServiceRequest[] right = new ServiceRequest[high - mid];
        System.arraycopy(arr, low, left, 0, left.length);
        System.arraycopy(arr, mid + 1, right, 0, right.length);

        int i = 0, j = 0, k = low;
        while (i < left.length && j < right.length) {
            if (compare(left[i], right[j], sortBy) <= 0) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }
        while (i < left.length) arr[k++] = left[i++];
        while (j < right.length) arr[k++] = right[j++];
    }

    public static void quickSort(ServiceRequest[] arr, String sortBy) {
        if (arr == null) throw new IllegalArgumentException("arr must not be null");
        if (arr.length < 2) return;
        quickSortHelper(arr, 0, arr.length - 1, sortBy);
    }

    private static void quickSortHelper(ServiceRequest[] arr, int low, int high, String sortBy) {
        if (low >= high) return;
        int pivotIndex = partition(arr, low, high, sortBy);
        quickSortHelper(arr, low, pivotIndex - 1, sortBy);
        quickSortHelper(arr, pivotIndex + 1, high, sortBy);
    }

    // Median-of-three pivot selection so an already-sorted (or reverse-sorted)
    // array — the classic worst case for a fixed first/last pivot — still splits
    // roughly in half instead of degrading to O(n^2) recursion depth.
    private static int partition(ServiceRequest[] arr, int low, int high, String sortBy) {
        int mid = low + (high - low) / 2;
        if (compare(arr[mid], arr[low], sortBy) < 0) swap(arr, low, mid);
        if (compare(arr[high], arr[low], sortBy) < 0) swap(arr, low, high);
        if (compare(arr[high], arr[mid], sortBy) < 0) swap(arr, mid, high);
        swap(arr, mid, high);

        ServiceRequest pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (compare(arr[j], pivot, sortBy) <= 0) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, high);
        return i + 1;
    }

    // For performance lab — sorts a copy, returns time in nanoseconds
    // algorithm values: "selection", "insertion", "merge", "quick"
    public static long timeSort(String algorithm, ServiceRequest[] arr, String sortBy) {
        if (algorithm == null || arr == null) {
            throw new IllegalArgumentException("algorithm and arr must not be null");
        }
        ServiceRequest[] copy = new ServiceRequest[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);

        long start = System.nanoTime();
        switch (algorithm) {
            case "selection": selectionSort(copy, sortBy); break;
            case "insertion": insertionSort(copy, sortBy); break;
            case "merge": mergeSort(copy, sortBy); break;
            case "quick": quickSort(copy, sortBy); break;
            default: throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        }
        return System.nanoTime() - start;
    }
}
