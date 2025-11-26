package model;

import java.awt.image.BufferedImage;
import java.util.List;

/** V2 투어 패키지: 기존 TourPackage를 상속하고 확장 필드만 추가 */
public class TourPackage2 extends TourPackage {

    public final int extraPersonFee;          // 기본 인원 초과 1인당 추가비
    public final String[] transport;          // 교통 수단 목록
    public final int[] headcount_range;       // [min, max]
    public final UnavailableDateRule unavailables;

    public final BufferedImage[] shots;       // 상세 이미지들(없으면 빈 배열)
    public final BufferedImage thumbnail;     // 목록 썸네일(없으면 null)
    public final String contact_number;       // 제공자 연락처

    public TourPackage2(int id, String name, String place, int price, int day_long,
                        int extraPersonFee, String[] transport, int[] headcount_range,
                        UnavailableDateRule unavailables,
                        BufferedImage[] shots, BufferedImage thumbnail,
                        String contact_number, List<String> schedule) {
        // 부모가 가진 공통 필드(id, name, place, price, day_long, schedule) 초기화
        super(id, name, place, price, day_long, schedule);

        // 확장 필드 초기화
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
    }

    @Override
    public String toString() {
        return "TourPackage2{" +
                "id=" + id +
                ", name=" + name +
                ", place=" + place +
                ", price=" + price +
                ", day_long=" + day_long +
                ", extraPersonFee=" + extraPersonFee +
                ", transport=" + java.util.Arrays.toString(transport) +
                ", headcount_range=" + java.util.Arrays.toString(headcount_range) +
                ", contact_number='" + contact_number + '\'' +
                ", schedule=" + schedule +
                '}';
    }
}
