<meta name="layout" content="popup"/>
<r:script>
function initializePopup() {
	$("#modalBox.help #help-index li a").click(goToSection);
	$("#modalBox.help #help-content").delegate("a", "click", goToSection);
	$("div#help-index a:first").click();

	var selecters = ['#help-index > ul','#help-index > ul > li > ul','#help-index > ul > li > ul > li > ul']
	$.each($("#help-index > ul,li:has(ul)"), function(i, selecter) {
        $(selecter).accordion({ 
			collapsible: true,
			heightStyle: "content",
			autoHeight: false, 
			active: true 
		});
    });
	
}

function goToSection() {
	var menuItem = $(this);

	var section = menuItem.attr("href");
	if(section.indexOf("http:") == 0) {
		if(jQuery.browser.msie && jQuery.browser.version <= 7) {
			var loc = window.location.toString();
			var lastSlash = loc.lastIndexOf("/") + 1;
			loc = loc.substring(0, lastSlash);
			if(section.indexOf(loc) !== 0) {
				return true;
			}
			section = section.substring(lastSlash);
		} else {
			return true;
		} 
	}
	$("#modalBox.help #help-index li.selected").removeClass("selected");
	menuItem.parent().addClass("selected");
	$("#help-content").load(url_root + "help/section", { helpSection:section }, function() {
		if(!jQuery.browser.msie || jQuery.browser.version > 7) {
			// This is a workaround for the image URL bug when viewing help from second+
			// action URLs TODO long-term solution is to fix help generation/iframe it
			$("#help-content img").each(function(i, e) {
				e = $(e);
				e.attr("src", url_root + "help/" + e.attr("src"));
			});
		}
	});
	return false;
}
</r:script>
<div id="help-index">
	<fsms:render template="index"/>
</div>
<div id="help-content">
</div>

