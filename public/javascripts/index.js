var post_template = null;
var post_comment_template = null;
var comment_template = null;

function fixPostsContent() {
    $(".post-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}

function fixCommentsContent() {
    $(".comment-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}

function loadPostComments(postId)
{
    $("#post-comments-" + postId).empty();
    getJson("/comments/" + postId, null)
    .done(function(result) {
        if (result.resultCode) {
            console.log("result code " + result.resultCode);
            return;
        }
        if (result.comments.length == 0)
            return;

        for (var i = 0; i < result.comments.length;  i++) {
            var comment = result.comments[i];
            var rendered = Mustache.render(comment_template,
                            {content: comment.content, uid: comment.uid});

            $("#post-comments-" + postId).prepend(rendered);
        }
        fixCommentsContent();
    })
    .fail(function() {
        console.log("get comments failed");
    });
}

function bindPostComment(postId) {
    $("#post-comment-link-" + postId).click(function( event ) {
        event.preventDefault();
        $("#post-comment-block-" + postId).append(Mustache.render(post_comment_template, {postId: postId}));
        $("#post-comment-submit-" + postId).click(function (event) {
            event.preventDefault();
            $("#post-comment-error-text-" + postId).text("");
            postJson("/comment/" + postId, JSON.stringify({ "content": $("#post-comment-content-" + postId).val(),
            "postId" : postId, "parentId" : -1}))
            .done(function(result) {
                if (result.resultCode != 0) {
                    $("#post-comment-error-text-" + postId).text("post failed error code " + result.resultCode);
                    return;
                }
                $("#post-comment-block-" + postId).empty();
                loadPostComments(postId);
            })
            .fail(function() {
                $("#post-comment-error-text-" + postId).text("HTTP request failed");
            });
        });
        $("#post-comment-cancel-" + postId).click(function (event) {
            event.preventDefault();
            $("#post-comment-block-" + postId).empty();
        });
    });
}

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
                            {title: post.title, content: post.content, uid: post.uid, postId : post.postId});

            $("#posts").prepend(rendered);
            bindPostComment(post.postId);
            loadPostComments(post.postId);
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
        $.get("/assets/templates/post_comment.html", function(template) {
            post_comment_template = template;
            $.get("/assets/templates/comment.html", function(template) {
                comment_template = template;
                loadPosts(0, 20);
            });
        });
    });

}

$(document).ready(onReady);