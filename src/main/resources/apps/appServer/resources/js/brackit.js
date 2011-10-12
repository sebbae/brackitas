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
					lineNumbers : true
				});

var editor = CodeMirror
		.fromTextArea(
				'code2',
				{
					parserfile : [
							"http://localhost:8080/apps/appServer/resources/js/tokenizexquery.js",
							"http://localhost:8080/apps/appServer/resources/js/parsexquery.js" ],
					stylesheet : [ "http://localhost:8080/apps/appServer/resources/css/xqcolors.css" ],
					path : "http://localhost:8080/apps/appServer/resources/js/",
					continuousScanning : false, // 500,
					lineNumbers : true
				});

var editor = CodeMirror
		.fromTextArea(
				'code3',
				{
					parserfile : [
							"http://localhost:8080/apps/appServer/resources/js/tokenizexquery.js",
							"http://localhost:8080/apps/appServer/resources/js/parsexquery.js" ],
					stylesheet : [ "http://localhost:8080/apps/appServer/resources/css/xqcolors.css" ],
					path : "http://localhost:8080/apps/appServer/resources/js/",
					continuousScanning : false, // 500,
					lineNumbers : true
				});