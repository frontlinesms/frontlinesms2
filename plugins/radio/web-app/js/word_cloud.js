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
	$("#main-list-foot").hide();
	$("#poll-details").hide();
	pollGraph.loaded = false
	$("#poll-graph-btn").html(i18n("fmessage.showpolldetails"));

	$("a[name=show-wordcloud-btn]").hide();
	$("#show-messages-btn").show();
}

function showMessages(){
	$("#poll-details").hide();
	pollGraph.loaded = false
	$("#poll-graph-btn").html(i18n("fmessage.showpolldetails"));
	$("#wordcloud-container").hide();
	$("#main-list").show();
	$("#main-list-foot").show();

	$("a[name=show-wordcloud-btn]").show();
	$("#show-messages-btn").hide();
}

$("#poll-graph-btn").live("click", function(){
	if(pollGraph.loaded) {
		$("#wordcloud-container").hide();
		$("a[name=show-wordcloud-btn]").show();
		$("#show-messages-btn").hide();
	} else {
		$("#wordcloud-container").hide();
		$("a[name=show-wordcloud-btn]").show();
		$("#show-messages-btn").hide();
	}
});