import common.Result;
import facade.AppDataFacade;
import model.*;
import model.enums.*;
import ports.*;
import util.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 테스트 전용 인메모리 스텁 구현 + 간단 시나리오 */
public class Main {

    // ---------- 스텁: 데이터 관리팀 영역 대체(로컬 테스트 전용) ----------
    static class MemUserStore implements UserStore {
        private final Map<String, User> map = new ConcurrentHashMap<>();
        public Optional<User> findById(String id){ return Optional.ofNullable(map.get(id)); }
        public User save(User u){ map.put(u.id, u); return u; }
    }
    static class MemPackageStore implements PackageStore {
        private final Map<String, TravelPackage> map = new ConcurrentHashMap<>();
        public Optional<TravelPackage> findById(String id){ return Optional.ofNullable(map.get(id)); }
        public List<TravelPackage> findAll(){ return new ArrayList<>(map.values()); }
        public List<TravelPackage> search(Region r, Theme t, Integer maxPrice){
            return map.values().stream()
                    .filter(p -> r==null || p.region==r)
                    .filter(p -> t==null || p.theme==t)
                    .filter(p -> maxPrice==null || p.price<=maxPrice)
                    .sorted(Comparator.comparingDouble((TravelPackage p)->-p.rating)
                            .thenComparingInt(p->p.price))
                    .toList();
        }
        // 시드 편의용
        public void seed(TravelPackage p){ map.put(p.id, p); }
    }
    static class MemReservationStore implements ReservationStore {
        private final Map<String, Reservation> map = new ConcurrentHashMap<>();
        public Optional<Reservation> findById(String id){ return Optional.ofNullable(map.get(id)); }
        public List<Reservation> findByUser(String userId){
            return map.values().stream().filter(r->r.userId.equals(userId)).toList();
        }
        public List<Reservation> findByPackage(String pid){
            return map.values().stream().filter(r->r.packageId.equals(pid)).toList();
        }
        public Reservation save(Reservation r){ map.put(r.id, r); return r; }
    }
    static class MemReviewStore implements ReviewStore {
        private final Map<String, Review> map = new ConcurrentHashMap<>();
        public Review save(Review r){ map.put(r.id, r); return r; }
        public List<Review> findByPackage(String pid){
            return map.values().stream().filter(v->v.packageId.equals(pid)).toList();
        }
        public List<Review> findByUser(String uid){
            return map.values().stream().filter(v->v.userId.equals(uid)).toList();
        }
    }

    // ---------- 실행 ----------
    public static void main(String[] args) {
        // 스텁 생성
        var users = new MemUserStore();
        var pkgs  = new MemPackageStore();
        var resv  = new MemReservationStore();
        var revs  = new MemReviewStore();

        // 패키지 시드
        pkgs.seed(new TravelPackage("PKG-Jeju-34","제주힐링3박4일", Region.JEJU, Theme.HEALING,4,59,4.3));
        pkgs.seed(new TravelPackage("PKG-Busan-22","부산미식2박3일", Region.BUSAN, Theme.FOOD,3,39,4.5));
        pkgs.seed(new TravelPackage("PKG-Seoul-11","서울문화1박2일", Region.SEOUL, Theme.CULTURE,2,19,4.0));

        // 파사드 생성(포트 주입)
        var app = new AppDataFacade(users, pkgs, resv, revs);

        // 1) 회원가입·로그인
        Result<User> r1 = app.signUp("user01","password123");
        System.out.println("signUp: " + r1.ok + " / " + r1.msg);
        Result<User> r2 = app.signIn("user01","password123");
        System.out.println("signIn: " + r2.ok + " / " + r2.msg);
        var uid = r2.data.id;

        // 2) 패키지 검색
        var found = app.search(Region.JEJU, null, 100);
        System.out.println("검색 결과 수: " + found.size());

        // 3) 예약
        var pkgId = found.get(0).id;
        var booked = app.book(uid, pkgId);
        System.out.println("예약: " + booked.ok + " / " + booked.data.id);

        // 4) 리뷰 작성
        var review = app.writeReview(uid, pkgId, 5, "좋았어요");
        System.out.println("리뷰: " + review.ok);

        // 5) 추천
        var rec = app.recommendFor(uid, 5);
        System.out.println("추천 결과 수: " + rec.size());

        // 6) 예약 취소
        var canceled = app.cancel(booked.data.id, uid);
        System.out.println("취소: " + canceled.ok + " / " + canceled.data.status);

        // 7) 내 예약 목록
        var my = app.myReservations(uid);
        System.out.println("내 예약 수: " + my.size());
    }
}
