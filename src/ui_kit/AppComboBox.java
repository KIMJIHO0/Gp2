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

    // 스타일 적용
    private void initStyle() {
        setFont(UITheme.COMBOBOX_FONT);
        setForeground(UITheme.COMBOBOX_FG_COLOR);
        setBackground(UITheme.COMBOBOX_BG_COLOR);
        setBorder(UITheme.COMBOBOX_BORDER);
        setRenderer(new AppCellRenderer());
    }

    // 드롭다운 항목별 스타일도 적용하여 생성
    private static class AppCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            setFont(UITheme.COMBOBOX_FONT);
            setBorder(BorderFactory.createEmptyBorder(
                UITheme.COMBOBOX_ITEM_PADDING.top, 
                UITheme.COMBOBOX_ITEM_PADDING.left, 
                UITheme.COMBOBOX_ITEM_PADDING.bottom, 
                UITheme.COMBOBOX_ITEM_PADDING.right
            ));

            return this;
        }
    }
}
