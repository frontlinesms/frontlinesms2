var interval;
var timeout;

function startShow(data) {
	if(data.indexOf("already on air") == -1) {
		$("#on-air").addClass("active");
		$("#show-" + data).addClass("active");
		$("#show-" + data).show();
		$("#on-air").effect("pulsate", {}, 1000);
		document.getElementsByClassName("start-show")[0].setAttribute("disabled","disabled");
		document.getElementsByClassName("stop-show")[0].setAttribute("disabled","");
	} else {
		window.location = window.location
	}
}

function stopShow(data) {
	$("#on-air").stop(true, true);
	$("#on-air").removeClass("active");
	$("#on-air").css("opacity", "1");
	$("#show-" + data).removeClass("active");
	$("#show-" + data).hide();
	document.getElementsByClassName("start-show")[0].setAttribute("disabled","");
	document.getElementsByClassName("stop-show")[0].setAttribute("disabled","disabled");
}