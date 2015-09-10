var post_comment_template = null;
var comment_template = null;

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
                            {content: comment.content, uid: comment.uid, userName: comment.userName, date: comment.date});

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
        $("#post-comment-link-" + postId).hide();
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
            $("#post-comment-link-" + postId).show();
        });
        $("#post-comment-cancel-" + postId).click(function (event) {
            event.preventDefault();
            $("#post-comment-block-" + postId).empty();
            $("#post-comment-link-" + postId).show();
        });
    });
}

function onReady()
{
    $.get("/assets/templates/post_comment.html", function(template) {
        post_comment_template = template;
    });

    $.get("/assets/templates/comment.html", function(template) {
        comment_template = template;
    });
    var postId = $("#post-id").text();
    loadPostComments(postId);
    bindPostComment(postId);
    fixCommentsContent();
}
$(document).ready(onReady);