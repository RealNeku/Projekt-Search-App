package mainapp;


import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ui.MainFrame mainFrame = new ui.MainFrame();
            mainFrame.setVisible(true);
        });
    }
}