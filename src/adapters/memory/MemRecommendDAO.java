package adapters.memory;

import dao.RecommendationDAO;
import manager.TourCatalog;
import model.Recommendation;
import model.TourPackage;

import java.util.List;
import java.util.stream.Collectors;

/** 간단한 자동 추천 알고리즘 DAO */
public class MemRecommendDAO implements RecommendationDAO {

    private final TourCatalog catalog;

    public MemRecommendDAO(TourCatalog catalog) {
        this.catalog = catalog;
    }

    @Override
    public List<Recommendation> getRecommendations() {

        return catalog.getAllTours().stream()
                .filter(t -> t.getPrice() < 500000)
                .map(t -> new Recommendation(
                        t.getId(),
                        makeReason(t)
                ))
                .toList();
    }

    private String makeReason(TourPackage t) {
        if (t.getPrice() < 400000) return "가성비 최고 여행!";
        if (t.getPlace().equals("제주도")) return "힐링 여행으로 추천!";
        return "인기 여행지 추천!";
    }
}
