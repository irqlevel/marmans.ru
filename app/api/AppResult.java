package api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;

import java.util.HashMap;
import java.util.Map;

public class AppResult {
    public String resultDesc = "UNDEFINED";
    public int resultCode = -1;
    public long uid = -1;
    public long id = -1;

    private static Map<Integer, String> resultCodeToDescMap = new HashMap<Integer, String>();
    public static final int ESUCCESS = 0;
    public static final int EINVAL = 1;
    public static final int EIO = 2;
    public static final int ENOMEM = 3;
    public static final int ENOTFOUND = 4;
    public static final int EXISTS = 5;
    public static final int EPERM = 6;
    public static final int ESIGNIN = 7;
    public static final int EUNDEF = 8;
    public static final int EDB_UPDATE = 9;
    public static final int EDB_QUERY = 10;
    public static final int EEXCEPT = 11;
    public static final int EAUTH = 12;

    static {
        resultCodeToDescMap.put(ESUCCESS, "success");
        resultCodeToDescMap.put(EINVAL, "invalid value");
        resultCodeToDescMap.put(EIO, "I/O failed");
        resultCodeToDescMap.put(ENOMEM, "no memory");
        resultCodeToDescMap.put(ENOTFOUND, "object not found");
        resultCodeToDescMap.put(EXISTS, "object already exists");
        resultCodeToDescMap.put(EPERM, "no permissions");
        resultCodeToDescMap.put(ESIGNIN, "signin is required");
        resultCodeToDescMap.put(EUNDEF, "undefined");
        resultCodeToDescMap.put(EDB_UPDATE, "database update failed");
        resultCodeToDescMap.put(EDB_QUERY, "database query failed");
        resultCodeToDescMap.put(EEXCEPT, "unhandled exception");
        resultCodeToDescMap.put(EAUTH, "authorization failed");
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
        this.resultDesc = resultCodeToDesc(resultCode);
    }

    private AppResult() {
        setResultCode(EUNDEF);
    }

    private static String resultCodeToDesc(int resultCode) {
        String desc = resultCodeToDescMap.get(resultCode);
        if (desc == null)
            return "unexpected result code " + resultCode;
        return desc;
    }

    public static AppResult success() {
        AppResult result = new AppResult();
        result.setResultCode(ESUCCESS);
        return result;
    }

    public static AppResult error(int resultCode) {
        AppResult result = new AppResult();
        result.setResultCode(resultCode);
        if (resultCode != 0)
            Logger.error("app result error=" + resultCode);
        return result;
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("resultDesc", resultDesc);
        json.put("resultCode", resultCode);
        json.put("uid", uid);
        json.put("id", id);
        return json;
    }
}
