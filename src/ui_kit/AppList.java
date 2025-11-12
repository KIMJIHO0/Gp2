/**
 * AppList
 * JList인 척 하는(동일 인터페이스) JScrollPane(스크롤 제공)
 */

package ui_kit;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseListener;

public class AppList<E> extends JScrollPane {

    private final JList<E> list;

    /**
     * 기본 AppList를 생성합니다.
     */
    public AppList() {
        this.list = new JList<>();
        initScrollPane();
        initStyle();
    }

    /**
     * 지정된 모델로 AppList를 생성합니다.
     * @param dataModel 사용할 ListModel
     */
    public AppList(ListModel<E> dataModel) {
        this.list = new JList<>(dataModel);
        initScrollPane();
        initStyle();
    }

    /**
     * JList를 JScrollPane의 Viewport로 설정합니다.
     */
    private void initScrollPane() {
        setViewportView(list);
    }

    /**
     * UITheme을 사용하여 JList와 JScrollPane의 스타일을 지정합니다.
     */
    private void initStyle() {
        // JList 스타일
        list.setFont(UITheme.LIST_FONT);
        list.setForeground(UITheme.LIST_FG_COLOR);
        list.setBackground(UITheme.LIST_BG_COLOR);
        list.setSelectionForeground(UITheme.LIST_SELECTION_FG_COLOR);
        list.setSelectionBackground(UITheme.LIST_SELECTION_BG_COLOR);
        list.setCellRenderer(new AppListCellRenderer<>()); // 커스텀 렌더러 사용

        // JScrollPane (테두리) 스타일
        setBorder(UITheme.LIST_BORDER);
        getViewport().setBackground(UITheme.LIST_BG_COLOR);
    }

    // --- JList 핵심 메서드 래핑 ---

    /**
     * JList의 ListModel을 설정합니다.
     * @param model 설정할 ListModel
     */
    public void setModel(ListModel<E> model) {
        list.setModel(model);
    }

    /**
     * 현재 JList의 ListModel을 반환합니다.
     * @return 현재 ListModel
     */
    public ListModel<E> getModel() {
        return list.getModel();
    }

    /**
     * 현재 선택된 항목(첫 번째)을 반환합니다.
     * @return 선택된 항목 (없으면 null)
     */
    public E getSelectedValue() {
        return list.getSelectedValue();
    }

    /**
     * 현재 선택된 모든 항목을 반환합니다.
     * @return 선택된 항목들의 리스트
     */
    public java.util.List<E> getSelectedValuesList() {
        return list.getSelectedValuesList();
    }
    
    /**
     * 특정 인덱스의 항목을 선택합니다.
     * @param index 선택할 인덱스
     */
    public void setSelectedIndex(int index) {
        list.setSelectedIndex(index);
    }

    /**
     * 선택된 인덱스를 반환합니다.
     * @return 선택된 인덱스
     */
    public int getSelectedIndex() {
        return list.getSelectedIndex();
    }
    
    /**
     * 리스트의 선택 모드를 설정합니다.
     * (예: ListSelectionModel.SINGLE_SELECTION)
     * @param selectionMode 설정할 선택 모드
     */
    public void setSelectionMode(int selectionMode) {
        list.setSelectionMode(selectionMode);
    }

    /**
     * 내부 JList에 MouseListener를 추가합니다.
     * (더블 클릭 감지 등에 사용)
     * @param listener 추가할 MouseListener
     */
    @Override
    public void addMouseListener(MouseListener listener) {
        // JScrollPane이 아닌 내부 JList에 리스너를 연결해야 합니다.
        list.addMouseListener(listener);
    }

    /**
     * 내부 JList에 MouseListener를 제거합니다.
     * @param listener 제거할 MouseListener
     */
    @Override
    public void removeMouseListener(MouseListener listener) {
        list.removeMouseListener(listener);
    }

    /**
     * 커스텀 스타일(특히 패딩)을 적용하기 위한 내부 셀 렌더러입니다.
     */
    private static class AppListCellRenderer<E> extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            // 부모(DefaultListCellRenderer)의 메서드를 호출하여 기본 스타일(선택 색상 등)을 적용
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            // UITheme에 정의된 패딩을 적용
            Border paddingBorder = BorderFactory.createEmptyBorder(
                UITheme.LIST_CELL_PADDING.top,
                UITheme.LIST_CELL_PADDING.left,
                UITheme.LIST_CELL_PADDING.bottom,
                UITheme.LIST_CELL_PADDING.right
            );
            setBorder(paddingBorder);
            
            return c;
        }
    }
    
    /**
     * 내부 JList 컴포넌트에 직접 접근해야 할 때 사용합니다.
     * (권장되지는 않지만, 고급 기능을 위해 제공)
     * @return 내부 JList 컴포넌트
     */
    public JList<E> getInternalList() {
        return list;
    }
}