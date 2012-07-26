function showWordCloud(stats){
	$("#wordcloud").html("");
	var word_array = []
	$.each(stats, function(key, value){
		word_array.push({text : key, weight: value});
	});

	$(function() {
		var w = $("#wordcloud").parent().width();
		var h = $("#wordcloud").parent().height();
		$("#wordcloud").jQCloud(word_array, {
			width : w,
			height : 0.8*h 
		});
	});
	console.log($("#main-list").offset().top);
	$("#wordcloud-container").css('top', $("#main-list").offset().top);
	$("#wordcloud-container").show();
	$("#main-list").hide();
	$("#main-list-foot").hide();
	$("#poll-details").hide();
	if(typeof(pollGraph) !== "undefined") { pollGraph.loaded = false; }
	$("#poll-graph-btn").html(i18n("fmessage.showpolldetails"));

	$("a[name=show-wordcloud-btn]").hide();
	$("#show-messages-btn").show();
}

function showMessages(){
	$("#poll-details").hide();
	if(typeof(pollGraph) !== "undefined") { pollGraph.loaded = false; }
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