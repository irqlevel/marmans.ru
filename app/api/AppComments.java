package api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.util.List;
import models.Comment;

public class AppComments extends AppResult {
    List<Comment> comments;

    public AppComments(AppResult result) {
        this.setResultCode(result.resultCode);
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public ObjectNode toJson() {
        ObjectNode json = super.toJson();
        ArrayNode postsArr = json.putArray("comments");
        for (Comment comment : comments) {
            postsArr.add(comment.toJson());
        }
        return json;
    }
}

