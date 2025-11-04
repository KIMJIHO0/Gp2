package service;

import java.util.List;
import model.TravelPackage;

public interface RecommendationService {
    /**
     * 간단 추천:
     *  - 최근 예약/리뷰에서 많이 본 region/theme 가중치
     *  - 평점 상위 우선
     *  - 이미 예약한 동일 상품은 제외
     */
    List<TravelPackage> recommendForUser(String userId, int limit);
}
