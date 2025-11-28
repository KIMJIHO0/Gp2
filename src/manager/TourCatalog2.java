package manager;

import dao.TourDAO2;
import model.TourPackage2;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * TourCatalog2
 * - DAO: TourDAO2로 고정
 * - 제네릭 베이스를 상속해 공통 기능 재사용
 * - V2 전용 검증/요금 계산 메서드 제공
 */
public class TourCatalog2 extends GenericTourCatalog<TourPackage2, TourDAO2> {

    public TourCatalog2(TourDAO2 dao) {
        super(dao);
    }

    /* ===== Generic 베이스 구현부 ===== */

    @Override
    protected List<TourPackage2> findAll() {
        return dao.findAll();
    }

    @Override
    protected Optional<TourPackage2> findById(int id) {
        return dao.findById(id);
    }

    /* ===== V1 호환 편의 메서드 ===== */

    /** 단건 조회(없으면 null) — V1 명명 유지 */
    public TourPackage2 getTour(int id) {
        return get(id);
    }

    /** 전체 목록 — V1 명명 유지 */
    public List<TourPackage2> getAllTours() {
        return getAll();
    }

    /** ID 목록 — V1 명명 유지 */
    public List<Integer> getTourIds() {
        return getIds(t -> t.id);
    }

    /* ===== 요구된 추가 메서드 3개 ===== */

    /** 시작일 예약 가능 여부(true면 가능) */
    public boolean checkDateValidate(TourPackage2 t, LocalDate startDate) {
        if (t == null || startDate == null) return false;
        if (t.unavailables == null) return true;
        return !t.unavailables.isUnavailable(startDate);
    }

    /** 인원수가 [min,max] 범위 내인지 */
    public boolean checkHeadcountValidate(TourPackage2 t, int headcount) {
        if (t == null) return false;
        int min = t.headcount_range[0];
        int max = t.headcount_range[1];
        return headcount >= min && headcount <= max;
    }

    /** 총비용 = price + max(0, headcount - min) * extraPersonFee */
    public int totalPrice(TourPackage2 t, int headcount) {
        if (t == null) return 0;
        int min = t.headcount_range[0];
        int extra = Math.max(0, headcount - min);
        long total = (long) t.price + (long) extra * (long) t.extraPersonFee;
        if (total < 0) return 0;
        if (total > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) total;
    }
}
