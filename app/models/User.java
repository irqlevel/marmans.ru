package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class User {
    public long uid = -1;
    public String name = null;
    public String email = null;
    public String hashp = null;
    public long avatarId = -1;
    public long thumbnailId = -1;
    public String avatarUrl = null;
    public String thumbnailUrl = null;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("uid", uid);
        json.put("name", name);
        json.put("email", email);
        json.put("hashp", hashp);
        json.put("avatarId", avatarId);
        json.put("thumbnailId", thumbnailId);
        json.put("avatarUrl", avatarUrl);
        json.put("thumbnailUrl", thumbnailUrl);
        return json;
    }
}

