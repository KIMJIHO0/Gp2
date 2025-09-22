package kiost;

import kiost.panels.MainPanel;
import kiost.panels.StartPanel;
import kiost.service.DataStore;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public static final String START = "start";
    public static final String MAIN  = "main";

    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    public final DataStore store = new DataStore();

    public MainFrame() {
        setTitle("Wedding Kiosk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        root.add(new StartPanel(this), START);
        root.add(new MainPanel(this),  MAIN);

        setContentPane(root);
        showCard(START);
    }

    public void showCard(String name) {
        cards.show(root, name);
    }
}
