/**
 * 상세 검색 바 컴포넌트 (DetailedSearchBar)
 * * 구조:
 * [TopPanel]
 * - [상세검색 토글 버튼] (클릭 시 하단 패널 열림/닫힘, 화살표 반전)
 * - [검색창 패널] (검색 아이콘 + 텍스트 필드 + 검색 실행 버튼)
 * * [BottomPanel] (초기 상태: 숨김)
 * - [구분선]
 * - [필터 영역] (지역, 가격대, 인원수 등 콤보박스 - 첫 번째 행)
 * - [액션 영역] (필터 적용 버튼 - 두 번째 행 오른쪽)
 */

package pages.component;

import ui_kit.AppPanel;
import ui_kit.AppButton;
import ui_kit.AppComboBox;
import ui_kit.AppTextField;
import ui_kit.UITheme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.Image;
import java.awt.Color;
import java.awt.FlowLayout;


public class DetailedSearchBar extends AppPanel {

    // --- Components ---
    // Top Area
    private AppButton toggleButton;      // "상세검색" + 화살표
    private AppTextField searchTextField;
    private AppButton searchActionButton; // 검색창 내부 오른쪽 "엔터/검색" 버튼

    // Bottom Area (Collapsible)
    private AppPanel bottomPanel;        // 구분선 + 필터 + 적용 버튼을 감싸는 패널
    private AppComboBox<String> regionComboBox;
    private AppComboBox<String> priceComboBox;
    private AppComboBox<String> peopleComboBox;
    private AppComboBox<String> transportComboBox;
    private AppButton applyFilterButton; // "필터 적용" 버튼

    // State
    private boolean isExpanded = false; // 상세 검색창 열림 여부

    // Icon Paths
    private static final String ICON_ARROW_DOWN = "res/icons/arrow_down.png";
    private static final String ICON_ARROW_UP = "res/icons/arrow_up.png";
    private static final String ICON_SEARCH = UITheme.SEARCH_ICON_PATH;
    private static final String ICON_ENTER = UITheme.RESET_ICON_PATH; // 우측 화살표 아이콘 (사용자 요청 반영)

    public DetailedSearchBar() {
        super();
        init();
    }

    private void init() {
        // 1. 메인 패널 설정: 수직 배치 (위: 검색바, 아래: 상세필터)
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(UITheme.SEARCH_BAR_BG_COLOR);
        setBorder(UITheme.SEARCH_BAR_PADDING);

        // 2. 상단 영역 (Top Bar) 생성
        AppPanel topPanel = new AppPanel(new BorderLayout(19, 0));
        topPanel.setBackground(UITheme.TRANSPARENT);
        topPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, UITheme.SEARCH_BAR_HEIGHT)); // 높이 고정

        // 2-1. 상세검색 토글 버튼 (WEST)
        toggleButton = new AppButton("상세검색", false);
        toggleButton.setPreferredSize(new Dimension(114, UITheme.SEARCH_BAR_HEIGHT));
        toggleButton.setBackground(Color.WHITE);
        toggleButton.setHorizontalTextPosition(SwingConstants.LEFT); // 텍스트 왼쪽, 아이콘 오른쪽
        updateToggleIcon(); // 초기 아이콘 설정
        
        // 토글 액션 리스너 (내부 로직)
        toggleButton.addActionListener(e -> toggleDetailPanel());
        
        topPanel.add(toggleButton, BorderLayout.WEST);

        // 2-2. 검색 입력창 패널 (CENTER)
        AppPanel searchInputPanel = new AppPanel(new BorderLayout());
        searchInputPanel.setBackground(Color.WHITE);
        searchInputPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // 외곽선

        // 왼쪽: 돋보기 아이콘 (단순 표시용)
        AppButton searchIconDisplay = createIconButton(ICON_SEARCH);
        searchIconDisplay.setEnabled(false); // 클릭 안되게
        searchInputPanel.add(searchIconDisplay, BorderLayout.WEST);

        // 가운데: 텍스트 필드
        searchTextField = new AppTextField();
        searchTextField.setBackground(Color.WHITE);
        searchTextField.setBorder(null); // 내부 보더 제거
        searchInputPanel.add(searchTextField, BorderLayout.CENTER);

        // 오른쪽: 검색 실행 버튼 (엔터 화살표)
        searchActionButton = createIconButton(ICON_ENTER);
        searchInputPanel.add(searchActionButton, BorderLayout.EAST);

        topPanel.add(searchInputPanel, BorderLayout.CENTER);
        
        add(topPanel);

        // 3. 하단 영역 (Bottom Panel) 생성 - 초기에는 숨김
        createBottomPanel();
        add(bottomPanel);
    }

    private void createBottomPanel() {
        bottomPanel = new AppPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(UITheme.TRANSPARENT);
        bottomPanel.setVisible(false); // [핵심] 초기 상태: 숨김

        // 3-1. 구분선 (Divider)
        bottomPanel.add(Box.createVerticalStrut(15)); // 상단 마진
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(55, 236, 167));
        separator.setBackground(new Color(55, 236, 167));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        bottomPanel.add(separator);

        bottomPanel.add(Box.createVerticalStrut(14)); // 구분선 아래 마진

        // 3-2. 필터 영역 (첫 번째 행, 왼쪽 정렬)
        AppPanel filtersPanel = new AppPanel(new FlowLayout(FlowLayout.LEFT, 19, 0));
        filtersPanel.setBackground(UITheme.TRANSPARENT);
        // BoxLayout 내에서 높이가 무한히 늘어나지 않도록 고정
        filtersPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 27));

        regionComboBox = createComboBox(new String[]{"지역"});
        priceComboBox = createComboBox(new String[]{"가격대"});
        peopleComboBox = createComboBox(new String[]{"인원수"});
        transportComboBox = createComboBox(new String[]{"교통수단"});

        filtersPanel.add(regionComboBox);
        filtersPanel.add(priceComboBox);
        filtersPanel.add(peopleComboBox); 
        filtersPanel.add(transportComboBox); 

        bottomPanel.add(filtersPanel);

        // 3-3. 버튼 영역 (두 번째 행, 오른쪽 정렬)
        AppPanel buttonPanel = new AppPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UITheme.TRANSPARENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        applyFilterButton = new AppButton("필터 적용", true); // Primary 스타일
        applyFilterButton.setPreferredSize(new Dimension(92,27));
        buttonPanel.add(applyFilterButton);

        bottomPanel.add(buttonPanel);
    }

    /**
     * 상세 검색 패널 열기/닫기 토글 로직
     */
    private void toggleDetailPanel() {
        isExpanded = !isExpanded;
        bottomPanel.setVisible(isExpanded);
        updateToggleIcon();
        
        // 레이아웃 갱신 (부모 컨테이너가 변경된 크기를 인지하도록)
        revalidate();
        repaint();
    }

    /**
     * 상태에 따라 토글 버튼의 화살표 아이콘 변경
     */
    private void updateToggleIcon() {
        String iconPath = isExpanded ? ICON_ARROW_UP : ICON_ARROW_DOWN;
        
        // 아이콘 로드 로직 (createIconButton과 유사하나, 여기서는 setIcon만 수행)
        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(12, 12, Image.SCALE_SMOOTH);
            toggleButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            // 아이콘 없으면 텍스트로 대체 표시 (디버깅용)
            toggleButton.setText(isExpanded ? "상세검색 ▲" : "상세검색 ▼");
            toggleButton.setIcon(null);
        }
    }

    // --- Helper Methods ---

    private AppComboBox<String> createComboBox(String[] items) {
        AppComboBox<String> comboBox = new AppComboBox<>(items);
        comboBox.setPreferredSize(new Dimension(114, 27)); // 사진에 맞춰 높이 조절
        return comboBox;
    }

    private AppButton createIconButton(String iconPath) {
        AppButton iconButton = new AppButton("", false);
        final int ICON_SIZE = UITheme.SEARCH_BAR_ICON_SIZE;
        try {
            ImageIcon originalIcon = new ImageIcon(iconPath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH);
            iconButton.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            if (iconPath.contains("search")) iconButton.setText("🔍");
            else if (iconPath.contains("enter")) iconButton.setText("⏎");
            else iconButton.setText("Button");
        }
        
        iconButton.setOpaque(false);
        iconButton.setContentAreaFilled(false);
        iconButton.setBorderPainted(false);
        iconButton.setFocusPainted(false);
        iconButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        return iconButton;
    }

    // --- Data Setters ---

    public void setRegions(String[] regions) {
        updateComboBox(regionComboBox, regions);
    }

    public void setPrices(String[] prices) {
        updateComboBox(priceComboBox, prices);
    }
    
    // 필요 시 추가 (인원수, 교통수단 등)
    public void setPeoples(String[] items) { updateComboBox(peopleComboBox, items); }
    public void setTransports(String[] items) { updateComboBox(transportComboBox, items); }

    private void updateComboBox(AppComboBox<String> comboBox, String[] items) {
        comboBox.removeAllItems();
        if (items != null) {
            for (String item : items) {
                comboBox.addItem(item);
            }
            if (comboBox.getItemCount() > 0) comboBox.setSelectedIndex(0);
        }
    }

    // --- External Listeners & Getters ---

    /**
     * 검색 실행 리스너 등록
     * (상단 검색창의 텍스트 필드 엔터 또는 우측 화살표 버튼 클릭 시)
     */
    public void addSearchListener(ActionListener callback) {
        searchTextField.addActionListener(callback);
        searchActionButton.addActionListener(callback);
    }

    /**
     * 필터 적용 리스너 등록
     * (하단 '필터 적용' 버튼 클릭 시에만 동작)
     */
    public void addApplyFilterListener(ActionListener callback) {
        applyFilterButton.addActionListener(callback);
    }

    // 데이터 조회 메서드들
    public String getSearchText() {
        return searchTextField.getText();
    }

    public String getSelectedRegion() {
        return (String) regionComboBox.getSelectedItem();
    }

    public String getSelectedPriceRange() {
        return (String) priceComboBox.getSelectedItem();
    }
    
    // 추가 콤보박스 데이터 조회용
    public String getSelectedPeople() { return (String) peopleComboBox.getSelectedItem(); }
    public String getSelectedTransport() { return (String) transportComboBox.getSelectedItem(); }
}