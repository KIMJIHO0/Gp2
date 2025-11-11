public class SessionManager {
    private Long currentUserId = null; // 로그인한 사용자 ID

    public void login(Long userId) {
        this.currentUserId = userId;
        System.out.println("SessionManager: User " + userId + " logged in.");
    }

    public void logout() {
        this.currentUserId = null;
    }

    public Long getCurrentUserId() {
        return this.currentUserId;
    }

    public boolean isLoggedIn() {
        return this.currentUserId != null;
    }
}