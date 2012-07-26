var interval; // TODO not safe variable name
var timeout; // TODO not safe variable name

function startShow(data) {
	if(data.ok) { 
		$("#on-air").addClass("onAirIsActive"); // TODO cache jQuery objects
		$("#show-" + data).addClass("onAirIsActive"); // TODO cache jQuery objects
		$("#show-" + data).show();
		$("#on-air").effect("pulsate", {}, 1000);
		$(".start-show").attr("disabled","disabled");
		document.getElementsByClassName("stop-show")[0].setAttribute("disabled",""); // TODO use jquery
	} else {
		window.location = window.location
	}
}

function stopShow(data) {
	$("#on-air").stop(true, true); // TODO cache jQuery objects
	$("#on-air").removeClass("onAirIsActive");
	$("#on-air").css("opacity", "1");
	$("#show-" + data).removeClass("onAirIsActive"); // TODO cache jQuery objects
	$("#show-" + data).hide();
	document.getElementsByClassName("start-show")[0].setAttribute("disabled",""); // TODO use jquery
	document.getElementsByClassName("stop-show")[0].setAttribute("disabled","disabled"); // TODO use jquery
}

