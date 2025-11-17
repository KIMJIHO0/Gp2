/**
 * 여행 패키지 정보들을 목록화하여 보여주고, 좌측 메뉴도 함께 제공하는 페이지.
 * 좌측 메뉴는 BorderLayout.WEST로, 
 * 나머지는 CENTER로 분할하고 각각은 복잡하게 구성되어있기에 별도의 컴포넌트로 구현함.
 */

package pages;

import ui_kit.*;
import pages.component.Sidebar;
import pages.component.SearchBar; // 1. SearchBar 임포트

import java.awt.BorderLayout;
import java.awt.Color; // 2. Color, BorderFactory 임포트


public class CatalogPage extends AppPage {
    
    // (ServiceContext가 정의되어 있다고 가정)
    public CatalogPage(ServiceContext ctx){ 
        super(new BorderLayout(), ctx);
        init();
    }

    @Override
    public String getPageId(){
        return "catalog";
    }

    private Sidebar sideNav;
    private SearchBar searchBar; // 3. SearchBar 필드 추가
    private AppPanel packageListPanel; // 4. 패키지 목록을 위한 임시 패널

    public void init(){
        // 본인 스타일링
        setOpaque(true);
        setBackground(UITheme.PANEL_BACKGROUND_COLOR);

        // --- 1. 사이드바 (기존 코드) ---
        sideNav = new Sidebar();
        sideNav.addMenu("여행 패키지 목록", null);
        sideNav.addMenu("예약 내역 확인", null);
        sideNav.addMenu("추천 패키지", null);
        sideNav.setLogoutCallback(null);

        add(sideNav, BorderLayout.WEST);
        
        // --- 2. 메인 콘텐츠 패널 (신규 코드) ---
        // (CENTER 영역은 SearchBar와 List, 두 개로 나뉘므로 새 패널 필요)
        AppPanel mainContentPanel = new AppPanel(new BorderLayout(0, 10)); // 수직 갭 10px
        mainContentPanel.setOpaque(true);
        mainContentPanel.setBackground(UITheme.PANEL_BACKGROUND_COLOR);

        // "위/좌/우 패딩"을 위해 UITheme의 공통 패널 보더 사용
        // (UITheme.PANEL_BORDER가 15px의 상하좌우 여백을 제공한다고 가정)
        mainContentPanel.setBorder(UITheme.CATALOG_PANEL_BORDER);
        // (배경색은 AppPage의 기본값을 따름)

        // --- 3. 검색창 (mainContentPanel의 NORTH) ---
        searchBar = new SearchBar();
        searchBar.setRegions(new String[]{"지역"});
        searchBar.setPrices(new String[]{"가격"});
        mainContentPanel.add(searchBar, BorderLayout.NORTH);

        // --- 4. 패키지 목록 (mainContentPanel의 CENTER) ---
        // (이곳은 이미지의 스크롤 목록이 들어갈 자리입니다)
        packageListPanel = new AppPanel(); // 임시 패널
        packageListPanel.setBackground(Color.WHITE); // 구분을 위한 임시 배경색
        mainContentPanel.add(packageListPanel, BorderLayout.CENTER);

        // --- 5. 완성된 메인 패널을 페이지의 CENTER에 추가 ---
        add(mainContentPanel, BorderLayout.CENTER);

        // --- 6. (권장) 검색 바 콜백 연결 ---
        searchBar.addSearchListener(e -> {
            System.out.println("검색 실행:");
            System.out.println(" - 지역: " + searchBar.getSelectedRegion());
            System.out.println(" - 가격: " + searchBar.getSelectedPriceRange());
            System.out.println(" - 텍스트: " + searchBar.getSearchText());
            // TODO: packageListPanel의 내용을 실제 검색 결과로 업데이트
        });

        searchBar.addResetListener(e -> {
            searchBar.clearFilters();
            System.out.println("필터 초기화됨");
            // TODO: packageListPanel의 내용을 전체 목록으로 복원
        });
    }
}