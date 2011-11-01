var editor = CodeMirror
		.fromTextArea(
				'code',
				{
					parserfile : [
							"http://localhost:8080/apps/appServer/resources/js/tokenizexquery.js",
							"http://localhost:8080/apps/appServer/resources/js/parsexquery.js" ],
					stylesheet : [ "http://localhost:8080/apps/appServer/resources/css/xqcolors.css" ],
					path : "http://localhost:8080/apps/appServer/resources/js/",
					continuousScanning : 500,
					indentUnit : 4,
					lineNumbers : true
				});