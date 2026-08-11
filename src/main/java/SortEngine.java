import java.util.Random;
public class SortEngine {

    // All sort methods take an array and a sort key
    // sortBy: "urgencyScore", "deadline", "timeSubmitted", "requestId"

    private static final Random RANDOM = new Random(42);

    private static final String[] SORTING_FIELDS = {
            "urgencyScore", "deadline", "timeSubmitted", "requestId"
    };

    private static final int WARMUP_ITERATIONS = 50;
    private static final int TIMED_ITERATIONS = 20;

    private static void requireSortBy(String sortBy){
        for(String field: SORTING_FIELDS){
            if(field.equals(sortBy)) return;
        }
        throw new IllegalArgumentException("Invalid sort by: " + sortBy);
    }

    private static void validateSortInput(ServiceRequest[] arr, String sortBy) {
        if (arr == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        if (sortBy == null) {
            throw new IllegalArgumentException("sortBy must not be null");
        }

        requireSortBy(sortBy);


        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                throw new IllegalArgumentException("Array must not contain null — found one at index " + i);
            }
        }
    }

    private static int compare(ServiceRequest a, ServiceRequest b, String sortBy) {
        return switch (sortBy) {
            case "urgencyScore" ->
                    Integer.compare(a.getUrgencyScore(), b.getUrgencyScore());

            case "deadline" ->
                    a.getDeadline().compareTo(b.getDeadline());

            case "timeSubmitted" ->
                    a.getTimeSubmitted().compareTo(b.getTimeSubmitted());

            case "requestId" ->
                    a.getRequestId().compareTo(b.getRequestId());

            default ->
                    throw new IllegalArgumentException("Invalid sort by: " + sortBy);
        };
    }

    private static void swap(ServiceRequest[] arr, int i, int j){
        ServiceRequest temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void selectionSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        selectionSortCore(arr, sortBy);
    }

    private static void selectionSortCore(ServiceRequest[] arr, String sortBy) {
        for(int i = 0; i < arr.length-1; i++) {
            int min =i;
            for(int j = i+1; j < arr.length; j++) {
                if(compare(arr[j], arr[min], sortBy) < 0) min = j;
            }
            swap(arr,i,min);
        }
    }

    public static void insertionSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        insertionSortCore(arr, sortBy);
    }

    private static void insertionSortCore(ServiceRequest[] arr, String sortBy) {
        for(int i = 1; i < arr.length; i++) {
            ServiceRequest key = arr[i];
            int j = i-1;
            while(j >= 0 && compare(arr[j], key,sortBy) > 0) {
                arr[j+1] = arr[j];
                j--;
            }
            arr[j+1] = key;

        }
    }

    public static void mergeSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        mergeSortRecursive(arr,sortBy);
    }

    private static void mergeSortRecursive(ServiceRequest[] arr, String sortBy) {
        if(arr.length <= 1) return;
        int mid = arr.length/2;
        ServiceRequest [] leftArray = new ServiceRequest[mid];
        ServiceRequest [] rightArray = new ServiceRequest[arr.length-mid];
        int j = 0;
        for(int i = 0; i < arr.length; i++) {
            if(i < mid) {
                leftArray[i] = arr[i];
            }
            else{
                rightArray[j++] = arr[i];
            }

        }

        mergeSortRecursive(leftArray, sortBy);
        mergeSortRecursive(rightArray, sortBy);
        merge(arr, leftArray, rightArray, sortBy);
    }

    private static void merge(ServiceRequest[] arr, ServiceRequest[] leftArray, ServiceRequest[] rightArray, String sortBy) {
        int i = 0;
        int j = 0;
        int k = 0;

        while(i < leftArray.length && j < rightArray.length) {
            if(compare(leftArray[i], rightArray[j],sortBy) <= 0 ){
                arr[k++] = leftArray[i++];
            }
            else{
                arr[k++] = rightArray[j++];
            }
        }

        while (i < leftArray.length){
            arr[k++] = leftArray[i++];
        }
        while (j < rightArray.length){
            arr[k++] = rightArray[j++];
        }
    }


    public static void quickSort(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        int low = 0;
        int high = arr.length-1;
        quickSortRecursive(arr, low, high, sortBy, true);
    }

    public static void quickSortLastPivot(ServiceRequest[] arr, String sortBy) {
        validateSortInput(arr, sortBy);
        quickSortRecursive(arr, 0, arr.length - 1, sortBy, false);
    }

    private static int partition(ServiceRequest[] arr, int low, int high, String sortBy, boolean randomPivot) {
        if (randomPivot) {
            int pivotIndex = low+RANDOM.nextInt(high-low+1);
            swap(arr, pivotIndex,high);
        }
        ServiceRequest pivot = arr[high];
        int i = low - 1;
        for(int j = low; j < high;j++) {
            if(compare(arr[j], pivot,sortBy) < 0){
                i++;
                swap(arr,i,j);
            }
        }
        swap(arr,i+1,high);
        return i+1;
    }

    private static void quickSortRecursive(ServiceRequest[] arr, int low, int high, String sortBy, boolean randomPivot){
        while (low < high){
            int pivot = partition(arr, low, high, sortBy, randomPivot);

            if (pivot - low < high - pivot){
                quickSortRecursive(arr, low, pivot - 1, sortBy, randomPivot);
                low = pivot + 1;
            }
            else{
                quickSortRecursive(arr, pivot + 1, high, sortBy, randomPivot);
                high = pivot - 1;
            }
        }
    }


    private static final String[] SORT_ALGORITHMS = {
            "selection", "insertion", "merge", "quick", "quickLastPivot"
    };

    private static void requireAlgorithm(String algorithm) {
        if (algorithm == null) {
            throw new IllegalArgumentException("algorithm must not be null");
        }
        for (String known : SORT_ALGORITHMS) {
            if (known.equals(algorithm)) return;
        }
        throw new IllegalArgumentException(
                "Invalid algorithm: " + algorithm + " — expected one of " + String.join(", ", SORT_ALGORITHMS));
    }

    private static ServiceRequest[] copyOf(ServiceRequest[] arr) {
        ServiceRequest[] copy = new ServiceRequest[arr.length];
        System.arraycopy(arr, 0, copy, 0, arr.length);
        return copy;
    }

    private static void runSortCore(String algorithm, ServiceRequest[] arr, String sortBy) {
        switch (algorithm) {
            case "selection" -> selectionSortCore(arr, sortBy);
            case "insertion" -> insertionSortCore(arr, sortBy);
            case "merge" -> mergeSortRecursive(arr, sortBy);
            case "quick" -> quickSortRecursive(arr, 0, arr.length - 1, sortBy, true);
            case "quickLastPivot" -> quickSortRecursive(arr, 0, arr.length - 1, sortBy, false);
            default -> throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        }
    }




    public static long timeSort(String algorithm, ServiceRequest[] arr, String sortBy) {
        requireAlgorithm(algorithm);
        validateSortInput(arr, sortBy);

        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            runSortCore(algorithm, copyOf(arr), sortBy);
        }

        ServiceRequest[][] copies = new ServiceRequest[TIMED_ITERATIONS][];
        for (int i = 0; i < TIMED_ITERATIONS; i++) {
            copies[i] = copyOf(arr);
        }

        long start = System.nanoTime();
        for (int i = 0; i < TIMED_ITERATIONS; i++) {
            runSortCore(algorithm, copies[i], sortBy);
        }
        long elapsed = System.nanoTime() - start;

        return elapsed / TIMED_ITERATIONS;
    }
}