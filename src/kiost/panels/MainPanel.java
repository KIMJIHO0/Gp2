package kiost.panels;
import kiost.MainFrame;
import kiost.model.TeamSide;
import kiost.service.DataStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public class MainPanel extends JPanel {
    private final DataStore store;

    public MainPanel(MainFrame frame) {
        this.store = frame.store;

        setLayout(new BorderLayout(0, 20));

        JLabel title = new JLabel("신랑 / 신부 선택", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        add(title, BorderLayout.NORTH);

        // 선택 영역
        JRadioButton groom = new JRadioButton("신랑측");
        JRadioButton bride = new JRadioButton("신부측");
        groom.setFont(groom.getFont().deriveFont(18f));
        bride.setFont(bride.getFont().deriveFont(18f));

        ButtonGroup group = new ButtonGroup();
        group.add(groom);
        group.add(bride);

        JPanel choice = new JPanel();
        choice.add(groom);
        choice.add(bride);
        add(choice, BorderLayout.CENTER);

        // 다음 단계용 버튼(선택 저장만. 화면 전환은 이후 단계에서 연결)
        JButton next = new JButton("다음");
        next.setEnabled(false);
        next.addActionListener(e -> {
            TeamSide t = store.getTeam();
            JOptionPane.showMessageDialog(
                    this,
                    (t == TeamSide.GROOM ? "신랑측" : "신부측") + " 선택됨",
                    "확인",
                    JOptionPane.INFORMATION_MESSAGE
            );
            // 이후 단계: frame.showCard(다음화면);
        });

        JPanel bottom = new JPanel();
        bottom.add(next);
        add(bottom, BorderLayout.SOUTH);

        // 선택 이벤트 → DataStore에 저장 + 다음 버튼 활성화
        groom.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                store.setTeam(TeamSide.GROOM);
                next.setEnabled(true);
            }
        });
        bride.addItemListener(ev -> {
            if (ev.getStateChange() == ItemEvent.SELECTED) {
                store.setTeam(TeamSide.BRIDE);
                next.setEnabled(true);
            }
        });
    }
}
