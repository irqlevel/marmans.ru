function showJoinError(errorMessage) {
    $("#join-error-text").text(errorMessage);
    $("#join-error").show();
}

function onReady()
{
    $("#join-error").hide();
    $("#join-form" ).submit(function( event ) {
        $("#join-error").hide();
        event.preventDefault();

        postJson("/join", JSON.stringify({ "email": $('#inputEmail').val(), "password" : $('#inputPassword').val() }))
        .done(function(result) {
            if (result.resultCode) {
                showJoinError(result.resultDesc);
            } else {
                window.location.replace("/profile");
            }
        })
        .fail(function() {
            showJoinError("HTTP request failed");
        });
    });
}
$(document).ready(onReady);
