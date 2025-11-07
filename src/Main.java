import adapters.memory.*;
import dao.*;
import manager.*;
import model.*;
import util.Passwords;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // 1. DAO 인스턴스 준비(인메모리)
        UserDAO userDAO = new MemUserDAO();
        MemTourDAO memTourDAO = new MemTourDAO();
        ReservationDAO reservationDAO = new MemReservationDAO();
        ReviewDAO reviewDAO = new MemReviewDAO();

        // 2. 테스트용 상품 데이터 시드
        memTourDAO.seed(new TourPackage(1, "제주힐링3박4일", "제주", 590000,
                List.of("숙소", "요트", "스노클링", "감귤")));
        memTourDAO.seed(new TourPackage(2, "부산2박3일", "부산", 390000,
                List.of("광안리", "해운대", "자유일정")));

        // 3. Manager 생성
        UserManager userManager = new UserManager(userDAO);
        TourCatalog tourCatalog = new TourCatalog(memTourDAO);
        ReservationManager reservationManager =
                new ReservationManager(reservationDAO, userDAO, memTourDAO);
        ReviewManager reviewManager =
                new ReviewManager(reviewDAO, reservationDAO, userDAO, memTourDAO);

        // 4. 시나리오 테스트

        // 회원 가입
        System.out.println("[sign up] " + userManager.register(1001, "pw1234"));

        // 로그인 체크
        System.out.println("[login ok] " + userManager.checkPassword(1001, "pw1234"));
        System.out.println("[login fail] " + userManager.checkPassword(1001, "wrong"));

        // 패키지 목록
        System.out.println("[tours] " + tourCatalog.getTourIds());

        // 예약
        var reserveCode = reservationManager.reserve(1001, 1, LocalDate.now().plusDays(10));
        System.out.println("[reserve] " + reserveCode);

        // 내 예약 목록 조회
        System.out.println("[my reservations] " + reservationManager.getListByUserId(1001));

        // 리뷰 등록
        var reviewCode = reviewManager.review(1001, 1, 9, "좋았음");
        System.out.println("[review] " + reviewCode);

        // 평균 평점
        System.out.println("[avg rate] " + reviewManager.getAverageRateOfTour(1));
    }
}
