import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GameController {
    private GameBoard playerBoard; // Human player's board
    private GameBoard enemyBoard;  // Computer's board
    private Random random;

    public GameController() {
        playerBoard = new GameBoard(10, 10);
        enemyBoard = new GameBoard(10, 10);
        random = new Random();
        autoSetupPhase();
    }

    // Automatically place ships on both boards using fixed positions.
    private void autoSetupPhase() {
        // For player's board:
        List<String> pCarrier = Arrays.asList("A1", "A2", "A3", "A4", "A5");      // Carrier along edge
        List<String> pBattleship = Arrays.asList("C3", "D3", "E3", "F3");         // Battleship vertical center
        List<String> pCruiser = Arrays.asList("H8", "H9", "H10");                 // Cruiser in corner
        List<String> pDestroyer = Arrays.asList("J1", "J2", "J3");                // Destroyer opposite corner
        List<String> pSubmarine = Arrays.asList("E6", "F6");
    
        playerBoard.placeShip(new Carrier(), pCarrier);
        playerBoard.placeShip(new Battleship(), pBattleship);
        playerBoard.placeShip(new Cruiser(), pCruiser);
        playerBoard.placeShip(new Destroyer(), pDestroyer);
        playerBoard.placeShip(new Submarine(), pSubmarine);
    
        // For enemy (computer) board:
        List<String> eCarrier = Arrays.asList("B4", "C4", "D4", "E4", "F4");     // Carrier vertical middle
        List<String> eBattleship = Arrays.asList("H1", "H2", "H3", "H4");        // Battleship horizontal edge
        List<String> eCruiser = Arrays.asList("A7", "B7", "C7");                 // Cruiser vertical top
        List<String> eDestroyer = Arrays.asList("J6", "J7", "J8");               // Destroyer bottom edge
        List<String> eSubmarine = Arrays.asList("D9", "E9");
    
        enemyBoard.placeShip(new Carrier(), eCarrier);
        enemyBoard.placeShip(new Battleship(), eBattleship);
        enemyBoard.placeShip(new Cruiser(), eCruiser);
        enemyBoard.placeShip(new Destroyer(), eDestroyer);
        enemyBoard.placeShip(new Submarine(), eSubmarine);
    }

    // Process the player's attack on the enemy board.
    public String playerAttack(String position) {
        return enemyBoard.attackPosition(position);
    }

    // The computer selects a random available position on the player's board and attacks.
    public String computerAttack() {
        List<String> availablePositions = new ArrayList<>();
        for (int row = 0; row < playerBoard.getRows(); row++) {
            for (int col = 0; col < playerBoard.getCols(); col++) {
                char cell = playerBoard.getBoard()[row][col];
                if (cell != 'X' && cell != 'M') {
                    String pos = "" + (char)('A' + col) + (row + 1);
                    availablePositions.add(pos);
                }
            }
        }
        if (availablePositions.isEmpty()) return "No available moves!";
        String pos = availablePositions.get(random.nextInt(availablePositions.size()));
        String result = playerBoard.attackPosition(pos);
        System.out.println("Computer attacks " + pos + ": " + result);
        return result;
    }

    public GameBoard getPlayerBoard() {
        return playerBoard;
    }

    public GameBoard getEnemyBoard() {
        return enemyBoard;
    }
    
    // Checks whether either side has lost all ships.
    public boolean isGameOver() {
        return playerBoard.allShipsSunk() || enemyBoard.allShipsSunk();
    }
}
