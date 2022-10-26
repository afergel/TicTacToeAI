package tictactoeai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.Random;

/**
 * A basic game of tic-tac-toe that runs in the console.
 * The player plays against an AI that uses the minimax algorithm.
 * 
 * @author afergel
 */
public class TicTacToeAI {
    
    /**
     * On the board, 0 represents a blank spot
     * 1 represents a spot with the player's mark
     * -1 represents a spot with the computer's mark
     */
    static int[][] board = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
    static boolean cpuGoesFirst;
    static boolean validChoice;
    
    public static void main(String[] args) {
        
        final Scanner SCAN = new Scanner(System.in);
        final Random RAND = new Random();
        
        System.out.print("Do you want to go first? (Y/N) > ");
        validChoice = false;
        while (!validChoice) {
            String goFirst = SCAN.next();
            if (goFirst.toUpperCase().equals("Y") || goFirst.toUpperCase().equals("N")) {
                cpuGoesFirst = goFirst.toUpperCase().equals("N");
                validChoice = true;
            }
            else System.out.print("Invalid input; Please type \"Y\" or \"N\" >");
        }
        
        // One iteration of this loop represents one game
        while (true) {
            
            // List of available spaces
            ArrayList<Integer> options = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

            // If the computer goes first, it places a mark randomly
            if (cpuGoesFirst) {
                int firstMove = RAND.nextInt(9);
                board[firstMove / 3][firstMove % 3] = -1;
                options.remove(firstMove);
            }

            // Main gameplay loop
            while (true) {
                printBoard();
                
                // Player's turn
                System.out.print("Choose a square (0 - 8) > ");
                int choice;
                validChoice = false;
                while (!validChoice) {
                    if (SCAN.hasNextInt()) {
                        choice = SCAN.nextInt();
                        if (options.contains(choice)) {
                            validChoice = true;
                            board[choice / 3][choice % 3] = 1;
                            options.remove(options.indexOf(choice));
                        }
                        else {
                            // This prints if the selected space is already taken or if the number is outside the range 0-8
                            System.out.print("Invalid number; Please try again > ");
                        }
                    }
                    else {
                        System.out.print("Please type an integer > ");
                        SCAN.nextLine();
                    }
                }
                // After the player makes a move, check if they won or if the board is full
                if (checkWin(board) == 1 || options.isEmpty()) break;

                // Computer's turn
                ArrayList<Integer> analysis = analyzeBoard(board, options, true);
                int computerChoice = options.get(analysis.indexOf(Collections.min(analysis)));
                board[computerChoice / 3][computerChoice % 3] = -1;
                options.remove(options.indexOf(computerChoice));
                
                // After the computer makes a move, check if it won or if the board is full
                if (checkWin(board) == -1 || options.isEmpty()) break;
            }

            printBoard();
            switch (checkWin(board)) {
                case 1:
                    System.out.println("You won (Somehow?).");
                    break;
                case -1:
                    System.out.println("You lost.");
                    break;
                default:
                    System.out.println("It's a tie.");
                    break;
            }
        
            // Reset all spaces on the board to 0
            for (int[] row : board) {
                for (int i = 0; i < 3; i++) {
                    row[i] = 0;
                }
            }
            
            // The player and the computer alternate between who goes first
            cpuGoesFirst = !cpuGoesFirst;
        }
    }
    
    /**
     * Uses a straightforward minimax algorithm to determine the best possible move on a given board.
     * 
     * @param board the board to be analyzed
     * @param options the list of available spaces the computer may choose
     * @param isComputersTurn whether or not the computer can make the next move on the board being analyzed
     * @return an ArrayList containing the "values" of all possible options
     */
    public static ArrayList<Integer> analyzeBoard(int[][] board, ArrayList<Integer> options, boolean isComputersTurn) {
        
        /**
         * If the game is already over, return an ArrayList containing only:
         *      1 if the player wins
         *      -1 if the computer wins
         *      0 if it's a draw
         */
        if (checkWin(board) == 1 || checkWin(board) == -1 || options.isEmpty()) {
            return new ArrayList<>(Arrays.asList(checkWin(board)));
        }
        
        // If the game is not over yet, analyze each possible option
        ArrayList<Integer> optionValues = new ArrayList<>();
        int i = 0;
        
        // For each possible option, run this method recursively until the game reaches an end statep
        for (int choice : options) {
            
            ArrayList<Integer> tempOptions = new ArrayList<>();
            for (int num : options) {
                tempOptions.add(num);
            }
            
            int[][] tempBoard = new int[3][3];
            for (int j = 0; j < 3; j++) {
                System.arraycopy(board[j], 0, tempBoard[j], 0, 3);
            }
            
            tempBoard[choice / 3][choice % 3] = (isComputersTurn) ? -1 : 1;
            tempOptions.remove(i);
            i++;
            if (isComputersTurn) {
                optionValues.add(Collections.max(analyzeBoard(tempBoard, tempOptions, !isComputersTurn)));
            }
            else {
                optionValues.add(Collections.min(analyzeBoard(tempBoard, tempOptions, !isComputersTurn)));
            }
        }
        
        return optionValues;
    }
    
    /**
     * Checks to see if a given board has a winner.
     * 
     * @param board the board to be analyzed
     * @return 1 if the player has won, -1 if the computer has won, and 0 if there's no current winner
     */
    public static int checkWin(int[][] board) {
        for (int[] row : board) {
            int total = 0;
            for (int square : row) total += square;
            if (total == 3) return 1;
            if (total == -3) return -1;
        }
        
        for (int i = 0; i < 3; i++) {
            int total = 0;
            for (int[] row : board) total += row[i];
            if (total == 3) return 1;
            if (total == -3) return -1;
        }
        
        int diagonalLTR = board[0][0] + board[1][1] + board[2][2];
        int diagonalRTL = board[0][2] + board[1][1] + board[2][0];
        if (diagonalLTR == 3 || diagonalRTL == 3) return 1;
        if (diagonalLTR == -3 || diagonalRTL == -3) return -1;
        
        return 0;
    }
    
    /**
     * Prints the board to the console.
     * The player going first always has X; the player going second always has O
     * Blank spaces are marked with a hyphen
     */
    public static void printBoard() {
        for (int[] row : board) {
            for (int square : row) {
                switch (square) {
                    case 1:
                        System.out.print(cpuGoesFirst ? "O " : "X ");
                        break;
                    case -1:
                        System.out.print(cpuGoesFirst ? "X " : "O ");
                        break;
                    default:
                        System.out.print("- ");
                        break;
                }
            }
            System.out.println();
        }
    }
}