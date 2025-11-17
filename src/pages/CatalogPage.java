/**
 * 여행 패키지 정보들을 목록화하여 보여주고, 좌측 메뉴도 함께 제공하는 페이지.
 * JList/AppList 대신 AppScrollPane을 이용해 직접 구현
 * 클릭 가능한 TourBanner 컴포넌트 목록을 구현합니다.
 */

package pages;

import ui_kit.*;
import pages.component.Sidebar;
import pages.component.SearchBar;
import pages.component.TourBanner; // 1. TourBanner 임포트

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;


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
    private SearchBar searchBar;
    private AppPanel packageListPanel; // 4. 패키지 배너들을 담을 컨테이너 패널

    public void init(){
        // 본인 스타일링 (페이지 전체의 기본 배경색)
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
        
        // --- (요청) 메인 패널 배경색 투명하게 ---
        mainContentPanel.setBackground(new Color(0,0,0,0));
        // ---

        // "위/좌/우 패딩"을 위해 UITheme의 공통 패널 보더 사용
        mainContentPanel.setBorder(UITheme.CATALOG_PANEL_BORDER);


        // --- 3. 검색창 (mainContentPanel의 NORTH) ---
        searchBar = new SearchBar();
        searchBar.setRegions(new String[]{"지역", "서울", "부산", "제주"});
        searchBar.setPrices(new String[]{"가격", "~50만원", "~100만원", "100만원~"});
        mainContentPanel.add(searchBar, BorderLayout.NORTH);

        // --- 4. 패키지 목록 (mainContentPanel의 CENTER) ---
        
        // 4-1. 배너들을 세로로 쌓을 컨테이너 패널 (JList 대신)
        packageListPanel = new AppPanel();
        packageListPanel.setLayout(new BoxLayout(packageListPanel, BoxLayout.Y_AXIS));
        packageListPanel.setBackground(new Color(0,0,0,0)); // (요청) 투명 배경

        // 4-2. 예시용 TourBanner 15개 추가
        addSampleBanners(packageListPanel);

        // 4-3. 스크롤 기능 추가
        AppScrollPane scrollPane = new AppScrollPane(packageListPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null); // 스크롤패널 자체의 테두리 제거
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도
        
        // (요청) 스크롤 패널과 뷰포트도 투명하게
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        // --- 5. 완성된 메인 패널을 페이지의 CENTER에 추가 ---
        add(mainContentPanel, BorderLayout.CENTER);

        // --- 6. (권장) 검색 바 콜백 연결 ---
        searchBar.addSearchListener(e -> {
            System.out.println("검색 실행:");
            System.out.println(" - 지역: " + searchBar.getSelectedRegion());
            System.out.println(" - 가격: " + searchBar.getSelectedPriceRange());
            System.out.println(" - 텍스트: " + searchBar.getSearchText());
            // TODO: packageListPanel의 내용을 실제 검색 결과로 업데이트
            // 예: packageListPanel.removeAll(); addSampleBanners(packageListPanel); ...
        });

        searchBar.addResetListener(e -> {
            searchBar.clearFilters();
            System.out.println("필터 초기화됨");
            // TODO: packageListPanel의 내용을 전체 목록으로 복원
        });
    }
    
    /**
     * 예시용 TourBanner를 패널에 추가하는 헬퍼 메서드
     * @param panel 배너를 추가할 패널
     */
    private void addSampleBanners(AppPanel panel) {
        // String[] sampleImages = {
        //     "res/icons/search.png", // (실제 파일 경로로 변경 필요)
        //     "res/icons/search.png",
        //     "res/icons/search.png"
        // };
        String[] ratings = {"★★★★☆", "★★★★★", "★★★☆☆", "★★★★☆"};

        for (int i = 0; i < 15; i++) {
            TourBanner banner = new TourBanner(
                "[특가] " + (i % 3 == 0 ? "제주" : "부산") + " 힐링 " + (i%2 + 2) + "박 " + (i%2 + 3) + "일",
                "제주", 
                (i%2 + 2) + "박 " + (i%2 + 3) + "일", 
                (59 + i*3) + "만원", 
                ratings[i % 4]
            );
            
            // 썸네일 설정
            // banner.setThumbnail(sampleImages[i % 3]);
            
            // 콜백 연결 (람다에서 배너 참조)
            int c = i;
            banner.addDetailButtonListener(e -> {
                System.out.println("상세보기 클릭: " + c);
                // TODO: 상세 페이지로 이동 (ServiceContext.getInstance().navigate(...))
            });
            
            panel.add(banner);
        }
    }
}