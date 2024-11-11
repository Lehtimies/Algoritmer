public class DisjunctSets {
    private int [] cellArray;

    // Initialize a DisjunctSet
    public DisjunctSets(int numElements) {
        cellArray = new int[numElements];
        for (int i = 0; i < cellArray.length; i++)
            cellArray[i] = -1;
    }

    public void union(int x, int y) {
        int root1 = find(x);
        int root2 = find(y);

        if (root1 != root2) { // Only union if roots are different
            if (cellArray[root2] < cellArray[root1]) {
                cellArray[root2] += cellArray[root1];
                cellArray[root1] = root2;
            } else {
                cellArray[root1] += cellArray[root2];
                cellArray[root2] = root1;
            }
        }
    }

    public int find(int x) {
        if (cellArray[x] < 0) {
            return x; // x is the root
        } else {
            cellArray[x] = find(cellArray[x]); // Path compression: make x point directly to the root
            return cellArray[x];
        }
    }


    public int [] getCellArray() {
        return cellArray;
    }

    public boolean allConnected() {
        int root = cellArray[find(0)]; // Find the root of the first element
        return root == -cellArray.length;
    }
}

