package com.game.aichat;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            BaiduSearchGUI searchGUI = new BaiduSearchGUI();
            searchGUI.setVisible(true);
        });
    }
}
