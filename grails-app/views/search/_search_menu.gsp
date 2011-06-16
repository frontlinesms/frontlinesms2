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
		<div id="field">
			<label for="keywords" class="keywords">Keyword or phrase:</label>
			<g:textField name="keywords" id="keywords" value=""/>
		</div>
		<div id="filter">
		  <g:if test="${groupInstanceList || pollInstanceList}">
			  <label>Limit Search to:</label>
			  <div class="field">
				  <select id="grouplist" name="groupList">
						  <option value="">Select group</option>
						  <g:each in="${groupInstanceList}" status="i" var="g">
							  <option value="${g.id}">${g.name}</option>
						  </g:each>
				  </select>
			  </div>
			  <div class="field">
				  <select id="activitylist" name="activityList">
						  <option value="">Select activity / folder</option>
						  <g:each in="${pollInstanceList}" status="i" var="p">
							  <option value="${p.id}">${p.title}</option>
						  </g:each>
						  <g:each in="${folderInstanceList}" status="i" var="f">
							  <option value="${f.id}">${f.value}</option>
						  </g:each>
				  </select>
				<g:hiddenField name="selectedActivity" value=""/>
			  </div></g:if>

		</div>
	</div>
	<div class="buttons">
		<g:actionSubmit class="search" name="search" value="${message(code: 'default.button.search.label', default: 'Search')}"/>
	</div>
</g:form>