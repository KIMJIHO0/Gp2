/**
 * 공통 AppLabel
 * 안티앨리어싱 기본 적용(스무스한 폰트!)
 * 또한 기본 Label타입으로 fontweight 조절 기능 제공
 * 
 * 알글르ㅡ르르르르
 */


package ui_kit;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class AppLabel extends JLabel {

    public enum LabelType {
        NORMAL, // 기본
        BOLD,   // 두껍게
        TITLE,  // 제목
        SMALL   // 작게
    }

    public AppLabel(String text) {
        this(text, LabelType.NORMAL);
    }

    public AppLabel(String text, LabelType type) {
        super(text);
        setFont(getFontForType(type));
        setForeground(UITheme.COLOR_FOREGROUND);
    }

    private Font getFontForType(LabelType type) {
        switch (type) {
            case BOLD: return UITheme.FONT_BOLD;
            case TITLE: return UITheme.FONT_TITLE;
            case SMALL: return UITheme.FONT_SMALL;
            default: return UITheme.FONT_BASE;
        }
    }

    // 안티앨리어싱 로직 후킹
    // 클로드는 위대하고 Opus는 최고다!
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        // 텍스트 렌더링 힌트 설정
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        super.paintComponent(g2);
        g2.dispose();
    }
}