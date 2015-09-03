function showSigninError(errorMessage) {
    $("#signin-error-text").text(errorMessage);
    $("#signin-error").show();
}

function onReady()
{
    $("#signin-error").hide();
    $("#signin-form" ).submit(function( event ) {
        $("#signin-error").hide();
        event.preventDefault();

        postJson("/signin", JSON.stringify({ "email": $('#inputEmail').val(), "password" : $('#inputPassword').val() }))
        .done(function(result) {
            if (result.resultCode) {
                showSigninError(result.resultDesc);
            } else {
                window.location.replace("/profile");
            }
         })
        .fail(function() {
            showSigninError("HTTP request failed");
        });
    });
}
$(document).ready(onReady);
