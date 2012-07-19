function showWordCloud(stats){
	$("#wordcloud").html("");
	$.each(stats, function(key, value){
		$("#wordcloud").append("<a href='#' rel='"+value+"'>"+key+"</a>");
	});

	$.fn.tagcloud.defaults = {
	  size: {start: 10, end: 30, unit: 'pt'},
	  color: {start: '#8467D7', end: '#8D38C9'}
	};

	$(function () {
	  $('#wordcloud a').tagcloud();
	});

	$("#wordcloud-container").show();
	$("#main-list").hide();

	$("a[name=show-wordcloud-btn]").hide();
	$("#show-messages-btn").show();
}

function showMessages(){
	$("#wordcloud-container").hide();
	$("#main-list").show();

	$("a[name=show-wordcloud-btn]").show();
	$("#show-messages-btn").hide();
}