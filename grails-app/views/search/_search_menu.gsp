<%@ page contentType="text/html;charset=UTF-8" %>
<script type="text/javascript">
			$(document).ready(function() {
				$('select[name="activityList"]').change(updateSelectedActivity);
			});
			
			function updateSelectedActivity() {
				var f = $('input:hidden[name="selectedActivity"]');
				var selectedActivity = $('select[name="activityList"]').find('option:selected');
				if(selectedActivity.attr("value") == '') return;
				f.val(selectedActivity.text());
			}
</script>
<g:form name="searchDetails" action="search" class="content-menu">
	<div id="search-details" >
		<div class="field">
			<label for="keywords" class="keywords">Keyword or phrase:</label>
			<g:textField name="keywords" id="keywords" value=""/>
		</div>
		<div id="search-filters">
			<g:if test="${groupInstanceList || pollInstanceList}">
				<label>Limit Search to:</label>
				<div class="field">
					<g:select name="groupId" from="${groupInstanceList}" value="groupId"
							  optionKey="id"
							  optionValue="name"/>
				</div>
				<div class="field">
					<g:select name="activityId" from="${pollInstanceList + folderInstanceList}" value="activityId"
							  optionKey="${{(it instanceof frontlinesms2.Poll?'poll':'folder') + '-' + it.id}}"
							  optionValue="${{it instanceof frontlinesms2.Poll? it.title: it.value}}"/>
				</div>
			</g:if>

		</div>
	</div>
	<div class="buttons">
		<g:actionSubmit class="search" name="search" value="${message(code: 'default.button.search.label', default: 'Search')}"/>
	</div>
</g:form>