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
    public long imageId = -1;
    public String imageUrl = null;
    public String userPicUrl = null;
    public int active = 0;
    public String youtubeLinkId = null;
    public long nrComments = 0;
    public long nrViews = 0;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("title", title);
        json.put("content", content);
        json.put("uid", uid);
        json.put("creationTime", creationTime);
        json.put("postId", postId);
        json.put("userName", userName);
        json.put("date", date);
        json.put("imageId", imageId);
        json.put("imageUrl", imageUrl);
        json.put("userPicUrl", userPicUrl);
        json.put("active", active);
        json.put("youtubeLinkId", youtubeLinkId);
        json.put("nrComments", nrComments);
        json.put("nrViews", nrViews);
        return json;
    }
}
