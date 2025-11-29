package pages;

import ui_kit.*;
import util.RateToStar;
import pages.component.Sidebar;
import pages.component.DetailedSearchBar;
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
import java.util.Set;
import java.util.HashSet;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.lang.Math;
import javax.swing.BoxLayout;
import javax.swing.ScrollPaneConstants;


public class CatalogPage extends AppPage {
    public static final String ID = "catalog";

    // --- 필드 정의 ---
    private static final String[] menuIds = new String[]{"tours", "reserves", "recommends"};
    private Sidebar sideNav;
    private DetailedSearchBar searchBar;
    
    private Map<String, AppScrollPane> packageLists = new HashMap<>(); 
    private AppPanel listContainer;
    private CardLayout listLayout;
    
    private String activeMenuId = menuIds[0]; 
    private Map<String, List<TourPackage>> cachedTourData = new HashMap<>(); 
    private Map<Integer, Reservation> reservationMap = new HashMap<>();


    public CatalogPage(ServiceContext ctx){ 
        super(new BorderLayout(), ctx);
        init();
    }

    @Override
    public String getPageId(){ return CatalogPage.ID; }


    /**
     * 페이지가 화면에 표시될 때 호출됩니다.
     * context 파라미터에 따라 초기 진입 탭을 결정하고 렌더링합니다.
     * * @param context 1(기본): 목록, 2: 예약 내역, 3: 추천 목록
     */
    @Override
    public void onPageShown(Object ctx){
        // String targetMenuId = menuIds[0]; // 기본값: 일반 목록 (1 또는 null/그 외)

        // if(context instanceof Integer){
        //     int mode = (Integer) context;
        //     if(mode == 2) {
        //         targetMenuId = menuIds[1]; // 예약 내역
        //     } else if(mode == 3) {
        //         targetMenuId = menuIds[2]; // 추천 목록
        //     }
        // }
        
        // changeList는 일반 목록에 대해 캐싱된 데이터를 사용하고,
        // 예약/추천 목록은 최신 데이터를 로드하므로 효율성과 데이터 정합성을 모두 보장합니다.
        // changeList(targetMenuId);

        // 세션이 없으면 로그인 페이지로 강제 복귀
        if(!context.get(SessionManager.class).isLoggedIn()){
            System.out.println("로그인된 사용자가 없어 로그인 페이지로 복귀합니다.");
            navigateTo(LoginPage.PAGE_ID);
            return;
        }

        // 없는 페이지면 0으로
        int menu_idx = (int)ctx - 1;
        sideNav.clickMenu((menu_idx < 0 || menu_idx > 2)? 0: menu_idx);
    }


    public void init(){
        setOpaque(true);
        setBackground(UITheme.PANEL_BACKGROUND_COLOR);

        // --- 1. 사이드바 ---
        sideNav = new Sidebar();
        sideNav.addMenu("여행 패키지 목록", e -> changeList(menuIds[0]));
        sideNav.addMenu("예약 내역 확인", e -> changeList(menuIds[1]));
        sideNav.addMenu("추천 패키지", e -> changeList(menuIds[2]));
        sideNav.setLogoutCallback(e -> {
            var sm = context.get(SessionManager.class);
            if(!sm.isLoggedIn()){
                System.err.println("로그인도 안 되어있는데 이 페이지를 보여주고 있다고? 뭔가 잘못됐습니다!");
                return;
            }

            sm.logout();
            navigateTo(LoginPage.PAGE_ID);
        }); 

        add(sideNav, BorderLayout.WEST);

        // --- 2. 메인 콘텐츠 패널 ---
        AppPanel mainContentPanel = new AppPanel(new BorderLayout(0, 45));
        mainContentPanel.setBackground(UITheme.TRANSPARENT);
        mainContentPanel.setBorder(UITheme.CATALOG_PANEL_BORDER);


        // --- 3. 검색창 ---
        searchBar = new DetailedSearchBar();
        mainContentPanel.add(searchBar, BorderLayout.NORTH);
        // var searchBar2 = new DetailedSearchBar();
        // mainContentPanel.add(searchBar2, BorderLayout.NORTH);

        // --- 4. 패키지 목록 컨테이너 ---
        listLayout = new CardLayout();
        listContainer = new AppPanel(listLayout);
        listContainer.setBackground(UITheme.TRANSPARENT);
        
        for(var menu : menuIds){
            AppScrollPane listPanel = createListPanel();
            packageLists.put(menu, listPanel);
            listContainer.add(menu, listPanel);
        }
        mainContentPanel.add(listContainer, BorderLayout.CENTER);


        // --- 5. 페이지 조립 완료 ---
        add(mainContentPanel, BorderLayout.CENTER);

        applySearchCallbacks();
        changeList(menuIds[0]); 
    }


    private AppPanel getViewOfList(String menuId){
        if (!packageLists.containsKey(menuId)) {
            throw new Error("Undefined menu ID: " + menuId);
        }
        return (AppPanel) packageLists.get(menuId).getViewport().getView();
    }
    
    protected void changeList(String id){
        this.activeMenuId = id; 
        
        // 탭 전환 시 필터 초기화
        // searchBar.clearFilters();
        // 기본값 재설정 (데이터 로드 전 임시)
        searchBar.setRegions(new String[]{"지역"});
        searchBar.setPrices(new String[]{"가격"});

        TourCatalog catalog = context.get(TourCatalog.class);
        SessionManager session = context.get(SessionManager.class);

        // 1. 일반 목록
        if(id.equals(menuIds[0])){
            if (!cachedTourData.containsKey(id)) {
                List<TourPackage> allTours = new ArrayList<>();
                for(int tourId : catalog.getTourIds()){
                    allTours.add(catalog.getTour(tourId));
                }
                cachedTourData.put(id, allTours);
            }
            initComboboxFilters(); 
        }
        // 2. 예약 내역
        else if(id.equals(menuIds[1])){
            List<TourPackage> reservedPackages = new ArrayList<>();
            reservationMap.clear();

            if(session.isLoggedIn()){
                ReservationManager reserves = context.get(ReservationManager.class);
                List<Integer> reservedIds = reserves.getListByUserId(session.getCurrentUserId().intValue());
                
                for(int rId : reservedIds){
                    Reservation r = reserves.getReservation(rId);
                    if(r != null && !r.status.equals("canceled")) {
                        TourPackage t = catalog.getTour(r.tour_id);
                        if(t != null){
                            reservedPackages.add(t);
                            reservationMap.put(t.id, r);
                        }
                    }
                }
            }
            cachedTourData.put(id, reservedPackages);
        }
        // 3. 추천 패키지
        else if(id.equals(menuIds[2])){
            List<TourPackage> recommendedPackages = new ArrayList<>();
            if(session.isLoggedIn()){
                RecommendationManager recommender = context.get(RecommendationManager.class);
                var recommendations = recommender.recommend(session.getCurrentUserId().intValue());
                for(var rec : recommendations){
                    TourPackage t = catalog.getTour(rec.getTourId());
                    if(t != null) recommendedPackages.add(t);
                }
            }
            cachedTourData.put(id, recommendedPackages);
            initComboboxFilters();
        }
        
        reRenderBanners(id); 
        listLayout.show(listContainer, id);
    }


    private AppScrollPane createListPanel(){
        var panel = new AppPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UITheme.TRANSPARENT);

        var scroller = new AppScrollPane(panel); 
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setUnitIncrement(16);

        return scroller;
    }

    private boolean isReservationCompleted(LocalDate startDate, int duration) {
        LocalDate today = LocalDate.now();
        return startDate.plusDays(duration).isBefore(today); 
    }

    private boolean isPriceInRange(int price, String rangeStr) {
        try {
            if (rangeStr.contains("~")) {
                String[] parts = rangeStr.replace("만원", "").trim().split("~");
                int min = Integer.parseInt(parts[0].trim()) * 10000;
                int max = Integer.parseInt(parts[1].trim()) * 10000;
                return price >= min && price < max; 
            } else if (rangeStr.contains("이상")) {
                String part = rangeStr.replace("만원 이상", "").trim();
                int min = Integer.parseInt(part) * 10000;
                return price >= min;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    private void applySearchCallbacks(){
        // 실제 필터링은 필터값을 이용하여 reRender 측에서 수행.
        // 1. 검색 필터 적용
        searchBar.addSearchListener(e -> reRenderBanners(activeMenuId));
        // 2. 상세 필터 적용
        searchBar.addApplyFilterListener(_ -> reRenderBanners(activeMenuId));
    }

    /**
     * 현재 데이터 기반 필터 옵션 생성
     */
    private void initComboboxFilters(){
        List<TourPackage> data = cachedTourData.getOrDefault(activeMenuId, new ArrayList<>());

        if (data.isEmpty()) {
            searchBar.setRegions(new String[]{"지역"});
            searchBar.setPrices(new String[]{"가격"});
            return;
        }

        // 1. 지역 목록
        Set<String> regionSet = new HashSet<>();
        for(TourPackage p : data) {
            regionSet.add(p.place);
        }
        List<String> regions = new ArrayList<>(regionSet);
        regions.add(0, "지역");
        searchBar.setRegions(regions.toArray(new String[0]));

        // 2. 가격 구간
        int maxPrice = 0;
        for(TourPackage p : data) {
            if(p.price > maxPrice) maxPrice = p.price;
        }
        
        int N = data.size();
        int K = (int) Math.round(1 + 3.322 * (N > 0 ? Math.log10(N) : 1));
        if (K < 3) K = 3;

        int rangeVal = (int) Math.ceil((double) maxPrice / 10000);
        int width = (int) Math.ceil((double) rangeVal / K);
        if (width == 0) width = 10;

        List<String> prices = new ArrayList<>();
        prices.add("가격");
        for (int i = 0; i < K; i++) {
            int start = i * width;
            int end = (i + 1) * width;
            if (start >= rangeVal) break;
            if (i == K - 1 || end >= rangeVal) {
                prices.add(start + "만원 이상");
            } else {
                prices.add(start + "만원 ~ " + end + "만원");
            }
        }
        searchBar.setPrices(prices.toArray(new String[0]));

        // Todo - TourPackage2에 따른 추가 필터들
        // 3. 인원수
        // searchBar.setPeoples(items);
        // 4. 교통수단
        // searchBar.setTransports(items);
    }


    private void reRenderBanners(String menuId) {
        AppPanel targetPanel = getViewOfList(menuId);
        targetPanel.removeAll(); 
        
        List<TourPackage> sourceData = cachedTourData.getOrDefault(menuId, new ArrayList<>());
        
        // 검색어 앞뒤 공백 제거 (검색 오류 방지)
        String keyword = searchBar.getSearchText().trim().toLowerCase();
        
        // null 체크 추가 (NPE 방지)
        // 1. 지역 필터링
        String regionFilter = searchBar.getSelectedRegion();
        if(regionFilter == null) regionFilter = "지역";

        // 2. 가격 필터링
        String priceFilter = searchBar.getSelectedPriceRange();
        if(priceFilter == null) priceFilter = "가격";
        
        List<TourPackage> filtered = new ArrayList<>();
        
        for(TourPackage tour : sourceData){
            boolean matchKeyword = keyword.isEmpty() || tour.name.toLowerCase().contains(keyword);
            boolean matchRegion = regionFilter.equals("지역") || tour.place.equals(regionFilter);
            boolean matchPrice = priceFilter.equals("가격") || isPriceInRange(tour.price, priceFilter);

            if(matchKeyword && matchRegion && matchPrice)
                filtered.add(tour);
        }

        // 3. 인원수 필터링
        // filtered...
        // 4. 교통수단 필터링
        // filtered...

        renderBanners(targetPanel, filtered, menuId);

        targetPanel.revalidate();
        targetPanel.repaint();
    }


    private void renderBanners(AppPanel panel, List<TourPackage> data, String menuId) {
        ReviewManager reviewManager = context.get(ReviewManager.class);
        ReservationManager reservationManager = context.get(ReservationManager.class);

        if (data.isEmpty()) {
            AppLabel emptyLabel = new AppLabel("표시할 항목이 없습니다.");
            panel.add(emptyLabel);
            return;
        }
        
        for (TourPackage tour : data) {
            String rating = RateToStar.stringify((int)Math.round(reviewManager.getAverageRateOfTour(tour.id)));
            String duration = (tour.day_long - 1) + "박 " + tour.day_long + "일";
            String priceStr = (tour.price / 10000) + "만원";
            
            ActionListener detailAction = e -> navigateTo("tourDetail", Long.valueOf(tour.id));

            if (menuId.equals(menuIds[1])) {
                Reservation reserve = reservationMap.get(tour.id);
                if (reserve == null) continue;

                boolean isCompleted = isReservationCompleted(reserve.start_date, tour.day_long);
                
                ActionListener secondaryAction;
                if (isCompleted) {
                    secondaryAction = e -> navigateTo("reviewWrite", Long.valueOf(tour.id));
                } else {
                    secondaryAction = e -> {
                        reservationManager.cancel(reserve.id);
                        System.out.println(reserve.id + "번 예약 취소됨.");
                        changeList(menuIds[1]);
                    };
                }

                ReservationBanner banner = new ReservationBanner(
                    tour.name, tour.place, duration, priceStr, rating,
                    isCompleted, detailAction, secondaryAction
                );
                panel.add(banner);
            }
            else {
                TourBanner banner = new TourBanner(
                    tour.name, tour.place, duration, priceStr, rating
                );
                banner.addDetailButtonListener(detailAction);
                panel.add(banner);
            }
        }
    }
}