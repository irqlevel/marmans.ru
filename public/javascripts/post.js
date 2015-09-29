var post_comment_template = null;
var comment_template = null;
var reply_comment_template = null;

function fixCommentsContent() {
    $(".comment-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}

function renderComment(postId, comment, level)
{
    var rendered = Mustache.render(comment_template,
                    {commentId: comment.commentId, content: comment.content, uid: comment.uid, userName: comment.userName, userPicUrl: comment.userPicUrl, date: comment.date});
    $("#post-comments-" + postId).append(rendered);
    $("#post-comments-" + postId).append("<hr>");
    var margin = level*15;
    $("#comment-container-" + comment.commentId).css('margin-left', margin + "px");

    comment.childs.sort(function(a, b) {
        return a.creationTime - b.creationTime;
    });

    for (var i = 0; i < comment.childs.length; i++) {
        var child = comment.childs[i];
        renderComment(postId, child, level + 1);
    }
}

function bindCommentsReply(postId)
{
    $(".comment-reply-link").click(function (event) {
        event.preventDefault();
        var id = $(this).attr("id");
        var commentId = parseInt(id.replace(/^comment-reply-link-/, ''));
        $("#comment-reply-link-" + commentId).hide();
        $("#reply-comment-block").empty();
        $("#reply-comment-block-" + commentId).append(Mustache.render(reply_comment_template, {commentId: commentId}));
        $("#reply-comment-content-" + commentId).focus();
        $("#reply-comment-submit-" + commentId).click(function (event) {
            event.preventDefault();
            $("#reply-comment-error-text-" + commentId).empty();
            postJson("/comment/" + commentId + "/reply", JSON.stringify({ "content": $("#reply-comment-content-" + commentId).val()}))
            .done(function(result) {
                if (result.resultCode != 0) {
                    $("#reply-comment-error-text-" + commentId).text("post failed error code " + result.resultCode);
                    if (result.resultCode == EAUTH) {
                        window.location.replace("/signin");
                    }
                    return;
                }
                $("#reply-comment-block-" + commentId).empty();
                $("#comment-reply-link-" + commentId).show();
                loadPostComments(postId, result.id);
            })
            .fail(function() {
                $("#reply-comment-error-text-" + commentId).text("HTTP request failed");
            });
        });
        $("#reply-comment-cancel-" + commentId).click(function (event) {
            event.preventDefault();
            $("#comment-reply-link-" + commentId).show();
            $("#reply-comment-block-" + commentId).empty();
        });
    });
}

function renderComments(postId, comments)
{
    var commentsMap = new Map();
    var topComments = [];
    for (var i = 0; i < comments.length;  i++) {
        var comment = comments[i];
        comment.childs = [];
        if (comment.parentId == -1) {
            topComments.push(comment);
        }
        commentsMap.set(comment.commentId, comment);
    }

    for (var i = 0; i < comments.length;  i++) {
        var comment = comments[i];
        if (comment.parentId != -1) {
            if (!commentsMap.has(comment.parentId)) {
                console.log("no parent for comment " + comment.parentId);
                return;
            }
            var parent = commentsMap.get(comment.parentId);
            parent.childs.push(comment);
        }
    }

    topComments.sort(function(a, b) {
        return a.creationTime - b.creationTime;
    });

    for (var i = 0; i < topComments.length; i++) {
        var comment = topComments[i];
        renderComment(postId, comment, 1);
    }
}

function scrollToComment(commentId)
{
    console.log("scroll to " + commentId);
    $("body, html").animate({
        scrollTop: $("#comment-container-" + commentId).offset().top - 100
    }, 10);
}

function loadPostComments(postId, scrollToCommentId)
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
        renderComments(postId, result.comments);
        fixCommentsContent();
        bindCommentsReply(postId);
        if (scrollToCommentId >= 0)
            scrollToComment(scrollToCommentId);
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
        $("#post-comment-content-" + postId).focus();
        $("#post-comment-submit-" + postId).click(function (event) {
            event.preventDefault();
            $("#post-comment-error-text-" + postId).empty();
            postJson("/comment/" + postId, JSON.stringify({ "content": $("#post-comment-content-" + postId).val(),
            "postId" : postId, "parentId" : -1}))
            .done(function(result) {
                if (result.resultCode != 0) {
                    $("#post-comment-error-text-" + postId).text("post failed error code " + result.resultCode);
                    if (result.resultCode == EAUTH) {
                        window.location.replace("/signin");
                    }
                    return;
                }
                $("#post-comment-block-" + postId).empty();
                $("#post-comment-link-" + postId).show();
                loadPostComments(postId, result.id);
            })
            .fail(function() {
                $("#post-comment-error-text-" + postId).text("HTTP request failed");
            });
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
    $.get("/assets/templates/reply_comment.html", function(template) {
        reply_comment_template = template;
    });
    var postId = $("#post-id").text();
    loadPostComments(postId, -1);
    bindPostComment(postId);
    fixCommentsContent();
    fixPostsContent();
}
$(document).ready(onReady);