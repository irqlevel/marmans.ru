package models;

import java.security.SecureRandom;
import java.util.Base64;

public class Session {
    private static final String TAG = "Session";
    private String session = null;
    private long uid = -1;
    private long expires = -1;

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
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

    public String genSession() {
        SecureRandom rng = new SecureRandom();
        byte[] rndBytes = new byte[16];
        rng.nextBytes(rndBytes);
        return Base64.getEncoder().encodeToString(rndBytes);
    }
}
