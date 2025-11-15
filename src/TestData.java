import manager.*;
import model.*;
import repository.*;

import java.time.LocalDate;

public class TestData {
  public static void main(String[] args) {

    // ---------- 1. Repository를 통한 파일 데이터 로딩 ----------
    System.out.println("=== Loading Data From Files ===");

    UserRepository userRepo = new UserRepository("src/data/UserData.txt");
    TourRepository tourRepo = new TourRepository("src/data/TourPackageData.txt");
    ReservationRepository reservationRepo = new ReservationRepository("src/data/ReservationData.txt");
    ReviewRepository reviewRepo = new ReviewRepository("src/data/ReviewData.txt");

    // ---------- 2. Manager 계층 연결 ----------
    UserManager userManager = new UserManager(userRepo);
    TourCatalog tourCatalog = new TourCatalog(tourRepo);
    ReservationManager reservationManager =
        new ReservationManager(reservationRepo, userRepo, tourRepo);
    ReviewManager reviewManager =
        new ReviewManager(reviewRepo, reservationRepo, userRepo, tourRepo);


    // ---------- 3. 데이터 정상 출력 테스트 ----------
    System.out.println("\n=== First User ===");
    User u = userManager.getUser(1245); // 존재하는 사용자
    if (u != null) System.out.println("User Found: id=" + u.id + ", pw=" + u.password);

    // ---------- 4. 기능 테스트 (예약 / 리뷰 / 평균평점) ----------
    System.out.println("\n=== 기능 테스트 ===");

    LocalDate startDate = LocalDate.now().plusDays(10);
    var reserveCode = reservationManager.reserve(1245, 3, startDate);
    System.out.println("예약 결과: " + reserveCode);

    var reviewCode = reviewManager.review(1245, 3, 9, "테스트 리뷰입니다.");
    System.out.println("리뷰 작성 결과: " + reviewCode);

    double avg = reviewManager.getAverageRateOfTour(3);
    System.out.println("평균 평점(3번 여행): " + avg);


    // ---------- 5. 전체 저장된 데이터 확인 ----------
    System.out.println("\n=== 모든 유저 출력 ===");
    for (User user : userRepo.getAllUsers()) {
      System.out.println("User: " + user.id + ", pw=" + user.password);
    }

    System.out.println("\n=== 모든 여행 상품 출력 ===");
    for (TourPackage t : tourRepo.getAllTours()) {
      System.out.println("Tour: " + t.id + " / " + t.name + " / " + t.place);
    }
  }
}
