/**
 * 여행 패키지 정보들을 목록화하여 보여주고, 좌측 메뉴도 함께 제공하는 페이지.
 * JList/AppList 대신 AppScrollPane을 이용해 직접 구현
 * 클릭 가능한 TourBanner 컴포넌트 목록을 구현합니다.
 */

// Todo
// 스크롤 패널 CardLayout으로 씌우고 좌측 메뉴 클릭에 따라 전환되도록

package pages;

import ui_kit.*;
import util.RateToStar;
import pages.component.Sidebar;
import pages.component.ReservationBanner;
import pages.component.SearchBar;
import pages.component.TourBanner;
import manager.RecommendationManager;
import manager.ReservationManager;
import manager.ReviewManager;
import manager.SessionManager;
import manager.TourCatalog;
import model.Reservation;
import model.TourPackage;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;


public class CatalogPage extends AppPage {
    public static final String ID = "catalog";

    public CatalogPage(ServiceContext ctx){ 
        super(new BorderLayout(), ctx);
        init();
    }

    @Override
    public String getPageId(){ return CatalogPage.ID; }

    private static final String[] menuIds = new String[]{"tours", "reserves", "recommends"};
    private Sidebar sideNav;
    private SearchBar searchBar;
    private Map<String, AppScrollPane> packageLists = new HashMap<>(); // 4. 패키지 배너들을 담을 컨테이너 패널
    private AppPanel listContainer;
    private CardLayout listLayout;

    public void init(){
        // 본인 스타일링 (페이지 전체의 기본 배경색)
        setOpaque(true);
        setBackground(UITheme.PANEL_BACKGROUND_COLOR);

        // --- 1. 사이드바 ---
        sideNav = new Sidebar();
        sideNav.addMenu("여행 패키지 목록", e -> changeList(menuIds[0]));
        sideNav.addMenu("예약 내역 확인", e -> changeList(menuIds[1]));
        sideNav.addMenu("추천 패키지", e -> changeList(menuIds[2]));
        sideNav.setLogoutCallback(null);

        add(sideNav, BorderLayout.WEST);

        // --- 2. 메인 콘텐츠 패널 (신규 코드) ---
        // (CENTER 영역은 SearchBar와 List, 두 개로 나뉘므로 새 패널 필요)
        AppPanel mainContentPanel = new AppPanel(new BorderLayout(0, 45)); // 수직 갭 10px
        mainContentPanel.setBackground(UITheme.TRANSPARENT);

        // "위/좌/우 패딩"을 위해 UITheme의 공통 패널 보더 사용
        mainContentPanel.setBorder(UITheme.CATALOG_PANEL_BORDER);


        // --- 3. 검색창 (mainContentPanel의 NORTH) ---
        searchBar = new SearchBar();
        searchBar.setRegions(new String[]{"지역"});
        searchBar.setPrices(new String[]{"가격"});
        mainContentPanel.add(searchBar, BorderLayout.NORTH);

        // --- 4. 패키지 목록 (mainContentPanel의 CENTER) ---
        // CardLayout으로 여러 목록을 교체 가능하도록
        listLayout = new CardLayout();
        listContainer = new AppPanel(listLayout);
        listContainer.setBackground(UITheme.TRANSPARENT);
        
        // 페이지들 생성 및 추가
        for(var menu : menuIds){
            AppScrollPane listPanel = createListPanel();
            packageLists.put(menu, listPanel);
            listContainer.add(menu, listPanel);
        }
        listLayout.show(listContainer, menuIds[0]);

        mainContentPanel.add(listContainer, BorderLayout.CENTER);


        // 좌측 메뉴에 Card 전환 기능 콜백 추가


        // --- 5. 완성된 메인 패널을 페이지의 CENTER에 추가 ---
        add(mainContentPanel, BorderLayout.CENTER);

        applySearchCallbacks();
        renderNormalBanners(getViewOfList(menuIds[0]));
        changeList(menuIds[0]);
    }

    // 렌더링되는 목록 유형 전환
    private List<TourBanner> renderedBanners = new ArrayList<>();
    private AppPanel getViewOfList(String menuId){
        try {
            return (AppPanel)packageLists.get(menuId).getViewport().getView();
        } catch(Exception e){
            throw new Error("Undefined menu: " + menuId);
            return null;
        }
    }
    protected void changeList(String id){
        for(String menuId : menuIds){
            if(id.equals(menuId)){
                // 1. 일반 패키지 목록
                if(id.equals(menuIds[0])){
                    // 최초 1회만 렌더링
                    // renderNormalBanners(getViewOfList(id));
                    listLayout.show(listContainer, id);
                }
                // 2. 예약 내역
                else if(id.equals(menuIds[1])){
                    // 매번 재렌더링
                    renderReservations(getViewOfList(id));
                    listLayout.show(listContainer, id);
                }
                // 3. 추천 패키지
                else if(id.equals(menuIds[2])){

                }
                // 기타
                else
                    throw new Error("Undefined menu: " + id);
                break;
            }
        }
    }


    // TourBanner들 리스트 형태로 넣을 배너
    private AppScrollPane createListPanel(){
        var panel = new AppPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.TRANSPARENT);

        // 스크롤 기능 추가
        var scroller = new AppScrollPane(panel); // 래핑
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setUnitIncrement(16);

        return scroller;
    }

    /**
     * 검색바 콜백 등록
     * 현재 리스트업된 결과들 필터링
     */
    private void applySearchCallbacks(){
        searchBar.addSearchListener(e -> {
            
        });

        searchBar.addResetListener(e -> {
            searchBar.clearFilters();
            // 필터 초기화 반영
        });
    }

    /**
     * 1. 일반 목록 렌더링
     * @param panel 적용할 패널
     */
    private void renderNormalBanners(AppPanel panel){
        TourCatalog catalog = context.get(TourCatalog.class);
        for(final int id : catalog.getTourIds()){
            TourPackage tour = catalog.getTour(id);
            TourBanner banner = new TourBanner(
                tour.name,
                tour.place,
                (tour.day_long-1)+"박 "+tour.day_long+"일",
                (tour.price/10000)+"만원",
                RateToStar.stringify((int)Math.round(context.get(ReviewManager.class).getAverageRateOfTour(id)))
            );

            banner.addDetailButtonListener(e -> {
                // System.out.println("상세보기 클릭: " + tour.name);
                navigateTo("tourDetail", tour.id);
            });

            System.out.println(tour.name);
            panel.add(banner);
        }
    }

    /**
     * 2. 예약 내역 렌더링
     * @param panel 적용할 패널
     */
    private void renderReservations(AppPanel panel){
        TourCatalog catalog = context.get(TourCatalog.class);
        ReservationManager reserves = context.get(ReservationManager.class);
        SessionManager session = context.get(SessionManager.class);
        if(!session.isLoggedIn())
            return;

        
        LocalDate today = LocalDate.now();
        for(final int id : reserves.getListByUserId(session.getCurrentUserId().intValue())){
            TourPackage tour = catalog.getTour(id);
            Reservation reserve = reserves.getReservation(id);
            boolean isCompleted = today.isAfter(reserve.start_date.plusDays(tour.day_long));
    
            ReservationBanner banner = new ReservationBanner(
                tour.name,
                tour.place,
                (tour.day_long-1)+"박 "+tour.day_long+"일",
                (tour.price/10000)+"만원",
                RateToStar.stringify((int)Math.round(context.get(ReviewManager.class).getAverageRateOfTour(id))),
                isCompleted,
                e -> navigateTo("tourDetail", tour.id),
                isCompleted
                    ? (e -> navigateTo("reviewWrite", tour.id))
                    : (e -> {
                        reserves.cancel(id);
                        changeList(menuIds[1]); // 재렌더링 유도
                    })
            );

            System.out.println(tour.name);
            panel.add(banner);
        }
    }
    
    /**
     * 3. 추천 목록 렌더링
     * @param panel 적용할 패널
     */
    private void renderRecommendations(AppPanel panel){
        TourCatalog catalog = context.get(TourCatalog.class);
        RecommendationManager recommender = context.get(RecommendationManager.class);
        SessionManager session = context.get(SessionManager.class);
        if(!session.isLoggedIn())
            return;

        for(final int id : recommender.){
            TourPackage tour = catalog.getTour(id);
            TourBanner banner = new TourBanner(
                tour.name,
                tour.place,
                (tour.day_long-1)+"박 "+tour.day_long+"일",
                (tour.price/10000)+"만원",
                RateToStar.stringify((int)Math.round(context.get(ReviewManager.class).getAverageRateOfTour(id)))
            );

            banner.addDetailButtonListener(e -> {
                // System.out.println("상세보기 클릭: " + tour.name);
                navigateTo("tourDetail", tour.id);
            });

            System.out.println(tour.name);
            panel.add(banner);
        }
    }


    /**
     * 예시용 TourBanner를 패널에 추가하는 헬퍼 메서드
     * @param panel 배너를 추가할 패널
     */
    // private void addSampleBanners(AppPanel panel) {
    //     // String[] sampleImages = {
    //     //     "res/icons/search.png", // (실제 파일 경로로 변경 필요)
    //     //     "res/icons/search.png",
    //     //     "res/icons/search.png"
    //     // };
    //     String[] ratings = {"★★★★☆", "★★★★★", "★★★☆☆", "★★★★☆"};

    //     for (int i = 0; i < 15; i++) {
    //         TourBanner banner = new TourBanner(
    //             "[특가] " + (i % 3 == 0 ? "제주" : "부산") + " 힐링 " + (i%2 + 2) + "박 " + (i%2 + 3) + "일",
    //             "제주", 
    //             (i%2 + 2) + "박 " + (i%2 + 3) + "일", 
    //             (59 + i*3) + "만원", 
    //             ratings[i % 4]
    //         );
            
    //         // 썸네일 설정
    //         // banner.setThumbnail(sampleImages[i % 3]);
            
    //         // 콜백 연결 (람다에서 배너 참조)
    //         int c = i;
    //         banner.addDetailButtonListener(e -> {
    //             System.out.println("상세보기 클릭: " + c);
    //             // TODO: 상세 페이지로 이동 (ServiceContext.getInstance().navigate(...))
    //         });
            
    //         panel.add(banner);
    //     }
    // }
}