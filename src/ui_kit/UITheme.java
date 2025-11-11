/**
 * UI의 공통(기본) '스타일'과 관련된 상수 모음
 * 일단은 일괄 적용하나, 상속받은 클래스에서 덮어씌울 수 있습니당
 */

package ui_kit;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Insets;


public class UITheme {

    // --- Fonts ---
    // 일단 시스템 기본값
    public static final Font FONT_BASE = new Font(Font.DIALOG, Font.PLAIN, 13);
    public static final Font FONT_BOLD = FONT_BASE.deriveFont(Font.BOLD);
    public static final Font FONT_TITLE = FONT_BASE.deriveFont(Font.BOLD, 16f);
    public static final Font FONT_SMALL = FONT_BASE.deriveFont(11f);

    // --- Colors ---
    // 색은 대충 html color palette 참고
    public static final Color COLOR_PRIMARY = new Color(0, 123, 255); // 파랑
    public static final Color COLOR_PRIMARY_DARK = new Color(0, 105, 217);
    public static final Color COLOR_FOREGROUND = new Color(33, 37, 41); // 짙은?어두운? 회색
    public static final Color COLOR_BACKGROUND = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(222, 226, 230); // 밝은 화색

    // --- Borders & Insets ---
    /** 컴포넌트 내부 여백 (ex. TextField, TextArea) */
    public static final Insets PADDING
        = new Insets(5, 8, 5, 8);
    
    /** 패널용 공통 여백 */
    public static final Border PANEL_PADDING
        = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    
    /** 라인이 있는 공통 테두리 */
    public static final Border LINE_BORDER
        = BorderFactory.createLineBorder(COLOR_BORDER);
}