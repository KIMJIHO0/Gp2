/**
 * 이미지의 검색 바 컴포넌트.
 * [콤보1][콤보2][검색버튼] [   검색창    ] [초기화버튼]
 * BorderLayout을 사용하여 검색창(CENTER)이 확장되도록 구현
 */

package pages.component;

import ui_kit.AppPanel;
import ui_kit.AppButton;
import ui_kit.AppComboBox; // 가정: 콤보박스 컴포넌트
import ui_kit.AppTextField; // 가정: 텍스트필드 컴포넌트
import ui_kit.UITheme;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon; // 아이콘 사용

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
        // AppPanel westPanel = new AppPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // (X)
        AppPanel westPanel = new AppPanel(new GridBagLayout()); // (O)
        westPanel.setBackground(new Color(0,0,0,0));

        // GridBagLayout의 설정을 위한 GridBagConstraints
        GridBagConstraints gbc = new GridBagConstraints();
        
        // GridBagLayout의 기본 앵커는 CENTER이므로 컴포넌트들이 수직 중앙 정렬됨
        // gbc.anchor = GridBagConstraints.CENTER; // (이것이 기본값)

        // FlowLayout의 hgap(5)을 insets(오른쪽 여백)로 대체
        gbc.insets = new Insets(0, 0, 0, 5); 

        // 2-1. 콤보박스들
        regionComboBox = createComboBox(new String[]{"지역"});
        priceComboBox = createComboBox(new String[]{"가격대"});
        
        westPanel.add(regionComboBox, gbc);
        westPanel.add(priceComboBox, gbc);

        add(westPanel, BorderLayout.WEST);


        // 3. CENTER 영역: 입력창
        var searchPanel = new AppPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);

        // 3-1. 검색 아이콘 버튼 (마지막 컴포넌트이므로 오른쪽 여백 0)
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
     * @param regions 콤보박스에 표시할 문자열 배열
     */
    public void setRegions(String[] regions) {
        // 기존 아이템을 모두 제거합니다.
        regionComboBox.removeAllItems();
        
        // 새 아이템을 추가합니다.
        if (regions != null) {
            for (String region : regions) {
                regionComboBox.addItem(region);
            }
        }
    }

    /**
     * "가격대" 콤보박스의 목록을 설정합니다.
     * @param prices 콤보박스에 표시할 문자열 배열
     */
    public void setPrices(String[] prices) {
        // 기존 아이템을 모두 제거합니다.
        priceComboBox.removeAllItems();
        
        // 새 아이템을 추가합니다.
        if (prices != null) {
            for (String price : prices) {
                priceComboBox.addItem(price);
            }
        }
    }

    /**
     * 아이콘 버튼 공통 스타일 생성 (배경/테두리 투명)
     * + 아이콘 크기 조절 로직 추가
     * @param iconPath 아이콘 파일 경로
     * @return 스타일이 적용된 AppButton
     */
    private AppButton createIconButton(String iconPath) {
        AppButton iconButton = new AppButton("", false); // AppButton(text, autoStyling=false)
        
        // --- 아이콘 크기 조절 로직 ---
        final int ICON_SIZE = UITheme.SEARCH_BAR_ICON_SIZE;
        
        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            // ImageIcon에서 Image 추출
            Image originalImage = originalIcon.getImage();
            // Image 크기 조절 (SCALE_SMOOTH로 부드럽게)
            Image scaledImage = originalImage.getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            // 조절된 Image로 새 ImageIcon 생성
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            iconButton.setIcon(scaledIcon);
            
        } catch (Exception e) {
            // 아이콘 로드 실패 시 텍스트로 대체
            e.printStackTrace(); // 디버깅을 위해 로그 출력
            if (iconPath.contains("search")) iconButton.setText("검색");
            if (iconPath.contains("reset")) iconButton.setText("초기화");
        }
        
        // 아이콘 크기에 맞춰 버튼의 '선호하는 크기'를 고정 (테두리/여백 없는 아이콘 버튼)
        // iconButton.setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
        // 패딩 못 넣어서 삭제
        
        // Sidebar 버튼처럼 투명하게 만듦
        iconButton.setOpaque(false);
        iconButton.setContentAreaFilled(false);
        iconButton.setBorderPainted(false);
        iconButton.setFocusPainted(false);

        // 살짝 padding
        iconButton.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        
        return iconButton;
    }


    // --- 외부 콜백 및 데이터 접근 ---

    /**
     * 검색 버튼 클릭 또는 텍스트필드 Enter 시 호출될 콜백을 등록합니다.
     * @param callback 부모가 전달할 실제 검색 로직
     */
    public void addSearchListener(ActionListener callback) {
        searchButton.addActionListener(callback);
        searchTextField.addActionListener(callback);
    }
    
    /**
     * 초기화(화살표) 버튼 클릭 시 호출될 콜백을 등록합니다.
     * @param callback 부모가 전달할 초기화 로직
     */
    public void addResetListener(ActionListener callback) {
        resetButton.addActionListener(callback);
    }

    // 부모 컴포넌트가 검색 파라미터를 가져갈 수 있도록 Getter 제공
    public String getSelectedRegion() {
        return (String) regionComboBox.getSelectedItem();
    }

    public String getSelectedPriceRange() {
        return (String) priceComboBox.getSelectedItem();
    }

    public String getSearchText() {
        return searchTextField.getText();
    }

    // (필요시) 콤보박스나 텍스트필드를 초기화하는 public 메서드
    public void clearFilters() {
        regionComboBox.setSelectedIndex(0);
        priceComboBox.setSelectedIndex(0);
        searchTextField.setText("");
    }
}