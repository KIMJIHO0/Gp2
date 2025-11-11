/**
 * "제목"이 있는 공통 패널 래퍼(By. Gemini)
 */

package ui_kit;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import java.awt.LayoutManager;


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
        // 타이틀 보더 생성
        titledBorder = BorderFactory.createTitledBorder(
            UITheme.LINE_BORDER, // 기본 테두리
            title,               // 제목
            TitledBorder.LEFT,   // 정렬
            TitledBorder.TOP,
            UITheme.FONT_BOLD,   // 제목 폰트
            UITheme.COLOR_FOREGROUND // 제목 색상
        );
        
        // 패널의 기본 여백과 타이틀 보더를 조합
        setBorder(BorderFactory.createCompoundBorder(
            titledBorder,      // 바깥쪽: 타이틀 보더
            UITheme.PANEL_PADDING // 안쪽: 공통 여백
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