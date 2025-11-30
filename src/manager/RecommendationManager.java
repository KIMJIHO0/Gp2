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

    // userId 기반 추천
    public List<Recommendation> recommend(User2 user) {

        // 나이,성별 기반 추천
        List<Integer> byDemo = dao.getDemographicRecommendedTourIds(user);

        List<Recommendation> results = new ArrayList<>();
        for (int id : byDemo) {
            results.add(new Recommendation(id, "나이/성별 기반 추천"));
        }

        //사용자의 이전 예약 패키지 목록
        List<TourPackage2> history = dao.getUsedTourPackages(user.id);

        if (!history.isEmpty()) {
            results.addAll(recommendByUserPreferences(history));
            results.addAll(recommendByLastPrice(history));
        }

        //중복 제거
        Map<Integer, Recommendation> map = new LinkedHashMap<>();
        for (Recommendation r : results) {
            map.putIfAbsent(r.getTourId(), r);
        }

        return new ArrayList<>(map.values());
    }

    //사용자의 선호 기반 추천 
    private List<Recommendation> recommendByUserPreferences(List<TourPackage2> history) {

        // 가장 선호 지역
        String favPlace = history.stream()
                .collect(Collectors.groupingBy(t -> t.place, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 평균 가격
        double avgPrice = history.stream()
                .mapToInt(t -> t.price)
                .average()
                .orElse(0);

        List<Recommendation> list = new ArrayList<>();

        // 전체 투어 패키지 조회
        for (TourPackage2 t : getAllTours()) {
            boolean ok = true;

            if (favPlace != null && !t.place.equals(favPlace)) ok = false;
            if (t.price < avgPrice - 50000 || t.price > avgPrice + 50000) ok = false;

            if (ok) list.add(new Recommendation(t.id, "사용자 선호 기반 추천"));
        }

        return list;
    }


    //최근 구매 가격 기반 추천 
    private List<Recommendation> recommendByLastPrice(List<TourPackage2> history) {

        TourPackage2 last = history.get(history.size() - 1);
        int price = last.price;

        int min = price - 50000;
        int max = price + 50000;

        List<Recommendation> list = new ArrayList<>();

        for (TourPackage2 t : getAllTours()) {
            if (t.price >= min && t.price <= max) {
                list.add(new Recommendation(t.id, "최근 예약 가격 기반 추천"));
            }
        }

        return list;
    }


    //전체 투어 목록 조회 
    private List<TourPackage2> getAllTours() {
        // DAO에는 findAll이 없으므로 1~100 범위 조회 방식(규칙에 따라 조정)
        List<TourPackage2> list = new ArrayList<>();
        for (int id = 1; id <= 50; id++) {
            TourPackage2 t = dao.getTourById(id);
            if (t != null) list.add(t);
        }
        return list;
    }
}
