package manager;

import dao.RecommendationDAO;
import model.Recommendation;
import model.TourPackage;

import java.util.List;
import java.util.ArrayList;

public class RecommendationManager {

    private final RecommendationDAO dao;
    private final TourCatalog catalog;
    private final ReservationManager reservationManager;

    public RecommendationManager(RecommendationDAO dao, TourCatalog catalog, ReservationManager reservationManager) {
        this.dao = dao;
        this.catalog = catalog;
        this.reservationManager = reservationManager;
    }

    /**
     * userId 기반 추천
     * - 최근 예약한 상품 가격대와 비슷한 여행 추천
     */
    public List<Recommendation> recommend(int userId) {

        // 1. 사용자의 예약 목록
        var myList = reservationManager.getListByUserId(userId);

        if (myList.isEmpty()) {
            // 예약이 없으면 기본 추천 반환
            return dao.getRecommendations();
        }

        // 2. 최근 예약한 여행 상품 ID
        int lastTourId = myList.get(myList.size() - 1);

        // 3. 해당 상품 정보 조회
        TourPackage lastTour = catalog.getTour(lastTourId);
        if (lastTour == null) {
            return dao.getRecommendations();
        }

        int lastPrice = lastTour.price;
        int minPrice = lastPrice - 50000;  // -5만원
        int maxPrice = lastPrice + 50000;  // +5만원

        List<Recommendation> result = new ArrayList<>();

        // catalog.getAllTours() 없음 → getTourIds() 순회로 대체
        for (int id : catalog.getTourIds()) {
            TourPackage t = catalog.getTour(id);
            if (t == null) continue;

            if (t.price >= minPrice && t.price <= maxPrice) {
                result.add(new Recommendation(
                        t.id,
                        "최근 예약(" + lastPrice + "원)과 비슷한 가격대 추천"
                ));
            }
        }

        return result;
    }
}
