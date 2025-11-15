package manager;

import dao.TourDAO;
import model.TourPackage;

import java.util.List;

public class TourCatalog {

    private final TourDAO tourDAO;

    public TourCatalog(TourDAO tourDAO) {
        this.tourDAO = tourDAO;
    }

    public boolean exists(int id) {
        return tourDAO.getTour(id).isPresent();
    }

    public List<Integer> getTourIds() {
        return tourDAO.getAllTours().stream()
                .map(TourPackage::getId)
                .toList();
    }

    public TourPackage getTour(int id) {
        return tourDAO.getTour(id).orElse(null);
    }

    /** 추천 DAO가 전체 패키지 목록을 사용하기 위한 메서드 */
    public List<TourPackage> getAllTours() {
        return tourDAO.getAllTours();
    }
}
