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
    public String userName = null;
    public String date = null;
    public long imageId = -1;
    public String imageUrl = null;
    public String userPicUrl = null;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("uid", uid);
        json.put("postId", postId);
        json.put("parentId", parentId);
        json.put("content", content);
        json.put("commentId", commentId);
        json.put("creationTime", creationTime);
        json.put("userName", userName);
        json.put("date", date);
        json.put("imageId", imageId);
        json.put("imageUrl", imageUrl);
        json.put("userPicUrl", userPicUrl);
        return json;
    }
}

