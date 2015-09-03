package api;

import com.fasterxml.jackson.databind.JsonNode;


public class AppUserProfile {
    public String name;

    public static AppUserProfile parseJson(JsonNode json)
    {
        AppUserProfile result = new AppUserProfile();
        result.name = json.findPath("name").textValue();
        return result;
    }
}
