package api;

import com.fasterxml.jackson.databind.JsonNode;

public class AppPostCreate {
    public String title;
    public String content;
    public String youtubeVideoLink;

    public static AppPostCreate parseJson(JsonNode json)
    {
        AppPostCreate result = new AppPostCreate();
        result.title = json.findPath("title").textValue();
        result.content = json.findPath("content").textValue();
        result.youtubeVideoLink = json.findPath("youtubeVideoLink").textValue();
        return result;
    }
}
