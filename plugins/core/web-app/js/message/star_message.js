function setStarStatus(object,data){
	if(data == 'starred') {
		$("#" + object + " a").removeClass("unstarred");
		$("#" + object + " a").addClass(data);
	} else {
		$("#" + object + " a").removeClass("starred");
		$("#" + object + " a").addClass(data);
	}	
}
