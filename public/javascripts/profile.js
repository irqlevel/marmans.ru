function showProfileError(errorMessage) {
    $("#profile-error-text").text(errorMessage);
    $("#profile-error").show();
}

function stringToBytes ( str ) {
  var ch, st, re = [];
  for (var i = 0; i < str.length; i++ ) {
    ch = str.charCodeAt(i);  // get char
    st = [];                 // set up "stack"
    do {
      st.push( ch & 0xFF );  // push byte to stack
      ch = ch >> 8;          // shift value down by 1 byte
    }
    while ( ch );
    // add stack contents to result
    // done because chars have "wrong" endianness
    re = re.concat( st.reverse() );
  }
  // return an array of bytes
  return re;
}

function toUTF8Array(str) {
    var utf8 = [];
    for (var i=0; i < str.length; i++) {
        var charcode = str.charCodeAt(i);
        if (charcode < 0x80) utf8.push(charcode);
        else if (charcode < 0x800) {
            utf8.push(0xc0 | (charcode >> 6),
                      0x80 | (charcode & 0x3f));
        }
        else if (charcode < 0xd800 || charcode >= 0xe000) {
            utf8.push(0xe0 | (charcode >> 12),
                      0x80 | ((charcode>>6) & 0x3f),
                      0x80 | (charcode & 0x3f));
        }
        else {
            // let's keep things simple and only handle chars up to U+FFFF...
            utf8.push(0xef, 0xbf, 0xbd); // U+FFFE "replacement character"
        }
    }
    return utf8;
}

function onReady()
{
    $("#profile-error").hide();
    $("#profile-upload-avatar-error").hide();
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
                console.log("file " + file.name + " size=" + file.size + " read" + " type=" + file.type);
                console.log("data length=" + e.target.result.length + " type=" + typeof(e.target.result));

                var bytes = new Uint8Array(e.target.result.length);
                for (var i = 0; i < e.target.result.length; i++) {
                    bytes[i] = e.target.result.charCodeAt(i);
                }
                $.ajax({
                    method: "POST",
                    url: "/profile/upload/avatar",
                    data: bytes,
                    processData: false,
                    headers: {"X-Csrf-Token": csrfToken, "X-File-Name" : file.name, "X-File-Size" : file.size,
                              "X-File-Type" : file.type},
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
