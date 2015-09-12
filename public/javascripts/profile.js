function showProfileError(errorMessage) {
    $("#profile-error-text").text(errorMessage);
    $("#profile-error").show();
}

function onReady()
{
    $("#profile-error").hide();
    $("#profile-upload-avatar-error").hide();
    $("#signout" ).click(function( event ) {
        event.preventDefault();
        postJson("/signout", "{}")
        .done(function(result) {
                window.location.replace("/");
        })
        .fail(function() {
        });
    });
    $("#profile-form" ).submit(function( event ) {
        $("#profile-error").hide();
        event.preventDefault();

        postJson("/profile", JSON.stringify({"name": $('#inputName').val()}))
        .done(function(result) {
            if (result.resultCode) {
                showProfileError(result.resultDesc);
            } else {
                window.location.replace("/profile");
            }
        })
        .fail(function() {
            showProfileError("HTTP request failed");
        });
    });

    $("#profile-upload-avatar-form" ).submit(function( event ) {
        $("#profile-upload-avatar-error").hide();
        event.preventDefault();
        file = $("#avatarFile").prop('files')[0];
        if (!file.type.match('image.*')) {
            $("#profile-upload-avatar-error").text("avatar should be an image file");
            $("#profile-upload-avatar-error").show();
            return;
        }
        if (file.size >= (1024*1024)) {
            $("#profile-upload-avatar-error").text("avatar image file exceeds 1MB");
            $("#profile-upload-avatar-error").show();
            return;
        }
        var reader = new FileReader();
        reader.onload= (function (file) {
            return function (e) {
                console.log("file " + file.name + " size=" + file.size + " read");
                console.log("data length=" + e.target.result.length);

                var bytesArray = new Uint8Array(e.target.result.length);
                for (var i = 0; i < e.target.result.length; i++) {
                    bytesArray[i] = e.target.result[i];
                }
                $.ajax({
                    method: "POST",
                    url: "/profile/upload/avatar",
                    data: bytesArray,
                    processData: false,
                    headers: {"X-Csrf-Token": csrfToken, "X-File-Name" : file.name, "X-File-Size" : file.size},
                    dataType: "json",
                    contentType: "application/octet-stream",
                }).done(function(result) {
                    if (result.resultCode) {
                        $("#profile-upload-avatar-error").text(result.resultDesc);
                        $("#profile-upload-avatar-error").show();
                    } else {
                        window.location.replace("/profile");
                    }
                })
                .fail(function() {
                    $("#profile-upload-avatar-error").text("post failure");
                    $("#profile-upload-avatar-error").show();
                });
            };
        })(file);

        reader.readAsBinaryString(file);
    });
}
$(document).ready(onReady);
