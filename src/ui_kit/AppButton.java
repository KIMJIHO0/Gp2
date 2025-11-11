/**
 * 공통 AppButton.
 * 기본 스타일(색상, 폰트), (탭, 마우스 클릭 등)포커스 핸들링, 커서 변경 적용
 * 이건 일단 해두면 예쁘겠죠?
 */

package ui_kit;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AppButton extends JButton {

    public AppButton(String text) {
        super(text);
        
        setFont(UITheme.FONT_BOLD);
        setBackground(UITheme.COLOR_PRIMARY);
        setForeground(Color.WHITE);
        
        // 버튼 영역에 마우스가 오면 손가락 커서로 변경
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // 클릭 시 버튼 주위에 생기는 점선(포커스) 제거 (미관상)
        setFocusPainted(false);
        
        // (선택) 마우스 오버/클릭 시 색상 변경 효과
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(UITheme.COLOR_PRIMARY_DARK);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(UITheme.COLOR_PRIMARY);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                setBackground(UITheme.COLOR_PRIMARY.darker());
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                setBackground(UITheme.COLOR_PRIMARY);
            }
        });
    }
}