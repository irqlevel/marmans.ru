package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppUserSignin {
    public String email;
    public String password;

    public static AppUserSignin parseJson(String json) throws Exception {
        JsonNode node = new ObjectMapper().readTree(json);
        AppUserSignin result = new AppUserSignin();
        result.email = node.findPath("email").textValue();
        result.password = node.findPath("password").textValue();
        return result;
    }
}
