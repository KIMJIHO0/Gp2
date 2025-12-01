package pages.component;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
    public void setSlids(List<BufferedImage> images) {
        buildSlides(images);
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

    private void buildSlides(List<BufferedImage> images) {
        imgPanel.removeAll();
        currentIndex = 0;
        itemCount = 0;

        // 데이터가 없을 때: 기본 안내 슬라이드 하나
        if (images == null || images.isEmpty()) {
            showEmptySlide("표시할 이미지가 없습니다.");
            return;
        }

        int i = 0;
        for (BufferedImage img : images) {
            if (img == null) continue; //null 건너뜀

            try {
                // 패널 크기 기준으로 스케일링. 0 나오면 기본값 사용.
                int targetW = getWidth() > 0 ? getWidth() : 500;
                int targetH = getHeight() > 0 ? getHeight() : 340;

                if (targetW <= 0 || targetH <= 0) {
                    targetW = 500;
                    targetH = 340;
                }

                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                ImageIcon icon = new ImageIcon(scaled);

                AppLabel imageLabel = new AppLabel("");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                imageLabel.setIcon(icon);

                AppPanel slide = new AppPanel(new BorderLayout());
                slide.add(imageLabel, BorderLayout.CENTER);

                imgPanel.add(slide, "slide-" + i);
                i++;

            } catch (Exception ex) {
                // 이 이미지 한 장만 실패하고, 나머지는 계속 진행
                System.err.println("이미지 슬라이드 생성 실패: " + ex.getMessage());
                // continue; // 그냥 다음 이미지로
            }
        }

        itemCount = i;

        // 쓸 수 있는 이미지가 하나도 없으면 기본 안내 슬라이드로 대체
        if (itemCount == 0) {
            showEmptySlide("이미지를 불러올 수 없습니다.");
        } else {
            showIndex(0);
        }
    }

// "표시할 이미지가 없습니다" 스타일 공통 처리용
private void showEmptySlide(String message) {
    AppLabel label = new AppLabel(message, AppLabel.LabelType.MUTED);
    label.setHorizontalAlignment(SwingConstants.CENTER);

    AppPanel slide = new AppPanel(new BorderLayout());
    slide.add(label, BorderLayout.CENTER);

    imgPanel.add(slide, "empty");
    itemCount = 1;
    currentIndex = 0;
    imgLayout.show(imgPanel, "empty");
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