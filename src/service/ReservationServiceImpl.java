package service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import common.Result;
import model.Reservation;
import model.enums.ReservationStatus;
import ports.PackageStore;
import ports.ReservationStore;
import util.Ids;

public class ReservationServiceImpl implements ReservationService {
    private final ReservationStore reservations;
    private final PackageStore packages;

    public ReservationServiceImpl(ReservationStore reservations, PackageStore packages){
        this.reservations = reservations; this.packages = packages;
    }

    @Override
    public Result<Reservation> book(String userId, String packageId) {
        if (packages.findById(packageId).isEmpty())
            return Result.fail("NOT_FOUND", "패키지 없음");
        var r = new Reservation(
                Ids.newId(), userId, packageId,
                Instant.now().toString(), ReservationStatus.BOOKED);
        return Result.ok(reservations.save(r)); // 저장은 포트
    }

    @Override
    public Result<Reservation> cancel(String reservationId, String userId) {
        Optional<Reservation> opt = reservations.findById(reservationId);
        if (opt.isEmpty()) return Result.fail("NOT_FOUND", "예약 없음");
        var r = opt.get();
        if (!r.userId.equals(userId)) return Result.fail("FORBIDDEN", "본인 예약 아님");
        var canceled = r.withStatus(ReservationStatus.CANCELED);
        return Result.ok(reservations.save(canceled));
    }

    @Override public List<Reservation> listByUser(String userId) { return reservations.findByUser(userId); }
}
