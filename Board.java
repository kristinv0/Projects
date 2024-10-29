//Kristin Vo - vo000100
import java.util.ArrayList;
import java.util.Scanner;

public class Board {
    public static final int boardSize = 8;
    public static Scanner scanner = new Scanner(System.in); // Scanner object for user input

    // Initialize the board with starting pieces
    public static void initializeBoard(ArrayList<ArrayList<Character>> board) {
        for (int row = 0; row < boardSize; row++) {
            board.add(new ArrayList<>());
            for (int col = 0; col < boardSize; col++) {
                board.get(row).add('-');
            }
        }
        board.get(3).set(3, 'B');
        board.get(3).set(4, 'W');
        board.get(4).set(3, 'W');
        board.get(4).set(4, 'B');
    }

    // Display the current state of the board with valid moves indicated
    public static void render(ArrayList<ArrayList<Character>> board, ArrayList<int[]> validMoves) {
        System.out.print("  ");
        for (int col = 0; col < boardSize; col++) {
            System.out.print(col + " ");
        }
        System.out.println();

        for (int row = 0; row < boardSize; row++) {
            System.out.print(row + " ");
            for (int col = 0; col < boardSize; col++) {
                char spot = board.get(row).get(col);
                if (spot == '-') {
                    for (int[] move : validMoves) {
                        if (move[0] == row && move[1] == col) {
                            spot = 'V'; // Mark valid moves as 'V'
                            break;
                        }
                    }
                }
                System.out.print(spot + " ");
            }
            System.out.println();
        }
    }

    // Get the valid moves for a player
    public static ArrayList<int[]> getValidMoves(ArrayList<ArrayList<Character>> board, char player) {
        ArrayList<int[]> validMoves = new ArrayList<>();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (isValidMove(board, row, col, player)) {
                    validMoves.add(new int[]{row, col});
                }
            }
        }

        return validMoves;
    }

    // Check if a move is valid for a player
    public static boolean isValidMove(ArrayList<ArrayList<Character>> board, int row, int col, char player) {
        if (board.get(row).get(col) == '-') {
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr != 0 || dc != 0) {
                        int r = row + dr;
                        int c = col + dc;
                        boolean foundOpponent = false;
                        while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board.get(r).get(c) != '-') {
                            if (board.get(r).get(c) != player) {
                                foundOpponent = true;
                            } else if (foundOpponent) {
                                return true;
                            }
                            r += dr;
                            c += dc;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Make a move for a player
    public static void makeMove(ArrayList<ArrayList<Character>> board, int row, int col, char player) {
        if (isValidMove(board, row, col, player)) {
            board.get(row).set(col, player);
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr != 0 || dc != 0) {
                        int r = row + dr;
                        int c = col + dc;
                        boolean foundOpponent = false;
                        ArrayList<int[]> toFlip = new ArrayList<>();
                        while (r >= 0 && r < boardSize && c >= 0 && c < boardSize && board.get(r).get(c) != '-') {
                            if (board.get(r).get(c) != player) {
                                foundOpponent = true;
                                toFlip.add(new int[]{r, c});
                            } else if (foundOpponent) {
                                for (int[] position : toFlip) {
                                    board.get(position[0]).set(position[1], player);
                                }
                                break;
                            } else {
                                break;
                            }
                            r += dr;
                            c += dc;
                        }
                    }
                }
            }
        }
    }

    // Check if the game is over (no valid moves for both players)
    public static boolean isGameOver(ArrayList<ArrayList<Character>> board) {
        return getValidMoves(board, 'B').isEmpty() && getValidMoves(board, 'W').isEmpty();
    }

    // Determine the winner based on the number of pieces each player has
    public static char determineWinner(ArrayList<ArrayList<Character>> board) {
        int countX = 0;
        int countO = 0;

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board.get(row).get(col) == 'X') {
                    countX++;
                } else if (board.get(row).get(col) == 'O') {
                    countO++;
                }
            }
        }

        return countX > countO ? 'B' : countO > countX ? 'W' : 'T'; // 'T' for tie
    }

    public static void main(String[] args) {
        ArrayList<ArrayList<Character>> board = new ArrayList<>();
        initializeBoard(board);

        char currentPlayer = 'B';

        // Main game loop
        while (!isGameOver(board)) {
            ArrayList<int[]> validMoves = getValidMoves(board, currentPlayer);
            render(board, validMoves);

            if (validMoves.isEmpty()) {
                // No valid moves for the current player
                System.out.println("No valid moves for Player " + currentPlayer + ".");
            } else {
                if (currentPlayer == 'W') {
                    // AI player (random move selection)
                    int randomIndex = (int) (Math.random() * validMoves.size());
                    int[] randomMove = validMoves.get(randomIndex);
                    int row = randomMove[0];
                    int col = randomMove[1];
                    System.out.println("AI Player W chooses row: " + row + ", column: " + col);
                    makeMove(board, row, col, currentPlayer);
                } else {
                    // Human player
                    int row, col;
                    boolean validInput;
                    do {
                        validInput = true; // Assume valid input
                        System.out.print("Enter row (0-7): ");
                        row = scanner.nextInt();
                        System.out.print("Enter column (0-7): ");
                        col = scanner.nextInt();
                        if (!isValidMove(board, row, col, currentPlayer)) {
                            System.out.println("Invalid move. Try again.");
                            validInput = false;
                        }
                    } while (!validInput);
                    makeMove(board, row, col, currentPlayer);
                }
            }

            currentPlayer = (currentPlayer == 'B') ? 'W' : 'B';
        }

        render(board, new ArrayList<>()); // Render final board without valid moves
        char winner = determineWinner(board);
        if (winner == 'T') {
            System.out.println("Game over. It's a tie!");
        } else {
            System.out.println("Game over. Winner: Player " + winner);
        }

        // Count the number of pieces for each player and display
        int countB = countPieces(board, 'B');
        int countW = countPieces(board, 'W');
        System.out.println("Player B has " + countB + " pieces.");
        System.out.println("Player AI (W) has " + countW + " pieces.");
    }

    // Count the number of pieces a player has on the board
    public static int countPieces(ArrayList<ArrayList<Character>> board, char player) {
        int count = 0;
        for (ArrayList<Character> row : board) {
            for (char piece : row) {
                if (piece == player) {
                    count++;
                }
            }
        }
        return count;
    }
}
