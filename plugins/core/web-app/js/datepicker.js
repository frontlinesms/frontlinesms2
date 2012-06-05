$(function(){
	$(".datepicker").datepicker({
		buttonImage : '../images/icons/calendar.png',
		buttonImageOnly: true,
		showOn: 'both',
		onSelect: function (dateText, inst) {
			$($(this).prev('input').val(dateText));
			$($(this).prev('input').trigger("change"));
		}
	});	
});
