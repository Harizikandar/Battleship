import java.util.*;

public class BruteForce {
    private final int BOARD_SIZE = 10;
    private char[][] board;
    private Queue<String> moves;
    private Set<String> remainingMoves;
    
    public BruteForce() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        moves = new LinkedList<>();
        remainingMoves = new HashSet<>();
        initializeBoard();
        generateMoves();
    }
    
    private void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '-';
            }
        }
    }
    
    private void generateMoves() {
        // Generate all possible moves in a systematic pattern
        // First, add moves in a checkerboard pattern
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = (i % 2); j < BOARD_SIZE; j += 2) {
                String move = (char)('A' + j) + String.valueOf(i + 1);
                moves.add(move);
                remainingMoves.add(move);
            }
        }
        
        // Then add the remaining squares
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = ((i + 1) % 2); j < BOARD_SIZE; j += 2) {
                String move = (char)('A' + j) + String.valueOf(i + 1);
                moves.add(move);
                remainingMoves.add(move);
            }
        }
    }
    
    public String getNextMove() {
        if (moves.isEmpty()) {
            generateMoves(); // Regenerate moves if we've run out
        }
        String move = moves.poll();
        remainingMoves.remove(move);
        return move;
    }
    
    public void updateBoard(String position, String result) {
        int col = position.charAt(0) - 'A';
        int row = Integer.parseInt(position.substring(1)) - 1;
        
        if (result.contains("Hit")) {
            board[row][col] = 'H';
            // Prioritize adjacent squares if it's a hit
            prioritizeAdjacentSquares(row, col);
        } else if (result.contains("Miss")) {
            board[row][col] = 'M';
        }
    }
    
    private void prioritizeAdjacentSquares(int row, int col) {
        // Remove adjacent squares from their current position in the queue
        // and add them to the front if they haven't been tried yet
        String[] adjacentMoves = {
            (char)('A' + col) + String.valueOf(row),     // Up
            (char)('A' + col) + String.valueOf(row + 2), // Down
            (char)('A' + (col - 1)) + String.valueOf(row + 1), // Left
            (char)('A' + (col + 1)) + String.valueOf(row + 1)  // Right
        };
        
        List<String> currentMoves = new ArrayList<>(moves);
        moves.clear();
        
        // Add valid adjacent moves first
        for (String move : adjacentMoves) {
            if (remainingMoves.contains(move)) {
                moves.add(move);
            }
        }
        
        // Add back all other moves
        for (String move : currentMoves) {
            if (!Arrays.asList(adjacentMoves).contains(move)) {
                moves.add(move);
            }
        }
    }
    
    public boolean hasMovesLeft() {
        return !moves.isEmpty();
    }
}
