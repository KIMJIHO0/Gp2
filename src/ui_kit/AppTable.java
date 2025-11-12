/**
 * AppTable
 * JTable인 척 하는(인터페이스 제공) JScrollPane 래퍼(스크롤 제공)
 * JTable과 JTableHeader의 스타일은 UITheme 참고
 */

package ui_kit;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseListener;

public class AppTable extends JScrollPane {

    private final JTable table;

    /**
     * 기본 AppTable을 생성합니다.
     */
    public AppTable() {
        this.table = new JTable();
        initScrollPane();
        initStyle();
    }

    /**
     * 지정된 모델로 AppTable을 생성합니다.
     * @param model 사용할 TableModel
     */
    public AppTable(TableModel model) {
        this.table = new JTable(model);
        initScrollPane();
        initStyle();
    }

    /**
     * JTable을 JScrollPane의 Viewport로 설정합니다.
     */
    private void initScrollPane() {
        setViewportView(table);
    }

    /**
     * UITheme을 사용하여 JTable, JTableHeader, JScrollPane의 스타일을 지정합니다.
     */
    private void initStyle() {
        // JTable 스타일
        table.setFont(UITheme.TABLE_FONT);
        table.setForeground(UITheme.TABLE_FG_COLOR);
        table.setBackground(UITheme.TABLE_BG_COLOR);
        table.setSelectionForeground(UITheme.TABLE_SELECTION_FG_COLOR);
        table.setSelectionBackground(UITheme.TABLE_SELECTION_BG_COLOR);
        table.setGridColor(UITheme.TABLE_GRID_COLOR);
        table.setRowHeight(UITheme.TABLE_ROW_HEIGHT);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // 컬럼 자동 리사이즈
        table.setFillsViewportHeight(true); // 빈 공간도 테이블 배경색으로 채움

        // 커스텀 셀 렌더러 (패딩 적용)
        AppTableCellRenderer cellRenderer = new AppTableCellRenderer();
        table.setDefaultRenderer(Object.class, cellRenderer);
        table.setDefaultRenderer(Number.class, cellRenderer);
        table.setDefaultRenderer(Boolean.class, table.getDefaultRenderer(Boolean.class)); // 체크박스 유지

        // JTableHeader 스타일
        JTableHeader header = table.getTableHeader();
        header.setFont(UITheme.TABLE_HEADER_FONT);
        header.setForeground(UITheme.TABLE_HEADER_FG_COLOR);
        header.setBackground(UITheme.TABLE_HEADER_BG_COLOR);
        header.setReorderingAllowed(false); // (선택) 헤더 순서 변경 비활성화

        // JScrollPane (테두리) 스타일
        setBorder(UITheme.TABLE_BORDER);
        getViewport().setBackground(UITheme.TABLE_BG_COLOR);
    }

    // --- JTable 핵심 메서드 래핑 ---

    /**
     * JTable의 TableModel을 설정합니다.
     * @param model 설정할 TableModel
     */
    public void setModel(TableModel model) {
        table.setModel(model);
    }

    /**
     * 현재 JTable의 TableModel을 반환합니다.
     * @return 현재 TableModel
     */
    public TableModel getModel() {
        return table.getModel();
    }

    /**
     * 현재 선택된 행(첫 번째)의 인덱스를 반환합니다.
     * @return 선택된 행 인덱스 (없으면 -1)
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    /**
     * 현재 선택된 열(첫 번째)의 인덱스를 반환합니다.
     * @return 선택된 열 인덱스 (없으면 -1)
     */
    public int getSelectedColumn() {
        return table.getSelectedColumn();
    }

    /**
     * 특정 행과 열에 있는 값(Value)을 반환합니다.
     * (주의: 모델 인덱스가 아닌 뷰 인덱스)
     * @param row 뷰의 행 인덱스
     * @param column 뷰의 열 인덱스
     * @return 해당 셀의 Object 값
     */
    public Object getValueAt(int row, int column) {
        return table.getValueAt(row, column);
    }

    /**
     * 테이블의 선택 모드를 설정합니다.
     * (예: ListSelectionModel.SINGLE_SELECTION)
     * @param selectionMode 설정할 선택 모드
     */
    public void setSelectionMode(int selectionMode) {
        table.setSelectionMode(selectionMode);
    }

    /**
     * 내부 JTable에 MouseListener를 추가합니다.
     * (더블 클릭 감지 등에 사용)
     * @param listener 추가할 MouseListener
     */
    @Override
    public void addMouseListener(MouseListener listener) {
        table.addMouseListener(listener);
    }

    /**
     * 내부 JTable에 MouseListener를 제거합니다.
     * @param listener 제거할 MouseListener
     */
    @Override
    public void removeMouseListener(MouseListener listener) {
        table.removeMouseListener(listener);
    }

    /**
     * 커스텀 스타일(특히 패딩)을 적용하기 위한 내부 셀 렌더러입니다.
     */
    private static class AppTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // 부모(DefaultTableCellRenderer)의 메서드를 호출하여 기본 스타일(선택 색상 등)을 적용
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // UITheme에 정의된 패딩을 적용
            Border paddingBorder = BorderFactory.createEmptyBorder(
                UITheme.TABLE_CELL_PADDING.top,
                UITheme.TABLE_CELL_PADDING.left,
                UITheme.TABLE_CELL_PADDING.bottom,
                UITheme.TABLE_CELL_PADDING.right
            );
            setBorder(paddingBorder);
            
            // (선택) 짝수/홀수 행 배경색 구분
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? UITheme.TABLE_BG_COLOR : UITheme.TABLE_HEADER_BG_COLOR);
            }

            return c;
        }
    }
    
    /**
     * 내부 JTable 컴포넌트에 직접 접근해야 할 때 사용합니다.
     * (권장되지는 않지만, 고급 기능을 위해 제공)
     * @return 내부 JTable 컴포넌트
     */
    public JTable getInternalTable() {
        return table;
    }
}