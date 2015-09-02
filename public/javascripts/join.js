$( "#join-form" ).submit(function( event ) {
    event.preventDefault();
    $.post("/join", JSON.stringify({ "email": $('#inputEmail').val(), "password" : $('#inputPassword').val() }),
        function(result) {
            console.log(result);
        },
    "json");
});