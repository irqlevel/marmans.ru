package models;

import lib.Rng;

import java.security.SecureRandom;
import java.util.Base64;

public class UserSession {
    private static final String TAG = "Session";
    private String value = null;
    private String csrfToken = null;
    private long uid = -1;
    private long expires = -1;

    public String getCsrfToken() {return csrfToken;}

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

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

    public static UserSession genSession(long uid, long expires) {
        UserSession session = new UserSession();

        session.setExpires(System.currentTimeMillis() + expires);
        session.setUid(uid);
        session.setValue(new Rng().genBase64UrlString(16));
        session.setCsrfToken(new Rng().genBase64UrlString(16));
        return session;
    }
}
