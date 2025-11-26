package ui_kit;

import javax.swing.*;
import java.awt.*;

/**
 * AppOptionPane
 * JOptionPane과 동일한 인터페이스를 가지지만,
 * UITheme의 스타일(폰트, 색상)이 적용되고 긴 텍스트 처리가 개선된 커스텀 다이얼로그입니다.
 */
public class AppOptionPane extends JOptionPane {

    // --- 1. Message Dialog Wrappers ---

    public static void showMessageDialog(Component parentComponent, Object message) {
        showMessageDialog(parentComponent, message, "알림", INFORMATION_MESSAGE);
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType) {
        showMessageDialog(parentComponent, message, title, messageType, null);
    }

    public static void showMessageDialog(Component parentComponent, Object message, String title, int messageType, Icon icon) {
        applyTheme(); // 테마 적용
        JOptionPane.showMessageDialog(parentComponent, processMessage(message), title, messageType, icon);
    }

    // --- 2. Confirm Dialog Wrappers ---

    public static int showConfirmDialog(Component parentComponent, Object message) {
        return showConfirmDialog(parentComponent, message, "확인", YES_NO_CANCEL_OPTION);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType) {
        return showConfirmDialog(parentComponent, message, title, optionType, QUESTION_MESSAGE);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType) {
        return showConfirmDialog(parentComponent, message, title, optionType, messageType, null);
    }

    public static int showConfirmDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon) {
        applyTheme();
        return JOptionPane.showConfirmDialog(parentComponent, processMessage(message), title, optionType, messageType, icon);
    }

    // --- 3. Input Dialog Wrappers ---

    public static String showInputDialog(Object message) {
        return showInputDialog(null, message);
    }

    public static String showInputDialog(Component parentComponent, Object message) {
        return showInputDialog(parentComponent, message, "입력", QUESTION_MESSAGE);
    }

    public static String showInputDialog(Component parentComponent, Object message, String title, int messageType) {
        return (String) showInputDialog(parentComponent, message, title, messageType, null, null, null);
    }

    public static Object showInputDialog(Component parentComponent, Object message, String title, int messageType, Icon icon, Object[] selectionValues, Object initialSelectionValue) {
        applyTheme();
        return JOptionPane.showInputDialog(parentComponent, processMessage(message), title, messageType, icon, selectionValues, initialSelectionValue);
    }

    // --- 4. Option Dialog (Generic) ---

    public static int showOptionDialog(Component parentComponent, Object message, String title, int optionType, int messageType, Icon icon, Object[] options, Object initialValue) {
        applyTheme();
        return JOptionPane.showOptionDialog(parentComponent, processMessage(message), title, optionType, messageType, icon, options, initialValue);
    }

    // =================================================================================
    // Internal Utilities & Styling Logic
    // =================================================================================

    /**
     * UIManager를 통해 JOptionPane의 전역 스타일을 UITheme 값으로 덮어씌웁니다.
     * 이 메서드는 다이얼로그가 뜨기 직전에 호출되어야 합니다.
     */
    private static void applyTheme() {
        // 배경색
        UIManager.put("OptionPane.background", UITheme.DIALOG_BG_COLOR);
        UIManager.put("Panel.background", UITheme.DIALOG_BG_COLOR);

        // 텍스트 (기본 메시지 폰트 및 색상)
        UIManager.put("OptionPane.messageFont", UITheme.DIALOG_MSG_FONT);
        UIManager.put("OptionPane.messageForeground", UITheme.DIALOG_FG_COLOR);

        // 버튼 스타일 (옵션 버튼들)
        UIManager.put("Button.background", UITheme.BUTTON_BG_COLOR);
        UIManager.put("Button.foreground", UITheme.BUTTON_FG_COLOR);
        UIManager.put("Button.font", UITheme.BUTTON_FONT);
        UIManager.put("Button.select", UITheme.BUTTON_BG_COLOR_HOVER);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0)); // 포커스 점선 제거 (선택 사항)
    }

    /**
     * 전달된 메시지를 분석하여 스타일링된 컴포넌트로 변환합니다.
     * 문자열인 경우:
     * 1. 폰트와 색상을 UITheme에 맞춤.
     * 2. 자동으로 줄바꿈(Line Wrap)을 처리하는 JTextArea로 변환.
     * 3. 내용이 너무 길 경우(max height 초과) 자동으로 JScrollPane에 태움.
     *
     * @param message 원본 메시지 객체
     * @return 스타일링된 컴포넌트 또는 원본 객체
     */
    private static Object processMessage(Object message) {
        if (!(message instanceof String)) {
            return message; // 문자열이 아니면(예: 패널, 아이콘 등) 그대로 반환
        }

        String text = (String) message;
        JTextArea textArea = new JTextArea(text);
        
        // 스타일 적용
        textArea.setFont(UITheme.DIALOG_MSG_FONT);
        textArea.setForeground(UITheme.DIALOG_FG_COLOR);
        textArea.setBackground(UITheme.DIALOG_BG_COLOR);
        
        // 기능 설정
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false); // 텍스트 드래그는 가능하지만 포커스는 안 잡히게
        textArea.setOpaque(true);

        // 사이즈 계산 (너무 넓거나 길어지는 것을 방지)
        int textWidth = SwingUtilities.computeStringWidth(textArea.getFontMetrics(textArea.getFont()), text);
        int cols = Math.min(50, textWidth / 10 + 1); // 적절한 너비 추정
        textArea.setColumns(cols);

        // 내용이 너무 많으면 스크롤, 적으면 그냥 텍스트 영역 반환
        // (단순 계산: 줄바꿈 문자가 많거나 길이가 300자 이상이면 스크롤 권장)
        if (text.length() > 300 || text.split("\n").length > 10) {
            textArea.setRows(10);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder()); // 스크롤 판 테두리 제거
            scrollPane.getViewport().setBackground(UITheme.DIALOG_BG_COLOR);
            scrollPane.setPreferredSize(new Dimension(UITheme.DIALOG_MAX_WIDTH, UITheme.DIALOG_MAX_HEIGHT));
            return scrollPane;
        } else {
            return textArea;
        }
    }
}