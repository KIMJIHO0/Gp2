package repository;

import dao.TourDAO2;
import model.TourPackage2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TourRepository2 implements TourDAO2 {
  private final List<TourPackage2> tours = new ArrayList<>();

  public TourRepository2(String filename) {
    Manager manager = new Manager();
    manager.readAll(filename, new Factory() {
      @Override
      public Manageable create() {
        return new TourItem();
      }
    });

    for(Manageable m : manager.mList){
      TourItem item = (TourItem) m;
      tours.add(item.toTour());
    }
  }

  @Override
  public Optional<TourPackage2> findById(int id) {
    return tours.stream()
        .filter(t->t.id == id)
        .findFirst();
  }

  @Override
  public List<TourPackage2> findAll() {
    return new ArrayList<>(tours);
  }
}
