function setStarStatus(object,data){
	if(data == 'starred') {
		$("#"+object).addClass(data);
		$("#"+object).removeClass("unstarred");
	} else {
		$("#"+object).addClass(data);
		$("#"+object).removeClass("starred");
	}	
}
