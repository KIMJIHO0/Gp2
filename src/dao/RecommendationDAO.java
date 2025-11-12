package dao;

import java.util.List;
import model.Recommendation;


//추천 패키지 데이터를 제공하는 인터페이스

public interface RecommendationDAO {
    List<Recommendation> getRecommendations();
}