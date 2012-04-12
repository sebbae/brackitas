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
	
	$("#save").click(function() {
		editor.save();
		var pathName = $("#name").val();
		var query = editor.getValue();
		
		$.ajax( {
			type : "POST",
			data : "name=" + pathName + "&query=" + query,
			url : "../fileController/save",
			cache : false
		}).done(function(html) {
			$("#editorMessage").html(html);
		});
	});	
	
	$("#rename").click(function() {
		var pathName = $("#name").val();
		var app = $("#app").val();
		$.ajax( {
			type : "POST",
			data : "name=" + pathName + "&app=" + app + "&action=rename",
			url : "../rscController/renameFileForm",
			cache : false
		}).done(function(html) {
			$("#col3_content").html(html);
		});
	});	
	
	$("#compile").click(function() {
		editor.save();		
		var pathName = $("#name").val();
		var query = editor.getValue();
		var app = $("#app").val();
		$.ajax( {
			type : "POST",
			data : "name=" + pathName + "&query=" + query,
			url : "../fileController/compile",
			cache : false
		}).done(function(html) {
			$("#editorMessage").html(html);
			$.ajax( {
				type : "POST",
				data : "app=" + app,
				url : "../../views/appView/createMenuRequest",
				cache : false
			}).done(function(html) {
				$("#col1").html(html);
			});				
		});
	});
	
	$("#delete").click(function() {
		var answer = confirm("Are you sure you want to delete?")
	    if (answer) {
		    var pathName = $("#name").val();
			var app = $("#app").val();
			$.ajax( {
				type : "POST",
				data : "name=" + pathName + "&app=" + app + "&action=rename",
				url : "../fileController/delete",
				cache : false
			}).done(function(html) {
				$("#col3_content").html(html);
			});
			$.ajax( {
				type : "POST",
				data : "app=" + app,
				url : "../../views/appView/createMenuRequest",
				cache : false
			}).done(function(html) {
				$("#col1").html(html);
			});
	    }
	});
	
	$("#addForm").click(function() {
		var pathName = $("#name").val();
		var app = $("#app").val();
		$.ajax( {
			type : "POST",
			data : "name=" + pathName + "&app=" + app + "&action=rename",
			url : "../../views/fileView/createFormForm",
			cache : false
		}).done(function(html) {
			$("#col3").css("margin-right","25%");
			$("#col3_content").html(html);

			$.ajax( {
				type : "POST",
				url : "../../views/fileView/createFormOptions",
				cache : false
			}).done(function(html2) {
				$("#col2_content").html(html2);
			});
		});
	});

})();