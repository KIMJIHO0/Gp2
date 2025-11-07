package model;

import java.time.LocalDate;

/** 리뷰 정보 */
public class Review {
    public final int id;
    public final int writer_id;       // 작성자 id
    public final int tour_id;         // 대상 상품 id
    public final int rate;            // 1~10
    public final String content;
    public final LocalDate written_date;

    public Review(int id, int writer_id, int tour_id,
                  int rate, String content, LocalDate written_date) {
        this.id = id;
        this.writer_id = writer_id;
        this.tour_id = tour_id;
        this.rate = rate;
        this.content = content;
        this.written_date = written_date;
    }
}
