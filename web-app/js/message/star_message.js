function setStarStatus(object,data){
	if($("#"+object).hasClass("starred")) {
		$("#"+object).removeClass("starred");
	}

	$("#"+object).addClass(data);
	if(data != '') {
		$("#"+object).empty().append("Remove Star");
	} else {
		$("#"+object).empty().append("Add Star");
	}
}
