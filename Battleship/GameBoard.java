import java.util.ArrayList;
import java.util.List;

public class GameBoard {
    private char[][] board; // 2D array to represent the board
    private final int rows;
    private final int cols;

    // Constructor to initialize the board
    public GameBoard(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        this.board = new char[rows][cols];
        initializeBoard();
    }

    // Initialize the board with empty space ('-')
    private void initializeBoard(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                board[i][j] = '-';
            }
        }
    }

    // Display the board to the console
    public void displayBoard(boolean hideShips){
        System.out.print(" ");
        for(int col = 0; col < cols; col++){
            System.out.print((char) ('A' + col) + " ");
        }
        System.out.println();

        for(int i = 0; i < rows; i++){
            System.out.print((i + 1) + " ");
            for (int j = 0; j < cols; j++){
                if(hideShips && board[i][j] == 'S'){
                    System.out.print("- ");
                }else{
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

    // Place a ship on the board
    public boolean placeShip(Ship ship, List<String> positions){
        // Check if positions count matches ship size
        if(positions.size() != ship.getSize()){
            return false;
        }
        // Validate positions availability and cantiquity
        if (validateShipPlacement(positions)) {
            // Convert positions to uppercase for consistency
            List<String> upperPositions = new ArrayList<>();
            for(String pos : positions){
                upperPositions.add(pos.toUpperCase());
            }
            ship.placeShip(upperPositions); // Store uppercase positions in Ship
            for(String pos : positions){
                int[] coordinates = convertPosition(pos);
                board[coordinates[0]][coordinates[1]] = 's';
            }
            return true;
        }
        return false;
    }

    // Validate ship placement
    private boolean validateShipPlacement(List<String> positions) {
        // Check if positions are valid and unoccupied
        for (String pos : positions) {
            int[] coordinates = convertPosition(pos);
            if (coordinates == null || board[coordinates[0]][coordinates[1]] != '-') {
                return false; // Invalid position or already occupied
            }
        }

        // Check if positions are contiguous and aligned
        List<String> sortedPositions = new ArrayList<>(positions);
        sortedPositions.sort((a, b) -> {
            int[] coordA = convertPosition(a);
            int[] coordB = convertPosition(b);
            return (coordA[0] == coordB[0]) ? Integer.compare(coordA[1], coordB[1]) 
                                            : Integer.compare(coordA[0], coordB[0]);
        });

        int prevRow = convertPosition(sortedPositions.get(0))[0];
        int prevCol = convertPosition(sortedPositions.get(0))[1];
        for (int i = 1; i < sortedPositions.size(); i++) {
            int[] curr = convertPosition(sortedPositions.get(i));
            if (curr[0] != prevRow && curr[1] != prevCol) {
                return false; // Diagonal placement
            }
            if (curr[0] == prevRow) {
                if (curr[1] != prevCol + 1) return false; // Horizontal gap
            } else {
                if (curr[0] != prevRow + 1) return false; // Vertical gap
            }
            prevRow = curr[0];
            prevCol = curr[1];
        }

        return true;
    }


    // Convert a position like "A1" to board coordinates
    private int[] convertPosition(String position){
        position = position.toUpperCase();
        if(position.length() < 2) return null;

        char column = position.charAt(0);
        int row;
        try{
            row = Integer.parseInt(position.substring(1)) - 1;
        }catch (NumberFormatException e){
            return null;
        }

        int col = column - 'A';
        if(row >= 0 && row < rows && col >= 0 && col < cols){
            return new int[]{row, col};
        }
        return null;
    }
}