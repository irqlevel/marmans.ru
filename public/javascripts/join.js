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
        $.ajax({
            method: "POST",
            url: "/join",
            data: JSON.stringify({ "email": $('#inputEmail').val(), "password" : $('#inputPassword').val() }),
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            }).done(function(result) {
                if (result.resultCode) {
                    showJoinError(result.result);
                } else {
                    window.location.replace("/signin");
                }
            }).fail(function() {
                showJoinError("HTTP request failed");
            });
    });
}
$(document).ready(onReady);
