package api;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AppUserJoin {
    public String email;
    public String password;

    public static AppUserJoin parseJson(String json) throws Exception {
        JsonNode node = new ObjectMapper().readTree(json);
        AppUserJoin result = new AppUserJoin();
        result.email = node.findPath("email").textValue();
        result.password = node.findPath("password").textValue();
        return result;
    }
}
