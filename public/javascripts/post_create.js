function showPostCreateError(errorMessage) {
    $("#post-create-error-text").text(errorMessage);
    $("#post-create-error").show();
}

function onReady()
{
    $("#post-create-error").hide();
    $("#post-create-form" ).submit(function( event ) {
        $("#post-create-error").hide();
        event.preventDefault();

        postJson("/post/create", JSON.stringify({ "title": $('#inputTitle').val(), "content" : $('#inputContent').val()}))
        .done(function(result) {
            if (result.resultCode) {
                showPostCreateError(result.resultDesc);
            } else {
                window.location.replace("/post/" + result.id);
            }
        })
        .fail(function() {
            showPostCreateError("HTTP request failed");
        });
    });
}
$(document).ready(onReady);