function showProfileError(errorMessage) {
    $("#profile-error-text").text(errorMessage);
    $("#profile-error").show();
}

function onReady()
{
    $("#profile-error").hide();
    $("#profile-form" ).submit(function( event ) {
        $("#profile-error").hide();
        event.preventDefault();

        postJson("/profile", JSON.stringify({"name": $('#inputName').val()}))
        .done(function(result) {
            if (result.resultCode) {
                showProfileError(result.result);
            } else {
                window.location.replace("/profile");
            }
        })
        .fail(function() {
            showProfileError("HTTP request failed");
        });
    });
}
$(document).ready(onReady);
