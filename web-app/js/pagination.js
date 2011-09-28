function disablePaginationControls() {
	if($(".prevLink").size() == 0) {
		$("#page-arrows").prepend('<a href="#" class="prevLink disabled">Back</a>');
	}
	if($(".nextLink").size() == 0) {
		$("#page-arrows").append('<a href="#" class="nextLink disabled">Back</a>');
	}
	$(".disabled").click(function(e) {e.preventDefault()});
}