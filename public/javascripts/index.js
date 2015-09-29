var post_template = null;
var post_comment_template = null;
var comment_template = null;

function loadPosts(offset, limit)
{
    getJson("/posts/latest/offset/" + offset + "/limit/" + limit, null)
    .done(function(result) {
        if (result.resultCode) {
            console.log("result code " + result.resultCode);
            return;
        }
        if (result.posts.length == 0)
            return;

        for (var i = 0; i < result.posts.length;  i++) {
            var post = result.posts[i];
            var rendered = Mustache.render(post_template,
                            {title: post.title, content: post.content, uid: post.uid, postId : post.postId,
                             userName : post.userName, userPicUrl : post.userPicUrl, date: post.date});

            $("#posts").append(rendered);
        }
        fixPostsContent();
    })
    .fail(function() {
        console.log("get posts failed");
    });
}

function onReady()
{
    $.get("/assets/templates/post.html", function(template) {
        post_template = template;
    });

    $.get("/assets/templates/post_comment.html", function(template) {
        post_comment_template = template;
    });

    $.get("/assets/templates/comment.html", function(template) {
        comment_template = template;
    });

    loadPosts(0, 20);
}

$(document).ready(onReady);