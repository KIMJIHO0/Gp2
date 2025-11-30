package manager;

import dao.RecommendationDAO;
import model.Recommendation;
import model.TourPackage2;
import model.User2;

import java.util.*;
import java.util.stream.Collectors;

public class RecommendationManager {

    private final RecommendationDAO dao;

    public RecommendationManager(RecommendationDAO dao) {
        this.dao = dao;
    }

    public List<Recommendation> recommend(User2 user) {

        //추천 결과(중복 제거는 뒤에서)
        List<Recommendation> results = new ArrayList<>();

        //나이,성별 기반 추천 
        List<Integer> idsByDemo = dao.getDemographicRecommendedTourIds(user);
        for (int id : idsByDemo) {
            TourPackage2 t = dao.getTourById(id);
            if (t != null) results.add(
                    new Recommendation(t, "나이/성별 기반 추천 (" + user.age + "세, " + user.sex + ")")
            );
        }

        //사용자의 예약 내역
        List<TourPackage2> history = dao.getUsedTourPackages(user.id);

        if (!history.isEmpty()) {
            results.addAll(recommendByUserPreferences(history));
            results.addAll(recommendByLastPrice(history));
        }

        //tourId 기준으로 중복 제거
        Map<Integer, Recommendation> dedup = new LinkedHashMap<>();
        for (Recommendation r : results) {
            dedup.putIfAbsent(r.getTourPackage().id, r);
        }

        return new ArrayList<>(dedup.values());
    }

    //사용자 선호 기반 추천 (지역/가격/교통) 
    private List<Recommendation> recommendByUserPreferences(List<TourPackage2> history) {

        // 선호 지역
        String favPlace = history.stream()
                .collect(Collectors.groupingBy(t -> t.place, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 평균 가격
        double avgPrice = history.stream()
                .mapToInt(t -> t.price)
                .average().orElse(0);

        // 선호 교통수단
        String favTransport = history.stream()
                .collect(Collectors.groupingBy(t -> t.transport[0], Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<Recommendation> result = new ArrayList<>();

        for (TourPackage2 t : getAllTours()) {
            boolean ok = true;

            if (favPlace != null && !t.place.equals(favPlace)) ok = false;
            if (favTransport != null && !t.transport[0].equals(favTransport)) ok = false;
            if (t.price < avgPrice - 50000 || t.price > avgPrice + 50000) ok = false;

            if (ok) {
                result.add(new Recommendation(
                        t,
                        "사용자 선호 기반 추천 (지역/가격/교통)"
                ));
            }
        }
        return result;
    }

    //최근 예약 가격대 기반 추천 
    private List<Recommendation> recommendByLastPrice(List<TourPackage2> history) {

        TourPackage2 last = history.get(history.size() - 1);
        int price = last.price;
        int min = price - 50000;
        int max = price + 50000;

        List<Recommendation> result = new ArrayList<>();

        for (TourPackage2 t : getAllTours()) {
            if (t.price >= min && t.price <= max) {
                result.add(new Recommendation(
                        t,
                        "최근 예약 가격대와 유사한 추천"
                ));
            }
        }
        return result;
    }

    //DAO에 전체 조회 기능이 없으므로 보조 메서드 사용
    private List<TourPackage2> getAllTours() {
        List<TourPackage2> list = new ArrayList<>();
        for (int id = 1; id <= 1000; id++) {
            TourPackage2 t = dao.getTourById(id);
            if (t != null) list.add(t);
        }
        return list;
    }
}
