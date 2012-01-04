//javascript loaded for blinking on-air button and rendering flash messages

function onAir(data) {
	if(data.indexOf("show started") == 0) {
		$("#on-air").addClass("active");
		$("#on-air").effect("pulsate", {}, 1000);
		document.getElementsByClassName("start-show")[0].setAttribute("disabled","disabled");
		document.getElementsByClassName("stop-show")[0].setAttribute("disabled","");
	} else {
		window.location = window.location
	}
}