package ui_kit;

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;

/**
 * 공통 AppTextField
 * UITheme 기반 기본 폰트 및 내부 여백(padding) 적용
 * + Placeholder 기능 추가
 * + Padding 설정 기능 추가
 */
public class AppTextField extends JTextField {

    private String placeholder;

    public AppTextField(int columns) {
        super(columns);
        initStyle();
    }

    public AppTextField() {
        super();
        initStyle();
    }

    private void initStyle() {
        setFont(UITheme.TEXTFIELD_FONT);
        setForeground(UITheme.TEXTFIELD_FG_COLOR);
        setBackground(UITheme.TEXTFIELD_BG_COLOR);
        setBorder(UITheme.TEXTFIELD_BORDER);
    }

    /**
     * 텍스트 필드 내부의 여백(Padding)을 설정합니다.
     * JTextField의 setMargin은 커스텀 Border 사용 시 무시될 수 있으므로,
     * CompoundBorder를 사용하여 강제로 여백을 부여합니다.
     */
    public void setPadding(Insets insets) {
        setBorder(BorderFactory.createCompoundBorder(
            UITheme.TEXTFIELD_BORDER, // 바깥쪽: 기존 테두리
            BorderFactory.createEmptyBorder(insets.top, insets.left, insets.bottom, insets.right) // 안쪽: 여백
        ));
        repaint();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint(); // 변경 시 즉시 다시 그리기
    }

    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 텍스트가 비어있고, placeholder가 설정되어 있을 때만 그리기
        if (getText().length() == 0 && placeholder != null && !placeholder.isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            
            // 텍스트 품질 향상 (안티앨리어싱)
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            
            // 스타일 설정
            g2.setColor(Color.GRAY); // 혹은 UITheme.PLACEHOLDER_COLOR
            g2.setFont(getFont());   // 현재 폰트 그대로 사용

            // 폰트 정보 가져오기
            FontMetrics fm = g2.getFontMetrics();
            
            // 위치 계산 (Padding/Insets 고려)
            // setPadding으로 변경된 Insets가 여기서 자동으로 반영됨
            Insets insets = getInsets();
            
            // x: 왼쪽 여백 + 텍스트 시작점
            int x = insets.left;
            
            // y: 수직 중앙 정렬 계산 (매직 넘버 제거)
            int contentHeight = getHeight() - insets.top - insets.bottom;
            int textY = (contentHeight - fm.getHeight()) / 2 + fm.getAscent();
            int y = insets.top + textY;

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}