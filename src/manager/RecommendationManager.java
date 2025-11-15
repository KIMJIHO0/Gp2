package manager;

import dao.RecommendationDAO;
import model.Recommendation;

public class RecommendationManager {

    private final RecommendationDAO dao;
    private final TourCatalog catalog;

    public RecommendationManager(RecommendationDAO dao, TourCatalog catalog) {
        this.dao = dao;
        this.catalog = catalog;
    }

    public void printRecommendations() {
        System.out.println("\n=== 이번 주 추천 여행 패키지 ===");

        for (Recommendation r : dao.getRecommendations()) {
            var t = catalog.getTour(r.getTourId());
            if (t != null) {
                System.out.println("[패키지 " + r.getTourId() + "] "
                        + t.getName() + " (" + t.getPlace() + ") → "
                        + r.getReason());
            }
        }
    }
}
