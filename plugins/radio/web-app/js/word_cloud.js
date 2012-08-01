function showWordCloud(stats) {
	$("#wordcloud").html("");
	var word_array = [];
	$.each(stats, function(key, value) {
		word_array.push({
			text:key,
			weight:value,
			html:{
				class:"word",
				onClick:"searchForWord('" + key + "')"
			}
		});
	});

	$(function() {
		var w = $("#wordcloud").parent().width();
		var h = $("#wordcloud").parent().height();
		$("#wordcloud").jQCloud(word_array, {
			width:w,
			height:0.8*h,
			afterCloudRender:wordCloudLoaded
		});
	});
	console.log($("#main-list").offset().top);
	var top = $("#main-list-head").offset().top + $("#main-list-head").height();
	console.log("top is "+top);
	$("#wordcloud-container").css('top', top);
	$("#wordcloud-container").show();
	$("#main-list").hide();
	$("#main-list-foot").hide();
	$("#poll-details").hide();
	if(typeof(pollGraph) !== "undefined") { pollGraph.loaded = false; }
	fsmsButton.find("#poll-graph-btn").html(i18n("fmessage.showpolldetails"));
	fsmsButton.find("#show-messages-btn").addClass('disabled');
	fsmsButton.find("#poll-graph-btn").addClass('disabled');
	fsmsButton.find("#show-messages-btn").show();
	fsmsButton.find("input[name=show-wordcloud-btn]").hide();
}

function wordCloudLoaded() {
	fsmsButton.find("#show-messages-btn").removeClass('disabled');
	fsmsButton.find("#poll-graph-btn").removeClass('disabled');
	fsmsButton.find("#reset-words-btn").removeClass('disabled');
}

function showMessages(){
	if(fsmsButton.find("#show-messages-btn").hasClass('disabled')) {
		return;
	}
	$("#poll-details").hide();
	if(typeof(pollGraph) !== "undefined") { pollGraph.loaded = false; }
	fsmsButton.find("#poll-graph-btn").html(i18n("fmessage.showpolldetails"));
	$("#wordcloud-container").hide();
	$("#main-list").show();
	$("#main-list-foot").show();

	fsmsButton.find("input[name=show-wordcloud-btn]").show();
	fsmsButton.find("#show-messages-btn").hide();
}

$("div#wordcloud span.word")
	.live("mouseenter",
		function() {
			var wordId = $(this).attr("id");
			var theX = $('#'+wordId +' #remove-word-'+wordId);
			if (!theX.length) {
				$(this).attr('orig-font-size', $(this).css('font-size'));
				$(this).animate({'font-size': '50px'}, 100);
				$("<a href='#'><span class='remove-word' id='remove-word-"+wordId+"'> X </span></a>").appendTo($(this));
				theX = $('#remove-word-'+wordId);
				theX.click(function(evnt) {
					evnt.stopPropagation();
					if ($("#show-messages-btn").hasClass('disabled'))
						return;
					removeWord($(this).attr('id').substr(('remove-word-').length));
				});
			}
			else {
				theX.show();
				$(this).animate({'font-size': '55px'}, 100);
			}
			$("span.word:not(#"+wordId+")").css("opacity", 0.6);
			$(this).css('z-index', 1);
		})
	.live("mouseleave",
		function() {
			$(this).animate({'font-size': $(this).attr('orig-font-size')}, 100);
			var wordId = $(this).attr("id");
			var theX = $('#remove-word-'+wordId);
			theX.hide();
			$(this).css('z-index', 0);
			$("span.word").css("opacity", 1);
		}
	);

function removeWord(wordId) {
	$("#"+wordId).children().remove();
	var word = $("#"+wordId).text();
	var ignoreWords = $("input#ignoreWords");
	ignoreWords.val(ignoreWords.val() + word + ",");
	console.log("IGNORE: "+$("input#ignoreWords").val());
	fsmsButton.find("input[name=show-wordcloud-btn]").click();
	fsmsButton.find("#reset-words-btn").addClass('disabled');
	fsmsButton.find("#reset-words-btn").show();
}

function searchForWord(word) {
	console.log("searching for "+word);
	var searchUrl = url_root + "search" + '/result?searchString=' + word;
	window.location = searchUrl;
}

function resetWordcloud() {
	if(fsmsButton.find("#show-messages-btn").hasClass('disabled')) {
		return;
	}
	$("input#ignoreWords").val("");
	fsmsButton.find("input[name=show-wordcloud-btn]").click();
	fsmsButton.find("#reset-words-btn").hide();
}

$("#poll-graph-btn").live("click", function() {
	$("#wordcloud-container").hide();
	fsmsButton.find("input[name=show-wordcloud-btn]").show();
	fsmsButton.find("#show-messages-btn").hide();
});

