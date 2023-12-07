import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ToolBar {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Toolbar with Icon");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar("Toolbar");

        String basePath = "/GUIOmokGameConsole/assets/";
        String[] fileNames = {"ResetGame.png", "NewGameIcon.png", "ChangeName.png", "LeaveGame.png"};

        for (String fileName : fileNames) {
            URL iconURL = ToolBar.class.getResource(basePath + fileName);
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);

                // Create a button
                JButton button = new JButton(icon);
                button.setFocusable(false);

                // Add button
                toolBar.add(button);
            } else {
                System.out.println("Icon not found for: " + fileName);
            }
        }

        // Add the toolbar to the frame
        frame.add(toolBar, BorderLayout.NORTH);

        // Finalize and display the frame
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}