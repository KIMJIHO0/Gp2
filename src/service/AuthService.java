package service;

import common.Result;
import model.User;

public interface AuthService {
    Result<User> signUp(String id, String pw);
    Result<User> signIn(String id, String pw);
}
