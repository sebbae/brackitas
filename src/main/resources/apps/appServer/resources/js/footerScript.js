(function() {
	var editor = CodeMirror.fromTextArea(document.getElementById("code"),
			{
				lineNumbers : true,
				matchBrackets : true,
				continuousScanning : false,
				indentUnit : 4,
				height : "500px",
				tabMode : "shift", // or "spaces", "default", "shift",
				enterMode : "indent",
				gutter : false,
				reindentOnLoad : false,
				activeTokens : null,
				cursorActivity : null,
				lineWrapping : true,
				onCursorActivity : function() {
					editor.setLineClass(hlLine, null);
					hlLine = editor.setLineClass(editor.getCursor().line,
							"activeline");
				},
				extraKeys : {
					"Ctrl-Space" : function(cm) {
						CodeMirror.simpleHint(cm, CodeMirror.xqueryHint);
					}
				}
			});
	var hlLine = editor.setLineClass(0, "activeline");
})();