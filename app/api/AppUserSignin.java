package api;

import com.fasterxml.jackson.databind.JsonNode;


public class AppUserSignin {
    public String email;
    public String password;

    public static AppUserSignin parseJson(JsonNode json)
    {
        AppUserSignin result = new AppUserSignin();
        result.email = json.findPath("email").textValue();
        result.password = json.findPath("password").textValue();
        return result;
    }
}
