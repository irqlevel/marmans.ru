function onReady()
{
    $("#signout" ).click(function( event ) {
        event.preventDefault();
        $.ajax({
            method: "POST",
            url: "/signout",
            }).done(function(result) {
                window.location.replace("/");
            }).fail(function() {

            });
    });
}
$(document).ready(onReady);