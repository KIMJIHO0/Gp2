package kiost.panels;

import kiost.MainFrame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class StartPanel extends JPanel {
    public StartPanel(MainFrame frame) {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("예식장 키오스크", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));

        JButton startBtn = new JButton(new AbstractAction("Start") {
            @Override public void actionPerformed(ActionEvent e) {
                frame.showCard(MainFrame.MAIN);
            }
        });
        startBtn.setFont(startBtn.getFont().deriveFont(20f));

        JPanel center = new JPanel();
        center.add(startBtn);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }
}

