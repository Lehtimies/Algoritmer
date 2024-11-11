import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.UIManager;
import javax.swing.JOptionPane;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Ex1 {
    // TODO: Ändra tillbaka till WIDTH = 800 och HEIGHT = 800
    private static final int WIDTH = 500;  // Size of the window in pixels
    private static final int HEIGHT = 500;

    static int cells=100;    // The size of the maze is cells*cells (default is 20*20)

    public static void main(String[] args) {

        // Get the size of the maze from the command line
        if (args.length > 0) {
            try {
                cells = Integer.parseInt(args[0]);  // The maze is of size cells*cells
            } catch (NumberFormatException e) {
                System.err.println("Argument " + args[0] + " should be an integer");
                System.exit(-1);
            }
        }
        // Check that the size is valid
        if ( (cells <= 1) || (cells > 100) ) {
            System.err.println("Invalid size, must be between 2 and 100 ");
            System.exit(-1);
        }
        Runnable r = new Runnable() {
            public void run() {
                // Create a JComponent for the maze
                MazeComponent mazeComponent = new MazeComponent(WIDTH, HEIGHT, cells);
                // Change the text of the OK button to "Close"
                UIManager.put("OptionPane.okButtonText", "Close");
                JOptionPane.showMessageDialog(null, mazeComponent, "Maze " + cells + " by " + cells,
                        JOptionPane.INFORMATION_MESSAGE);
            }
        };
        SwingUtilities.invokeLater(r);
    }
}

class MazeComponent extends JComponent {
    protected int width;
    protected int height;
    protected int cells;
    protected int cellWidth;
    protected int cellHeight;
    Random random;

    // Draw a maze of size w*h with c*c cells
    MazeComponent(int w, int h, int c) {
        super();
        cells = c;                // Number of cells
        cellWidth = w/cells;      // Width of a cell
        cellHeight = h/cells;     // Height of a cell
        width =  c*cellWidth;     // Calculate exact dimensions of the component
        height = c*cellHeight;
        setPreferredSize(new Dimension(width+1,height+1));  // Add 1 pixel for the border
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.yellow);                    // Yellow background
        g.fillRect(0, 0, width, height);

        // Draw a grid of cells
        g.setColor(Color.blue);                 // Blue lines
        for (int i = 0; i<=cells; i++) {        // Draw horizontal grid lines
            g.drawLine (0, i*cellHeight, cells*cellWidth, i*cellHeight);
        }
        for (int j = 0; j<=cells; j++) {       // Draw verical grid lines
            g.drawLine (j*cellWidth, 0, j*cellWidth, cells*cellHeight);
        }

        // Mark entry and exit cells
        paintCell(0,0,Color.green, g);               // Mark entry cell
        drawWall(-1, 0, 2, g);                       // Open up entry cell
        paintCell(cells-1, cells-1,Color.pink, g);   // Mark exit cell
        drawWall(cells-1, cells-1, 2, g);            // Open up exit cell

        g.setColor(Color.yellow);                 // Use yellow lines to remove existing walls
        createMaze(cells, g);
    }

    private void createMaze (int cells, Graphics g) {
        // Start runtime counter
        long startTime = System.currentTimeMillis();

        // Initialize random and create a variable for totalCells
        random = new Random();
        int totalCells = cells * cells;

        // Create a new disjunctSet for all the cells
        DisjunctSets maze = new DisjunctSets(totalCells);

        // Remove walls until every cell belongs to the same disjunctSet
        while (!maze.allConnected()) {
            int [] mazeArray = maze.getCellArray();
            int randomCell = random.nextInt(mazeArray.length);
            int randomCellRoot = maze.find(randomCell);

            // Get cells adjacent to the randomCell
            int cellToLeft = randomCell - 1;
            int cellToRight = randomCell + 1;
            int cellAbove = randomCell - cells;
            int cellBelow = randomCell + cells;

            // Get random wall (number between 0-3); 0 left, 1 up, 2 right, 3 down
            int randomWall = random.nextInt(4);

            // Check if cell is outer cell and wall is outer wall
            int xCoordinate = randomCell % cells;
            int yCoordinate = randomCell / cells;

            // If outer wall then flip the wall to be removed
            if (xCoordinate == 0 && randomWall == 0) {
                randomWall = 2;
            }
            if (xCoordinate == cells-1 && randomWall == 2) {
                randomWall = 0;
            }
            if (yCoordinate == 0 && randomWall == 1) {
                randomWall = 3;
            }
            if (yCoordinate == cells-1 && randomWall == 3) {
                randomWall = 1;
            }

            /* Select adjacent cell based on wall direction
            If the selected cell and adjacent cell have the same root then break out of the switch case
            and choose a new random cell */
            switch (randomWall) {
                case 0:
                    int cellToLeftRoot = maze.find(cellToLeft);
                    if (randomCellRoot == cellToLeftRoot) {
                        break;
                    }
                    maze.union(cellToLeftRoot, randomCellRoot);
                    drawWall(xCoordinate, yCoordinate, randomWall, g);
                    break;
                case 1:
                    int cellAboveRoot = maze.find(cellAbove);
                    if (randomCellRoot == cellAboveRoot) {
                        break;
                    }
                    maze.union(cellAboveRoot, randomCellRoot);
                    drawWall(xCoordinate, yCoordinate, randomWall, g);
                    break;
                case 2:
                    int cellToRightRoot = maze.find(cellToRight);
                    if (randomCellRoot == cellToRightRoot) {
                        break;
                    }
                    maze.union(cellToRight, randomCell);
                    drawWall(xCoordinate, yCoordinate, randomWall, g);
                    break;
                case 3:
                    int cellBelowRoot = maze.find(cellBelow);
                    if (randomCellRoot == cellBelowRoot) {
                        break;
                    }
                    maze.union(cellBelow, randomCell);
                    drawWall(xCoordinate, yCoordinate, randomWall, g);
                    break;
            }
        }

        // End of runtime and print time
        long endTime = System.currentTimeMillis();
        long runTime = endTime - startTime;
        System.out.println("Maze creation runtime: " + runTime + " ms");

    }

    // Paints the interior of the cell at postion x,y with colour c
    private void paintCell(int x, int y, Color c, Graphics g) {
        int xpos = x*cellWidth;    // Position in pixel coordinates
        int ypos = y*cellHeight;
        g.setColor(c);
        g.fillRect(xpos+1, ypos+1, cellWidth-1, cellHeight-1);
    }


    // Draw the wall w in cell (x,y) (0=left, 1=up, 2=right, 3=down)
    private void drawWall(int x, int y, int w, Graphics g) {
        int xpos = x*cellWidth;    // Position in pixel coordinates
        int ypos = y*cellHeight;

        switch(w){
            case (0):       // Wall to the left
                g.drawLine(xpos, ypos+1, xpos, ypos+cellHeight-1);
                break;
            case (1):       // Wall at top
                g.drawLine(xpos+1, ypos, xpos+cellWidth-1, ypos);
                break;
            case (2):      // Wall to the right
                g.drawLine(xpos+cellWidth, ypos+1, xpos+cellWidth, ypos+cellHeight-1);
                break;
            case (3):      // Wall at bottom
                g.drawLine(xpos+1, ypos+cellHeight, xpos+cellWidth-1, ypos+cellHeight);
                break;
        }
    }
}

class DisjunctSets {
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

