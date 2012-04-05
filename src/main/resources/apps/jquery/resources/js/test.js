// instead of window.onload
$(document).ready(function() {
	$("a").click(function(event) {
	      event.preventDefault();
	  	  request();	
	});
	
	// Select an element with id: someId and insert dynamic html into it
	$("#yeah").html("<b>So Bold!</b>");
	// or $("#someId").html("<b>So Bold!</b>");

	
});

function request(){ 
    var email = $("#yeah").text();
    alert(email);
    $.get("index", function(data){
    	$("#result").html(data);
    });
    
};

function displayMessage(message){
    alert(message);
};