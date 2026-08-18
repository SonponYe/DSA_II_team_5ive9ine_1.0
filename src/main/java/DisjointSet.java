public class DisjointSet {

    private final int[] parent;
    private final int[] rank;

    public DisjointSet(int size) {
        parent = new int[size];
        rank = new int[size];
        for (int i = 0; i < size; i++) {
            parent[i] = i;
        }
    }

    public void makeSet(int element) {
        parent[element] = element;
        rank[element] = 0;
    }

    public int find(int element) {                 // with path compression
        if (parent[element] != element) {
            parent[element] = find(parent[element]);
        }
        return parent[element];
    }

    public void union(int elementA, int elementB) { // by rank
        int rootA = find(elementA);
        int rootB = find(elementB);

        if (rootA == rootB) {
            return;
        }

        if (rank[rootA] < rank[rootB]) {
            parent[rootA] = rootB;
        } else if (rank[rootA] > rank[rootB]) {
            parent[rootB] = rootA;
        } else {
            parent[rootB] = rootA;
            rank[rootA]++;
        }
    }

    public boolean connected(int elementA, int elementB) {
        return find(elementA) == find(elementB);
    }
}
