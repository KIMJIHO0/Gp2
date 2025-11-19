
package ui_kit;

import javax.swing.JTextArea;

/**
 * 공통 AppTextArea
 * 일단 패널에 add만 하면 거의 끝. 심플!
 */
public class AppTextArea extends AppScrollPane {
    // 실제 입력 핸들링용 인스턴스
    private final JTextArea textArea;

    public AppTextArea(int rows, int columns) {
        super(); // JScrollPane 생성자 호출
        
        textArea = new JTextArea(rows, columns);
        
        textArea.setFont(UITheme.TEXTAREA_FONT);
        textArea.setForeground(UITheme.TEXTAREA_FG_COLOR);
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

    // --- TextArea 위장용 Wrapper 메서드들 ---
    // AppTextArea(실은 AppScrollPane이었다?)를 TextArea처럼 사용 가능
    
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
