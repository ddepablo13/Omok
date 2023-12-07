import javax.swing.*;
import java.awt.*;
import java.io.IOException;

//Entry point of the game
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::callCreateGUIFrame);
    }


    public static void callCreateGUIFrame(){
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "An error occurred while starting the application: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }

    private static void createAndShowGUI() throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        JFrame frame = new JFrame("Omok Game Selection");
        ImageIcon icon = new ImageIcon("/images/MainMenuPic.png");
        frame.setIconImage(icon.getImage());

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        MainMenuSelect mainMenuSelect = new MainMenuSelect(500, 600);
        frame.add(mainMenuSelect, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
