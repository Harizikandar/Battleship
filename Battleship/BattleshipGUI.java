import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BattleshipGUI extends JFrame {
    private GameController gameController;
    private JPanel playerPanel;
    private JPanel enemyPanel;
    private JLabel messageLabel;
    private final int SIZE = 10;
    private JButton[][] playerButtons;
    private JButton[][] enemyButtons;

    // Brute Force fields
    private Timer bruteForceTimer;
    private int currentRow = 0;
    private int currentCol = 0;
    private int bfMoveCount = 0;
    private long bfStartTime = 0;
    
    // Probability Density fields
    private Timer probabilityTimer;
    private PDSearch pdSearch;  // inner class instance for PD mode
    private long pdStartTime = 0;
    
    // Parity Search fields
    private Timer parityTimer;
    private ArrayList<int[]> parityCells;
    private int parityIndex = 0;
    private int parityMoveCount = 0;
    private long parityStartTime = 0;

    public void startBruteForce() {
        currentRow = 0;
        currentCol = 0;
        bfMoveCount = 0;
        bfStartTime = System.nanoTime();
    
        if (bruteForceTimer != null && bruteForceTimer.isRunning()) {
            bruteForceTimer.stop();
        }
    
        updateStatsLabel(bfMoveCount, "Brute force search started...");
        bruteForceTimer = new Timer(500, e -> doOneBruteForceStep());
        bruteForceTimer.start();
    }
    
    
    public BattleshipGUI() {
        gameController = new GameController();
        setTitle("Battleship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    
        // Initialize button arrays first
        playerButtons = new JButton[SIZE][SIZE];
        enemyButtons = new JButton[SIZE][SIZE];
    
        // Top panel with message
        JPanel topPanel = new JPanel(new BorderLayout());
        messageLabel = new JLabel("Game started. Waiting for algorithm...", SwingConstants.CENTER);
        topPanel.add(messageLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
    
        // Panels for boards
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Added gap between boards
        playerPanel = new JPanel(new GridLayout(SIZE, SIZE, 1, 1)); // Added small gaps between cells
        enemyPanel = new JPanel(new GridLayout(SIZE, SIZE, 1, 1));

        // Create back button
        JButton backButton = new JButton("Back to Menu");
        backButton.setFont(new Font("Arial", Font.BOLD, 12));
        backButton.setBackground(new Color(255, 255, 255));
        backButton.setFocusPainted(false);

        // Add hover effect to back button
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(200, 200, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(255, 255, 255));
            }
        });
        
        // Add action listener to back button
        backButton.addActionListener(e -> {
            // Stop any running timers
            if (bruteForceTimer != null && bruteForceTimer.isRunning()) {
                bruteForceTimer.stop();
            }
            if (probabilityTimer != null && probabilityTimer.isRunning()) {
                probabilityTimer.stop();
            }
            if (parityTimer != null && parityTimer.isRunning()) {
                parityTimer.stop();
            }
            
            // Close current window and open menu
            dispose();
            SwingUtilities.invokeLater(() -> {
                Menu menu = new Menu();
                menu.setVisible(true);
            });
        });
        
        // Create a panel for the back button and add it to the left side
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(backButton);
        
        // Add components to top panel
        topPanel.add(buttonPanel, BorderLayout.WEST);
        messageLabel = new JLabel("Game started. Waiting for algorithm...", SwingConstants.CENTER);
        topPanel.add(messageLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Set sea colors for board panels
        Color seaColor = new Color(0, 105, 148); // Deep sea blue
        Color buttonColor = new Color(65, 157, 200); // Lighter blue for cells
        Color hitColor = new Color(178, 34, 34); // Red for hits
        Color missColor = new Color(169, 169, 169); // Gray for misses
        
        // Initialize player's board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = new JButton();
                button.setBackground(buttonColor);
                button.setPreferredSize(new Dimension(40, 40));
                button.setMargin(new Insets(0, 0, 0, 0));
                
                // Get the current cell state from the game controller
                char cellState = gameController.getPlayerBoard().getBoard()[i][j];
                
                // Set the button text/appearance based on the cell state
                if (cellState == 'S') {
                    button.setText("■"); // Ship symbol
                    button.setForeground(Color.WHITE); // White text for ships
                    button.setFont(new Font("Arial", Font.BOLD, 20));
                }
                
                playerButtons[i][j] = button;
                playerPanel.add(button);
            }
        }
    
        // Initialize enemy board
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = new JButton();
                button.setBackground(buttonColor);
                button.setPreferredSize(new Dimension(40, 40));
                button.setMargin(new Insets(0, 0, 0, 0));
                
                // Get the current cell state from the game controller
                char cellState = gameController.getEnemyBoard().getBoard()[i][j];
                
                // Set the button text/appearance based on the cell state
                if (cellState == 'S') {
                    button.setText("■"); // Ship symbol
                    button.setForeground(Color.WHITE); // White text for ships
                    button.setFont(new Font("Arial", Font.BOLD, 20));
                }
                
                enemyButtons[i][j] = button;
                enemyPanel.add(button);
            }
        }
        
        // Add borders to distinguish the boards
        playerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), "Player's Board",
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.BLUE));
        enemyPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2), "Enemy's Board",
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), Color.RED));

         // After adding panels to the frame in the constructor
         boardsPanel.add(playerPanel);
         boardsPanel.add(enemyPanel);
         add(boardsPanel, BorderLayout.CENTER);
         
         // Update the initial board state
         updatePlayerBoard();
         
         pack();
         setLocationRelativeTo(null);
    }
    
    // ----------------- Helper Method to Update Stats -----------------
    private void updateStatsLabel(int moves, String prefix) {
        char[][] board = gameController.getEnemyBoard().getBoard();
        int hitCount = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 'X') {
                    hitCount++;
                }
            }
        }
        int sunkCount = 0;
        for (Ship ship : gameController.getEnemyBoard().getShips()) {
            if (ship.isSunk()) {
                sunkCount++;
            }
        }
        messageLabel.setText("<html>" + prefix + "<br/>Moves : " + moves + "    Hit : " + hitCount + "    Sunk : " + sunkCount + "</html>");
    }
    
    // ----------------- Utility Method: Convert Position -----------------
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
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) return null;
        return new int[]{row, col};
    }
    
    // ----------------- TARGET MODE Logic (Shared by all search modes) -----------------
    
    // Check target mode based on unsunk ships having recorded hits.
    private boolean isTargetModeActive() {
        for (Ship ship : gameController.getEnemyBoard().getShips()) {
            if (!ship.isSunk() && !ship.getHitPositions().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    // Target move: For each unsunk ship with hits, determine orientation and fire at an adjacent cell.
    private void doTargetStep() {
        for (Ship ship : gameController.getEnemyBoard().getShips()) {
            if (ship.isSunk() || ship.getHitPositions().isEmpty()) {
                continue;
            }
            List<int[]> hitCoords = new ArrayList<>();
            for (String pos : ship.getHitPositions()) {
                int[] coord = convertPosition(pos);
                if (coord != null) {
                    hitCoords.add(coord);
                }
            }
            if (hitCoords.isEmpty()) continue;
            
            int targetRow = -1, targetCol = -1;
            char[][] board = gameController.getEnemyBoard().getBoard();
            // If only one hit, check all four directions.
            if (hitCoords.size() == 1) {
                int r = hitCoords.get(0)[0], c = hitCoords.get(0)[1];
                int[][] directions = { {-1,0}, {1,0}, {0,-1}, {0,1} };
                for (int[] d : directions) {
                    int nr = r + d[0], nc = c + d[1];
                    if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                        if (board[nr][nc] == '-' || board[nr][nc] == 'S') {
                            targetRow = nr;
                            targetCol = nc;
                            break;
                        }
                    }
                }
            } else {
                // Multiple hits: determine orientation.
                boolean horizontal = true, vertical = true;
                int baseRow = hitCoords.get(0)[0];
                int baseCol = hitCoords.get(0)[1];
                for (int[] coord : hitCoords) {
                    if (coord[0] != baseRow)
                        horizontal = false;
                    if (coord[1] != baseCol)
                        vertical = false;
                }
                if (horizontal && !vertical) {
                    int minCol = SIZE, maxCol = -1;
                    for (int[] coord : hitCoords) {
                        minCol = Math.min(minCol, coord[1]);
                        maxCol = Math.max(maxCol, coord[1]);
                    }
                    if (minCol - 1 >= 0 && (board[baseRow][minCol - 1] == '-' || board[baseRow][minCol - 1] == 'S')) {
                        targetRow = baseRow;
                        targetCol = minCol - 1;
                    } else if (maxCol + 1 < SIZE && (board[baseRow][maxCol + 1] == '-' || board[baseRow][maxCol + 1] == 'S')) {
                        targetRow = baseRow;
                        targetCol = maxCol + 1;
                    }
                } else if (vertical && !horizontal) {
                    int minRow = SIZE, maxRow = -1;
                    for (int[] coord : hitCoords) {
                        minRow = Math.min(minRow, coord[0]);
                        maxRow = Math.max(maxRow, coord[0]);
                    }
                    if (minRow - 1 >= 0 && (board[minRow - 1][baseCol] == '-' || board[minRow - 1][baseCol] == 'S')) {
                        targetRow = minRow - 1;
                        targetCol = baseCol;
                    } else if (maxRow + 1 < SIZE && (board[maxRow + 1][baseCol] == '-' || board[maxRow + 1][baseCol] == 'S')) {
                        targetRow = maxRow + 1;
                        targetCol = baseCol;
                    }
                } else {
                    // Not clearly aligned: fallback to adjacent cells of the first hit.
                    int r = hitCoords.get(0)[0], c = hitCoords.get(0)[1];
                    int[][] directions = { {-1,0}, {1,0}, {0,-1}, {0,1} };
                    for (int[] d : directions) {
                        int nr = r + d[0], nc = c + d[1];
                        if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE) {
                            if (board[nr][nc] == '-' || board[nr][nc] == 'S') {
                                targetRow = nr;
                                targetCol = nc;
                                break;
                            }
                        }
                    }
                }
            }
            if (targetRow != -1 && targetCol != -1) {
                String pos = "" + (char)('A' + targetCol) + (targetRow + 1);
                String result = gameController.getEnemyBoard().attackPosition(pos);
                bfMoveCount++;  // or pdSearch.incrementMoveCount() if in PD mode; here we assume shared target logic
                updateEnemyBoard();
                updateStatsLabel(bfMoveCount, "Target Mode: Attacking " + pos + ": " + result);
                return;
            }
        }
    }
    
    // ----------------- BRUTE FORCE (with Target Mode) -----------------
    private void doOneBruteForceStep() {
        // Check if all ships are sunk
        if (gameController.getEnemyBoard().allShipsSunk()) {
            endBruteForceSearch("All enemy ships sunk!");
            return;
        }
    
        // Check if target mode is active (when a ship has been hit but not sunk)
        if (isTargetModeActive()) {
            doTargetStep();
            return;
        }
    
        // Check if we've searched the entire grid
        if (currentRow >= SIZE) {
            endBruteForceSearch("Searched all positions");
            return;
        }
    
        // Get current position and check if it's already been attacked
        String pos = "" + (char)('A' + currentCol) + (currentRow + 1);
        char currentCell = gameController.getEnemyBoard().getBoard()[currentRow][currentCol];
        
        // If position hasn't been attacked yet
        if (currentCell != 'X' && currentCell != 'M') {
            // Attack the position
            String result = gameController.getEnemyBoard().attackPosition(pos);
            bfMoveCount++;
            updateEnemyBoard();
            updateStatsLabel(bfMoveCount, "Brute Force: Attacking " + pos + ": " + result);
    
            // Check if this attack won the game
            if (gameController.getEnemyBoard().allShipsSunk()) {
                long elapsedTime = System.nanoTime() - bfStartTime;
                double ms = elapsedTime / 1_000_000.0;
                double s = ms / 1000.0;
                updateStatsLabel(bfMoveCount, "All enemy ships sunk by brute force at " + pos + "! Time: " + ms + " ms (" + s + " s).");
                bruteForceTimer.stop();
                return;
            }
        }
    
        // Move to next position
        currentCol++;
        if (currentCol >= SIZE) {
            currentCol = 0;
            currentRow++;
        }
    
        // If we've completed the search without finding all ships
        if (currentRow >= SIZE) {
            endBruteForceSearch("Completed grid search without finding all ships");
        }
    }

    private void endBruteForceSearch(String reason) {
        bruteForceTimer.stop();
        long elapsedTime = System.nanoTime() - bfStartTime;
        double ms = elapsedTime / 1_000_000.0;
        double s = ms / 1000.0;
        updateStatsLabel(bfMoveCount, "Brute force finished: " + reason + " Time: " + ms + " ms (" + s + " s).");
    }
    
    // ----------------- PROBABILITY DENSITY METHODS -----------------
    public void startProbabilityDensity() {
        pdSearch = new PDSearch(gameController.getEnemyBoard());
        pdStartTime = System.nanoTime();
        
        if (probabilityTimer != null && probabilityTimer.isRunning()) {
            probabilityTimer.stop();
        }
        updateStatsLabel(0, "Probability Density search started...");
        probabilityTimer = new Timer(500, e -> doOneProbabilityStep());
        probabilityTimer.start();
    }
    
    private void doOneProbabilityStep() {
        if (gameController.getEnemyBoard().allShipsSunk()) {
            probabilityTimer.stop();
            long elapsedTime = System.nanoTime() - pdStartTime;
            double ms = elapsedTime / 1_000_000.0;
            double s = ms / 1000.0;
            updateStatsLabel(pdSearch.getMoveCount(), "Probability Density sunk all ships! Time: " + ms + " ms (" + s + " s).");
            return;
        }
        if (isTargetModeActive()) {
            doTargetStep();
            return;
        }
        String result = pdSearch.doOneStep();
        if (result.equals("No valid moves!")) {
            probabilityTimer.stop();
            updateStatsLabel(pdSearch.getMoveCount(), "Probability Density: " + result);
            return;
        }
        updateEnemyBoard();
        updateStatsLabel(pdSearch.getMoveCount(), "Probability: " + result + " (Move: " + pdSearch.getMoveCount() + ")");
    }
    
    // ----------------- PARITY SEARCH (with Target Mode) -----------------
    public void startParitySearch() {
        parityCells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((i + j) % 2 == 0) {
                    parityCells.add(new int[]{i, j});
                }
            }
        }
        parityIndex = 0;
        parityMoveCount = 0;
        parityStartTime = System.nanoTime();
        if (parityTimer != null && parityTimer.isRunning()) {
            parityTimer.stop();
        }
        updateStatsLabel(parityMoveCount, "Parity search started...");
        parityTimer = new Timer(500, e -> doOneParityStep());
        parityTimer.start();
    }
    
    private void doOneParityStep() {
        if (gameController.getEnemyBoard().allShipsSunk()) {
            parityTimer.stop();
            long elapsedTime = System.nanoTime() - parityStartTime;
            double ms = elapsedTime / 1_000_000.0;
            double s = ms / 1000.0;
            updateStatsLabel(parityMoveCount, "All enemy ships sunk by parity search! Time: " + ms + " ms (" + s + " s).");
            return;
        }
        if (isTargetModeActive()) {
            doTargetStep();
            return;
        }
        if (parityIndex < parityCells.size()) {
            int[] coords = parityCells.get(parityIndex);
            parityIndex++;
            String pos = "" + (char)('A' + coords[1]) + (coords[0] + 1);
            String result = gameController.getEnemyBoard().attackPosition(pos);
            if (!result.equals("Position already attacked!")) {
                parityMoveCount++;
            }
            updateEnemyBoard();
            updateStatsLabel(parityMoveCount, "Parity: Attacking " + pos + ": " + result);
        } else {
            parityTimer.stop();
            long elapsedTime = System.nanoTime() - parityStartTime;
            double ms = elapsedTime / 1_000_000.0;
            double s = ms / 1000.0;
            updateStatsLabel(parityMoveCount, "Parity search finished! Time: " + ms + " ms (" + s + " s).");
        }
    }
    
    // ----------------- Inner Class for Probability Density Search -----------------
    private class PDSearch {
        private GameBoard enemyBoard;
        private int SIZE;
        private int moveCount;
        private int[][] probabilityGrid;

        public PDSearch(GameBoard enemyBoard) {
            this.enemyBoard = enemyBoard;
            this.SIZE = enemyBoard.getRows();
            this.probabilityGrid = new int[SIZE][SIZE];
            this.moveCount = 0;
        }
        
        private boolean isCellAvailable(int row, int col, char[][] board) {
            char cell = board[row][col];
            return (cell == '-' || cell == 'S' || cell == 'X');
        }
        
        private void computeProbabilityGrid() {
            // Reset probability grid
            for (int i = 0; i < SIZE; i++) {
                Arrays.fill(probabilityGrid[i], 0);
            }
            
            char[][] board = enemyBoard.getBoard();
            
            // For each unsunk ship
            for (Ship ship : enemyBoard.getShips()) {
                if (ship.isSunk()) continue;
                
                int shipLength = ship.getSize();
                
                // Try placing ship at each position horizontally and vertically
                for (int i = 0; i < SIZE; i++) {
                    for (int j = 0; j < SIZE; j++) {
                        if (canPlaceHorizontally(i, j, shipLength, board)) {
                            // Increment probability for all cells in horizontal placement
                            for (int k = 0; k < shipLength; k++) {
                                probabilityGrid[i][j + k]++;
                            }
                        }
                        if (canPlaceVertically(i, j, shipLength, board)) {
                            // Increment probability for all cells in vertical placement
                            for (int k = 0; k < shipLength; k++) {
                                probabilityGrid[i + k][j]++;
                            }
                        }
                    }
                }
            }
        }
    
        private boolean canPlaceHorizontally(int row, int col, int shipLength, char[][] board) {
            if (col + shipLength > SIZE) return false;
            
            // Check if all cells in the placement are available
            for (int j = col; j < col + shipLength; j++) {
                if (!isCellAvailable(row, j, board)) return false;
            }
            return true;
        }
    
        private boolean canPlaceVertically(int row, int col, int shipLength, char[][] board) {
            if (row + shipLength > SIZE) return false;
            
            // Check if all cells in the placement are available
            for (int i = row; i < row + shipLength; i++) {
                if (!isCellAvailable(i, col, board)) return false;
            }
            return true;
        }
    
        private int[] getBestCell() {
            computeProbabilityGrid();
            int bestRow = -1;
            int bestCol = -1;
            int maxProb = -1;
            char[][] board = enemyBoard.getBoard();
            
            // Find cell with highest probability that hasn't been attacked
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (board[i][j] != 'M' && board[i][j] != 'X' && 
                        probabilityGrid[i][j] > maxProb) {
                        maxProb = probabilityGrid[i][j];
                        bestRow = i;
                        bestCol = j;
                    }
                }
            }
            return new int[]{bestRow, bestCol};
        }
    
        public String doOneStep() {
            int[] best = getBestCell();
            int row = best[0];
            int col = best[1];
            
            if (row == -1 || col == -1) {
                return "No valid moves!";
            }
            
            String pos = "" + (char)('A' + col) + (row + 1);
            String result = enemyBoard.attackPosition(pos);
            moveCount++;
            return "Attacking " + pos + ": " + result;
        }
        
        public int getMoveCount() {
            return moveCount;
        }
        
        public boolean isSearchComplete() {
            return enemyBoard.allShipsSunk();
        }
    }
    
    // ----------------- Helper Method to Update Enemy Board Display -----------------
    private void updateEnemyBoard() {
        char[][] board = gameController.getEnemyBoard().getBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = enemyButtons[i][j];
                char cell = board[i][j];
                
                if (cell == 'S') {
                    button.setText("■");
                    button.setForeground(Color.WHITE);
                    button.setFont(new Font("Arial", Font.BOLD, 20));
                } else if (cell == 'X') {
                    button.setText("X");
                    button.setBackground(new Color(178, 34, 34)); // Red for hits
                    button.setForeground(Color.WHITE);
                } else if (cell == 'M') {
                    button.setText("○");
                    button.setBackground(new Color(169, 169, 169)); // Gray for misses
                    button.setForeground(Color.WHITE);
                }
            }
        }
    }
    
    // (Optional) Update player's board if needed.
    private void updatePlayerBoard() {
        char[][] board = gameController.getPlayerBoard().getBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = playerButtons[i][j];
                char cell = board[i][j];
                
                if (cell == 'S') {
                    button.setText("■");
                    button.setForeground(Color.WHITE);
                    button.setFont(new Font("Arial", Font.BOLD, 20));
                } else if (cell == 'X') {
                    button.setText("X");
                    button.setBackground(new Color(178, 34, 34)); // Red for hits
                    button.setForeground(Color.WHITE);
                } else if (cell == 'M') {
                    button.setText("○");
                    button.setBackground(new Color(169, 169, 169)); // Gray for misses
                    button.setForeground(Color.WHITE);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipGUI());
    }
}