package model;

public class User {
    public final String id;
    public final String pwHash;
    public final String createdAt;
    public User(String id, String pwHash, String createdAt){
        this.id = id; this.pwHash = pwHash; this.createdAt = createdAt;
    }
}


