function disablePaginationControls() {
	if($(".prevLink").size() == 0) {
		$("#paging").prepend('<a href="#" class="prevLink disabled"></a>');
	} else {
		$("#paging .prevLink").empty();
	}
	if($(".nextLink").size() == 0) {
		$("#paging").append('<a href="#" class="nextLink disabled"></a>');
	} else {
		$("#paging .nextLink").empty();
	}
	$(".disabled").click(function(e) {e.preventDefault()});
}