/**
 * HTML의 <div> 태그처럼, 순수하게 컴포넌트들을 그룹화하기 위한 단순 레이아웃 패널.
 *
 * 배경이 투명(Opaque(false))하고 기본 패딩(내부 여백)이 없음.
 * 따라서 부모 컨테이너(AppPanel, AppTitledPanel)에 자연스럽게 스며들어 컴포넌트 배치를 돕는 용도
 */

package ui_kit;

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.FlowLayout;


public class AppPanel extends JPanel {

    public AppPanel(LayoutManager layout) {
        super(layout);
        initStyle();
    }

    public AppPanel() {
        // 기본값 = FlowLayout(LEFT), 가로 정렬
        super(new FlowLayout(FlowLayout.LEFT, 5, 5));
        initStyle();
    }

    private void initStyle() {
        // 배경 투명화. 이외에 없음.
        setOpaque(false);
    }
}