package model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** 예약 불가 규칙 집합 */
public class UnavailableDateRule {

    /** [start, end] 양끝 포함 구간 */
    public static final class DateRange implements Comparable<DateRange> {
        public final LocalDate start;
        public final LocalDate end;

        public DateRange(LocalDate start, LocalDate end) {
            if (start == null || end == null || end.isBefore(start))
                throw new IllegalArgumentException("Invalid range");
            this.start = start; this.end = end;
        }

        public boolean contains(LocalDate d) {
            return !d.isBefore(start) && !d.isAfter(end);
        }

        @Override public int compareTo(DateRange o) { return this.start.compareTo(o.start); }
    }

    private final List<DateRange> period;      // 정렬 보장
    private final List<DayOfWeek> days;        // 불가 요일
    private final List<LocalDate> specified;   // 개별 불가일

    public UnavailableDateRule(List<DateRange> period,
                               List<DayOfWeek> days,
                               List<LocalDate> specified) {
        var p = new ArrayList<>(period == null ? List.of() : period);
        Collections.sort(p);
        this.period = Collections.unmodifiableList(p);
        this.days = Collections.unmodifiableList(
                new ArrayList<>(days == null ? List.of() : days));
        this.specified = Collections.unmodifiableList(
                new ArrayList<>(specified == null ? List.of() : specified));
    }

    /** 해당 날짜가 예약 불가인지 검사 */
    public boolean isUnavailable(LocalDate date) {
        if (date == null) return true;
        if (specified.contains(date)) return true;
        if (days.contains(date.getDayOfWeek())) return true;
        for (DateRange r : period) if (r.contains(date)) return true;
        return false;
    }
}
