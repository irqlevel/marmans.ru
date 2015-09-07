package api;

import com.fasterxml.jackson.databind.JsonNode;

public class AppCommentCreate {
    public long postId;
    public String content;
    public long parentId;

    public static AppCommentCreate parseJson(JsonNode json)
    {
        AppCommentCreate result = new AppCommentCreate();
        result.postId = json.findPath("postId").asLong();
        result.content = json.findPath("content").textValue();
        result.parentId = json.findPath("parentId").asLong();
        return result;
    }
}
