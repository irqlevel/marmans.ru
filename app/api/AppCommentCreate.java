package api;

import com.fasterxml.jackson.databind.JsonNode;

public class AppCommentCreate {
    public long postId;
    public String content;
    public long parentId;

    public static AppCommentCreate parseJson(JsonNode json)
    {
        AppCommentCreate result = new AppCommentCreate();
        result.postId = json.findPath("postId").asLong(-1);
        result.content = json.findPath("content").textValue();
        result.parentId = json.findPath("parentId").asLong(-1);
        return result;
    }
}
