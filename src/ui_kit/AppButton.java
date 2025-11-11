/**
 * 공통 AppButton.
 * 기본 스타일(색상, 폰트), (탭, 마우스 클릭 등)포커스 핸들링, 커서 변경 적용
 * 이건 일단 해두면 예쁘겠죠?
 */

package ui_kit;

import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;



public class AppButton extends JButton {

    public AppButton(String text) {
        super(text);
        
        setFont(UITheme.BUTTON_FONT);
        setBackground(UITheme.BUTTON_BG_COLOR);
        setForeground(UITheme.BUTTON_FG_COLOR);
        
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(UITheme.BUTTON_BG_COLOR_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UITheme.BUTTON_BG_COLOR);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(UITheme.BUTTON_BG_COLOR.darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(UITheme.BUTTON_BG_COLOR_HOVER);
            }
        });
    }
}
