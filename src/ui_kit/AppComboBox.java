/**
 * 공통 AppComboBox.
 * 제네릭 지원 및 기본 폰트 및 스타일 적용(UITheme)
 * 드롭다운 리스트의 렌더러에도 폰트와 여백 적용
 * 
 * L&F 외부 스타일 정책 적용 시 일부는 무시될 수 있음! 꼭 테스트할 것
 */

package ui_kit;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.BorderFactory;
import java.awt.Component;
import java.util.Vector;

/**
 * 공통 AppComboBox.
 * 제네릭을 지원하며, 기본 폰트 및 스타일이 적용됩니다.
 * [핵심] 드롭다운 리스트의 렌더러에도 폰트와 여백을 적용하여 일관성을 유지합니다.
 */
public class AppComboBox<E> extends JComboBox<E> {

    public AppComboBox() {
        super();
        initStyle();
    }

    public AppComboBox(E[] items) {
        super(items);
        initStyle();
    }

    public AppComboBox(Vector<E> items) {
        super(items);
        initStyle();
    }

    private void initStyle() {
        setFont(UITheme.FONT_BASE);
        setForeground(UITheme.COLOR_FOREGROUND);
        setBackground(UITheme.COLOR_BACKGROUND); // L&F에 따라 무시될 수 있음
        setBorder(UITheme.LINE_BORDER);

        // [핵심] 콤보박스 및 드롭다운 리스트의 아이템 렌더러 설정
        setRenderer(new AppCellRenderer());
    }

    /**
     * 콤보박스의 셀 렌더러에 공통 폰트와 여백을 적용합니다.
     */
    private static class AppCellRenderer extends DefaultListCellRenderer {
        
        // 원본 렌더러를 조합(Composition)하거나 상속(Inheritance)할 수 있습니다.
        // 여기서는 DefaultListCellRenderer를 상속받아 간단히 커스터마이징합니다.

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            
            // 1. 기본 렌더링(선택 배경색, 텍스트 설정 등)은 부모에 위임
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            // 2. 공통 스타일 적용
            setFont(UITheme.FONT_BASE);
            
            // 3. 드롭다운 목록에 내부 여백(padding) 적용
            setBorder(BorderFactory.createEmptyBorder(
                UITheme.PADDING.top, 
                UITheme.PADDING.left, 
                UITheme.PADDING.bottom, 
                UITheme.PADDING.right
            ));

            return this;
        }
    }
}