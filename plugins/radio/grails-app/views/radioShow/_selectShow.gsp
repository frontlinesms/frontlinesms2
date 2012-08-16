<%@ page import="frontlinesms2.radio.RadioShow" %>
<radio:selectShow wrapInFormTag="${wrapInFormTag}"
		hideLabel="${hideLabel}"
		from="${radioShows?:RadioShow.findAllByDeleted(false)}"
		radioShowInstance="${radioShowInstance}"
		activityInstance="${activityInstanceToEdit ?: ownerInstance}"/>
<r:script>
mediumPopupDeferredInitialisers.push(function(dialog) {
	var radioShowSelecter = $("select#radioShowId");
	<g:if test="${activityInstanceToEdit}">
		radioShowSelecter.bind("change", function() {
			$("#radioShow-confirm").html('<p>' + $("#radioShowId :selected").text().toUpperCase() + '</p>');
		});
	</g:if>
	selectmenuTools.init(radioShowSelecter);
	radioShowSelecter.selectmenu({ showFirstItem:true });
});
</r:script>
