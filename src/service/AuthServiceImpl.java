package service;

import java.time.Instant;
import java.util.Optional;

import common.Result;
import model.User;
import ports.UserStore;       // ★포트만 의존
import util.Passwords;

public class AuthServiceImpl implements AuthService {
    private final UserStore users;

    public AuthServiceImpl(UserStore users){ this.users = users; }

    @Override
    public Result<User> signUp(String id, String pw) {
        if (id == null || !id.matches("^[a-zA-Z0-9_]{4,20}$"))
            return Result.fail("BAD_INPUT", "ID 형식 오류");
        if (pw == null || pw.length() < 8)
            return Result.fail("BAD_INPUT", "PW 형식 오류");

        if (users.findById(id).isPresent())
            return Result.fail("USER_EXISTS", "이미 존재하는 ID");

        String hash = Passwords.hash(pw);
        User u = new User(id, hash, Instant.now().toString());
        users.save(u);                      // 저장은 포트에 위임
        return Result.ok(u);
    }

    @Override
    public Result<User> signIn(String id, String pw) {
        Optional<User> opt = users.findById(id);
        if (opt.isEmpty()) return Result.fail("NOT_FOUND", "가입 정보 없음");
        User u = opt.get();
        if (!Passwords.matches(pw, u.pwHash))
            return Result.fail("PW_MISMATCH", "비밀번호 불일치");
        return Result.ok(u);
    }
}
