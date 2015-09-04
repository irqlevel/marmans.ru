function onReady()
{
    $(".post-content-text").each(function(index) {
        $(this).html($(this).html().replaceAll("\n", "<br>"));
    });
}
$(document).ready(onReady);