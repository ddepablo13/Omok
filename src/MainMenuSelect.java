import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
// Constructor. Initializes the main menu with background and icon images, and sets up the UI components.
public class MainMenuSelect extends JPanel {

    private JRadioButton humanButton;
    private JRadioButton computerButton;
    private JRadioButton javaCbutton;
    private JTextField playerNameField;
    private JTextField opponentNameField;
    private final BufferedImage backgroundImage;
    private final BufferedImage icon;

    public MainMenuSelect(int sizeWidth, int sizeHeight) throws IOException {
        BufferedImage originalIcon = new TakeImage("images/MainMenuPic.png", 150, 150).getImage();
        icon = createCircularImage(originalIcon);

        backgroundImage = new TakeImage("images/CloudBackground.png", sizeWidth, sizeHeight).getImage();
        initializeUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            int x = (this.getWidth() - backgroundImage.getWidth()) / 2;
            int y = (this.getHeight() - backgroundImage.getHeight()) / 2;
            g.drawImage(backgroundImage, x, y, this);
        }
    }

// Creates a circular image from the given BufferedImage.
    public BufferedImage createCircularImage(BufferedImage inputImage) {
        int diameter = Math.min(inputImage.getWidth(), inputImage.getHeight());
        BufferedImage circleBuffer = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = circleBuffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, diameter, diameter));

        g2.drawImage(inputImage, 0, 0, diameter, diameter, null);
        g2.dispose();

        return circleBuffer;
    }

    // Adds a focus listener to a JTextField to handle placeholder text behavior.
    private void addFocusListenerToTextField(JTextField textField, String defaultText) {
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(defaultText)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(defaultText);
                }
            }
        });
    }

    // Initializes the user interface components of the MainMenuSelect panel.
    private void initializeUI() {
        // Define a custom font
        Font customFont = new Font("DialogInput", Font.ROMAN_BASELINE, 14);

        this.setPreferredSize(new Dimension(450, 450));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel iconLabel = new JLabel(new ImageIcon(icon));
        this.add(iconLabel, gbc);

        // Set the custom font for each GUI component
        JLabel playerNameLabel = new JLabel("Your Name:");
        playerNameLabel.setFont(customFont);
        playerNameField = new JTextField("You", 10);
        playerNameField.setFont(customFont);
        addFocusListenerToTextField(playerNameField, "You");

        JLabel opponentNameLabel = new JLabel("Their Name:");
        opponentNameLabel.setFont(customFont);
        opponentNameField = new JTextField("Computer", 10);
        opponentNameField.setFont(customFont);
        addFocusListenerToTextField(opponentNameField, "Computer");
        opponentNameField.setEnabled(false);

        JLabel label = new JLabel("Select opponent:");
        label.setFont(customFont);
        this.add(label, gbc);

        humanButton = new JRadioButton("Human");
        humanButton.setFont(customFont);
        computerButton = new JRadioButton("Computer");
        computerButton.setFont(customFont);
        javaCbutton = new JRadioButton("Java Client");
        javaCbutton.setFont(customFont);

        humanButton.addActionListener(e -> opponentNameField.setEnabled(true));
        computerButton.addActionListener(e -> opponentNameField.setEnabled(false));
        javaCbutton.addActionListener(e -> opponentNameField.setEnabled(false));

        ButtonGroup group = new ButtonGroup();
        group.add(humanButton);
        group.add(computerButton);
        group.add(javaCbutton);

        JPanel radioPanel = new JPanel(new FlowLayout());
        radioPanel.add(computerButton);
        radioPanel.add(humanButton);
        radioPanel.add(javaCbutton);

        this.add(playerNameLabel, gbc);
        this.add(playerNameField, gbc);
        this.add(opponentNameLabel, gbc);
        this.add(opponentNameField, gbc);
        this.add(radioPanel, gbc);

        JButton playButton = new JButton("Play");
        playButton.setFont(customFont);
        playButton.addActionListener(this::playButtonActionPerformed);

        gbc.weighty = 1;
        this.add(playButton, gbc);
    }

    /**
     * Handles the action performed when the play button is clicked.
     * It sets up the game based on the selected options and starts a new game frame.
     */
    private void playButtonActionPerformed(ActionEvent e) {
        String playerName = playerNameField.getText().trim();
        String opponentName = opponentNameField.getText().trim();
        boolean isAI = computerButton.isSelected();

        if (playerName.isEmpty()) {
            playerName = "Player 1";
        }
        if (opponentName.isEmpty() || opponentName.equals("Computer")) {
            opponentName = isAI ? "Computer" : "Player 2";
        }

        if (humanButton.isSelected() || computerButton.isSelected()) {
            PlayerInterface player1 = new HumanPlayer(Stone.BLACK, playerName);
            PlayerInterface player2 = new HumanPlayer(Stone.WHITE, opponentName);
            startGame(player1, player2, isAI);
            Window topFrame = SwingUtilities.getWindowAncestor(this);
            if (topFrame != null) {
                topFrame.dispose();
            }
        }
        if (javaCbutton.isSelected()) {
            String strategy = selectJavaClientStrategy();
            if (strategy != null) {
                PlayerInterface player1 = new HumanPlayer(Stone.BLACK, playerName);
                JavaClientPlayer javaClientPlayer = new JavaClientPlayer(Stone.WHITE, strategy); // Assuming this class exists
                startGame(player1, javaClientPlayer, false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select an option!");
        }
    }

    // Prompts the user to select a strategy for the Java client player.
    private String selectJavaClientStrategy() {
        String[] strategies = {"Smart", "Random"};
        return (String) JOptionPane.showInputDialog(
                this, "Select Strategy:", "Java Client Strategy",
                JOptionPane.QUESTION_MESSAGE, null, strategies, strategies[0]);
    }

    /**
    * Starts a new game with the provided player interfaces and configuration.
    */
    private void startGame(PlayerInterface player1, PlayerInterface player2, boolean isAI) {
        JFrame gameFrame = new JFrame("Omok Game");
        SwingUtilities.invokeLater(() -> {
            try {
                BoardFrame board = new BoardFrame(player1, player2, isAI);
                ImageIcon icon = new ImageIcon("images/MainMenuPic.png");
                gameFrame.setIconImage(icon.getImage());
                gameFrame.setIconImage(icon.getImage());
                gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameFrame.setContentPane(board);
                gameFrame.pack();
                gameFrame.setLocationRelativeTo(null);
                gameFrame.setVisible(true);
                Window topFrame = SwingUtilities.getWindowAncestor(this);
                if (topFrame != null) {
                    topFrame.dispose();
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(gameFrame, "Failed to start due to an error.");
            }
        });
    }

}