package models;

public class User {
    private long uid = -1;
    private String name = null;
    private String email = null;
    private String hashp = null;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashp() { return hashp;}

    public void setHashp(String hashp) { this.hashp = hashp;}
}

