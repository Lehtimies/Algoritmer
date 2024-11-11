public class DisjunctSets {
    private int [] cellArray;

    /* Initiera en disjunkt mängd */
    public DisjunctSets(int numElements) {
        cellArray = new int[numElements];
        for (int i = 0; i < cellArray.length; i++)
            cellArray[i] = -1;
    }

    public void union(int rot1, int rot2) {
        if ( cellArray[rot2] < cellArray[rot1] ) /* rot2 är större */ {
            cellArray[rot2] += cellArray[rot1]; /* Addera storekarna */
            cellArray[rot1] = rot2; /* rot2 blir ny rot */
        } else /* rot1 är det större trädet */ {
            cellArray[rot1] += cellArray[rot2];
            cellArray[rot2] = rot1; /* rot1 blir ny rot */
        }
    }
    public int find(int x) {
        System.out.println("CellArray[x]: " + cellArray[x]);
        if( cellArray[x] < 0 ) /* x är en rot, returnera den */
            return x;
        else return find(cellArray[x]); /* annars gå ett steg uppåt */
    }

    public int [] getCellArray() {
        return cellArray;
    }
}

