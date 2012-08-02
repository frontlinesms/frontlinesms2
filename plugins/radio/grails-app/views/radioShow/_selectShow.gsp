<%@ page import="frontlinesms2.radio.RadioShow" %>
<radio:selectShow wrapInFormTag="${wrapInFormTag}"
		hideLabel="${hideLabel}"
		from="${radioShows?:RadioShow.findAllByDeleted(false)}"
		radioShowInstance="${radioShowInstance}"
		activityInstance="${activityInstanceToEdit ?: ownerInstance}"/>
<r:script>
	<g:if test="${activityInstanceToEdit}">
		$("#radioShowId").live("change", function() {
			$("#radioShow-confirm").html('<p>' + $("#radioShowId :selected").text().toUpperCase() + '</p>');
		});
	</g:if>
</r:script>
