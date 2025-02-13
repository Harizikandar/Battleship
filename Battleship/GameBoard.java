import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private char[][] board;
    private final int rows;
    private final int cols;
    private List<Ship> ships;

    public GameBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new char[rows][cols];
        this.ships = new ArrayList<>();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = '-';
            }
        }
    }

    public void displayBoard(boolean hideShips) {
        System.out.print("  ");
        for (int col = 0; col < cols; col++) {
            System.out.print((char) ('A' + col) + " ");
        }
        System.out.println();

        for (int i = 0; i < rows; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < cols; j++) {
                char symbol = board[i][j];
                System.out.print((hideShips && symbol == 'S' ? "-" : symbol) + " ");
            }
            System.out.println();
        }
    }

    public boolean placeShip(Ship ship, List<String> positions) {
        if (!isPositionsEmpty(positions)) return false;

        ship.placeShip(positions);
        ships.add(ship);
        
        for (String pos : positions) {
            int[] coords = convertPosition(pos);
            board[coords[0]][coords[1]] = 'S';
        }
        return true;
    }

    private boolean isPositionsEmpty(List<String> positions) {
        for (String pos : positions) {
            int[] coords = convertPosition(pos);
            if (coords == null || board[coords[0]][coords[1]] != '-') return false;
        }
        return true;
    }

    private int[] convertPosition(String position) {
        if (position.length() < 2) return null;
        
        char column = position.toUpperCase().charAt(0);
        int row;
        try {
            row = Integer.parseInt(position.substring(1)) - 1;
        } catch (NumberFormatException e) {
            return null;
        }

        int col = column - 'A';
        return (row >= 0 && row < rows && col >= 0 && col < cols) ? new int[]{row, col} : null;
    }

    public String attackPosition(String position) {
        int[] coords = convertPosition(position);
        if (coords == null) return "Invalid position!";
        
        // If this cell was already attacked, report it.
        char cell = board[coords[0]][coords[1]];
        if (cell == 'X' || cell == 'M') {
            return "Position already attacked!";
        }
        
        // Check if any ship occupies this position.
        for (Ship ship : ships) {
            if (ship.containsPosition(position)) {
                board[coords[0]][coords[1]] = 'X';
                String result = ship.registerHit(position);
                return result;
            }
        }
        
        board[coords[0]][coords[1]] = 'M';
        return "Miss!";
    }

    public boolean allShipsSunk() {
        return ships.stream().allMatch(Ship::isSunk);
    }
    
    // Getter for the board array so the GUI can update its display.
    public char[][] getBoard() {
        return board;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }

    public List<Ship> getShips() {
        return ships;
    }
    
}
