package ui_kit;

import javax.swing.JProgressBar;

/**
 * 공통 AppProgressBar.
 * 기본 스타일(색상, 폰트)이 적용되며, 퍼센트 텍스트가 기본으로 표시됩니다.
 */
public class AppProgressBar extends JProgressBar {

    public AppProgressBar() {
        super(); // 0-100 범위
        initStyle();
    }

    public AppProgressBar(int min, int max) {
        super(min, max);
        initStyle();
    }

    private void initStyle() {
        setFont(UITheme.FONT_BASE);
        
        // 프로그레스 바의 전경색 (진행 상태 바)
        setForeground(UITheme.COLOR_PRIMARY);
        
        // 프로그레스 바의 배경색 (트랙)
        setBackground(UITheme.COLOR_BORDER);
        
        // [Best Practice] 퍼센트 텍스트를 기본으로 표시
        setStringPainted(true);
        
        // 테두리 설정
        setBorder(UITheme.LINE_BORDER);
    }
}