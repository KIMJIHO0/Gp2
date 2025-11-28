package manager;

import java.util.List;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * 투어 카탈로그 공통 베이스(제네릭).
 * - 실제 조회(findAll/findById)는 하위 클래스가 DAO로 구현한다.
 */
public abstract class GenericTourCatalog<T, D> {

    protected final D dao;

    protected GenericTourCatalog(D dao) {
        this.dao = dao;
    }

    /** 하위에서 DAO로 구현 */
    protected abstract List<T> findAll();
    protected abstract Optional<T> findById(int id);

    /** 존재 여부 */
    public boolean exists(int id) {
        return findById(id).isPresent();
    }

    /** 단건 조회(없으면 null) */
    public T get(int id) {
        return findById(id).orElse(null);
    }

    /** 전체 목록 */
    public List<T> getAll() {
        return findAll();
    }

    /** ID 목록 (엔티티 id 추출 함수 전달) */
    public List<Integer> getIds(ToIntFunction<T> idGetter) {
        return findAll().stream().mapToInt(idGetter).boxed().toList();
    }
}
