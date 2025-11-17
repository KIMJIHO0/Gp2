package ui_kit;

import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 공통 AppButton.
 * 생성자의 enableAutoStyling 플래그를 통해
 * 기본 호버/클릭 스타일을 적용할지(true),
 * 외부(Sidebar 등)에서 스타일을 제어할지(false) 선택할 수 있습니다.
 */
public class AppButton extends JButton {

    /**
     * 기본 생성자: 자동 스타일링을 활성화합니다.
     */
    public AppButton(String text) {
        this(text, true); // true를 기본값으로 하여 기존 코드 호환성 유지
    }

    /**
     * 마스터 생성자: 자동 스타일링 여부를 선택합니다.
     * @param text 버튼 텍스트
     * @param enableAutoStyling true시 기본 호버/클릭 스타일 적용, false시 리스너 미등록
     */
    public AppButton(String text, boolean enableAutoStyling) {
        super(text);
        
        // --- 공통 스타일 ---
        setFont(UITheme.BUTTON_FONT);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setFocusPainted(false);
        
        if (enableAutoStyling) {
            // --- 1. 자동 스타일링 활성화 (기존 버튼) ---
            setBackground(UITheme.BUTTON_BG_COLOR);
            setForeground(UITheme.BUTTON_FG_COLOR);
            
            // 문제의 MouseAdapter를 조건부로 등록
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
        } else {
            // --- 2. 자동 스타일링 비활성화 (Sidebar용) ---
            // MouseAdapter를 등록하지 않으므로, 
            // Sidebar가 setBackground, setOpaque 등을 100% 제어할 수 있습니다.
            // 기본 스타일은 Sidebar의 styleMenuButton에서 설정할 것입니다.
        }
    }
}