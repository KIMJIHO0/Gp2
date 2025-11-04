package model;

import java.time.Instant;

/**
 * 리뷰 엔티티.
 * - id        : 리뷰 식별자
 * - userId    : 작성자
 * - packageId : 대상 패키지
 * - score     : 1~5
 * - content   : 내용
 * - createdAt : 작성 시각
 */
public class Review {
    public final String id;
    public final String userId;
    public final String packageId;
    public final int score;
    public final String content;
    public final String createdAt;

    public Review(String id, String userId, String packageId,
                  int score, String content, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.packageId = packageId;
        this.score = score;
        this.content = content;
        this.createdAt = createdAt;
    }
}
