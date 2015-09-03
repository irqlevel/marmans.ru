package models;

import java.security.SecureRandom;
import java.util.Base64;

public class UserSession {
    private static final String TAG = "Session";
    private String value = null;
    private long uid = -1;
    private long expires = -1;

    public String getValue() {return value;}

    public void setValue(String value) {
        this.value = value;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getExpires() {
        return expires;
    }

    public void setExpires(long expires) {
        this.expires = expires;
    }

    private static String genValue() {
        SecureRandom rng = new SecureRandom();
        byte[] rndBytes = new byte[16];
        rng.nextBytes(rndBytes);
        return Base64.getEncoder().encodeToString(rndBytes);
    }

    public static UserSession genSession(long uid, long expires) {
        UserSession session = new UserSession();

        session.setExpires(System.currentTimeMillis() + expires);
        session.setUid(uid);
        session.setValue(genValue());
        return session;
    }
}
