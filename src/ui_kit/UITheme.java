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
    private static final Color COLOR_GRID = new Color(233, 236, 239); // (Table용)

    // --- General Panel ---
    public static final Color PANEL_BACKGROUND_COLOR = COLOR_BACKGROUND_LIGHT; // AppTitledPanel, AppBasePanel 등 패널의 기본 배경색
    public static final Border PANEL_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15); // 패널류의 기본 내부 여백

    // --- AppButton ---
    public static final Font BUTTON_FONT = FONT_BOLD; // 버튼 텍스트 폰트
    public static final Color BUTTON_BG_COLOR = COLOR_PRIMARY; // 버튼 기본 배경색
    public static final Color BUTTON_BG_COLOR_HOVER = COLOR_PRIMARY_DARK; // 버튼 마우스 오버 배경색
    public static final Color BUTTON_FG_COLOR = COLOR_TEXT_ON_PRIMARY; // 버튼 텍스트 색상

    // --- AppLabel ---
    public static final Font LABEL_FONT_NORMAL = FONT_BASE; // 일반 라벨 폰트
    public static final Font LABEL_FONT_BOLD = FONT_BOLD; // 굵은 라벨 폰트
    public static final Font LABEL_FONT_TITLE = FONT_BASE.deriveFont(Font.BOLD, 18f); // 제목 라벨 폰트
    public static final Font LABEL_FONT_SUBTITLE = FONT_BASE.deriveFont(Font.BOLD, 14f); // 부제 라벨 폰트
    public static final Font LABEL_FONT_SMALL = FONT_BASE.deriveFont(11f); // 작은 라벨 폰트
    public static final Color LABEL_FG_COLOR_DEFAULT = COLOR_TEXT_DEFAULT; // 라벨 기본 텍스트 색상
    public static final Color LABEL_FG_COLOR_MUTED = new Color(108, 117, 125); // 흐린 라벨 텍스트 색상 (설명용)

    // --- AppTextField ---
    public static final Font TEXTFIELD_FONT = FONT_BASE; // 텍스트 필드 폰트
    public static final Color TEXTFIELD_FG_COLOR = COLOR_TEXT_DEFAULT; // 텍스트 필드 텍스트 색상
    public static final Color TEXTFIELD_BG_COLOR = COLOR_BACKGROUND_WHITE; // 텍스트 필드 배경색
    public static final Insets TEXTFIELD_PADDING = new Insets(8, 12, 8, 12); // 텍스트 필드 내부 여백 (Border와 조합)
    public static final Border TEXTFIELD_BORDER = BorderFactory.createCompoundBorder( // 텍스트 필드 최종 테두리 (라인 + 여백)
        BorderFactory.createLineBorder(COLOR_BORDER),
        BorderFactory.createEmptyBorder(TEXTFIELD_PADDING.top, TEXTFIELD_PADDING.left, TEXTFIELD_PADDING.bottom, TEXTFIELD_PADDING.right)
    );

    // --- AppTextArea ---
    public static final Font TEXTAREA_FONT = FONT_BASE; // 텍스트 영역 폰트
    public static final Color TEXTAREA_FG_COLOR = COLOR_TEXT_DEFAULT; // 텍스트 영역 텍스트 색상
    public static final Color TEXTAREA_BG_COLOR = COLOR_BACKGROUND_WHITE; // 텍스트 영역 배경색
    public static final Color TEXTAREA_BG_COLOR_DISABLED = COLOR_BACKGROUND_LIGHT; // 텍스트 영역 비활성 배경색
    public static final Insets TEXTAREA_PADDING = new Insets(8, 12, 8, 12); // 텍스트 영역 내부 여백 (Margin)
    public static final Border TEXTAREA_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 텍스트 영역 스크롤패널의 테두리

    // --- AppComboBox ---
    public static final Font COMBOBOX_FONT = FONT_BASE; // 콤보박스 폰트
    public static final Color COMBOBOX_FG_COLOR = COLOR_TEXT_DEFAULT; // 콤보박스 텍스트 색상
    public static final Color COMBOBOX_BG_COLOR = COLOR_BACKGROUND_WHITE; // 콤보박스 배경색
    public static final Border COMBOBOX_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 콤보박스 테두리
    public static final Insets COMBOBOX_ITEM_PADDING = new Insets(5, 10, 5, 10); // 콤보박스 드롭다운 항목 내부 여백

    // --- AppProgressBar ---
    public static final Font PROGRESSBAR_FONT = FONT_BASE.deriveFont(11f); // 진행률 표시줄 폰트
    public static final Color PROGRESSBAR_FG_COLOR = COLOR_PRIMARY; // 진행률 바(bar) 색상
    public static final Color PROGRESSBAR_BG_COLOR = COLOR_BACKGROUND_LIGHT; // 진행률 트랙(배경) 색상
    public static final Border PROGRESSBAR_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 진행률 표시줄 테두리

    // --- AppTitledPanel ---
    public static final Font TITLED_PANEL_FONT = FONT_BOLD; // 제목 패널의 '제목' 폰트
    public static final Color TITLED_PANEL_FG_COLOR = COLOR_TEXT_DEFAULT; // 제목 패널의 '제목' 텍스트 색상
    public static final Border TITLED_PANEL_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 제목 패널의 외곽선
    public static final Insets TITLED_PANEL_CONTENT_PADDING = new Insets(15, 15, 15, 15); // 제목 패널의 '내부' 여백

    // --- AppList ---
    public static final Font LIST_FONT = FONT_BASE; // 리스트 항목 폰트
    public static final Color LIST_FG_COLOR = COLOR_TEXT_DEFAULT; // 리스트 항목 텍스트 색상
    public static final Color LIST_BG_COLOR = COLOR_BACKGROUND_WHITE; // 리스트 배경색
    public static final Color LIST_SELECTION_FG_COLOR = COLOR_TEXT_ON_PRIMARY; // 선택된 리스트 항목 텍스트 색상
    public static final Color LIST_SELECTION_BG_COLOR = COLOR_PRIMARY; // 선택된 리스트 항목 배경색
    public static final Border LIST_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 리스트 스크롤패널의 테두리
    public static final Insets LIST_CELL_PADDING = new Insets(8, 12, 8, 12); // 리스트 각 항목(셀)의 내부 여백

    // --- AppTable ---
    public static final Font TABLE_FONT = FONT_BASE; // 테이블 셀 폰트
    public static final Font TABLE_HEADER_FONT = FONT_BOLD; // 테이블 헤더 폰트
    public static final Color TABLE_FG_COLOR = COLOR_TEXT_DEFAULT; // 테이블 셀 텍스트 색상
    public static final Color TABLE_BG_COLOR = COLOR_BACKGROUND_WHITE; // 테이블 셀 기본 배경색 (홀수 행)
    public static final Color TABLE_HEADER_FG_COLOR = COLOR_TEXT_DEFAULT; // 테이블 헤더 텍스트 색상
    public static final Color TABLE_HEADER_BG_COLOR = COLOR_BACKGROUND_LIGHT; // 테이블 헤더 배경색 (짝수 행)
    public static final Color TABLE_SELECTION_FG_COLOR = COLOR_TEXT_DEFAULT; // 선택된 테이블 셀 텍스트 색상
    public static final Color TABLE_SELECTION_BG_COLOR = COLOR_PRIMARY_DARK; // 선택된 테이블 셀 배경색
    public static final Color TABLE_GRID_COLOR = COLOR_GRID; // 테이블 셀 사이의 격자선 색상
    public static final Border TABLE_BORDER = BorderFactory.createLineBorder(COLOR_BORDER); // 테이블 스크롤패널의 테두리
    public static final int TABLE_ROW_HEIGHT = 30; // 테이블 각 행의 높이 (픽셀)
    public static final Insets TABLE_CELL_PADDING = new Insets(5, 8, 5, 8); // 테이블 각 셀의 내부 여백
}