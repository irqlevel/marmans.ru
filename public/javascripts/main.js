var csrfToken = "";
var currUserUid = -1;

String.prototype.replaceAll = function(search, replace){
  return this.split(search).join(replace);
}

function postJson(url, json)
{
        return $.ajax({
            method: "POST",
            url: url,
            data: json,
            headers: {"X-Csrf-Token": csrfToken},
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            });
}

function onReady()
{
    csrfToken = $("#csrfToken").text();
    currUserUid = parseInt($("#currUserUid").text());
    console.log("currUserUid=" + currUserUid + " csrfToken=" + csrfToken);
    $("#signout" ).click(function( event ) {
        event.preventDefault();
        postJson("/signout", "{}")
        .done(function(result) {
                window.location.replace("/");
        })
        .fail(function() {
        });
    });
}
$(document).ready(onReady);