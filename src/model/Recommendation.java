package model;

/** 추천 DTO (추천 대상 패키지의 id + 이유) */
public class Recommendation {

    private final int tourId;
    private final String reason;

    public Recommendation(int tourId, String reason) {
        this.tourId = tourId;
        this.reason = reason;
    }

    public int getTourId() { return tourId; }
    public String getReason() { return reason; }
}