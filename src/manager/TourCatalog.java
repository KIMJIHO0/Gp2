package manager;

import dao.TourDAO;
import model.TourPackage;

import java.util.List;

/**
 * 여행 상품 카탈로그 관리.
 * - 실제 데이터 접근(TourDAO 구현체)에 의존.
 * - 상품 존재 여부, ID 목록, 단건 조회 기능만 제공.
 */
public class TourCatalog {

    // 여행 상품 데이터 접근 인터페이스
    private final TourDAO tourDAO;

    /**
     * TourDAO 구현체를 주입받아 TourCatalog 생성.
     * UI/메인 코드에서 의존성 주입.
     */
    public TourCatalog(TourDAO tourDAO) {
        this.tourDAO = tourDAO;
    }

    /**
     * 특정 ID의 상품이 존재하는지 확인.
     * @param id 상품 ID
     * @return 존재하면 true, 아니면 false
     */
    public boolean exists(int id) {
        return tourDAO.getTour(id).isPresent();
    }

    /**
     * 모든 여행 상품의 ID 목록 반환.
     * UI에서 목록 화면 구성 시 사용.
     */
    public List<Integer> getTourIds() {
        return tourDAO.getAllTours().stream()
                .map(t -> t.id)
                .toList();
    }

    /**
     * ID에 해당하는 여행 상품 조회.
     * @param id 상품 ID
     * @return TourPackage 또는 없으면 null
     */
    public TourPackage getTour(int id) {
        return tourDAO.getTour(id).orElse(null);
    }
}
