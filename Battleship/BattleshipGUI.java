import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BattleshipGUI extends JFrame {
    private GameController gameController;
    private JPanel playerPanel;
    private JPanel enemyPanel;
    private JLabel messageLabel;
    private final int SIZE = 10;
    private JButton[][] playerButtons;
    private JButton[][] enemyButtons;
    
    public BattleshipGUI() {
        gameController = new GameController();
        setTitle("Battleship Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Message label at the top.
        messageLabel = new JLabel("Your turn! Click on an enemy cell to attack.", SwingConstants.CENTER);
        add(messageLabel, BorderLayout.NORTH);
        
        // Create panels for the two boards.
        JPanel boardsPanel = new JPanel(new GridLayout(1, 2));
        playerPanel = new JPanel(new GridLayout(SIZE, SIZE));
        enemyPanel = new JPanel(new GridLayout(SIZE, SIZE));
        
        playerButtons = new JButton[SIZE][SIZE];
        enemyButtons = new JButton[SIZE][SIZE];
        
        // Initialize the player's board (showing ships, hits, and misses).
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setEnabled(false); // Player board is display-only.
                button.setText(String.valueOf(gameController.getPlayerBoard().getBoard()[i][j]));
                playerButtons[i][j] = button;
                playerPanel.add(button);
            }
        }
        
        // Initialize the enemy board (hidden ships; only display hits and misses).
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40, 40));
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setText("-"); // Initially unknown.
                final int row = i;
                final int col = j;
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        handlePlayerAttack(row, col);
                    }
                });
                enemyButtons[i][j] = button;
                enemyPanel.add(button);
            }
        }
        
        boardsPanel.add(playerPanel);
        boardsPanel.add(enemyPanel);
        add(boardsPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // When the player clicks on an enemy board cell.
    private void handlePlayerAttack(int row, int col) {
        String pos = "" + (char)('A' + col) + (row + 1);
        String result = gameController.playerAttack(pos);
        
        // If the position was already attacked, prompt the player to choose another location.
        if (result.equals("Position already attacked!")) {
            messageLabel.setText("Position already attacked! Choose another location.");
            return; // Maintain the player's turn.
        }
        
        messageLabel.setText("You attacked " + pos + ": " + result);
        updateEnemyBoard();
        
        if (gameController.getEnemyBoard().allShipsSunk()) {
            JOptionPane.showMessageDialog(this, "Congratulations! You sank all enemy ships. You win!");
            System.exit(0);
        }
        
        // Delay the computer's move slightly.
        Timer timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String compResult = gameController.computerAttack();
                messageLabel.setText("Computer attacked: " + compResult);
                updatePlayerBoard();
                if (gameController.getPlayerBoard().allShipsSunk()) {
                    JOptionPane.showMessageDialog(BattleshipGUI.this, "All your ships are sunk. You lose!");
                    System.exit(0);
                }
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    // Refresh the enemy board display.
    private void updateEnemyBoard() {
        char[][] board = gameController.getEnemyBoard().getBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                char cell = board[i][j];
                if (cell == 'X' || cell == 'M') {
                    enemyButtons[i][j].setText(String.valueOf(cell));
                } else {
                    enemyButtons[i][j].setText("-");
                }
                // Reset background.
                enemyButtons[i][j].setBackground(null);
                enemyButtons[i][j].setOpaque(false);
            }
        }
        updateSunkShipsColor(gameController.getEnemyBoard(), enemyButtons);
    }
    
    // Refresh the player's board display.
    private void updatePlayerBoard() {
        char[][] board = gameController.getPlayerBoard().getBoard();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                playerButtons[i][j].setText(String.valueOf(board[i][j]));
                // Reset background.
                playerButtons[i][j].setBackground(null);
                playerButtons[i][j].setOpaque(false);
            }
        }
        updateSunkShipsColor(gameController.getPlayerBoard(), playerButtons);
    }
    
    // Helper method: For each sunk ship, update the corresponding buttons to have a red background.
    private void updateSunkShipsColor(GameBoard board, JButton[][] boardButtons) {
        // Iterate over all ships on the board.
        for (Ship ship : board.getShips()) {
            if (ship.isSunk()) {
                for (String pos : ship.getPositions()) {
                    int[] coords = convertPosition(pos);
                    if (coords != null) {
                        boardButtons[coords[0]][coords[1]].setBackground(Color.RED);
                        boardButtons[coords[0]][coords[1]].setOpaque(true);
                        boardButtons[coords[0]][coords[1]].setBorderPainted(false);
                    }
                }
            }
        }
    }
    
    // Converts a board position (e.g., "A5") to row and column indices.
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
        return new int[] { row, col };
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipGUI());
    }
}
