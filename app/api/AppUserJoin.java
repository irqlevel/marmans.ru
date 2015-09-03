package api;


import com.fasterxml.jackson.databind.JsonNode;

public class AppUserJoin {
    public String email;
    public String password;

    public static AppUserJoin parseJson(JsonNode json)
    {
        AppUserJoin result = new AppUserJoin();
        result.email = json.findPath("email").textValue();
        result.password = json.findPath("password").textValue();
        return result;
    }
}
