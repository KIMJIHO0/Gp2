
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
        setFont(UITheme.PROGRESSBAR_FONT);
        setForeground(UITheme.PROGRESSBAR_FG_COLOR);
        setBackground(UITheme.PROGRESSBAR_BG_COLOR);
        setStringPainted(true);
        setBorder(UITheme.PROGRESSBAR_BORDER);
    }
}
