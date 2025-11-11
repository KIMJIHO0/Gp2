
package ui_kit;

import javax.swing.JTextField;

/**
 * 공통 AppTextField
 * UITheme 기반 기본 폰트 및 내부 여백(padding) 적용
 */
public class AppTextField extends JTextField {

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
}
