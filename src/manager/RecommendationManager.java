package manager;

import dao.RecommendationDAO;
import dao.UserDAO2;
import model.Recommendation;
import model.TourPackage2;
import model.User2;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationManager {

    private final RecommendationDAO recommendationDAO;
    private final UserDAO2 userDAO2;

    public RecommendationManager(RecommendationDAO dao, UserDAO2 userDAO2) {
        this.recommendationDAO = dao;
        this.userDAO2 = userDAO2;
    }

    public List<Recommendation> recommend(int userId) {

        User2 user = userDAO2.getUser(userId).orElse(null);
        if (user == null) return List.of();

        Map<Integer, Recommendation> resultMap = new LinkedHashMap<>();

        /* ===================================================
         * 1) 연령/성별 기반 추천
         * =================================================== */
        List<Integer> demoIds =
                recommendationDAO.getDemographicRecommendedTourIds(user);

        for (int id : demoIds) {
            TourPackage2 t = recommendationDAO.getTourById(id);
            if (t != null) {
                String msg = String.format("연령(%d)/성별(%s) 기반 추천", user.age, user.sex);
                resultMap.put(id, new Recommendation(id, msg));
            }
        }

        /* ===================================================
         * 2) 예약 기반 추천
         * =================================================== */
        List<TourPackage2> used = recommendationDAO.getUsedTourPackages(userId);
        if (!used.isEmpty()) {

            // 1) 선호 지역
            String favPlace = used.stream()
                    .collect(Collectors.groupingBy(t -> t.place, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // 2) 선호 가격대 (평균 ± 50,000)
            double avgPrice = used.stream()
                    .mapToInt(t -> t.price)
                    .average()
                    .orElse(0);

            int minPrice = (int) avgPrice - 50_000;
            int maxPrice = (int) avgPrice + 50_000;

            // 3) 선호 교통수단
            String favTransport = used.stream()
                    .flatMap(t -> Arrays.stream(t.transport))
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            // 모든 투어에서 조건에 맞는 것 추천
            for (TourPackage2 t : used.get(0).catalog.getAllTours()) {
                boolean okPlace = t.place.equals(favPlace);
                boolean okPrice = t.price >= minPrice && t.price <= maxPrice;
                boolean okTransport = Arrays.asList(t.transport).contains(favTransport);

                if (okPlace && okPrice && okTransport) {
                    if (!resultMap.containsKey(t.id)) {
                        resultMap.put(t.id,
                                new Recommendation(t.id,
                                        "이전 예약 패턴 기반 추천"));
                    }
                }
            }
        }

        return new ArrayList<>(resultMap.values());
    }
}
