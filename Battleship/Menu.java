import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class Menu extends JFrame {
    private static final String TITLE = "Battleship Algorithm Selection";
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 400;
    private Image backgroundImage;

    public Menu() {
        setTitle(TITLE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("Battleship.jpg"));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
        
        // Create main panel with background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    // Draw the background image scaled to panel size
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient background
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 48, 73), 
                                                             getWidth(), getHeight(), new Color(0, 108, 163));
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Title Label
        JLabel titleLabel = createStyledLabel("Battleship Algorithm Selection", 24);
        
        // Description Label
        JLabel descLabel = createStyledLabel("Select an algorithm to start the game:", 16);

        // Create buttons panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setOpaque(false);

        // Create algorithm buttons
        JButton bruteForceBtn = createStyledButton("Brute Force Algorithm");
        JButton probabilityBtn = createStyledButton("Probability Density Algorithm");
        JButton parityBtn = createStyledButton("Parity Search Algorithm");

        // Add action listeners
        bruteForceBtn.addActionListener(createAlgorithmListener("BRUTE_FORCE"));
        probabilityBtn.addActionListener(createAlgorithmListener("PROBABILITY"));
        parityBtn.addActionListener(createAlgorithmListener("PARITY"));

        // Add components to panels
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(descLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        
        buttonsPanel.add(bruteForceBtn);
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(probabilityBtn);
        buttonsPanel.add(Box.createVerticalStrut(15));
        buttonsPanel.add(parityBtn);
        
        mainPanel.add(buttonsPanel);
        mainPanel.add(Box.createVerticalGlue());

        // Add main panel to frame
        add(mainPanel);
    }

    private JLabel createStyledLabel(String text, int fontSize) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, fontSize));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(250, 40));
        button.setMaximumSize(new Dimension(250, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(255, 255, 255));
        button.setFocusPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 255));
            }
        });
        
        return button;
    }

    private ActionListener createAlgorithmListener(String algorithm) {
        return e -> {
            dispose(); // Close the menu
            SwingUtilities.invokeLater(() -> {
                BattleshipGUI game = new BattleshipGUI();
                game.setSize(800, 600);  // Set initial size
                game.pack();
                game.setLocationRelativeTo(null);
                game.setVisible(true); // Make the game window visible
                
                // Add small delay to ensure GUI is ready
                Timer startTimer = new Timer(100, ev -> {
                    ((Timer)ev.getSource()).stop();
                    // Start the selected algorithm
                    switch (algorithm) {
                        case "BRUTE_FORCE":
                            game.startBruteForce();
                            break;
                        case "PROBABILITY":
                            game.startProbabilityDensity();
                            break;
                        case "PARITY":
                            game.startParitySearch();
                            break;
                    }
                });
                startTimer.setRepeats(false);
                startTimer.start();
            });
        }; // Add this closing brace
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Menu menu = new Menu();
            menu.setVisible(true);
        });
    }
}
