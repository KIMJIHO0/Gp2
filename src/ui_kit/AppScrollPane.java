package ui_kit;

import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * UITheme을 참조하는 커스텀 JScrollPane.
 * (image_997fb9.png 참조)
 * - 화살표 버튼이 없는 스크롤바
 * - 커스텀 썸/트랙 색상
 */
public class AppScrollPane extends JScrollPane {

    /**
     * 뷰(View)가 없는 빈 스크롤 패널을 생성합니다.
     */
    public AppScrollPane() {
        super();
        initScrollPane();
    }

    /**
     * 지정된 컴포넌트를 뷰포트로 하는 스크롤 패널을 생성합니다.
     * @param view 스크롤할 컴포넌트
     */
    public AppScrollPane(Component view) {
        super(view);
        initScrollPane();
    }

    /**
     * 공통 초기화 메서드
     */
    private void initScrollPane() {
        // 1. UITheme 스타일 적용
        setBorder(UITheme.SCROLLBAR_BORDER);
        getViewport().setBackground(UITheme.SCROLLBAR_VIEWPORT_BG);
        setOpaque(false); // 부모 패널의 배경이 비치도록
        getViewport().setOpaque(false);

        // 2. 스크롤바 정책 설정 (CatalogPage와 동일하게)
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        // 3. 커스텀 스크롤바 UI 적용
        JScrollBar verticalScrollBar = getVerticalScrollBar();
        verticalScrollBar.setUI(new CustomScrollBarUI());
        
        // (가로 스크롤바에도 동일하게 적용-필요시)
        // JScrollBar horizontalScrollBar = getHorizontalScrollBar();
        // horizontalScrollBar.setUI(new CustomScrollBarUI());

        // 4. 스크롤 속도 조절
        verticalScrollBar.setUnitIncrement(16);
    }

    /**
     * 스크롤바의 UI를 커스터마이징하는 내부 클래스
     */
    private static class CustomScrollBarUI extends BasicScrollBarUI {

        // --- 1. 색상 설정 ---
        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = UITheme.SCROLLBAR_THUMB_COLOR;
            this.trackColor = UITheme.SCROLLBAR_TRACK_COLOR;
        }

        // --- 2. 화살표 버튼 제거 ---
        // (빈 0x0 크기 버튼을 반환하여 버튼을 숨김)
        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0, 0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }

        // --- 3. 썸(Thumb) 그리기 ---
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(thumbColor);
            // (이미지는 둥글지 않으므로 fillRect 사용)
            g2.fillRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height);
            
            g2.dispose();
        }

        // --- 4. 트랙(Track) 그리기 ---
        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            
            g2.dispose();
        }
    }
}