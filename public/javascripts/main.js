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

function getJson(url, json)
{
        return $.ajax({
            method: "GET",
            url: url,
            data: json,
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            });
}

function fixPostsContent() {
    $(".post-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}

function onReady()
{
    csrfToken = $("#csrfToken").text();
    currUserUid = parseInt($("#currUserUid").text());
}
$(document).ready(onReady);