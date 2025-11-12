package model;

//추천 여행 패키지 정보를 담는 DTO

public class Recommendation {
    private int id;
    private String name;
    private String location;
    private int price;
    private String reason; // 추천 이유

    public Recommendation(int id, String name, String location, int price, String reason) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.price = price;
        this.reason = reason;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public int getPrice() { return price; }
    public String getReason() { return reason; }

    @Override
    public String toString() {
        return "★추천★ " + name + " (" + location + ") - " + price + "원 [" + reason + "]";
    }
}