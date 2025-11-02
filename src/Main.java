import common.Result;
import model.User;
import repository.InMemoryUserRepository;
import repository.UserRepository;
import service.AuthService;
import service.AuthServiceImpl;

public class Main {
    public static void main(String[] args) {
        UserRepository repo = new InMemoryUserRepository();
        AuthService auth = new AuthServiceImpl(repo);

        Result<User> r1 = auth.signUp("jiho", "password123");
        System.out.println("signUp: " + r1.ok + " / " + r1.msg);

        Result<User> r2 = auth.signIn("jiho", "password123");
        System.out.println("signIn: " + r2.ok + " / " + r2.msg);

        Result<User> r3 = auth.signIn("jiho", "wrongpw");
        System.out.println("signIn-wrong: " + r3.ok + " / " + r3.msg);
    }
}

