(function () {

    var editor = CodeMirror.fromTextArea(document.getElementById("code"), {
	    lineNumbers: true,
	    matchBrackets: true,
		continuousScanning : false,
		indentUnit : 4,
		height : "500px",
		tabMode : "shift", // or "spaces", "default", "shift",
		enterMode : "indent",
		gutter : false,
		reindentOnLoad : false,
		activeTokens : null,
		cursorActivity : null,
        extraKeys: {"F11": toggleFullscreenEditing, "Esc": toggleFullscreenEditing}
    });

    function toggleFullscreenEditing()
    {
    	alert ("here");
        var editorDiv = $('.CodeMirror-scroll');
        if (!editorDiv.hasClass('fullscreen')) {
            toggleFullscreenEditing.beforeFullscreen = { height: editorDiv.height(), width: editorDiv.width() }
            editorDiv.addClass('fullscreen');
            editorDiv.height('100%');
            editorDiv.width('100%');
            editor.refresh();
        }
        else {
            editorDiv.removeClass('fullscreen');
            editorDiv.height(toggleFullscreenEditing.beforeFullscreen.height);
            editorDiv.width(toggleFullscreenEditing.beforeFullscreen.width);
            editor.refresh();
        }
    }

})();