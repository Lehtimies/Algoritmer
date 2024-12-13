//package tictactoe;

import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;
import javax.swing.*;

// Class to represent Move objects
class Move {
    int val;   // Value of th emov
    int row;   // Row and column coordinates
    int col;

    public Move(int v, int r, int c) {
        val=v;
        row=r;
        col=c;
    }
}

// Class Button extends JButton with (x,y) coordinates
class Button extends javax.swing.JButton {
    public int i;   // The row and column coordinate of the button in a GridLayout
    public int j;

    public Button (int x, int y) {
        // Create a JButton with a blank icon. This also gives the button its correct size.
        super();
        super.setIcon(new javax.swing.ImageIcon(getClass().getResource("None.png")));
        this.i = x;
        this.j = y;
    }

    // Return row coordinate
    public int get_i () {
        return i;
    }

    // Return column coordinate
    public int get_j () {
        return j;
    }

}

public class LS extends javax.swing.JFrame {

    // Marks on the board
    public static final int EMPTY    = 0;
    public static final int HUMAN    = 1;
    public static final int COMPUTER = 2;

    // Outcomes of the game
    public static final int HUMAN_WIN    = 4;
    public static final int DRAW         = 5;
    public static final int CONTINUE     = 6;
    public static final int COMPUTER_WIN = 7;

    public static final int SIZE = 3;
    private final int[][] board = new int[SIZE][SIZE];  // The marks on the board
    private javax.swing.JButton[][] jB;           // The buttons of the board
    private int turn = HUMAN;                    // HUMAN starts the game

    /* Constructor for the Tic Tac Toe game */
    public LS() {
        // Close the window when the user exits
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        initBoard();      // Set up the board with all marks empty
    }

    // Initalize an empty board. 
    private void initBoard(){
        // Create a SIZE*SIZE gridlayput to hold the buttons
        java.awt.GridLayout layout = new GridLayout(SIZE, SIZE);
        getContentPane().setLayout(layout);

        // The board is a grid of buttons
        jB = new Button[SIZE][SIZE];
        for (int i=0; i<SIZE; i++) {
            for (int j=0; j<SIZE; j++) {
                // Create a new button and add an actionListerner to it
                jB[i][j] = new Button(i,j);
                // Add an action listener to the button to handle mouse clicks
                jB[i][j].addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent act) {
                        jBAction(act);
                    }
                });
                add(jB[i][j]);   // Add the buttons to the GridLayout

                board[i][j] = EMPTY;     // Initialize all marks on the board to empty
            }
        }
        // Pack the GridLayout and make it visible
        pack();
    }

    // Action listener which handles mouse clicks on the buttons
    private void jBAction(java.awt.event.ActionEvent act) {
        if (turn == HUMAN) {
            Button thisButton = (Button) act.getSource();   // Get the button clicked on
            // Get the grid coordinates of the clicked button
            int i = thisButton.get_i();
            int j = thisButton.get_j();
            if (board[i][j] != EMPTY) {
                System.out.println("Please choose an empty square");
                return;
            }

            thisButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("X.png")));
            place (i, j, turn);    // Mark the move on the board
            // Give the turn to the opponent
            turn = (turn == HUMAN) ? COMPUTER : HUMAN;
        }

        if (printResult() != CONTINUE) {
            return;
        }

        if (turn == COMPUTER) {
            Move bestMove = findCompMove();
            Button thisButton = (Button) jB[bestMove.row][bestMove.col];
            thisButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("O.png")));
            place(bestMove.row, bestMove.col, COMPUTER);

            if (printResult() != CONTINUE) {
                return;
            }

            turn = (turn == HUMAN) ? COMPUTER : HUMAN;
        }
    }

    private int printResult() {
		return switch (checkResult()) {
			case HUMAN_WIN -> {
				System.out.println("Winner Winner chicken dinner, you cheater!");
				yield HUMAN_WIN;
			}
			case COMPUTER_WIN -> {
				System.out.println("Womp Womp, Computer wins");
				yield COMPUTER_WIN;
			}
			case DRAW -> {
				System.out.println("It's a draw! Best you could do huh?");
				yield DRAW;
			}
			default -> CONTINUE;
		};
	}

    /**
     * This function should check if one player (HUMAN or COMPUTER) wins, if the board is full (DRAW)
     * or if the game should continue.
     * @return HUMAN_WIN, COMPUTER_WIN, DRAW or CONTINUE
     */
    private int checkResult() {
        int result = CONTINUE;

        // Check rows for a win
        for (int i = 0; i < SIZE; i++) {
            if (board[i][0] == HUMAN && board[i][1] == HUMAN && board[i][2] == HUMAN) {
                result = HUMAN_WIN;
            }
            if (board[i][0] == COMPUTER && board[i][1] == COMPUTER && board[i][2] == COMPUTER) {
                result = COMPUTER_WIN;
            }
        }

        // Check columns for a win
        for (int i = 0; i < SIZE; i++) {
            if (board[0][i] == HUMAN && board[1][i] == HUMAN && board[2][i] == HUMAN) {
                result = HUMAN_WIN;
            }
            if (board[0][i] == COMPUTER && board[1][i] == COMPUTER && board[2][i] == COMPUTER) {
                result = COMPUTER_WIN;
            }
        }

        // Check diagonals for a win
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == HUMAN) {
                result = HUMAN_WIN;
            }
            if (board[0][0] == COMPUTER) {
                result = COMPUTER_WIN;
            }
        }

        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == HUMAN) {
                result = HUMAN_WIN;
            }
            if (board[0][2] == COMPUTER) {
                result = COMPUTER_WIN;
            }
        }

        // Check if the board is full
        if (fullBoard()) {
            result = DRAW;
        }

        return result;
    }

    /**
     * Check if the board is full
     * @return true if the board is full, false otherwise
     */
    private boolean fullBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Place a mark for one of the players (HUMAN or COMPUTER) in the specified position
      */
    public void place (int row, int col, int player){
        board [row][col] = player;
    }

    /**
     * Find the best move for the computer
     * @return the best move for the computer
     */
    private Move findCompMove() {
        int responseValue;
        Move bestMove = new Move(-1, 0, 0);

        if (checkResult() != CONTINUE) {
            return new Move(checkResult(), 0, 0);
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    place(i, j, COMPUTER);
                    responseValue = findHumanMoveValue(false);
                    place(i, j, EMPTY);
                    if (responseValue > bestMove.val) {
                        bestMove.val = responseValue;
                        bestMove.row = i;
                        bestMove.col = j;
                    }
                }
            }
        }
        return bestMove;
    }

    /**
     * Find the best move for the human though a minmax algorithm
     * @return the best move for the human
     */
    private int findHumanMoveValue(Boolean computerMove) {
        int result = checkResult();

        if (result == HUMAN_WIN || result == COMPUTER_WIN) {
            return result;
        }
        if (fullBoard()) {
            return DRAW;
        }

		int bestMoveValue;
		if (computerMove) {
			bestMoveValue = -1000;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        place(i, j, COMPUTER);
                        bestMoveValue = Math.max(bestMoveValue, findHumanMoveValue(false));
                        place(i, j, EMPTY);
                    }
                }
            }
		} else {
			bestMoveValue = 1000;
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        place(i, j, HUMAN);
                        bestMoveValue = Math.min(bestMoveValue, findHumanMoveValue(true));
                        place(i, j, EMPTY);
                    }
                }
            }
		}
		return bestMoveValue;
	}

    public static void main (String [] args){

        String threadName = Thread.currentThread().getName();
        LS lsGUI = new LS();      // Create a new user inteface for the game
        lsGUI.setVisible(true);

        java.awt.EventQueue.invokeLater (new Runnable() {
            public void run() {
                while ( (Thread.currentThread().getName() == threadName) &&
                        (lsGUI.checkResult() == CONTINUE) ){
                    try {
                        Thread.sleep(100);  // Sleep for 100 millisecond, wait for button press
                    } catch (InterruptedException e) { }
				}
            }
        });
    }
}