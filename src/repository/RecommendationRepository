package repository;

import dao.RecommendationDAO;
import dao.ReservationDAO2;
import dao.TourDAO2;
import dao.UserDAO2;

import model.Reservation2;
import model.TourPackage2;
import model.User2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class RecommendationRepository implements RecommendationDAO {

    private final UserDAO2 userDAO2;
    private final ReservationDAO2 reservationDAO2;
    private final TourDAO2 tourDAO2;

    /** 연령 및 성별 데이터  */
    private static class Rule {
        int tourId;
        String sex; // MALE/FEMALE/ANY
        int minAge;
        int maxAge;
    }
    private final List<Rule> demographicRules = new ArrayList<>();

    public RecommendationRepository(
            UserDAO2 userDAO2,
            ReservationDAO2 reservationDAO2,
            TourDAO2 tourDAO2,
            String demographicFilePath
    ) {
        this.userDAO2 = userDAO2;
        this.reservationDAO2 = reservationDAO2;
        this.tourDAO2 = tourDAO2;

        loadDemographicData(demographicFilePath);
    }

    //demographic.txt 로딩
    private void loadDemographicData(String filename) {
        try (Scanner sc = new Scanner(new File(filename))) {
            while (sc.hasNext()) {
                String token = sc.next();
                if (token.startsWith("#")) {
                    if (sc.hasNextLine()) sc.nextLine();
                    continue;
                }

                Rule r = new Rule();
                r.tourId = Integer.parseInt(token);
                r.sex = sc.next();
                r.minAge = sc.nextInt();
                r.maxAge = sc.nextInt();
                demographicRules.add(r);

                if (sc.hasNextLine()) sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("연령/성별 기준 파일이 없습니다: " + filename);
        }
    }

    //연령 및 성별 기반 추천
    @Override
    public List<Integer> getDemographicRecommendedTourIds(User2 user) {
        List<Integer> result = new ArrayList<>();
        for (Rule r : demographicRules) {
            boolean ageOK = user.age >= r.minAge && user.age <= r.maxAge;
            boolean sexOK = r.sex.equals("ANY") || r.sex.equalsIgnoreCase(user.sex.toString());
            if (ageOK && sexOK) result.add(r.tourId);
        }
        return result;
    }

    //예약 기반 추천용 패키지 목록
    @Override
    public List<TourPackage2> getUsedTourPackages(int userId) {

        List<Reservation2> reservations =
                reservationDAO2.getReservationsByUser(userId);

        List<TourPackage2> result = new ArrayList<>();
        for (Reservation2 r : reservations) {
            tourDAO2.findById(r.tour_id).ifPresent(result::add);
        }
        return result;
    }

    //TourPackage 단건 조회
    
    @Override
    public TourPackage2 getTourById(int id) {
        return tourDAO2.findById(id).orElse(null);
    }
}
