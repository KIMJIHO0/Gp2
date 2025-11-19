/**
 * 이미지의 검색 바 컴포넌트.
 * [콤보1][콤보2][검색버튼] [   검색창    ] [초기화버튼]
 * BorderLayout을 사용하여 검색창(CENTER)이 확장되도록 구현
 */

package pages.component;

import ui_kit.AppPanel;
import ui_kit.AppButton;
import ui_kit.AppComboBox; 
import ui_kit.AppTextField;
import ui_kit.UITheme;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class SearchBar extends AppPanel {

    private AppComboBox<String> regionComboBox;
    private AppComboBox<String> priceComboBox;
    private AppTextField searchTextField;
    private AppButton searchButton;
    private AppButton resetButton;

    public SearchBar() {
        super();
        init();
    }

    private void init() {
        // 1. SearchBar 패널 자체의 스타일 설정
        setLayout(new BorderLayout(5, 0)); 
        setBackground(UITheme.SEARCH_BAR_BG_COLOR);
        setPreferredSize(new Dimension(0, UITheme.SEARCH_BAR_HEIGHT));
        setBorder(UITheme.SEARCH_BAR_PADDING);

        // 2. WEST 영역: GridBagLayout으로 변경
        AppPanel westPanel = new AppPanel(new GridBagLayout()); 
        westPanel.setBackground(UITheme.TRANSPARENT);

        // GridBagLayout의 설정을 위한 GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 5); 

        // 2-1. 콤보박스들 (초기값 설정)
        regionComboBox = createComboBox(new String[]{"지역"});
        priceComboBox = createComboBox(new String[]{"가격대"});
        
        westPanel.add(regionComboBox, gbc);
        westPanel.add(priceComboBox, gbc);

        add(westPanel, BorderLayout.WEST);


        // 3. CENTER 영역: 입력창
        var searchPanel = new AppPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);

        // 3-1. 검색 아이콘 버튼
        searchButton = createIconButton(UITheme.SEARCH_ICON_PATH);
        searchPanel.add(searchButton, BorderLayout.WEST);

        // 3-2. 텍스트필드
        searchTextField = new AppTextField();
        searchTextField.setBackground(UITheme.SEARCH_BAR_TEXT_FIELD_BG);
        searchPanel.add(searchTextField, BorderLayout.CENTER);

        // 3-3. 초기화 아이콘
        resetButton = createIconButton(UITheme.RESET_ICON_PATH);
        searchPanel.add(resetButton, BorderLayout.EAST);

        add(searchPanel, BorderLayout.CENTER);
    }

    /**
     * 콤보박스 공통 스타일 생성
     */
    private AppComboBox<String> createComboBox(String[] items) {
        AppComboBox<String> comboBox = new AppComboBox<>(items);
        comboBox.setPreferredSize(UITheme.SEARCH_BAR_COMBO_SIZE);
        comboBox.setBorder(null);
        return comboBox;
    }

    /**
     * "지역" 콤보박스의 목록을 설정합니다.
     * 설정 후 자동으로 첫 번째 항목(기본값)을 선택합니다.
     */
    public void setRegions(String[] regions) {
        regionComboBox.removeAllItems();
        if (regions != null) {
            for (String region : regions) {
                regionComboBox.addItem(region);
            }
            // [Fix] 아이템 재설정 후 0번 인덱스(제목: "지역") 선택 강제
            if (regionComboBox.getItemCount() > 0) {
                regionComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * "가격대" 콤보박스의 목록을 설정합니다.
     * 설정 후 자동으로 첫 번째 항목(기본값)을 선택합니다.
     */
    public void setPrices(String[] prices) {
        priceComboBox.removeAllItems();
        if (prices != null) {
            for (String price : prices) {
                priceComboBox.addItem(price);
            }
            // [Fix] 아이템 재설정 후 0번 인덱스(제목: "가격") 선택 강제
            if (priceComboBox.getItemCount() > 0) {
                priceComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * 아이콘 버튼 공통 스타일 생성
     */
    private AppButton createIconButton(String iconPath) {
        AppButton iconButton = new AppButton("", false); 
        
        final int ICON_SIZE = UITheme.SEARCH_BAR_ICON_SIZE;
        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);
            iconButton.setIcon(scaledIcon);
        } catch (Exception e) {
            // 아이콘 로드 실패 시 텍스트 대체
            if (iconPath.contains("search")) iconButton.setText("검색");
            if (iconPath.contains("reset")) iconButton.setText("초기화");
        }
        
        iconButton.setOpaque(false);
        iconButton.setContentAreaFilled(false);
        iconButton.setBorderPainted(false);
        iconButton.setFocusPainted(false);
        iconButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        
        return iconButton;
    }


    // --- 외부 콜백 및 데이터 접근 ---

    public void addSearchListener(ActionListener callback) {
        searchButton.addActionListener(callback);
        searchTextField.addActionListener(callback);
    }
    
    public void addResetListener(ActionListener callback) {
        resetButton.addActionListener(callback);
    }

    /**
     * [Fix] 콤보박스 변경 감지 리스너 추가
     * 지역이나 가격 변경 시 즉시 필터링을 수행하기 위해 필요합니다.
     */
    public void addFilterListener(ActionListener callback) {
        regionComboBox.addActionListener(callback);
        priceComboBox.addActionListener(callback);
    }

    public String getSelectedRegion() {
        Object selected = regionComboBox.getSelectedItem();
        return selected != null ? (String) selected : "지역";
    }

    public String getSelectedPriceRange() {
        Object selected = priceComboBox.getSelectedItem();
        return selected != null ? (String) selected : "가격"; // [Fix] null 방지
    }
    
    // CatalogPage 호환성용
    public String getSelectedPrice() {
        return getSelectedPriceRange();
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

    public void clearFilters() {
        if(regionComboBox.getItemCount() > 0) regionComboBox.setSelectedIndex(0);
        if(priceComboBox.getItemCount() > 0) priceComboBox.setSelectedIndex(0);
        searchTextField.setText("");
    }
}