package pages.component;

import ui_kit.AppButton;
import ui_kit.AppLabel;
import ui_kit.AppPanel;
import ui_kit.UITheme;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionListener;

/**
 * 패키지 여행 1개 항목을 보여주는 배너 컴포넌트 (이미지 1개)
 * 1. AppPanel 상속, BorderLayout
 * 5. setThumbnail(WEST), 8. 텍스트(CENTER), 6. 버튼(EAST)
 */
public class TourBanner extends AppPanel {

    // 컴포넌트 참조 필드
    private AppLabel thumbnailLabel;
    private AppLabel titleLabel;
    private AppLabel subLabel;
    protected AppButton detailButton;

    /**
     * 3. (제목, 지역 기간, 가격, 평점)을 받아 생성됨.
     */
    public TourBanner(String title, String region, String duration, String price, String headcount, String rating) {
        super(new BorderLayout(15, 0)); // 1. BorderLayout (수평 갭 15px)
        init();
        
        // 8. 텍스트 설정
        titleLabel.setText(title);
        subLabel.setText(String.format("%s | %s | %s | %s | %s", region, duration, price, headcount, rating));
    }

    private void init() {
        // 4. 배경은 투명
        setBackground(UITheme.TOUR_BANNER_BG_COLOR);
        // 전체 패널 패딩 적용
        setBorder(UITheme.TOUR_BANNER_PADDING);

        // 5. WEST: 썸네일 (setThumbnail로 채워질 영역)
        thumbnailLabel = new AppLabel("");
        
        // --- 썸네일 기본 상태: 0x0 크기, 보이지 않음 ---
        thumbnailLabel.setPreferredSize(new Dimension(0,0));
        thumbnailLabel.setVisible(false); 
        thumbnailLabel.setHorizontalAlignment(AppLabel.CENTER); // 아이콘 로드 실패시 텍스트 중앙정렬
        add(thumbnailLabel, BorderLayout.WEST);

        // 8. CENTER: 텍스트 (제목, 부가정보)
        AppPanel centerTextPanel = new AppPanel();
        centerTextPanel.setLayout(new BoxLayout(centerTextPanel, BoxLayout.Y_AXIS));
        centerTextPanel.setBackground(UITheme.TRANSPARENT);

        titleLabel = new AppLabel(" "); // 생성자에서 채워짐
        titleLabel.setFont(UITheme.TOUR_BANNER_TITLE_FONT);
        titleLabel.setForeground(UITheme.LABEL_FG_COLOR_DEFAULT);

        subLabel = new AppLabel(" "); // 생성자에서 채워짐
        subLabel.setFont(UITheme.TOUR_BANNER_SUB_FONT);
        subLabel.setForeground(UITheme.TOUR_BANNER_SUB_FG_COLOR);
        
        centerTextPanel.add(titleLabel);
        centerTextPanel.add(subLabel);
        add(centerTextPanel, BorderLayout.CENTER);

        // 6. EAST: 버튼 ("상세보기")
        // (수직 중앙 정렬을 위해 SearchBar에서 사용한 GridBagLayout 래퍼 사용)
        initEast();
    }
    protected void initEast(){
        AppPanel buttonWrapper = new AppPanel(new GridBagLayout());
        buttonWrapper.setBackground(UITheme.TRANSPARENT);

        detailButton = new AppButton("상세보기", false); // AppButton(text, autoStyling=true)
        detailButton.setBackground(UITheme.TOUR_BANNER_DETAILBTN_COLOR);
        detailButton.setPreferredSize(UITheme.TOUR_BANNER_EAST_BTN_SIZE);

        buttonWrapper.add(detailButton, new GridBagConstraints()); // 중앙에 배치
        add(buttonWrapper, BorderLayout.EAST);
    }

    /**
     * 5. 썸네일(사진 경로)을 받아 스케일링하여 WEST에 배치
     * @param imagePath
     */
    public void setThumbnail(String imagePath) {
        boolean success = false;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                Image originalImage = originalIcon.getImage();

                // 썸네일 크기에 맞게 스케일링
                Image scaledImage = originalImage.getScaledInstance(
                    UITheme.TOUR_BANNER_THUMBNAIL_SIZE.width, 
                    UITheme.TOUR_BANNER_THUMBNAIL_SIZE.height, 
                    Image.SCALE_SMOOTH
                );
                
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                thumbnailLabel.setIcon(scaledIcon);
                thumbnailLabel.setText(null); // 이미지가 있으니 텍스트 제거
                success = true; // 로드 성공

            } catch (Exception e) {
                e.printStackTrace();
                thumbnailLabel.setIcon(null);
                thumbnailLabel.setText("Load Error"); // 로드 실패 시
                success = false;
            }
        } else {
             thumbnailLabel.setIcon(null);
             thumbnailLabel.setText("No Image"); // 이미지 경로가 없을 때
             success = false;
        }

        // --- (수정) 썸네일 유무에 따라 크기와 가시성 제어 ---
        if (success) {
            // 이미지가 있으면: 크기 80x80, 보이게
            thumbnailLabel.setPreferredSize(UITheme.TOUR_BANNER_THUMBNAIL_SIZE);
            thumbnailLabel.setVisible(true);
        } else {
            // 이미지가 없으면: 크기 0x0, 보이지 않게
            thumbnailLabel.setPreferredSize(new Dimension(0,0));
            thumbnailLabel.setVisible(false);
        }
        // --- (수정 완료) ---
    }

    /**
     * 6. "상세보기" 버튼 콜백 등록 메서드
     * @param listener
     */
    public void addDetailButtonListener(ActionListener listener) {
        detailButton.addActionListener(listener);
    }

    /**
* 7. 가로 크기는 부모 컨테이너의 가로 100%
     * (BoxLayout(Y_AXIS) 등에 배치될 때를 대비)
     */
    @Override
    public Dimension getMaximumSize() {
        // (가로 최대, 세로 선호)
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}