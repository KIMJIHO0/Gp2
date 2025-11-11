/**
 * 공통 AppTextArea
 * JTextArea가 아니라 JScrollPane을 상속받아, 스크롤바 자동 포함. 이게 편할듯.
 * 일단 패널에 add만 하면 거의 끝. 심플!
 */

package ui_kit;

import javax.swing.JTextArea;
import javax.swing.JScrollPane;


public class AppTextArea extends JScrollPane {
    // 실제 입력 핸들링용 인스턴스
    private final JTextArea textArea;

    public AppTextArea(int rows, int columns) {
        super(); // JScrollPane 생성자 호출
        
        textArea = new JTextArea(rows, columns);
        
        // 스타일 적용
        textArea.setFont(UITheme.FONT_BASE);
        textArea.setForeground(UITheme.COLOR_FOREGROUND);
        
        // 자동 줄바꿈
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true); 
        
        // 패딩
        textArea.setMargin(UITheme.PADDING);

        // JScrollPane 관련 설정
        // JScrollPane의 Viewport에 textArea 탑재
        setViewportView(textArea);
        
        // 테두리 설정
        setBorder(UITheme.LINE_BORDER);
        
        // 스크롤바 정책
        //  세로: 필요할 때만 보이도록(~AS_NEEDED)
        //  가로: X
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
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
    }
    
    // 비상용 접근자
    public JTextArea getTextAreaComponent() {
        return this.textArea;
    }
}