package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class Post {
    public long uid = -1;
    public long postId = -1;
    public long creationTime = -1;
    public String title = null;
    public String content = null;
    public String userName = null;
    public String date = null;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("title", title);
        json.put("content", content);
        json.put("uid", uid);
        json.put("creationTime", creationTime);
        json.put("postId", postId);
        json.put("userName", userName);
        json.put("date", date);
        return json;
    }
}
