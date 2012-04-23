/*
 * [New BSD License]
 * Copyright (c) 2011, Brackit Project Team <info@brackit.org>  
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * 
 * @author Henrique Valer
 * 
 */
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

	$("#addForm").unbind('click').bind('click', (function() {
		var pathName = $("#name").val();
		var app = $("#app").val();
		$.ajax( {
			type : "POST",
			data : "name=" + pathName + "&app=" + app + "&action=rename",
			url : "../../views/fileView/createFormForm",
			cache : false
		}).done(function(html) {
			$("#col3").css("margin-right", "25%");
			$("#col3_content").html(html);

			$.ajax( {
				type : "POST",
				url : "../../views/fileView/createFormOptions",
				cache : false
			}).done(function(html2) {
				$("#col2_content").html(html2);
				addItemUnbind();
			});
		});
	}));

	function addItemUnbind() {
		$('#textInput')
				.unbind('click')
				.bind(
						'click',
						function() {
							$('#formPreview')
									.append("<li class=\"state-default\">text input <input type=\"text\" name=\"textInput\"></input></li>")
						});
		$('#paragraphInput')
				.unbind('click')
				.bind(
						'click',
						function() {
							$("#formPreview")
									.append(
											'<li class="state-default">text area   <textarea rows="6" cols="10" class="boxsizingBorder"/></li>');
						});
		$('#multipleChoiceInput')
				.unbind('click')
				.bind(
						'click',
						function() {
							$("#formPreview")
									.append(
											'<li class="state-default">multiple choice   <input type="radio" name="multipleChoice" value="option1" checked>option1</input></br><input type="radio" name="multipleChoice" value="option2">option2</input></br><input type="radio" name="multipleChoice" value="option3">option3</input></li>');
						});
		$('#dateInput')
				.unbind('click')
				.bind(
						'click',
						function() {
							$("#formPreview")
									.append(
											'<li class="state-default">date input   <input id="inputCalendar" class="hasDatepicker" type="text"/></li>');
						});
		$('#fileUpload')
				.unbind('click')
				.bind(
						'click',
						function() {
							$("#formPreview")
									.append(
											'<li class="state-default">file upload   <input type="file" name="fileUpload"/></li>');
						});
		$(function() {
			$("#formPreview").sortable( {
				revert : true
			});
		});
		
		$(function(){
			$("#createFormButton").click(function(){
				//alert($("#formPreview").html());
				$("#createFormPayload").val($("#formPreview").html());
			});
		});
		
		
	}
	;
})();