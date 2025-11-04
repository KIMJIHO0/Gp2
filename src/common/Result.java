package common; // 공통 유틸/응답 모델 패키지

/**
 * API/서비스 호출 결과를 한 타입으로 통일하기 위한 제네릭 래퍼.
 * - ok   : 성공 여부
 * - code : 결과 코드(예: "OK", "E_VALID", "E_DB")
 * - msg  : 사용자/로그 기록용 메시지
 * - data : 성공 시 페이로드(타입 T)
 */
public class Result<T> {

    // 불변 필드들. 생성 이후 변경 불가.
    public final boolean ok;
    public final String code;
    public final String msg;
    public final T data;

    // 외부에서 임의 생성을 막기 위한 private 생성자.
    // 정적 팩터리 메서드(ok, fail)만을 통해 만들게 한다.
    private Result(boolean ok, String code, String msg, T data) {
        this.ok = ok;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 성공 결과 생성기.
     * 사용: Result.ok(user) 또는 Result.<User>ok(user)
     * - ok   = true
     * - code = "OK"
     * - msg  = "성공"
     * - data = 전달된 페이로드
     */
    public static <T> Result<T> ok(T data){
        return new Result<>(true, "OK", "성공", data);
    }

    /**
     * 실패 결과 생성기.
     * 사용: Result.fail("E_VALID", "아이디가 필요합니다")
     * - ok   = false
     * - data = null
     */
    public static <T> Result<T> fail(String code, String msg){
        return new Result<>(false, code, msg, null);
    }
}
