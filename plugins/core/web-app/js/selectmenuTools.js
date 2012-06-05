var selectmenuTools = {
	init: function(selectId){
		$('#'+selectId).selectmenu();
	},

	snapback: function(selectId){
		$('#'+selectId).selectmenu("destroy");
		$('#'+selectId)[0].selectedIndex = 0;
		selectmenuTools.init(selectId);
	},

	removeSelected: function(selectId){
		$('#'+selectId).find("[value='"+$('#'+selectId).val()+"']").remove();
		selectmenuTools.snapback(selectId);
	}
}