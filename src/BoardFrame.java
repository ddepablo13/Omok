import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
// BoardFrame extends JPanel and is designed to represent the main game panel in a GUI application.
public class BoardFrame extends JPanel {
    private final boolean isAIGame;
    private OmokGameDesign game;
    private JToolBar toolBar;
    // Constructor for BoardFrame. Initializes the UI components and toolbar.
    public BoardFrame(PlayerInterface player1, PlayerInterface player2, boolean isAIGame) throws IOException {
        this.isAIGame = isAIGame;
        initializeToolBar();
        initializeUI(player1, player2);
    }
    // Initializes the toolbar with game-related buttons and actions.
    private void initializeToolBar() {
        toolBar = new JToolBar("Toolbar");

        // Define the base path and file names
        String basePath = "images/";
        String[] fileNames = {"NewGameIcon.png", "ChangeName.png", "LeaveGame.png"};

        // Actions corresponding to each toolbar button
        Runnable[] actions = {
                () -> game.resetGame(),
                this::changeUserNames,
                this::returnToSelectionMenu,
        };

        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            URL iconURL = getClass().getResource(basePath + fileName);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);

                // Create a button with the icon and action
                JButton button = new JButton(icon);
                button.setFocusable(false);
                int finalI = i;
                button.addActionListener(e -> actions[finalI].run());

                // Add the button to the toolbar
                toolBar.add(button);
            } else {
                System.out.println("Icon not found for: " + fileName);
            }
        }
    }
    // Handles the action to return to the main menu. Asks the user for confirmation before proceeding.
    // If confirmed, it disposes the current frame and launches a new main menu frame.
    private void returnToSelectionMenu() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Return to Main Menu?",
                "Return to Main Menu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.dispose();
            }
            EventQueue.invokeLater(() -> {
                try {
                    JFrame selectionFrame = new JFrame("Main Menu");
                    selectionFrame.setSize(new Dimension(500, 600));
                    MainMenuSelect mainMenuSelect = new MainMenuSelect(selectionFrame.getWidth(), selectionFrame.getHeight());
                    selectionFrame.setContentPane(mainMenuSelect);
                    selectionFrame.pack();
                    selectionFrame.setLocationRelativeTo(null);
                    selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    selectionFrame.setVisible(true);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
    // Provides functionality to change the names of the players.
    // Prompts the user for new names for both players and updates the names in the game if confirmed.
    private void changeUserNames() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Change Player Names?",
                "Change Names", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String player1Name = JOptionPane.showInputDialog(this, "Enter name for Player 1:", "Change Names", JOptionPane.QUESTION_MESSAGE);
            String player2Name = JOptionPane.showInputDialog(this, "Enter name for Player 2:", "Change Names", JOptionPane.QUESTION_MESSAGE);

            if (player1Name != null && player2Name != null) {
                game.getPlayer1().setName(player1Name);
                game.getPlayer1Panel().updateName(player1Name);
                game.getPlayer2().setName(player2Name);
                game.getPlayer2Panel().updateName(player2Name);
            }
        }
    }
    // Initializes the user interface components of the BoardFrame.
    // Sets the layout, adds the toolbar, and creates panels for player updates. Also initializes the main game component.
    private void initializeUI(PlayerInterface player1, PlayerInterface player2) throws IOException {
        this.setLayout(new BorderLayout());
        initializeToolBar();

        // Add the toolbar at the top (PAGE_START) of the panel.
        this.add(toolBar, BorderLayout.PAGE_START);

        PlayerUpdates player1Panel = new PlayerUpdates(player1);
        PlayerUpdates player2Panel = new PlayerUpdates(player2);

        this.add(player1Panel, BorderLayout.WEST);
        this.add(player2Panel, BorderLayout.EAST);

        game = new OmokGameDesign(player1Panel, player1, player2Panel, player2, isAIGame);
        this.add(game, BorderLayout.CENTER);

        player1Panel.setPreferredSize(new Dimension(100, getHeight()));
        player2Panel.setPreferredSize(new Dimension(100, getHeight()));
    }

}