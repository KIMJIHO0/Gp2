package model;

import java.util.List;

/** 여행 상품 정보 */
public class TourPackage {
    public final int id;
    public final String name;
    public final String place;          // 지역
    public final int price;
    public final int day_long;         // 일정 일수
    public final List<String> schedule;

    public TourPackage(int id, String name, String place, int price,
                       int day_long, List<String> schedule) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.price = price;
        this.day_long = day_long;
        this.schedule = List.copyOf(schedule);
    }

  @Override
  public String toString() {
    return  "TourPackage{" + "id=" + id + ", name=" + name + ", place=" + place+
        ", price=" + price + ", day_long=" + day_long + ", schedule=" + schedule + '}';
  }
}
