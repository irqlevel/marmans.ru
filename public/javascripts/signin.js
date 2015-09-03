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
        $.ajax({
            method: "POST",
            url: "/signin",
            data: JSON.stringify({ "email": $('#inputEmail').val(), "password" : $('#inputPassword').val() }),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            }).done(function(result) {
                if (result.resultCode) {
                    showSigninError(result.result);
                } else {
                    window.location.replace("/profile");
                }
            }).fail(function() {
                showSigninError("HTTP request failed");
            });
    });
}
$(document).ready(onReady);
