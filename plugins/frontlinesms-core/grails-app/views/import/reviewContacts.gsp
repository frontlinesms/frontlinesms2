<%@ page contentType="text/html;charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="settings"/>
		<title><g:message code="settings.general.header"/></title>
		<export:resource/>
	</head>
	<body>
		<div id="body-content-head">
			<h1><g:message code="settings.import.contact.review.header"/></h1>
		</div>
		<div id="body-content">
			<p class="known-fields">
				<g:message code="settings.import.contact.review.recognisedTitles" args="${[recognisedTitles.join(', ')]}"/>
			</p>
			<g:form name="reviewForm" action="importData">
				<input type="hidden" name="reviewDone" value="true"/>
				<input type="hidden" name="data" value="contacts"/>
				<input type="hidden" name="csv" value=""/>
				<table id="contactImportReview">
				<g:each in="${csvData}" var="row" status="rowIndex">
					<tr class="${rowIndex == 0 ? 'headers' : ''}">
					<g:each in="${row}" var="celldata" status="cellIndex">
						<td><input data-x="${cellIndex}" data-y="${rowIndex}" type="text" value="${celldata}"> </td>
					</g:each>
					</tr>
				</g:each>
				</table>
			</g:form>
			<a class="stroked icon-cloud-upload" id="submitContacts" onclick="contactImportReviewer.submit();">${g.message(code:'action.save')}</a>
		</div>
		<r:script>
			$(function() {
				contactImportReviewer.init();
			});
		</r:script>
	</body>
</html>

