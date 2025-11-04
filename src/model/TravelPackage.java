package model;

import model.enums.Region;
import model.enums.Theme;

/**
 * 여행 패키지 도메인.
 * - id        : 패키지 식별자
 * - name      : 패키지명
 * - region    : 지역
 * - theme     : 테마(힐링/레저/가족 등)
 * - days      : 기간(박수+1 정도의 일수)
 * - price     : 가격(만원 단위 예시)
 * - rating    : 평균 평점(0.0~5.0)
 */
public class TravelPackage {
    public final String id;
    public final String name;
    public final Region region;
    public final Theme theme;
    public final int days;
    public final int price;
    public final double rating;

    public TravelPackage(String id, String name, Region region, Theme theme,
                         int days, int price, double rating) {
        this.id = id;
        this.name = name;
        this.region = region;
        this.theme = theme;
        this.days = days;
        this.price = price;
        this.rating = rating;
    }
}
