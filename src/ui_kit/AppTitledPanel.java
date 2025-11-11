
package ui_kit;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.LayoutManager;

/**
 * "제목"이 있는 공통 패널 래퍼(By. Gemini)
 */
public class AppTitledPanel extends JPanel {

    private TitledBorder titledBorder;

    public AppTitledPanel(String title) {
        super();
        initBorder(title);
    }

    public AppTitledPanel(String title, LayoutManager layout) {
        super(layout);
        initBorder(title);
    }

    private void initBorder(String title) {
        titledBorder = BorderFactory.createTitledBorder(
            UITheme.TITLED_PANEL_BORDER,
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            UITheme.TITLED_PANEL_FONT,
            UITheme.TITLED_PANEL_FG_COLOR
        );
        
        setBorder(BorderFactory.createCompoundBorder(
            titledBorder,
            BorderFactory.createEmptyBorder(
                UITheme.TITLED_PANEL_CONTENT_PADDING.top,
                UITheme.TITLED_PANEL_CONTENT_PADDING.left,
                UITheme.TITLED_PANEL_CONTENT_PADDING.bottom,
                UITheme.TITLED_PANEL_CONTENT_PADDING.right
            )
        ));
    }
    
    /**
     * 동적으로 패널의 제목을 변경할 수 있도록 헬퍼 메서드를 제공합니다.
     */
    public void setTitle(String title) {
        titledBorder.setTitle(title);
        repaint(); // 보더를 다시 그리도록 요청
    }
}
