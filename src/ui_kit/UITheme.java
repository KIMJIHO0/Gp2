package ui_kit;

import java.awt.Color;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Insets;

/**
 * UI의 공통(기본) '스타일'과 관련된 상수 모음
 * 즉 단순한 색, 폰트, 여백 등의 스타일링은 여기 값의 변경만으로도 가능!
 * 일단은 일괄 적용하나, 상속받은 클래스에서 덮어씌울 수 있습니당
 */
public class UITheme {

    // --- Base Fonts & Colors ---
    private static final Font FONT_BASE = new Font(Font.DIALOG, Font.PLAIN, 13);
    private static final Font FONT_BOLD = FONT_BASE.deriveFont(Font.BOLD);
    private static final Color COLOR_PRIMARY = new Color(0, 123, 255);
    private static final Color COLOR_PRIMARY_DARK = new Color(0, 105, 217);
    private static final Color COLOR_TEXT_DEFAULT = new Color(33, 37, 41);
    private static final Color COLOR_TEXT_ON_PRIMARY = Color.WHITE;
    private static final Color COLOR_BACKGROUND_LIGHT = new Color(248, 249, 250);
    private static final Color COLOR_BACKGROUND_WHITE = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(222, 226, 230);

    // --- General Panel ---
    public static final Color PANEL_BACKGROUND_COLOR = COLOR_BACKGROUND_LIGHT;
    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);

    // --- AppButton ---
    public static final Font BUTTON_FONT = FONT_BOLD;
    public static final Color BUTTON_BG_COLOR = COLOR_PRIMARY;
    public static final Color BUTTON_BG_COLOR_HOVER = COLOR_PRIMARY_DARK;
    public static final Color BUTTON_FG_COLOR = COLOR_TEXT_ON_PRIMARY;

    // --- AppLabel ---
    public static final Font LABEL_FONT_NORMAL = FONT_BASE;
    public static final Font LABEL_FONT_BOLD = FONT_BOLD;
    public static final Font LABEL_FONT_TITLE = FONT_BASE.deriveFont(Font.BOLD, 18f);
    public static final Font LABEL_FONT_SUBTITLE = FONT_BASE.deriveFont(Font.BOLD, 14f);
    public static final Font LABEL_FONT_SMALL = FONT_BASE.deriveFont(11f);
    public static final Color LABEL_FG_COLOR_DEFAULT = COLOR_TEXT_DEFAULT;
    public static final Color LABEL_FG_COLOR_MUTED = new Color(108, 117, 125);

    // --- AppTextField ---
    public static final Font TEXTFIELD_FONT = FONT_BASE;
    public static final Color TEXTFIELD_FG_COLOR = COLOR_TEXT_DEFAULT;
    public static final Color TEXTFIELD_BG_COLOR = COLOR_BACKGROUND_WHITE;
    public static final Insets TEXTFIELD_PADDING = new Insets(8, 12, 8, 12);
    public static final Border TEXTFIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(COLOR_BORDER),
        BorderFactory.createEmptyBorder(TEXTFIELD_PADDING.top, TEXTFIELD_PADDING.left, TEXTFIELD_PADDING.bottom, TEXTFIELD_PADDING.right)
    );

    // --- AppTextArea ---
    public static final Font TEXTAREA_FONT = FONT_BASE;
    public static final Color TEXTAREA_FG_COLOR = COLOR_TEXT_DEFAULT;
    public static final Color TEXTAREA_BG_COLOR = COLOR_BACKGROUND_WHITE;
    public static final Color TEXTAREA_BG_COLOR_DISABLED = COLOR_BACKGROUND_LIGHT;
    public static final Insets TEXTAREA_PADDING = new Insets(8, 12, 8, 12);
    public static final Border TEXTAREA_BORDER = BorderFactory.createLineBorder(COLOR_BORDER);

    // --- AppComboBox ---
    public static final Font COMBOBOX_FONT = FONT_BASE;
    public static final Color COMBOBOX_FG_COLOR = COLOR_TEXT_DEFAULT;
    public static final Color COMBOBOX_BG_COLOR = COLOR_BACKGROUND_WHITE;
    public static final Border COMBOBOX_BORDER = BorderFactory.createLineBorder(COLOR_BORDER);
    public static final Insets COMBOBOX_ITEM_PADDING = new Insets(5, 10, 5, 10);

    // --- AppProgressBar ---
    public static final Font PROGRESSBAR_FONT = FONT_BASE.deriveFont(11f);
    public static final Color PROGRESSBAR_FG_COLOR = COLOR_PRIMARY;
    public static final Color PROGRESSBAR_BG_COLOR = COLOR_BACKGROUND_LIGHT;
    public static final Border PROGRESSBAR_BORDER = BorderFactory.createLineBorder(COLOR_BORDER);

    // --- AppTitledPanel ---
    public static final Font TITLED_PANEL_FONT = FONT_BOLD;
    public static final Color TITLED_PANEL_FG_COLOR = COLOR_TEXT_DEFAULT;
    public static final Border TITLED_PANEL_BORDER = BorderFactory.createLineBorder(COLOR_BORDER);
    public static final Insets TITLED_PANEL_CONTENT_PADDING = new Insets(15, 15, 15, 15);
}