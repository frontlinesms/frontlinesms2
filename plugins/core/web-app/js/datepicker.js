$(function(){
	$(".datepicker").datepicker({
		buttonImage : '../images/icons/calendar.png',
		buttonImageOnly: true,
		showOn: 'both',
		onSelect: function (dateText, inst) {
			var date = new Date(dateText)
			$($(this).parent().children('select').get(0)).val(date.getDate());
			$($(this).parent().children('select').get(1)).val(date.getMonth()+1);
			$($(this).parent().children('select').get(2)).val(date.getFullYear());
			$($(this).parent().children('select').trigger("change"));
		}
	});	
});
