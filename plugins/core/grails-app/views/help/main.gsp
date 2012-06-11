<meta name="layout" content="popup"/>
<r:script>
function initializePopup() {
	$("#modalBox.help #help-index li a").click(goToSection);
	$("#modalBox.help #help-content").delegate("a", "click", goToSection);
	$("div#help-index a:first").click();
}

function goToSection() {
	var menuItem = $(this);

	var section = menuItem.attr("href");
		$("#modalBox.help #help-index li.selected").removeClass("selected");
		menuItem.parent().addClass("selected");
		$("#help-content").load(url_root + "help/section", { helpSection:section }, function() {
			// This is a workaround for the image URL bug when viewing help from second+
			// action URLs TODO long-term solution is to fix help generation/iframe it
			$("#help-content img").each(function(i, e) {
				e = $(e);
				e.attr("src", url_root + "help/" + e.attr("src"));
			});
		});
	return false;
}
</r:script>
<div id="help-index">
	<fsms:render template="index"/>
</div>
<div id="help-content">
</div>

