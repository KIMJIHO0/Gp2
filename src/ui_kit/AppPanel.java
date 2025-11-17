/**
 * HTML의 <div> 태그처럼, 순수하게 컴포넌트들을 그룹화하기 위한 단순 레이아웃 패널.
 *
 * 배경이 투명(Opaque(false))하고 기본 패딩(내부 여백)이 없음.
 * 따라서 부모 컨테이너(AppPanel, AppTitledPanel)에 자연스럽게 스며들어 컴포넌트 배치를 돕는 용도
 */

package ui_kit;

import javax.swing.JPanel;
import java.awt.LayoutManager;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;


public class AppPanel extends JPanel {

    public AppPanel(LayoutManager layout) {
        super(layout);
        initStyle();
    }

    public AppPanel() {
        // 기본값 = FlowLayout(LEFT), 가로 정렬
        super(new FlowLayout(FlowLayout.LEFT));
        initStyle();
    }

    private void initStyle() {
        // 배경 투명화. 이외에 없음.
        setOpaque(false);
    }

    /**
     * 반투명 색상 적용 시 발생하는 오류 해결용
     */
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);

        if(bg == null) return;
        if(bg.getAlpha() < 255) // 반투명
            setOpaque(false);
        else                    // 불투명
            setOpaque(true);
    }
    
    /**
     * setOpaque=false(반투명)이면 super.paintComponent에서 배경을 안 그림.
     * 따라서 수동으로 그려주면 alpha값 포함된 색상 반영 가능.
     */
    @Override
    protected void paintComponent(Graphics g) {
        if(!isOpaque() && getBackground() != null){
            g.setColor(getBackground());
            g.fillRect(0,0,getWidth(),getHeight());
        }
        
        super.paintComponent(g);
    }
}