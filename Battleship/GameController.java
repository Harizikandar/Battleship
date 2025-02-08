import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private GameBoard player1Board;
    private GameBoard player2Board;
    private boolean player1Turn;
    private Scanner scanner;

    public GameController() {
        this.player1Board = new GameBoard(10, 10);
        this.player2Board = new GameBoard(10, 10);
        this.player1Turn = true;
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        setupPhase();
        playPhase();
        scanner.close();
    }

    private void setupPhase() {
        System.out.println("PLAYER 1: Place your ships.");
        player1Board.displayBoard(true);
        setupPlayerShips(player1Board);

        System.out.println("\nPLAYER 2: Place your ships.");
        player2Board.displayBoard(true);
        setupPlayerShips(player2Board);
    }

    private void setupPlayerShips(GameBoard board) {
        Ship[] ships = { new Battleship(), new Destroyer(), new Submarine() };
        for (Ship ship : ships) {
            while (true) {
                System.out.println("Enter positions for " + ship.getName() + " (size " + ship.getSize() + "): ");
                List<String> positions = Arrays.asList(scanner.nextLine().trim().split("\\s+"));
                if (Ship.isValidPlacement(positions, ship.getSize()) && board.placeShip(ship, positions)) {
                    break;
                }
                System.out.println("Invalid placement! Try again.");
            }
        }
    }

    private void playPhase() {
        while (true) {
            System.out.println("\n--- " + (player1Turn ? "PLAYER 1's Turn" : "PLAYER 2's Turn") + " ---");
            GameBoard targetBoard = player1Turn ? player2Board : player1Board;
            targetBoard.displayBoard(false);

            String result = playerTurn(targetBoard);
            System.out.println(result);

            if (targetBoard.allShipsSunk()) {
                System.out.println("Game Over! " + (player1Turn ? "PLAYER 1 WINS!" : "PLAYER 2 WINS!"));
                break;
            }

            player1Turn = !player1Turn;
        }
    }

    private String playerTurn(GameBoard targetBoard) {
        System.out.println("Enter attack position (e.g., A5): ");
        String attackPos = scanner.nextLine().trim().toUpperCase();
        return targetBoard.attackPosition(attackPos);
    }
}
