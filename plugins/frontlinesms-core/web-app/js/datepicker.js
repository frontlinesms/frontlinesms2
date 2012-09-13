$(function(){
	$(".datepicker").datepicker({
		buttonImage: url_root + "images/icons/calendar.png",
		buttonImageOnly: true,
		showOn: 'both',
		onSelect: function (dateText, inst) {
			var selects = $(this).parent().children('select');
			var date = new Date(dateText)
			$(selects.get(0)).val(date.getDate());
			$(selects.get(1)).val(date.getMonth()+1);
			$(selects.get(2)).val(date.getFullYear());
			$(selects.trigger("change"));
		}
	});	
});

