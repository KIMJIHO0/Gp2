package dao;

import model.TourPackage2;
import java.util.List;
import java.util.Optional;

/** TourPackage2 전용 DAO (기존 TourDAO는 그대로 유지) */
public interface TourDAO2 extends TourDAO {
    Optional<TourPackage2> findById(int id);
    List<TourPackage2> findAll();
}
