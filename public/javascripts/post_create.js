function postCreateShowError(msg) {
    $("#post-create-error-text").text(msg);
    $("#post-create-error").show();
}

function postCreateHideError() {
    $("#post-create-error").hide();
}

function uploadPostImage(postId)
{
    file = $("#postImage").prop('files')[0];
    if (!file.type.match('image.*')) {
        postCreateShowError("post image should be an image file");
        return;
    }

    if (file.size >= (1024*1024)) {
        postCreateShowError("post image file exceeds 1MB");
        return;
    }

    var reader = new FileReader();
    reader.onload= (function (file) {
        return function (e) {
            console.log("file " + file.name + " size=" + file.size + " read" + " type=" + file.type);
            console.log("data length=" + e.target.result.length + " type=" + typeof(e.target.result));

            var bytes = new Uint8Array(e.target.result.length);
            for (var i = 0; i < e.target.result.length; i++) {
                bytes[i] = e.target.result.charCodeAt(i);
            }
            $.ajax({
                method: "POST",
                url: "//post/" + postId + "/setImage",
                data: bytes,
                processData: false,
                headers: {"X-Csrf-Token": csrfToken, "X-File-Name" : file.name, "X-File-Size" : file.size,
                          "X-File-Type" : file.type},
                dataType: "json",
                contentType: "application/octet-stream",
            }).done(function(result) {
                if (result.resultCode) {
                    postCreateShowError(result.resultDesc);
                    postJson("/post/" + postId + "/delete", "{}")
                    .done(function(result) {
                    })
                    .fail(function() {
                    });
                } else {
                    postJson("/post/" + postId + "/setActive", "{}")
                    .done(function(result) {
                        window.location.replace("/post/" + postId);
                    })
                    .fail(function() {
                        postCreateShowError("post activation failed");
                    });
                }
            })
            .fail(function() {
                postCreateShowError("HTTP failure");
            });
        };
    })(file);

    reader.readAsBinaryString(file);
}

function onReady()
{
    $("#post-create-error").hide();
    $("#inputTitle").focus();
    $("#post-create-form" ).submit(function( event ) {
        event.preventDefault();
        postCreateHideError();
        postJson("/post/create", JSON.stringify({ "title": $('#inputTitle').val(), "content" : $('#inputContent').val()}))
        .done(function(result) {
            if (result.resultCode) {
                postCreateShowError(result.resultDesc);
            } else {
                uploadPostImage(result.id);
            }
        })
        .fail(function() {
            showPostCreateError("HTTP request failed");
        });
    });
}
$(document).ready(onReady);