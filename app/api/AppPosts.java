package api;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Post;

import java.util.List;

public class AppPosts extends AppResult {
    List<Post> posts;

    public AppPosts(AppResult result) {
        this.setResultCode(result.resultCode);
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    public ObjectNode toJson() {
        ObjectNode json = super.toJson();
        ArrayNode postsArr = json.putArray("posts");
        for (Post post : posts) {
            postsArr.add(post.toJson());
        }
        return json;
    }
}
