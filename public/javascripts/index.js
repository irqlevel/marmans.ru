var post_template = null;

function fixPostsContent() {
    $(".post-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}

function bindPostComment() {
    $(".post-comment" ).click(function( event ) {
        event.preventDefault();
        var postId = parseInt($(this).attr("data-post-id"));
        console.log("click comment for post " + postId);
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
            fixPostsContent();
            bindPostComment();
        }
    })
    .fail(function() {
        console.log("get posts failed");
    });
}

function onReady()
{
    $.get("/assets/templates/post.html", function(template) {
        post_template = template;
        loadPosts(0, 2);
    });

}

$(document).ready(onReady);