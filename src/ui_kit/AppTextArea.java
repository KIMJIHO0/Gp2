package ui_kit;

import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Insets;

/**
 * 공통 AppTextArea
 * JTextArea가 아니라 JScrollPane을 상속받아, 스크롤바 자동 포함.
 * 내부 JTextArea에 Placeholder 기능 추가됨.
 */
public class AppTextArea extends ui_kit.AppScrollPane {
    // 실제 입력 핸들링용 인스턴스
    private final JTextArea textArea;
    private String placeholder;

    public AppTextArea(int rows, int columns) {
        super(); // JScrollPane 생성자 호출
        
        // 내부 JTextArea 생성 시 익명 클래스로 paintComponent 오버라이딩
        textArea = new JTextArea(rows, columns) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // 외부 클래스(AppTextArea)의 placeholder 필드 참조
                if (getText().length() == 0 && placeholder != null && !placeholder.isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(Color.GRAY);
                    g2.setFont(getFont());

                    Insets insets = getInsets();
                    // TextArea는 보통 상단 정렬이므로 상단 여백 + Ascent 만큼 띄움
                    int x = insets.left;
                    int y = insets.top + g.getFontMetrics().getAscent();

                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        
        textArea.setFont(UITheme.TEXTAREA_FONT);
        textArea.setForeground(UITheme.TEXTAREA_FG_COLOR);
        
        // 초기 마진 설정
        textArea.setMargin(UITheme.TEXTAREA_PADDING);
        
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true); 
        
        setViewportView(textArea);
        setBorder(UITheme.TEXTAREA_BORDER);
        
        setVerticalScrollBarPolicy(AppScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(AppScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    
    // 기본 텍스트 입력행렬 오버로딩
    public AppTextArea() {
        this(5, 20); // 기본값 (5행 20열)
    }

    // --- Inset(Margin) 설정 ---
    /**
     * 텍스트 영역 내부의 여백(Margin)을 설정합니다.
     */
    public void setMargin(Insets insets) {
        textArea.setMargin(insets);
    }

    // --- Placeholder 설정 ---
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        // ScrollPane이 아니라 내부 TextArea를 다시 그려야 함
        textArea.repaint(); 
    }

    public String getPlaceholder() {
        return placeholder;
    }

    // --- TextArea 위장용 Wrapper 메서드들 ---
    
    public String getText() {
        return textArea.getText();
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public void append(String text) {
        textArea.append(text);
    }
    
    public void setEditable(boolean editable) {
        textArea.setEditable(editable);
        textArea.setBackground(editable ? UITheme.TEXTAREA_BG_COLOR : UITheme.TEXTAREA_BG_COLOR_DISABLED);
    }
    
    // 비상용 접근자
    public JTextArea getTextAreaComponent() {
        return this.textArea;
    }
}