package manager;

import dao.RecommendationDAO;
import manager.TourCatalog2;
import manager.ReservationManager2;
import manager.UserManager2;

import model.Recommendation;
import model.TourPackage2;
import model.User2;
import model.Reservation2;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class RecommendationManager {

    private final RecommendationDAO dao;
    private final TourCatalog2 tourCatalog;
    private final ReservationManager2 reservationManager;
    private final UserManager2 userManager;

    public RecommendationManager(
            RecommendationDAO dao,
            TourCatalog2 tourCatalog,
            ReservationManager2 reservationManager,
            UserManager2 userManager
    ) {
        this.dao = dao;
        this.tourCatalog = tourCatalog;
        this.reservationManager = reservationManager;
        this.userManager = userManager;
    }


    public List<Recommendation> recommend(int userId) {

        User2 user = userManager.getUser(userId);
        if (user == null) {
            return dao.getRecommendations();  // 기본 추천
        }

        // 필요한 데이터
        int age = user.age;
        String sex = user.sex;

        List<Integer> myReservations = reservationManager.getListByUserId(userId);

        // 최종 추천 결과
        List<Recommendation> results = new ArrayList<>();

        // 나이,성별 기반 추천
        results.addAll(recommendByDemographics(age, sex));

        // 예약 기반 사용자 선호 분석
        //     - 지역
        //     - 가격대
        //     - 교통수단
        
        if (!myReservations.isEmpty()) {
            results.addAll(recommendByUserPreferences(myReservations));
        }

        // 최근 예약 가격 기반 추천 (기존 기능 유지)
        results.addAll(recommendByLastPrice(myReservations));

        // 중복 제거
        Map<Integer, Recommendation> map = new LinkedHashMap<>();
        for (Recommendation r : results) {
            map.putIfAbsent(r.getTourId(), r);
        }

        return new ArrayList<>(map.values());
    }

    //  나이,성별 기반 추천 (DemographicData.txt 읽어서 계산)
    private List<Recommendation> recommendByDemographics(int age, String sex) {
        List<Recommendation> result = new ArrayList<>();

        File file = new File("data/DemographicData.txt");
        if (!file.exists()) return result;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = br.readLine()) != null) {

                // 예:  3|20-30|F
                String[] arr = line.split("\\|");
                if (arr.length != 3) continue;

                int id = Integer.parseInt(arr[0]);
                String[] ageRange = arr[1].split("-");
                int min = Integer.parseInt(ageRange[0]);
                int max = Integer.parseInt(ageRange[1]);
                String targetSex = arr[2];

                if (age >= min && age <= max && sex.equalsIgnoreCase(targetSex)) {
                    result.add(new Recommendation(
                            id,
                            "나이/성별 기반 추천 (" + age + "세, " + sex + ")"
                    ));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // 2. 예약 기반 선호 추천
    private List<Recommendation> recommendByUserPreferences(List<Integer> myReservations) {

        List<TourPackage2> history = myReservations.stream()
                .map(tourCatalog::getTour)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (history.isEmpty()) return Collections.emptyList();

        // 선호 지역
        String favPlace = history.stream()
                .collect(Collectors.groupingBy(t -> t.place, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 평균 가격대
        double avgPrice = history.stream()
                .mapToInt(t -> t.price)
                .average()
                .orElse(0);

        // 선호 교통수단
        String favTransport = history.stream()
                .collect(Collectors.groupingBy(t -> t.transport[0], Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        List<Recommendation> result = new ArrayList<>();

        for (TourPackage2 t : tourCatalog.getAllTours()) {

            boolean ok = true;

            if (favPlace != null && !t.place.equals(favPlace)) ok = false;
            if (favTransport != null && !t.transport[0].equals(favTransport)) ok = false;
            if (t.price < avgPrice - 50000 || t.price > avgPrice + 50000) ok = false;

            if (ok) {
                result.add(new Recommendation(
                        t.id,
                        "사용자 선호 기반 추천 (지역/가격/교통)"
                ));
            }
        }

        return result;
    }

    // 3. 최근 예약 가격 기반 추천 (기존 기능 그대로)
    private List<Recommendation> recommendByLastPrice(List<Integer> list) {

        if (list.isEmpty()) return Collections.emptyList();

        int lastId = list.get(list.size() - 1);
        TourPackage2 lastTour = tourCatalog.getTour(lastId);

        if (lastTour == null) return Collections.emptyList();

        int price = lastTour.price;
        int min = price - 50000;
        int max = price + 50000;

        List<Recommendation> result = new ArrayList<>();

        for (TourPackage2 t : tourCatalog.getAllTours()) {
            if (t.price >= min && t.price <= max) {
                result.add(new Recommendation(
                        t.id,
                        "최근 예약 가격대와 유사한 추천"
                ));
            }
        }

        return result;
    }
}
