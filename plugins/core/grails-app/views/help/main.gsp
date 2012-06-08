<meta name="layout" content="popup"/>
<r:script>
function initializePopup() {
	$("#help strong").parents("li").addClass("section");
	$("#help em").parents("li").addClass("sub-section");
	
	$("#help #index li a").click(goToSection);
	$("#help #file").delegate("a", "click", goToSection);
	$("div#index a:first").click();
}

function goToSection() {
	var menuItem = $(this);
	var section = menuItem.attr("href");
	$("#help #index li").removeClass("selected");
	menuItem.parent("li").addClass("selected");
	$("#file").load(url_root + "help/section", { helpSection:section });
	return false;
}
</r:script>
<div id="help">
	<div id="index">
		<fsms:render template="index"/>
	</div>
	<div id="file">
	</div>
</div>

