package dao;

import model.User2;
import model.TourPackage2;
import java.util.List;

public interface RecommendationDAO {

    /** 1. 연령/성별 기반 추천용 tourId 리스트 */
    List<Integer> getDemographicRecommendedTourIds(User2 user);

    /** 2. 예약 기반 추천을 위해 userId의 예약 목록 → TourPackage2 리스트 */
    List<TourPackage2> getUsedTourPackages(int userId);

    /** 3. Tour 패키지 단일 조회 */
    TourPackage2 getTourById(int id);
}
