import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

class PlayerUpdates extends JPanel {
    private BufferedImage backgroundImage;
    private Timer timer;
    private final JLabel statusLabel;
    private int dotCount = 0;
    private final JLabel nameLabel;

    public int getWinCounter() {
        return winCounter;
    }

    public void setWinCounter(int winCounter) {
        this.winCounter = winCounter;
    }

    private int winCounter = 0;

    public PlayerUpdates(PlayerInterface player) throws IOException {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("images/CloudBackground.png")));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

        this.setLayout(new BorderLayout());
        TakeImage takeImage = new TakeImage("images/UserPic.png", 100, 100);
        BufferedImage myPicture = takeImage.getImage();

        JLabel picLabel = new JLabel(new ImageIcon(myPicture));
        JPanel combinedPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
                }
            }
        };
        combinedPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;

        combinedPanel.add(picLabel, gbc);

        gbc.gridy++;

        nameLabel = new JLabel(player.getName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        combinedPanel.add(nameLabel, gbc);

        gbc.gridy++;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        combinedPanel.add(statusLabel, gbc);

        this.add(combinedPanel, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        initTimer();
    }

    /**
     * Sets a timer to update status every 1000 milliseconds
     */

    private void initTimer() {
        timer = new Timer(1000, e -> updateStatus());
        timer.setInitialDelay(0);
    }

    /**
     * Number of dots displayed on screen
     */

    private void updateStatus() {
        dotCount++;
        if (dotCount > 3) {
            dotCount = 1;
        }
        statusLabel.setText(" • ".repeat(dotCount));
    }

    /**
     * set the number of dots to 0
     */
    public void resetDots() {
        dotCount = 0;
        statusLabel.setText(" ");
    }

    /**
     * Displays the last move made
     */
    public void moveMade(int[] lastMove) {
        String moveText = String.format("<html>Move made!<br/>\nAt (%d, %d)</html>", lastMove[0], lastMove[1]);
        statusLabel.setText(moveText);
        timer.stop();
    }

    /**
     * Updates the displayed name
     */
    public void updateName(String newName) {
        nameLabel.setText(newName);
        revalidate();
        repaint();
    }

    public void highlight(boolean isTurn) {
        if (isTurn) {
            this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            resetDots();
            timer.start();
        }
        this.repaint();
    }
}