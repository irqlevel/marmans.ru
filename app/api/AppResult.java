package api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class AppResult {
    public String resultDesc = "UNDEFINED";
    public int resultCode = -1;
    public long uid = -1;

    public static AppResult success() {
        AppResult result = new AppResult();
        result.resultDesc = "success";
        result.resultCode = 0;
        return result;
    }

    public static AppResult error(int resultCode) {
        AppResult result = new AppResult();
        result.resultDesc = "error " + resultCode;
        result.resultCode = resultCode;
        return result;
    }

    public static AppResult error(int resultCode, String resultDesc) {
        AppResult result = new AppResult();
        result.resultDesc = resultDesc;
        result.resultCode = resultCode;
        return result;
    }

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("resultDesc", resultDesc);
        json.put("resultCode", resultCode);
        json.put("uid", uid);
        return json;
    }
}
