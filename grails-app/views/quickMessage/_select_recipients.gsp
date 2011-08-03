<%@ page import="grails.converters.JSON" contentType="text/html;charset=UTF-8" %>


<div id="groups">
	<g:each in="${groupList}" var="entry">
		<div>
						<input type="checkbox"   name="groups" value= "${entry.key}" onclick='selectMembers("${entry.key}", ${entry.value as JSON})'>
		${entry.key}(${entry.value.size()})
</input>
</div>
	</g:each>
</div>

<div id="contacts">
	<g:each in="${nonExistingRecipients}" var="address">
		<div>
			<input type="checkbox" name="addresses" value="${address}" checked>${address}</input>
		</div>
	</g:each>

	<g:each in="${contactList}" var="contact">
		<div>
							<input contacts="true" class= "" type="checkbox" name="addresses" value="${contact.primaryMobile}" <g:if test="${recipients.contains(contact.primaryMobile)}">checked</g:if>>
		${contact.name ?: contact.primaryMobile} <g:if test="${recipients.contains(contact.secondaryMobile) || recipients.contains(contact.email)}">(Primary)</g:if>
		</input>
  </div>
  <div>
		<g:if test="${recipients.contains(contact.secondaryMobile)}">
			<input type="checkbox" name="addresses" value="${contact.secondaryMobile}" checked>
			${contact.name ?: contact.secondaryMobile} (Secondary)
		</input>
		</g:if>
		</div>
  <div>
		<g:if test="${recipients.contains(contact.email)}">
			<input  type="checkbox" name="addresses" value="${contact.email}" checked>
			${contact.name ?: contact.email} (Email)
		</input>
		</g:if>
		</div>
	</g:each>
</div>
<g:link url="#" class="back">Back</g:link>
<g:link url="#" class="next">Next</g:link>






<script>
	function selectMembers(groupName, allContacts) {
		$.each(allContacts, function(index, value) {
			setValueForCheckBox(groupName, value, isCheckboxSelected(groupName))
		});
	}

	$("input[contacts='true']").live('click', function() {
		if (!($(this).is(":checked"))) {
			var groups = $(this).attr('class').split(" ")
			$.each(groups, function(index, value) {
				findInputWithValue(value).attr('checked', false);
			});
		}
	});

</script>




