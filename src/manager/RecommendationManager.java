package manager;

import dao.RecommendationDAO;
import model.Recommendation;
import java.util.List;
import java.util.ArrayList;

//추천 데이터 처리
public class RecommendationManager {
    private final RecommendationDAO dao;

    public RecommendationManager(RecommendationDAO dao) {
        this.dao = dao;
    }

    public void printRecommendations() {
        System.out.println("\n=== 이번 주 추천 여행 패키지 ===");
        List<Recommendation> list = dao.getRecommendations();
        for (Recommendation r : list) {
            System.out.println(r);
        }
    }

    public List<Recommendation> recommend(int userId){
        // Todo
        return new ArrayList<>();
    }
}