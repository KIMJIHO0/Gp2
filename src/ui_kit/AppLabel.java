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
import java.awt.Color;

public class AppLabel extends JLabel {

    // 자식에서 쓸 편의성 타입
    public enum LabelType {
        NORMAL,
        BOLD,
        TITLE,
        SUBTITLE,
        SMALL,
        MUTED
    }

    public AppLabel(String text) {
        this(text, LabelType.NORMAL);
    }

    public AppLabel(String text, LabelType type) {
        super(text);
        setFont(getFontForType(type));
        setForeground(getColorForType(type));
    }

    // 타입 -> 실제 폰트 변환
    private Font getFontForType(LabelType type) {
        switch (type) {
            case BOLD: return UITheme.LABEL_FONT_BOLD;
            case TITLE: return UITheme.LABEL_FONT_TITLE;
            case SUBTITLE: return UITheme.LABEL_FONT_SUBTITLE;
            case SMALL: return UITheme.LABEL_FONT_SMALL;
            default: return UITheme.LABEL_FONT_NORMAL;
        }
    }

    private Color getColorForType(LabelType type) {
        if (type == LabelType.MUTED) {
            return UITheme.LABEL_FG_COLOR_MUTED;
        }
        return UITheme.LABEL_FG_COLOR_DEFAULT;
    }

    // 안티앨리어싱 로직
    // 클로드는 위대하다!
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        super.paintComponent(g2);
        g2.dispose();
    }
}
