package facade;

import java.util.List;
import java.util.Optional;

import common.Result;
import model.*;
import model.enums.Region;
import model.enums.Theme;
import ports.*;
import service.*;

/**
 * UI는 이 클래스만 호출.
 * - 생성 시 데이터 관리 팀이 만든 포트 구현체를 주입받는다.
 * - 이 파일 안에서는 어떤 저장 방식도 모른다.
 */
public class AppDataFacade {

    private final AuthService auth;
    private final PackageService packages;
    private final ReservationService reservations;
    private final ReviewService reviews;
    private final RecommendationService recs;

    public AppDataFacade(UserStore userStore,
                         PackageStore packageStore,
                         ReservationStore reservationStore,
                         ReviewStore reviewStore) {
        this.auth = new AuthServiceImpl(userStore);
        this.packages = new PackageServiceImpl(packageStore);
        this.reservations = new ReservationServiceImpl(reservationStore, packageStore);
        this.reviews = new ReviewServiceImpl(reviewStore, packageStore);
        this.recs = new RecommendationServiceImpl(reservationStore, reviewStore, packageStore);
    }

    // 인증
    public Result<User> signUp(String id, String pw) { return auth.signUp(id, pw); }
    public Result<User> signIn(String id, String pw) { return auth.signIn(id, pw); }

    // 패키지
    public List<TravelPackage> listAllPackages(){ return packages.listAll(); }
    public List<TravelPackage> search(Region r, Theme t, Integer maxPrice){ return packages.search(r,t,maxPrice); }
    public Optional<TravelPackage> packageDetail(String id){ return packages.detail(id); }

    // 예약
    public Result<Reservation> book(String userId, String packageId){ return reservations.book(userId, packageId); }
    public Result<Reservation> cancel(String reservationId, String userId){ return reservations.cancel(reservationId, userId); }
    public List<Reservation> myReservations(String userId){ return reservations.listByUser(userId); }

    // 리뷰
    public Result<Review> writeReview(String userId, String packageId, int score, String content){
        return reviews.write(userId, packageId, score, content);
    }
    public List<Review> reviewsOfPackage(String packageId){ return reviews.listByPackage(packageId); }

    // 추천
    public List<TravelPackage> recommendFor(String userId, int limit){ return recs.recommendForUser(userId, limit); }
}
