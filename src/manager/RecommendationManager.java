package manager;

import dao.RecommendationDAO;
import manager.TourCatalog;
import manager.ReservationManager;
import model.Recommendation;
import model.TourPackage;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class RecommendationManager {

    private final RecommendationDAO dao;
    private final TourCatalog catalog;
    private final ReservationManager reservationManager;

    public RecommendationManager(RecommendationDAO dao, TourCatalog catalog, ReservationManager reservationManager) {
        this.dao = dao;
        this.catalog = catalog;
        this.reservationManager = reservationManager;
    }

    /** 기본 전체 추천 출력 */
    // public void printRecommendations() {
    //     System.out.println("\n=== 이번 주 추천 여행 패키지 ===");
    //     // for (Recommendation r : dao.getRecommendations()) {
    //     //     var t = catalog.getTour(r.getTourId());
    //     //     if (t != null) {
    //     //         System.out.println("[패키지 " + r.getTourId() + "] "
    //     //                 + t.getName() + " (" + t.getPlace() + ") → " + r.getReason());
    //     //     }
    //     // }
    // }

    public List<Recommendation> recommend(int userId) {

        var myList = reservationManager.getListByUserId(userId);

        // 1. 예약이 없으면 기본 추천 반환
        if (myList.isEmpty()) {
            return dao.getRecommendations();
        }

        return new ArrayList<Recommendation>();

        // // 2. 최근 예약한 여행 상품 ID
        // int lastTourId = myList.get(myList.size() - 1);

        // // 3. 최근 예약 상품 정보 가져오기
        // TourPackage lastTour = catalog.getTour(lastTourId);
        // if (lastTour == null) {
        //     return dao.getRecommendations();
        // }

        // int lastPrice = lastTour.price;
        // int minPrice = lastPrice - 50000; // 5만원 아래
        // int maxPrice = lastPrice + 50000; // 5만원 위

        // // 4. 비슷한 가격대의 여행 패키지 추천
        // return catalog.getTourIds().stream()
        //         .map(id -> catalog.getTour(id))
        //         .filter(t -> t.price >= minPrice && t.price <= maxPrice)
        //         .map(t -> new Recommendation(
        //                 t.id,
        //                 "최근 예약 상품(" + lastPrice + "원)과 비슷한 가격대 추천!"
        //         ))
        //         .toList();
    }

}
