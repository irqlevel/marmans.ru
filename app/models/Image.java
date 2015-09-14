package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

public class Image {
    public long imageId = -1;
    public long uid = -1;
    public String title = null;
    public String content = null;
    public long creationTime = -1;
    public String url = null;
    public long width = -1;
    public long height = -1;
    public String s3key = null;
    public String s3bucket = null;
    public String fileBaseName = null;
    public String fileExtension = null;
    public long fileSize = -1;
    public String fileType = null;
    public String fileName = null;

    public ObjectNode toJson() {
        ObjectNode json = Json.newObject();
        json.put("imageId", imageId);
        json.put("uid", uid);
        json.put("title", title);
        json.put("content", content);
        json.put("creationTime", creationTime);
        json.put("url", url);
        json.put("width", width);
        json.put("height", height);
        json.put("fileBaseName", fileBaseName);
        json.put("fileExtension", fileExtension);
        json.put("fileType", fileType);
        json.put("fileSize", fileSize);
        json.put("fileName", fileName);
        return json;
    }
}
