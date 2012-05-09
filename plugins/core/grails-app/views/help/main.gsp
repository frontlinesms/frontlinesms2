<div id="help">
	<div id="index" class="vertical-tabs ui-tabs-nav">
		<fsms:render template="index"/>
	</div>
	<div id="file">
	</div>
</div>
<g:javascript>
	function initializePopup() {
		$('#help strong').parents('li').addClass('section');
		$('#help em').parents('li').addClass('sub-section');
		
		$("#help #index li a").click(goToSection);
		$("#help #file").delegate("a", "click", goToSection);
	}
	
	function goToSection() {
		var section = $(this).attr('href');
		$("#help #index li").removeClass('selected');
		$(this).parent('li').addClass('selected');
		var new_url = url_root + "help/getSection";
		$.get(new_url, {helpSection: section}, function(data) {
			$('#file').contents().replaceWith($(data));
		});
		return false;
	}
</g:javascript>
