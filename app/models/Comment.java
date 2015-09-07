package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class Comment {
    public long uid = -1;
    public long postId = -1;
    public long parentId = -1;
    public long commentId = -1;
    public long creationTime = -1;
    public String content = null;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("uid", uid);
        json.put("postId", postId);
        json.put("parentId", parentId);
        json.put("content", content);
        json.put("commentId", commentId);
        json.put("creationTime", creationTime);
        return json;
    }
}

