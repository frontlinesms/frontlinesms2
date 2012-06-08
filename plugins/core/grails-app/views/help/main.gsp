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
	menuItem.parent("li").addClass("selected");
	$("#help-content").load(url_root + "help/section", { helpSection:section });
	return false;
}
</r:script>
<div id="help-index">
	<fsms:render template="index"/>
</div>
<div id="help-content">
</div>

