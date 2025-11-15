package model;

import java.util.List;

/** 여행 상품 정보 DTO */
public class TourPackage {
    private final int id;
    private final String name;
    private final String place;
    private final int price;
    private final int day_long;
    private final List<String> schedule;

    public TourPackage(int id, String name, String place,
                       int price, int day_long, List<String> schedule) {
        this.id = id;
        this.name = name;
        this.place = place;
        this.price = price;
        this.day_long = day_long;
        this.schedule = List.copyOf(schedule);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPlace() { return place; }
    public int getPrice() { return price; }
    public int getDayLong() { return day_long; }
    public List<String> getSchedule() { return schedule; }
}
