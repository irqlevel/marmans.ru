var csrfToken = "";
var currUserUid = -1;
var ESUCCESS = 0;
var EINVAL = 1;
var EIO = 2;
var ENOMEM = 3;
var ENOTFOUND = 4;
var EXISTS = 5;
var EPERM = 6;
var ESIGNIN = 7;
var EUNDEF = 8;
var EDB_UPDATE = 9;
var EDB_QUERY = 10;
var EEXCEPT = 11;
var EAUTH = 12;

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