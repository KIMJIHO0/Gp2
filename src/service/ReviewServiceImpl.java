package service;

import java.time.Instant;
import java.util.List;

import common.Result;
import model.Review;
import ports.PackageStore;
import ports.ReviewStore;
import util.Ids;

public class ReviewServiceImpl implements ReviewService {
    private final ReviewStore reviews;
    private final PackageStore packages;

    public ReviewServiceImpl(ReviewStore reviews, PackageStore packages) {
        this.reviews = reviews; this.packages = packages;
    }

    @Override
    public Result<Review> write(String userId, String packageId, int score, String content) {
        if (packages.findById(packageId).isEmpty())
            return Result.fail("NOT_FOUND", "패키지 없음");
        if (score < 1 || score > 5)
            return Result.fail("BAD_INPUT", "평점 1~5");
        var review = new Review(Ids.newId(), userId, packageId, score,
                content==null?"":content, Instant.now().toString());
        return Result.ok(reviews.save(review)); // 저장은 포트
    }

    @Override public List<Review> listByPackage(String packageId){ return reviews.findByPackage(packageId); }
    @Override public List<Review> listByUser(String userId){ return reviews.findByUser(userId); }
}
