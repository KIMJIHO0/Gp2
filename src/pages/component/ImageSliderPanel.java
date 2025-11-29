package pages.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import ui_kit.*;

/**
 * ImageSliderPanel
 * - CardLayout를 사용하여 한 번에 하나의 "슬라이드"만 표시
 * - 슬라이더 영역을 클릭하면 다음 슬라이드로 이동 (단방향, 순환)
 * - 현재 구현은 텍스트를 사용하는 더미 버전이지만,
 *   buildSlides(...) 내부만 바꾸면 이미지 슬라이더로 바로 확장 가능.
 */
public class ImageSliderPanel extends AppPanel {

    private final CardLayout imgLayout;
    private final AppPanel imgPanel;

    private int currentIndex = 0;
    private int itemCount = 0;

    
    public ImageSliderPanel() {
        super(new BorderLayout());

        this.imgLayout = new CardLayout();
        this.imgPanel = new AppPanel(imgLayout); // 이 패널 안에서 카드 전환

        // 슬라이더 영역 클릭 시 다음 카드로 이동
        this.imgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                next();
            }
        });

        //초기엔 비어 있음
        this.add(imgPanel, BorderLayout.CENTER);
    }

    //더미 텍스트 리스트 기준 임시 구성
    public void setSlids(List<String> texts) {
        buildSlides(texts);
        revalidate();
        repaint();
    }

    /**
     * 다음 슬라이드로 이동 (단방향, 순환).
     * 항목이 0개 또는 1개면 아무 것도 하지 않음.
     */
    public void next() {
        if (itemCount <= 1) return;
        int nextIndex = (currentIndex + 1) % itemCount;
        showIndex(nextIndex);
    }

    // ================= 내부 구현부 =================

    private void buildSlides(List<String> texts) {
        imgPanel.removeAll();
        currentIndex = 0;
        itemCount = 0;

        // 데이터가 없을 때: 기본 안내 슬라이드 하나
        if (texts == null || texts.isEmpty()) {
            AppLabel label = new AppLabel("표시할 이미지가 없습니다.", AppLabel.LabelType.MUTED);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            setBackground(new Color(0,0,0));

            AppPanel slide = new AppPanel(new BorderLayout());
            slide.add(label, BorderLayout.CENTER);

            imgPanel.add(slide, "empty");
            itemCount = 1;
            currentIndex = 0;
            imgLayout.show(imgPanel, "empty");
            return;
        }

        int i = 0;
        for (String text : texts) {
            // TODO: 실제 이미지 슬라이더 전환 시 여기 부분만 바꾸면 됨.
            // 예시)
            // AppLabel imageLabel = new AppLabel("");
            // imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            // imageLabel.setIcon(new ImageIcon(pathOrFromDto));
            //
            // 지금은 더미 텍스트만 사용:

            AppLabel label = new AppLabel(text, AppLabel.LabelType.TITLE);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            // 필요 시 크기 조정
            label.setFont(label.getFont().deriveFont(20f));

            AppPanel slide = new AppPanel(new BorderLayout());
            slide.add(label, BorderLayout.CENTER);

            String name = "slide-" + i;
            imgPanel.add(slide, name);
            i++;
        }

        itemCount = i;

        if (itemCount > 0) {
            showIndex(0);
        }
    }

    private void showIndex(int index) {
        if (index < 0 || index >= itemCount) {
            return;
        }
        currentIndex = index;
        String name = "slide-" + currentIndex;
        imgLayout.show(imgPanel, name);
    }
}