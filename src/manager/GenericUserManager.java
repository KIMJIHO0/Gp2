package manager;

import java.util.Optional;

/**
 * User 계열 공통 매니저(제네릭 베이스).
 * - 저장/조회는 DAO에 위임
 * - 비밀번호 정책은 하위에서 결정(해시/평문 등)
 */
public abstract class GenericUserManager<TUser, TDAO> {

    protected final TDAO userDAO;

    protected GenericUserManager(TDAO userDAO) {
        this.userDAO = userDAO;
    }

    /** 존재 여부 */
    public abstract boolean exists(int id);

    /** 조회(없으면 null) */
    public abstract TUser getUser(int id);

    /** 공통 등록 엔트리(객체형). 중복 체크까지는 공통 제공. */
    protected boolean registerInternal(int id, java.util.function.Supplier<Boolean> doSave) {
        if (exists(id)) return false;
        return doSave.get();
    }

    /** DAO 헬퍼: Optional → null */
    protected static <X> X orNull(Optional<X> o){ return o.orElse(null); }
}
