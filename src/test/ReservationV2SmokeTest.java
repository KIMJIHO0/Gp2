package test;

import dao.ReservationDAO2;
import dao.TourDAO2;
import dao.UserDAO;
import manager.ReservationManager2;
import model.Reservation2;
import model.TourPackage2;
import model.UnavailableDateRule;
import model.User;

import java.awt.image.BufferedImage;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/* ===================== 메모리 DAO 스텁들 ===================== */

/** 간단 UserDAO (메모리) */
class InMemoryUserDAO implements UserDAO {
    private final Map<Integer, User> users = new HashMap<>();

    public void put(User u) { users.put(u.id, u); }

    @Override public boolean addUser(User user) { users.put(user.id, user); return true; }
    @Override public Optional<User> getUser(int id) { return Optional.ofNullable(users.get(id)); }
    @Override public java.util.List<User> getAllUsers() { return new ArrayList<>(users.values()); }
    @Override public boolean updateUser(User user) { users.put(user.id, user); return true; }
    @Override public boolean deleteUser(int id) { return users.remove(id) != null; }
}

/** 간단 TourDAO2 (메모리) */
class InMemoryTourDAO2 implements TourDAO2 {
    private final Map<Integer, TourPackage2> map = new HashMap<>();
    public void put(TourPackage2 t){ map.put(t.id, t); }
    @Override public Optional<TourPackage2> findById(int id){ return Optional.ofNullable(map.get(id)); }
    @Override public List<TourPackage2> findAll(){ return new ArrayList<>(map.values()); }
}

/** 간단 ReservationDAO2 (메모리, auto-increment) */
class InMemoryReservationDAO2 implements ReservationDAO2 {
    private final Map<Integer, Reservation2> map = new HashMap<>();
    private final AtomicInteger seq = new AtomicInteger(1);

    @Override public boolean addReservation(Reservation2 r) {
        int id = seq.getAndIncrement();
        map.put(id, new Reservation2(id, r.client_id, r.tour_id, r.start_date, r.status, r.reservedDate, r.headcount));
        return true;
    }
    @Override public boolean updateReservation(Reservation2 r) { map.put(r.id, r); return true; }
    @Override public Optional<Reservation2> getReservation(int id) { return Optional.ofNullable(map.get(id)); }
    @Override public List<Reservation2> getReservationsByUser(int clientId) {
        List<Reservation2> out = new ArrayList<>();
        for (var r : map.values()) if (r.client_id == clientId) out.add(r);
        return out;
    }
}

/* ===================== 스모크 테스트 본문 ===================== */

public class ReservationV2SmokeTest {

    private static UnavailableDateRule sundayBlockedRule() {
        // 예시: 매주 일요일 불가, 나머지는 가능
        return new UnavailableDateRule(List.of(), List.of(DayOfWeek.SUNDAY), List.of());
    }

    private static TourPackage2 sampleTour2() {
        return new TourPackage2(
                3, "강릉감성커피로드", "강릉", 150_000, 2,
                30_000, new String[]{"BUS"}, new int[]{2, 6},
                sundayBlockedRule(),
                (BufferedImage[]) null, null, "02-1234-5678",
                List.of("안목해변 카페", "경포대 산책")
        );
    }

    private static LocalDate next(DayOfWeek dow) {
        LocalDate d = LocalDate.now().plusDays(1);
        while (d.getDayOfWeek() != dow) d = d.plusDays(1);
        return d;
    }

    public static void main(String[] args) {
        // ----- 1) 준비: DAO 스텁 + 데이터 주입 -----
        InMemoryUserDAO userDAO = new InMemoryUserDAO();
        userDAO.put(new User(1245, "pw1234"));          // 존재 사용자

        InMemoryTourDAO2 tourDAO2 = new InMemoryTourDAO2();
        tourDAO2.put(sampleTour2());                    // id=3 투어

        InMemoryReservationDAO2 resDAO2 = new InMemoryReservationDAO2();

        ReservationManager2 svc = new ReservationManager2(resDAO2, userDAO, tourDAO2);

        // ----- 2) 시나리오 테스트 -----
        System.out.println("=== Reservation V2 Smoke Test ===");

        // 2-1) 과거 날짜 → OLD_DATE
        var past = LocalDate.now().minusDays(1);
        System.out.println("reserve past: " +
                svc.reserve(1245, 3, past, 2));

        // 2-2) 일요일(예약 불가 규칙) → DATE_UNAVAILABLE
        var nextSunday = next(DayOfWeek.SUNDAY);
        System.out.println("reserve sunday: " +
                svc.reserve(1245, 3, nextSunday, 2));

        // 2-3) 인원수 초과 → INVALID_HEADCOUNT
        var weekday = next(DayOfWeek.MONDAY); // 월요일 가정(가능일)
        System.out.println("reserve headcount 8: " +
                svc.reserve(1245, 3, weekday, 8));

        // 2-4) 정상 예약 → SUCCESS
        System.out.println("reserve ok (4명): " +
                svc.reserve(1245, 3, weekday, 4));

        // 2-5) 동일 조건 중복 → ALREADY_RESERVED
        System.out.println("reserve duplicate: " +
                svc.reserve(1245, 3, weekday, 4));

        // 현재 사용자 예약 목록/ID 확인
        System.out.println("user reservations: " + svc.getListByUserId(1245));

        // 2-6) 취소: 존재하지 않는 예약 → INVALID_RESERVATION
        System.out.println("cancel #999: " + svc.cancel(999));

        // 실제 저장된 예약 중 첫 번째를 찾아 취소(성공 후 재취소)
        int anyId = svc.getListByUserId(1245).get(0);
        System.out.println("cancel #" + anyId + ": " + svc.cancel(anyId));         // SUCCESS
        System.out.println("cancel again #" + anyId + ": " + svc.cancel(anyId));   // ALREADY_CANCELED

        // 2-7) 조회 확인
        System.out.println("getReservation #" + anyId + ": " + svc.getReservation(anyId));
    }
}
