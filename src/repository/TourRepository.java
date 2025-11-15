package repository;

import dao.TourDAO;
import model.TourPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TourRepository implements TourDAO {

  private final List<TourPackage> tours = new ArrayList<>();

  public TourRepository(String filename) {
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
  public List<TourPackage> getAllTours() {
    return new ArrayList<>(tours);
  }

  @Override
  public Optional<TourPackage> getTour(int id) {
    return tours.stream()
        .filter(t->t.id == id)
        .findFirst();
  }

  public void seed(List<TourPackage> list){
    tours.addAll(list);
  }
}
