package common;

public class Result<T> {
    public final boolean ok;
    public final String code;
    public final String msg;
    public final T data;

    private Result(boolean ok, String code, String msg, T data) {
        this.ok = ok; this.code = code; this.msg = msg; this.data = data;
    }
    public static <T> Result<T> ok(T data){ return new Result<>(true, "OK", "성공", data); }
    public static <T> Result<T> fail(String code, String msg){ return new Result<>(false, code, msg, null); }
}

