<%@ page import="frontlinesms2.radio.RadioShow" %>
<radio:selectShow formtag="${formtag}" from="${radioShows?:RadioShow.findAllByDeleted(false)}" radioShowIntance="${radioShowIntance}" ownerInstance="${activityInstanceToEdit ?: ownerInstance}"/>
<r:script>
	$("#radioShowId").live("change", function() {
		$("#radioShow-confirm").html('<p>' + $("#radioShowId :selected").text().toUpperCase() + '</p>');
	});
</r:script>