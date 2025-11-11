/**
 * 공통 AppTextField
 * UITheme 기반 기본 폰트 및 내부 여백(padding) 적용
 */

package ui_kit;

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.Border;


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
        setFont(UITheme.FONT_BASE);
        setForeground(UITheme.COLOR_FOREGROUND);
        
        // 패딩 적용
        // EmptyBorder(top, left, bottom, right)
        Border padding = BorderFactory.createEmptyBorder(
            UITheme.PADDING.top, 
            UITheme.PADDING.left, 
            UITheme.PADDING.bottom, 
            UITheme.PADDING.right
        );
        
        // 기존 테두리와 여백 테두리를 조합
        setBorder(BorderFactory.createCompoundBorder(
            UITheme.LINE_BORDER, // 바깥쪽 테두리
            padding              // 안쪽 여백
        ));
    }
}