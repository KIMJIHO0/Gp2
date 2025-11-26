package model;

import java.awt.image.BufferedImage;
import java.util.List;

/** 확장된 투어 패키지 (이미지·불가일·인원 범위 등 추가) */
public class TourPackage2 {
    public final int id;
    public final String name;
    public final String place;
    public final int price;                   // 기본가(기본 인원 기준)
    public final int day_long;

    public final int extraPersonFee;          // 추가 1인당 비용
    public final String[] transport;          // 교통 수단
    public final int[] headcount_range;       // [min, max]
    public final UnavailableDateRule unavailables;

    public final BufferedImage[] shots;       // 상세 이미지들(없으면 빈 배열)
    public final BufferedImage thumbnail;     // 목록 썸네일(없으면 null)
    public final String contact_number;       // 제공자 연락처

    public final List<String> schedule;       // 간단 일정(문자열)

    public TourPackage2(int id, String name, String place, int price, int day_long,
                        int extraPersonFee, String[] transport, int[] headcount_range,
                        UnavailableDateRule unavailables,
                        BufferedImage[] shots, BufferedImage thumbnail,
                        String contact_number, List<String> schedule) {
        this.id = id; this.name = name; this.place = place;
        this.price = price; this.day_long = day_long;

        this.extraPersonFee = extraPersonFee;
        this.transport = transport == null ? new String[0] : transport.clone();

        if (headcount_range == null || headcount_range.length != 2
                || headcount_range[0] <= 0 || headcount_range[1] < headcount_range[0]) {
            throw new IllegalArgumentException("invalid headcount_range");
        }
        this.headcount_range = headcount_range.clone();

        this.unavailables = unavailables;

        this.shots = shots == null ? new BufferedImage[0] : shots.clone();
        this.thumbnail = thumbnail;
        this.contact_number = contact_number;

        this.schedule = List.copyOf(schedule == null ? List.of() : schedule);
    }
}
